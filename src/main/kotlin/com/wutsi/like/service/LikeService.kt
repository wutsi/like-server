package com.wutsi.like.service

import com.wutsi.like.dao.LikeRepository
import com.wutsi.like.domain.LikeEntity
import com.wutsi.like.model.CreateLikeRequest
import com.wutsi.stream.EventStream
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.Date
import javax.transaction.Transactional

@Service
public class LikeService(
    private val urlNormalizer: UrlNormalizer,
    private val dao: LikeRepository,
    private val events: EventStream
) {
    companion object {
        const val CACHE_NAME = "default"
    }

    @Transactional
    @CacheEvict(value = [CACHE_NAME], key = "#result.urlHash")
    fun create(request: CreateLikeRequest): LikeEntity {
        val like = dao.save(
            LikeEntity(
                canonicalUrl = urlNormalizer.normalize(request.canonicalUrl),
                urlHash = urlNormalizer.hash(request.canonicalUrl),
                deviceUUID = request.deviceUUID?.toLowerCase(),
                userId = request.userId,
                likeDateTime = Date()
            )
        )

        events.publish(
            type = "urn:wutsi:like:liked",
            payload = mapOf(
                "likeId" to like.id,
                "canonicalUrl" to like.canonicalUrl
            )
        )
        return like
    }

    @Transactional
    @CacheEvict(value = [CACHE_NAME], key = "#result.urlHash")
    fun delete(id: Long): LikeEntity? {
        val opt = dao.findById(id)
        if (!opt.isPresent)
            return null

        val like = opt.get()
        dao.delete(like)

        events.publish(
            type = "urn:wutsi:like:unliked",
            payload = mapOf(
                "likeId" to like.id,
                "canonicalUrl" to like.canonicalUrl
            )
        )
        return like
    }

    @Cacheable(value = [CACHE_NAME], key = "#result.urlHash")
    @CachePut(value = [CACHE_NAME], key = "#result.urlHash")
    fun count(canonicalUrl: String): LikeCounter {
        val urlHash = urlNormalizer.hash(canonicalUrl)
        return LikeCounter(
            urlHash = urlHash,
            count = dao.countByUrlHash(urlHash)
        )
    }

    fun search(
        canonicalUrl: String,
        userId: Long? = null,
        deviceUuid: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): List<LikeEntity> {
        val urlHash = urlNormalizer.hash(canonicalUrl)
        val pagination = PageRequest.of(offset / limit, limit, Sort.by("likeDateTime").descending())
        if (userId != null)
            return dao.findByUrlHashAndUserId(urlHash, userId, pagination)
        else if (deviceUuid != null)
            return dao.findByUrlHashAndDeviceUUID(urlHash, deviceUuid, pagination)
        else
            return dao.findByUrlHash(urlHash, pagination)
    }
}

data class LikeCounter(
    val urlHash: String,
    val count: Long
)
