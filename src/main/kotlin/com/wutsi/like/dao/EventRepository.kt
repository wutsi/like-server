package com.wutsi.like.dao

import com.wutsi.like.entity.EventEntity
import com.wutsi.like.event.EventType
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : CrudRepository<EventEntity, Long> {
    fun findByUrlHashAndType(urlHash: String, type: EventType): List<EventEntity>
    fun findByUrlHashAndUserIdAndType(urlHash: String, userId: Long, type: EventType, pagination: Pageable): List<EventEntity>
    fun findByUrlHashAndDeviceUUIDAndType(urlHash: String, deviceUUID: String, type: EventType, pagination: Pageable): List<EventEntity>
}
