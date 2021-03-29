package com.wutsi.like.domain

import com.wutsi.like.event.EventType
import com.wutsi.like.event.EventType.INVALID
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_EVENT")
data class EventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated
    val type: EventType = INVALID,

    val timestamp: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "canonical_url")
    val canonicalUrl: String = "",

    @Column(name = "url_hash")
    val urlHash: String = "",

    @Column(name = "device_uuid")
    val deviceUUID: String? = null,

    @Column(name = "user_id")
    val userId: Long? = null
) {
    fun generateUserOrDeviceKey(): String {
        val key = this.userId ?: this.deviceUUID
        return key.toString() ?: "-"
    }
}
