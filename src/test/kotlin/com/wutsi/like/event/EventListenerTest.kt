package com.wutsi.like.event

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.like.dao.EventRepository
import com.wutsi.like.domain.EventEntity
import com.wutsi.like.event.EventType.LEGACY_LIKED
import com.wutsi.like.event.EventType.LEGACY_UNLIKED
import com.wutsi.like.event.EventType.LIKED
import com.wutsi.like.event.EventType.SUBMITTED
import com.wutsi.like.service.LegacyService
import com.wutsi.like.service.UrlNormalizer
import com.wutsi.stream.Event
import com.wutsi.stream.EventStream
import com.wutsi.stream.ObjectMapperBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import com.wutsi.blog.stream.LikeEventPayload as LegacyEventPayload

internal class EventListenerTest {
    private lateinit var urlNormalizer: UrlNormalizer
    private lateinit var entity: EventEntity
    private lateinit var dao: EventRepository
    private lateinit var eventStream: EventStream
    private lateinit var cacheManager: CacheManager
    private lateinit var cache: Cache
    private lateinit var listener: EventListener
    private lateinit var legacyService: LegacyService

    @BeforeEach
    fun setUp() {
        urlNormalizer = UrlNormalizer()

        dao = mock()
        entity = EventEntity(id = 11L)
        doReturn(entity).whenever(dao).save(any())

        eventStream = mock()

        cache = mock()
        cacheManager = mock()
        doReturn(cache).whenever(cacheManager).getCache("default")

        legacyService = mock()

        listener = EventListener(
            urlNormalizer = urlNormalizer,
            dao = dao,
            eventStream = eventStream,
            cacheManager = cacheManager,
            legacyService = legacyService
        )
    }

    @Test
    fun `store LIKE event on SUBMITTED`() {
        val payload = createSubmittedPayload()
        val event = createEvent(SUBMITTED, payload)

        listener.onEvent(event)

        val entity = argumentCaptor<EventEntity>()
        verify(dao).save(entity.capture())

        assertEquals(LIKED, entity.firstValue.type)
        assertEquals("http://www.google.ca", entity.firstValue.canonicalUrl)
        assertEquals("e0be272bd656a32b5e4e701b4005344107dd26e435a0d1325d9723a885d5b625", entity.firstValue.urlHash)
        assertEquals(payload.deviceUUID, entity.firstValue.deviceUUID)
        assertNotNull(entity.firstValue.timestamp)
    }

    @Test
    fun `clear the cache on SUBMITTED`() {
        listener.onEvent(createEvent(SUBMITTED, createSubmittedPayload()))

        verify(cache).evict(any())
    }

    @Test
    fun `emit LIKED event on SUBMITTED`() {
        val payload = createSubmittedPayload()
        listener.onEvent(createEvent(SUBMITTED, payload))

        val actual = argumentCaptor<LikedEventPayload>()
        verify(eventStream).publish(eq(LIKED.urn), actual.capture())
        assertEquals(entity.id, actual.firstValue.likeId)
    }

    @Test
    fun `LEGACY_LIKED enqueued as SUBMITTED`() {
        doReturn("http://www.wutsi.com/read/123").whenever(legacyService).storyUrl(any())

        val payload = createLegacyPayload()
        listener.onEvent(createEvent(LEGACY_LIKED, payload))

        val actual = argumentCaptor<SubmittedEventPayload>()
        verify(eventStream).enqueue(eq(SUBMITTED.urn), actual.capture())

        assertEquals("http://www.wutsi.com/read/123", actual.firstValue.canonicalUrl)
        assertEquals(payload.deviceUUID, actual.firstValue.deviceUUID)
        assertEquals(payload.userId, actual.firstValue.userId)
        assertEquals(payload.likeDateTime.toInstant().toEpochMilli(), actual.firstValue.likeDateTime.toInstant().toEpochMilli())
    }

    @Test
    fun `LEGACY_UNLIKED enqueued as SUBMITTED`() {
        doReturn("http://www.wutsi.com/read/123").whenever(legacyService).storyUrl(any())

        val payload = createLegacyPayload()
        listener.onEvent(createEvent(LEGACY_UNLIKED, payload))

        val actual = argumentCaptor<SubmittedEventPayload>()
        verify(eventStream).enqueue(eq(SUBMITTED.urn), actual.capture())

        assertEquals("http://www.wutsi.com/read/123", actual.firstValue.canonicalUrl)
        assertEquals(payload.deviceUUID, actual.firstValue.deviceUUID)
        assertEquals(payload.userId, actual.firstValue.userId)
        assertEquals(payload.likeDateTime.toInstant().toEpochMilli(), actual.firstValue.likeDateTime.toInstant().toEpochMilli())
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
