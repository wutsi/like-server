package com.wutsi.like.event

import java.time.OffsetDateTime

data class LikeEventPayload(
    val canonicalUrl: String = "",
    val deviceUUID: String? = null,
    val userId: Long? = null,
    val likeDateTime: OffsetDateTime = OffsetDateTime.now()
)
