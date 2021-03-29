package com.wutsi.like.event

enum class EventType(
    val urn: String
) {
    INVALID("urn:wutsi:like:invalid"),
    LIKED("urn:wutsi:like:liked"),
}
