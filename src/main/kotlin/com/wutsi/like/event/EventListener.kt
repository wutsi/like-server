package com.wutsi.like.event

import com.wutsi.like.dao.EventRepository
import com.wutsi.like.domain.EventEntity
import com.wutsi.like.event.EventType.LIKED
import com.wutsi.like.service.UrlNormalizer
import com.wutsi.stream.Event
import com.wutsi.stream.ObjectMapperBuilder
import org.springframework.cache.CacheManager
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventListener(
    private val urlNormalizer: UrlNormalizer,
    private val dao: EventRepository,
    private val cacheManager: CacheManager
) {
    @EventListener
    fun onEvent(event: Event) {
        val type = event.type
        val mapper = ObjectMapperBuilder().build()

        if (type == LIKED.urn) {
            val payload = mapper.readValue(event.payload, LikeEventPayload::class.java)
            onLike(event, payload)
        }
    }

    private fun onLike(event: Event, payload: LikeEventPayload) {
        // Persist
        val entity = EventEntity(
            type = LIKED,
            urlHash = urlNormalizer.hash(payload.canonicalUrl),
            deviceUUID = payload.deviceUUID,
            userId = payload.userId,
            canonicalUrl = urlNormalizer.normalize(payload.canonicalUrl),
            timestamp = event.timestamp
        )
        dao.save(entity)

        // Remove from cache
        cacheManager.getCache("default").evict(entity.urlHash)
    }
}
