package com.wutsi.like.`delegate`

import com.wutsi.like.dao.EventRepository
import com.wutsi.like.dto.CreateLikeRequest
import com.wutsi.like.dto.CreateLikeResponse
import com.wutsi.like.entity.EventEntity
import com.wutsi.like.event.EventType.LIKED
import com.wutsi.like.event.LikedEventPayload
import com.wutsi.like.service.UrlNormalizer
import com.wutsi.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class CreateDelegate(
    private val urlNormalizer: UrlNormalizer,
    private val dao: EventRepository,
    private val eventStream: EventStream,
    private val cache: Cache
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CreateDelegate::class.java)
    }

    fun invoke(request: CreateLikeRequest): CreateLikeResponse {
        // Persist
        val entity = dao.save(
            EventEntity(
                type = LIKED,
                urlHash = urlNormalizer.hash(request.canonicalUrl),
                deviceUUID = request.deviceUUID,
                userId = request.userId,
                canonicalUrl = urlNormalizer.normalize(request.canonicalUrl),
                timestamp = request.likeDateTime?.let { it } ?: OffsetDateTime.now()
            )
        )

        // Publish event
        eventStream.publish(
            type = LIKED.urn,
            payload = LikedEventPayload(likeId = entity.id!!)
        )

        // Remove from cache
        cacheEvict(entity)

        return CreateLikeResponse(
            likeId = entity.id
        )
    }

    private fun cacheEvict(entity: EventEntity) {
        try {
            cache.evict(entity.urlHash)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to evict ${entity.urlHash} from the Cache", ex)
        }
    }
}
