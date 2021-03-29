package com.wutsi.like.dao

import com.wutsi.like.domain.EventEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : CrudRepository<EventEntity, Long> {
    fun findByUrlHash(urlHash: String): List<EventEntity>
    fun findByUrlHashAndUserId(urlHash: String, userId: Long, pagination: Pageable): List<EventEntity>
    fun findByUrlHashAndDeviceUUID(urlHash: String, deviceUUID: String, pagination: Pageable): List<EventEntity>
}
