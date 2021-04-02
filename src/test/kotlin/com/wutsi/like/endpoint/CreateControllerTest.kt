package com.wutsi.like.endpoint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.like.dao.EventRepository
import com.wutsi.like.dto.CreateLikeRequest
import com.wutsi.like.dto.CreateLikeResponse
import com.wutsi.like.event.EventType
import com.wutsi.like.service.UrlNormalizer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
internal class CreateControllerTest {
    @LocalServerPort
    private val port = 0

    @Autowired
    lateinit var dao: EventRepository

    @Autowired
    lateinit var urlNormalizer: UrlNormalizer

    @MockBean
    lateinit var cacheManager: CacheManager

    @MockBean
    lateinit var cache: Cache

    lateinit var url: String

    lateinit var rest: RestTemplate

    @BeforeEach
    fun setUp() {
        url = "http://127.0.0.1:$port/v1/likes"
        rest = RestTemplate()

        doReturn(cache).whenever(cacheManager).getCache("default")
    }

    @Test
    fun `like a link`() {
        val now = OffsetDateTime.of(2010, 11, 3, 2, 30, 0, 0, ZoneOffset.UTC)
        val request = CreateLikeRequest(
            canonicalUrl = "https://WWW.google.ca/",
            deviceUUID = UUID.randomUUID().toString(),
            userId = 11,
            likeDateTime = now
        )
        val response = rest.postForEntity(url, request, CreateLikeResponse::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val event = dao.findById(response.body.likeId).get()
        assertEquals("https://www.google.ca", event.canonicalUrl)
        assertEquals(EventType.LIKED, event.type)
        assertEquals(now.toInstant().toEpochMilli(), event.timestamp.toInstant().toEpochMilli())
        assertEquals("42a439d6731734d571b61d0b097d72ba507ec06d4a9092ef0d4627979d9eb3df", event.urlHash)
        assertEquals(request.deviceUUID, event.deviceUUID)
        assertEquals(request.userId, event.userId)
    }

    @Test
    fun `like a link width no likeDateTime default to now`() {
        val now = OffsetDateTime.now()
        val request = CreateLikeRequest(
            canonicalUrl = "https://WWW.google.ca/",
            deviceUUID = UUID.randomUUID().toString(),
            userId = 11,
            likeDateTime = null
        )
        val response = rest.postForEntity(url, request, CreateLikeResponse::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val event = dao.findById(response.body.likeId).get()
        assertTrue(event.timestamp.toInstant().toEpochMilli() - now.toInstant().toEpochMilli() < 500)
    }

    @Test
    fun `like a link - no canonicalUrl`() {
        val request = CreateLikeRequest(
            canonicalUrl = "",
            deviceUUID = null,
            userId = null
        )

        try {
            rest.postForEntity(url, request, CreateLikeResponse::class.java)
            fail()
        } catch (ex: HttpClientErrorException) {
            assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
        }
    }

    @Test
    fun `evict from cache when liked`() {
        val request = CreateLikeRequest(
            canonicalUrl = "https://WWW.google.ca/",
            deviceUUID = UUID.randomUUID().toString(),
            userId = 11,
            likeDateTime = null
        )
        val hash = urlNormalizer.hash(request.canonicalUrl)

        rest.postForEntity(url, request, CreateLikeResponse::class.java)

        verify(cache).evict(hash)
    }

    @Test
    fun `like should be successful event if cache eviction fails`() {
        val request = CreateLikeRequest(
            canonicalUrl = "https://WWW.google.ca/",
            deviceUUID = UUID.randomUUID().toString(),
            userId = 11,
            likeDateTime = null
        )
        val hash = urlNormalizer.hash(request.canonicalUrl)

        doThrow(RuntimeException::class).whenever(cache).evict(hash)

        val response = rest.postForEntity(url, request, CreateLikeResponse::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
