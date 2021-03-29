package com.wutsi.like.`delegate`

import com.wutsi.like.event.EventType
import com.wutsi.like.event.LikeEventPayload
import com.wutsi.like.model.CreateLikeRequest
import com.wutsi.like.model.CreateLikeResponse
import com.wutsi.stream.EventStream
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class CreateDelegate(
    private val events: EventStream
) {
    fun invoke(request: CreateLikeRequest): CreateLikeResponse {
        events.enqueue(
            type = EventType.LIKED.urn,
            payload = LikeEventPayload(
                canonicalUrl = request.canonicalUrl,
                userId = request.userId,
                deviceUUID = request.deviceUUID,
                likeDateTime = OffsetDateTime.now()
            )
        )

        return CreateLikeResponse(
            likeId = -1
        )
    }
}
