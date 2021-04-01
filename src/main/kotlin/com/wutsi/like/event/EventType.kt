package com.wutsi.like.event

import com.wutsi.blog.stream.EventType as LegacyEventType

enum class EventType(
    val urn: String
) {
    INVALID("urn:event:wutsi:like:invalid"),
    LIKED("urn:event:wutsi:like:liked"),

    LEGACY_LIKED(LegacyEventType.LIKED.urn),
    LEGACY_UNLIKED(LegacyEventType.UNLIKED.urn),
}
