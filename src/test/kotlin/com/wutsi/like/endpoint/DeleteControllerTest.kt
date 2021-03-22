package com.wutsi.like.endpoint

import com.wutsi.like.dao.LikeRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertFalse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DeleteController.sql"])
internal class DeleteControllerTest {
    @LocalServerPort
    private val port = 0

    @Autowired
    lateinit var likeDao: LikeRepository

    lateinit var rest: RestTemplate

    @BeforeEach
    fun setUp() {
        rest = RestTemplate()
    }

    @Test
    fun `unlike`() {
        rest.delete("http://127.0.0.1:$port/v1/likes/100")

        val like = likeDao.findById(100)
        assertFalse(like.isPresent)
    }

    @Test
    fun `unlike - Invalid like`() {
        rest.delete("http://127.0.0.1:$port/v1/likes/9999")
    }
}
