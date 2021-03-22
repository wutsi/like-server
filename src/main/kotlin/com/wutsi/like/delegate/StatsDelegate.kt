package com.wutsi.like.`delegate`

import com.wutsi.like.model.GetStatsResponse
import com.wutsi.like.service.LikeService
import org.springframework.stereotype.Service

@Service
public class StatsDelegate(private val service: LikeService) {
    public fun invoke(canonicalUrl: String): GetStatsResponse {
        val counter = service.count(canonicalUrl)
        return GetStatsResponse(
            canonicalUrl = canonicalUrl,
            count = counter.count
        )
    }
}
