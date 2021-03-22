package com.wutsi.like.service

import com.wutsi.like.dao.LikeRepository
import com.wutsi.like.domain.Like
import com.wutsi.like.model.CreateLikeRequest
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.Date
import javax.transaction.Transactional

@Service
public class LikeService(
    private val urlNormalizer: UrlNormalizer,
    private val dao: LikeRepository
) {
    companion object {
        const val CACHE_NAME = "default"
    }

    @Transactional
    @CacheEvict(value = [CACHE_NAME], key = "#result.urlHash")
    fun create(request: CreateLikeRequest): Like =
        dao.save(
            Like(
                canonicalUrl = urlNormalizer.normalize(request.canonicalUrl),
                urlHash = urlNormalizer.hash(request.canonicalUrl),
                deviceUUID = request.deviceUUID?.toLowerCase(),
                userId = request.userId,
                likeDateTime = Date()
            )
        )

    @Transactional
    @CacheEvict(value = [CACHE_NAME], key = "#result.urlHash")
    fun delete(id: Long): Like? {
        val opt = dao.findById(id)
        if (opt.isPresent) {
            val like = opt.get()
            dao.delete(like)
            return like
        }
        return null
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
}

data class LikeCounter(
    val urlHash: String,
    val count: Long
)
