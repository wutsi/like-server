package com.wutsi.like.endpoint

import com.wutsi.like.dao.LikeRepository
import com.wutsi.like.model.CreateLikeRequest
import com.wutsi.like.model.CreateLikeResponse
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
import kotlin.test.fail

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
internal class CreateControllerTest {
    @LocalServerPort
    private val port = 0

    @Autowired
    lateinit var likeDao: LikeRepository

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

        val like = likeDao.findById(response.body.likeId).get()
        assertEquals("https://www.google.ca", like.canonicalUrl)
        assertEquals("42a439d6731734d571b61d0b097d72ba507ec06d4a9092ef0d4627979d9eb3df", like.urlHash)
        assertEquals(request.deviceUUID, like.deviceUUID)
        assertEquals(request.userId, like.userId)
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
