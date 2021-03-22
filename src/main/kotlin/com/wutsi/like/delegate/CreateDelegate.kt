package com.wutsi.like.`delegate`

import com.wutsi.like.model.CreateLikeRequest
import com.wutsi.like.model.CreateLikeResponse
import com.wutsi.like.service.LikeService
import org.springframework.stereotype.Service

@Service
public class CreateDelegate(private val service: LikeService) {
    fun invoke(request: CreateLikeRequest): CreateLikeResponse {
        val like = service.create(request)
        return CreateLikeResponse(
            likeId = like.id!!
        )
    }
}
