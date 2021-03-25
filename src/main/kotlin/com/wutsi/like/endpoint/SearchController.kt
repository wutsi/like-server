package com.wutsi.like.endpoint

import com.wutsi.like.`delegate`.SearchDelegate
import com.wutsi.like.model.SearchLikeResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Int
import kotlin.Long
import kotlin.String

@RestController
public class SearchController(
    private val `delegate`: SearchDelegate
) {
    @GetMapping("/v1/likes")
    public fun invoke(
        @RequestParam(name = "canonical_url", required = false) canonicalUrl: String,
        @RequestParam(name = "user_id", required = false) userId: Long? = null,
        @RequestParam(name = "device_uuid", required = false) deviceUuid: String? = null,
        @RequestParam(name = "limit", required = false, defaultValue = "20") limit: Int = 20,
        @RequestParam(name = "offset", required = false, defaultValue = "0") offset: Int = 0
    ): SearchLikeResponse = delegate.invoke(canonicalUrl, userId, deviceUuid, limit, offset)
}
