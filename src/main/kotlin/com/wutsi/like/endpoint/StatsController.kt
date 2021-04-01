package com.wutsi.like.endpoint

import com.wutsi.like.`delegate`.StatsDelegate
import com.wutsi.like.dto.GetStatsResponse
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
        @RequestParam(name = "canonical_url", required = false) canonicalUrl: String,
        @RequestParam(name = "user_id", required = false) userId: Long? = null,
        @RequestParam(name = "device_uuid", required = false) deviceUuid: String? = null
    ): GetStatsResponse = delegate.invoke(canonicalUrl, userId, deviceUuid)
}
