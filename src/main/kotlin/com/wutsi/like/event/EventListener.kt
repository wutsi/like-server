package com.wutsi.like.event

import com.wutsi.like.delegate.CreateDelegate
import com.wutsi.like.dto.CreateLikeRequest
import com.wutsi.like.service.LegacyService
import com.wutsi.stream.Event
import com.wutsi.stream.ObjectMapperBuilder
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import com.wutsi.blog.stream.LikeEventPayload as LegacyEventPayload

@Service
class EventListener(
    private val createDelegate: CreateDelegate,
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
        if (isLegacyEvent(type)) {
            val payload = mapper.readValue(event.payload, LegacyEventPayload::class.java)
            onLegacyEvent(event, payload)
        }
    }

    private fun onLegacyEvent(event: Event, payload: LegacyEventPayload) {
        createDelegate.invoke(
            CreateLikeRequest(
                userId = payload.userId,
                deviceUUID = payload.deviceUUID,
                canonicalUrl = legacyService.storyUrl(payload.storyId),
                likeDateTime = payload.likeDateTime
            )
        )
    }

    private fun isLegacyEvent(type: String): Boolean =
        type == EventType.LEGACY_LIKED.urn || type == EventType.LEGACY_UNLIKED.urn
}
