package com.wutsi.like.service

import com.wutsi.like.dao.LikeRepository
import com.wutsi.like.domain.Like
import com.wutsi.like.model.CreateLikeRequest
import org.springframework.stereotype.Service
import java.util.Date
import javax.transaction.Transactional

@Service
public class LikeService(
    private val urlNormalizer: UrlNormalizer,
    private val dao: LikeRepository
) {
    @Transactional
    fun create(request: CreateLikeRequest): Like {
        val like = dao.save(
            Like(
                canonicalUrl = urlNormalizer.normalize(request.canonicalUrl),
                urlHash = urlNormalizer.hash(request.canonicalUrl),
                deviceUUID = request.deviceUUID?.toLowerCase(),
                userId = request.userId,
                likeDateTime = Date()
            )
        )
        return like
    }

    @Transactional
    fun delete(id: Long) {
        val like = dao.findById(id)
        if (like.isPresent) {
            dao.delete(like.get())
        }
    }

    fun count(canonicalUrl: String): Long {
        val urlHash = urlNormalizer.hash(canonicalUrl)
        return dao.countByUrlHash(urlHash)
    }
}
