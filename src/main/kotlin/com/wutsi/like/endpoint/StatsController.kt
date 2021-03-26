package com.wutsi.like.endpoint

import com.wutsi.like.`delegate`.StatsDelegate
import com.wutsi.like.model.GetStatsResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.String

@RestController
public class StatsController(
    private val `delegate`: StatsDelegate
) {
    @GetMapping("/v1/likes/stats")
    public fun invoke(@RequestParam(name = "canonical_url", required = false) canonicalUrl: String):
        GetStatsResponse = delegate.invoke(canonicalUrl)
}
