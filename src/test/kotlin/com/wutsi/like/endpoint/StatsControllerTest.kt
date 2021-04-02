package com.wutsi.like.endpoint

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.like.dto.GetStatsResponse
import com.wutsi.like.service.UrlNormalizer
import org.junit.jupiter.api.Assertions.assertEquals
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
import org.springframework.web.client.RestTemplate
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/StatsController.sql"])
internal class StatsControllerTest {
    @LocalServerPort
    private val port = 0

    @MockBean
    lateinit var cacheManager: CacheManager

    @MockBean
    lateinit var cache: Cache

    @Autowired
    lateinit var urlNormalizer: UrlNormalizer

    lateinit var rest: RestTemplate

    lateinit var url: String

    @BeforeEach
    fun setUp() {
        rest = RestTemplate()
        url = "http://127.0.0.1:$port/v1/likes/stats?canonical_url={url}"

        doReturn(cache).whenever(cacheManager).getCache("default")
    }

    @Test
    fun `get stats`() {
        val result = rest.getForEntity(url, GetStatsResponse::class.java, "https://www.google.com")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("https://www.google.com", result.body.canonicalUrl)
        assertEquals(4L, result.body.count)
        assertFalse(result.body.liked)
    }

    @Test
    fun `get stats - liked by userId`() {
        val result = rest.getForEntity("$url&user_id={user_id}", GetStatsResponse::class.java, "https://www.google.com", "1")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("https://www.google.com", result.body.canonicalUrl)
        assertEquals(4L, result.body.count)
        assertTrue(result.body.liked)
    }

    @Test
    fun `get stats - not-liked by userId`() {
        val result = rest.getForEntity("$url&user_id={user_id}", GetStatsResponse::class.java, "https://www.google.com", "99")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("https://www.google.com", result.body.canonicalUrl)
        assertEquals(4L, result.body.count)
        assertFalse(result.body.liked)
    }

    @Test
    fun `get stats - liked by device`() {
        val result = rest.getForEntity(
            "$url&device_uuid={device_uuid}",
            GetStatsResponse::class.java,
            "https://www.google.com",
            "4fad8daa-d502-41b7-ac4a-2c7d97fa7543"
        )

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("https://www.google.com", result.body.canonicalUrl)
        assertEquals(4L, result.body.count)
        assertTrue(result.body.liked)
    }

    @Test
    fun `get stats - not liked by device`() {
        val result = rest.getForEntity("$url&device_uuid={device_uuid}", GetStatsResponse::class.java, "https://www.google.com", "xxx")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("https://www.google.com", result.body.canonicalUrl)
        assertEquals(4L, result.body.count)
        assertFalse(result.body.liked)
    }

    @Test
    fun `get stats bad link`() {
        val result = rest.getForEntity(url, GetStatsResponse::class.java, "http://xXx.com")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("http://xXx.com", result.body.canonicalUrl)
        assertEquals(0L, result.body.count)
        assertFalse(result.body.liked)
    }

    @Test
    fun `stats returned are cached`() {
        val hash = urlNormalizer.hash("https://www.google.com")
        val result = rest.getForEntity("$url&user_id={user_id}", GetStatsResponse::class.java, "https://www.google.com", "1")

        verify(cache).put(hash, result.body)
    }

    @Test
    fun `get stats from DB when cache fails`() {
        val hash = urlNormalizer.hash("https://www.google.com")
        doThrow(RuntimeException::class).whenever(cache).get(hash, GetStatsResponse::class.java)

        val result = rest.getForEntity("$url&user_id={user_id}", GetStatsResponse::class.java, "https://www.google.com", "1")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(4L, result.body.count)
    }

    @Test
    fun `get stats from cache`() {
        val hash = urlNormalizer.hash("https://www.google.com")
        val response = GetStatsResponse(
            liked = true,
            count = 11L,
            canonicalUrl = "https://xxx.com"
        )
        doReturn(response).whenever(cache).get(hash, GetStatsResponse::class.java)

        val result = rest.getForEntity("$url&user_id={user_id}", GetStatsResponse::class.java, "https://www.google.com", "1")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response.canonicalUrl, result.body.canonicalUrl)
        assertEquals(response.count, result.body.count)
        assertEquals(response.liked, result.body.liked)
    }
}
