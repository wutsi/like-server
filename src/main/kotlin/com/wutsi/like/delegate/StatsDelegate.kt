package com.wutsi.like.`delegate`

import com.wutsi.like.model.GetStatsResponse
import org.springframework.stereotype.Service
import kotlin.Long
import kotlin.String

@Service
public class StatsDelegate {
    public fun invoke(
        canonicalUrl: String,
        deviceId: String,
        userId: Long
    ): GetStatsResponse {
        TODO()
    }
}
