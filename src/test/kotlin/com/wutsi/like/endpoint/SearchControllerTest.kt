package com.wutsi.like.endpoint

import com.wutsi.like.dto.SearchLikeResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchController.sql"])
internal class SearchControllerTest {
    @LocalServerPort
    private val port = 0

    lateinit var rest: RestTemplate

    lateinit var url: String

    @BeforeEach
    fun setUp() {
        rest = RestTemplate()
        url = "http://127.0.0.1:$port/v1/likes?canonical_url={url}"
    }

    @Test
    fun `search by userId`() {
        val result = rest.getForEntity("$url&user_id={user_id}", SearchLikeResponse::class.java, "https://www.google.com", 1)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(1, result.body.likes.size)

        val likes = result.body.likes.sortedBy { it.id }
        assertEquals("https://www.google.com", likes[0].canonicalUrl)
        assertEquals("4fad8daa-d502-41b7-ac4a-2c7d97fa75ff", likes[0].deviceUUID)
        assertEquals(1L, likes[0].userId)
    }

    @Test
    fun `search by userId - no likes`() {
        val result = rest.getForEntity("$url&user_id={user_id}", SearchLikeResponse::class.java, "https://www.google.com", 999)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(0, result.body.likes.size)
    }

    @Test
    fun `search deviceUUID`() {
        val result = rest.getForEntity(
            "$url&device_uuid={device_uuid}",
            SearchLikeResponse::class.java,
            "https://www.google.com",
            "4fad8daa-d502-41b7-ac4a-2c7d97fa7543"
        )

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(1, result.body.likes.size)

        assertEquals("https://www.google.com", result.body.likes[0].canonicalUrl)
        assertEquals("4fad8daa-d502-41b7-ac4a-2c7d97fa7543", result.body.likes[0].deviceUUID)
        assertNull(result.body.likes[0].userId)
    }

    @Test
    fun `search by url`() {
        val result = rest.getForEntity(url, SearchLikeResponse::class.java, "https://www.google.com")

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(0, result.body.likes.size)
    }
}
