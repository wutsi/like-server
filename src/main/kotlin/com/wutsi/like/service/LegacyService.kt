package com.wutsi.like.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
public class LegacyService(
    @Value("\${wutsi.website.story.read-url-prefix}") private val readUrlPrefix: String
) {
    fun storyUrl(storyId: Long): String =
        if (readUrlPrefix.endsWith("/"))
            "$readUrlPrefix$storyId"
        else
            "$readUrlPrefix/$storyId"
}
