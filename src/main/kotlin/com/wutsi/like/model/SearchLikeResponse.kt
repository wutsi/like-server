package com.wutsi.like.model

import kotlin.Int
import kotlin.collections.List

public data class SearchLikeResponse(
    public val offset: Int = 0,
    public val nextOffset: Int? = null,
    public val likes: List<Like> = emptyList()
)
