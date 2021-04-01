package com.wutsi.like.dto

public data class GetStatsResponse(
    public val canonicalUrl: String = "",
    public val count: Long = 0,
    public val liked: Boolean
)
