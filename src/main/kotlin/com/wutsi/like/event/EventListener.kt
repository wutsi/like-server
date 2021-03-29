package com.wutsi.like.event

import com.wutsi.like.dao.EventRepository
import com.wutsi.like.domain.EventEntity
import com.wutsi.like.event.EventType.LIKED
import com.wutsi.like.event.EventType.SUBMITTED
import com.wutsi.like.service.LegacyService
import com.wutsi.like.service.UrlNormalizer
import com.wutsi.stream.Event
import com.wutsi.stream.EventStream
import com.wutsi.stream.ObjectMapperBuilder
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventListener(
    private val urlNormalizer: UrlNormalizer,
    private val dao: EventRepository,
    private val eventStream: EventStream,
    private val cacheManager: CacheManager,
    private val legacyService: LegacyService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EventListener::class.java)
    }

    private val mapper = ObjectMapperBuilder().build()

    @EventListener
    fun onEvent(event: Event) {
        LOGGER.info("onEvent($event)")

        val type = event.type
        if (type == SUBMITTED.urn) {
            val payload = mapper.readValue(event.payload, SubmittedEventPayload::class.java)
            onSubmitted(event, payload)
        } else if (isLegacyEvent(type)) {
            val payload = mapper.readValue(event.payload, LegacyEventPayload::class.java)
            onLegacyEvent(event, payload)
        }
    }

    private fun onSubmitted(event: Event, payload: SubmittedEventPayload) {
        // Persist
        val entity = dao.save(
            EventEntity(
                type = LIKED,
                urlHash = urlNormalizer.hash(payload.canonicalUrl),
                deviceUUID = payload.deviceUUID,
                userId = payload.userId,
                canonicalUrl = urlNormalizer.normalize(payload.canonicalUrl),
                timestamp = event.timestamp
            )
        )

        // Publish event
        eventStream.publish(LIKED.urn, LikedEventPayload(likeId = entity.id!!))

        // Remove from cache
        cacheManager.getCache("default").evict(entity.urlHash)
    }

    private fun onLegacyEvent(event: Event, payload: LegacyEventPayload) {
        eventStream.enqueue(
            type = SUBMITTED.urn,
            payload = SubmittedEventPayload(
                userId = payload.userId,
                deviceUUID = payload.deviceUUID,
                likeDateTime = payload.likeDateTime,
                canonicalUrl = legacyService.storyUrl(payload.storyId)
            )
        )
    }

    private fun isLegacyEvent(type: String): Boolean =
        type == EventType.LEGACY_LIKED.urn || type == EventType.LEGACY_UNLIKED.urn
}
