package com.wutsi.like.event

import java.time.OffsetDateTime

data class LegacyEventPayload(
    val likeId: Long = -1,
    val storyId: Long = -1,
    val deviceUUID: String? = null,
    val userId: Long? = null,
    val likeDateTime: OffsetDateTime = OffsetDateTime.now()
)
