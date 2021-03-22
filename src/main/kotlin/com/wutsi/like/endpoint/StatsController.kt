package com.wutsi.like.endpoint

import com.wutsi.like.`delegate`.StatsDelegate
import com.wutsi.like.model.GetStatsResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long
import kotlin.String

@RestController
public class StatsController(
    private val `delegate`: StatsDelegate
) {
    @GetMapping("/v1/likes/stats")
    public fun invoke(
        @RequestParam(name = "canonicalUrl", required = false) canonicalUrl: String,
        @RequestParam(name = "deviceId", required = false) deviceId: String,
        @RequestParam(name = "userId", required = false) userId: Long
    ): GetStatsResponse = delegate.invoke(canonicalUrl, deviceId, userId)
}
