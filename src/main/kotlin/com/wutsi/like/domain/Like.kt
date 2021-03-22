package com.wutsi.like.domain

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_LIKE")
data class Like(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "canonical_url")
    val canonicalUrl: String,

    @Column(name = "device_id")
    val deviceId: String?,

    @Column(name = "user_id")
    val userId: Long?,

    @Column(name = "like_date_time")
    val likeDateTime: Date = Date()
)
