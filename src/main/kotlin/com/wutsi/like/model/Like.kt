package com.wutsi.like.model

import java.time.OffsetDateTime
import kotlin.Long
import kotlin.String

public data class Like(
    public val id: Long = 0,
    public val canonicalUrl: String = "",
    public val userId: Long? = null,
    public val deviceUUID: String? = null,
    public val likeDateTime: OffsetDateTime = OffsetDateTime.now()
)
