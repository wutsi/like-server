package com.wutsi.like.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UrlNormilizerTest {
    private val normalizer: UrlNormilizer = UrlNormilizer()

    @Test
    fun normalize() {
        assertEquals("https://www.google.ca", normalizer.normalize("https://WWW.google.ca"))
        assertEquals("https://www.google.ca", normalizer.normalize("https://www.google.ca/"))
        assertEquals("https://www.google.ca", normalizer.normalize("https://www.google.ca?"))
    }

    @Test
    fun hash() {
        val hash = normalizer.hash("https://WWW.google.ca")
        assertEquals(64, hash.length)
        assertEquals("42a439d6731734d571b61d0b097d72ba507ec06d4a9092ef0d4627979d9eb3df", hash)
    }
}
