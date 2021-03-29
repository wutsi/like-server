package com.wutsi.like.event

enum class EventType(
    val urn: String
) {
    INVALID("urn:wutsi:like:invalid"),
    LIKED("urn:wutsi:like:liked"),
    SUBMITTED("urn:wutsi:like:submitted"),

    LEGACY_LIKED("urn:wutsi:like:legacy-liked"),
    LEGACY_UNLIKED("urn:wutsi:like:legacy-unliked"),
}
