package com.wutsi.like.event

import com.wutsi.blog.stream.EventType as LegacyEventType

enum class EventType(
    val urn: String
) {
    INVALID("urn:wutsi:like:2.0:invalid"),
    LIKED("urn:wutsi:like:2.0:liked"),
    SUBMITTED("urn:wutsi:like:2.0:submitted"),

    LEGACY_LIKED(LegacyEventType.LIKED.urn),
    LEGACY_UNLIKED(LegacyEventType.UNLIKED.urn),
}
