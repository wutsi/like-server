package com.wutsi.like.event

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.like.delegate.CreateDelegate
import com.wutsi.like.dto.CreateLikeRequest
import com.wutsi.like.event.EventType.LEGACY_LIKED
import com.wutsi.like.event.EventType.LEGACY_UNLIKED
import com.wutsi.like.service.LegacyService
import com.wutsi.like.service.UrlNormalizer
import com.wutsi.stream.Event
import com.wutsi.stream.ObjectMapperBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.assertEquals
import com.wutsi.blog.stream.LikeEventPayload as LegacyEventPayload

internal class EventListenerTest {
    private lateinit var urlNormalizer: UrlNormalizer
    private lateinit var listener: EventListener
    private lateinit var legacyService: LegacyService
    private lateinit var createDelegate: CreateDelegate

    @BeforeEach
    fun setUp() {
        urlNormalizer = UrlNormalizer()

        legacyService = mock()

        createDelegate = mock()
        listener = EventListener(
            createDelegate = createDelegate,
            legacyService = legacyService
        )
    }

    @Test
    fun `LEGACY_LIKED forwared to CreateDelegate`() {
        doReturn("http://www.wutsi.com/read/123").whenever(legacyService).storyUrl(any())

        val payload = createLegacyPayload()
        listener.onEvent(createEvent(LEGACY_LIKED, payload))

        val request = argumentCaptor<CreateLikeRequest>()
        verify(createDelegate).invoke(request.capture())

        assertEquals("http://www.wutsi.com/read/123", request.firstValue.canonicalUrl)
        assertEquals(payload.deviceUUID, request.firstValue.deviceUUID)
        assertEquals(payload.userId, request.firstValue.userId)
        assertEquals(payload.likeDateTime.toInstant().toEpochMilli(), request.firstValue.likeDateTime?.toInstant()?.toEpochMilli())
    }

    @Test
    fun `LEGACY_UNLIKED enqueued as SUBMITTED`() {
        doReturn("http://www.wutsi.com/read/123").whenever(legacyService).storyUrl(any())

        val payload = createLegacyPayload()
        listener.onEvent(createEvent(LEGACY_UNLIKED, payload))

        val request = argumentCaptor<CreateLikeRequest>()
        verify(createDelegate).invoke(request.capture())

        assertEquals("http://www.wutsi.com/read/123", request.firstValue.canonicalUrl)
        assertEquals(payload.deviceUUID, request.firstValue.deviceUUID)
        assertEquals(payload.userId, request.firstValue.userId)
        assertEquals(payload.likeDateTime.toInstant().toEpochMilli(), request.firstValue.likeDateTime?.toInstant()?.toEpochMilli())
    }

    @Test
    private fun createEvent(type: EventType, payload: Any) = Event(
        id = UUID.randomUUID().toString(),
        type = type.urn,
        payload = ObjectMapperBuilder().build().writeValueAsString(payload)
    )

    private fun createSubmittedPayload() = SubmittedEventPayload(
        canonicalUrl = "http://www.google.ca",
        deviceUUID = UUID.randomUUID().toString(),
        userId = 11L
    )

    private fun createLegacyPayload() = LegacyEventPayload(
        deviceUUID = UUID.randomUUID().toString(),
        userId = 11L,
        storyId = 333L,
        likeDateTime = OffsetDateTime.now(),
        likeId = System.currentTimeMillis()
    )
}
