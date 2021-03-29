package com.wutsi.like.`delegate`

import com.wutsi.like.dao.EventRepository
import com.wutsi.like.domain.EventEntity
import com.wutsi.like.event.EventType.LIKED
import com.wutsi.like.model.Like
import com.wutsi.like.model.SearchLikeResponse
import com.wutsi.like.service.UrlNormalizer
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
public class SearchDelegate(
    private val dao: EventRepository,
    private val urlNormalizer: UrlNormalizer,
) {
    fun invoke(
        canonicalUrl: String,
        userId: Long? = null,
        deviceUuid: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): SearchLikeResponse {
        val urlHash = urlNormalizer.hash(canonicalUrl)
        val pagination = PageRequest.of(offset / limit, limit, Sort.by("timestamp").descending())
        val events = if (userId != null)
            dao.findByUrlHashAndUserIdAndType(urlHash, userId, LIKED, pagination)
        else if (deviceUuid != null)
            dao.findByUrlHashAndDeviceUUIDAndType(urlHash, deviceUuid, LIKED, pagination)
        else
            emptyList()

        val likes = events.groupBy { it.generateUserOrDeviceKey() }
            .filter { isLiked(it.value) }
            .map { it.value[0] }

        return SearchLikeResponse(
            likes = likes.map { toLike(it) }
        )
    }

    private fun isLiked(events: List<EventEntity>): Boolean =
        events.size % 2 == 1

    private fun toLike(obj: EventEntity) = Like(
        id = obj.id?.let { it } ?: -1,
        deviceUUID = obj.deviceUUID,
        userId = obj.userId,
        canonicalUrl = obj.canonicalUrl,
        likeDateTime = obj.timestamp
    )
}
