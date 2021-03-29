package com.wutsi.like.endpoint

import com.wutsi.like.dao.EventRepository
import com.wutsi.like.event.EventType
import com.wutsi.like.model.CreateLikeRequest
import com.wutsi.like.model.CreateLikeResponse
import com.wutsi.like.service.UrlNormalizer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
internal class CreateControllerTest {
    @LocalServerPort
    private val port = 0

    @Autowired
    lateinit var dao: EventRepository

    @Autowired
    lateinit var nomalizer: UrlNormalizer

    lateinit var url: String

    lateinit var rest: RestTemplate

    @BeforeEach
    fun setUp() {
        url = "http://127.0.0.1:$port/v1/likes"
        rest = RestTemplate()
    }

    @Test
    fun `like a link`() {
        val request = CreateLikeRequest(
            canonicalUrl = "https://WWW.google.ca/",
            deviceUUID = UUID.randomUUID().toString(),
            userId = 11
        )
        val response = rest.postForEntity(url, request, CreateLikeResponse::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        Thread.sleep(10000) // Wait for message to be consumed
        val hash = nomalizer.hash(request.canonicalUrl)
        val events = dao.findByUrlHash(hash)
        assertEquals(1, events.size)

        val event = events[0]
        assertEquals("https://www.google.ca", event.canonicalUrl)
        assertEquals(EventType.LIKED, event.type)
        assertNotNull(event.timestamp)
        assertEquals(hash, event.urlHash)
        assertEquals(request.deviceUUID, event.deviceUUID)
        assertEquals(request.userId, event.userId)
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
}
