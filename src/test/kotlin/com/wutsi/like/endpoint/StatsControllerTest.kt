package com.wutsi.like.endpoint

import com.wutsi.like.model.GetStatsResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.cache.CacheManager
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/StatsController.sql"])
internal class StatsControllerTest {
    @LocalServerPort
    private val port = 0

    @Autowired
    lateinit var cacheManager: CacheManager

    lateinit var rest: RestTemplate

    lateinit var url: String

    @BeforeEach
    fun setUp() {
        rest = RestTemplate()
        url = "http://127.0.0.1:$port/v1/likes/stats?canonical_url={url}"

        cacheManager.cacheNames.forEach {
            cacheManager.getCache(it).clear()
        }
    }

    @Test
    fun `get stats`() {
        val result = rest.getForEntity(url, GetStatsResponse::class.java, "https://www.google.com")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("https://www.google.com", result.body.canonicalUrl)
        assertEquals(4L, result.body.count)
    }

    @Test
    fun `get stats bad link`() {
        val result = rest.getForEntity(url, GetStatsResponse::class.java, "http://xXx.com")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("http://xXx.com", result.body.canonicalUrl)
        assertEquals(0L, result.body.count)
    }
}
