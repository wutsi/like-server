package com.wutsi.like.`delegate`

import com.wutsi.like.domain.LikeEntity
import com.wutsi.like.model.Like
import com.wutsi.like.model.SearchLikeResponse
import com.wutsi.like.service.LikeService
import org.springframework.stereotype.Service
import java.time.ZoneOffset

@Service
public class SearchDelegate(private val service: LikeService) {
    public fun invoke(
        canonicalUrl: String,
        userId: Long? = null,
        deviceUuid: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): SearchLikeResponse {
        val likes = service.search(canonicalUrl, userId, deviceUuid)
        return SearchLikeResponse(
            likes = likes.map { toLike(it) }
        )
    }

    private fun toLike(obj: LikeEntity) = Like(
        id = obj.id?.let { it } ?: -1,
        deviceUUID = obj.deviceUUID,
        userId = obj.userId,
        canonicalUrl = obj.canonicalUrl,
        likeDateTime = obj.likeDateTime.toInstant().atOffset(ZoneOffset.UTC)
    )
}
