package com.wutsi.like.model

import kotlin.collections.List

public data class SearchLikeResponse(
    public val likes: List<Like> = emptyList()
)
