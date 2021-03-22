package com.wutsi.like.service

import com.wutsi.like.dao.LikeRepository
import com.wutsi.like.domain.Like
import com.wutsi.like.model.CreateLikeRequest
import org.springframework.stereotype.Service
import java.util.Date

@Service
public class LikeService(
    private val urlNormilizer: UrlNormilizer,
    private val dao: LikeRepository
) {
    fun create(request: CreateLikeRequest): Like {
        val like = dao.save(
            Like(
                canonicalUrl = request.canonicalUrl,
                urlHash = urlNormilizer.hash(request.canonicalUrl),
                deviceUUID = request.deviceUUID,
                userId = request.userId,
                likeDateTime = Date()
            )
        )
        return like
    }

    fun delete(id: Long) {
        val like = dao.findById(id)
        if (like.isPresent)
            dao.delete(like.get())
    }
}
