package com.wutsi.like.service

import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Service
public class UrlNormilizer {
    fun normalize(url: String): String =
        url.toLowerCase()

    fun hash(url: String): String {
        val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(
            normalize(url).toByteArray(StandardCharsets.UTF_8)
        )
        return bytesToHex(hash)
    }

    private fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuilder(2 * hash.size)
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString().toLowerCase()
    }
}
