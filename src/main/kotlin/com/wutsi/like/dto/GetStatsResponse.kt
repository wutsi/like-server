package com.wutsi.like.dto

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class GetStatsResponse(
    public val canonicalUrl: String = "",
    public val count: Long = 0,
    public val liked: Boolean
)
