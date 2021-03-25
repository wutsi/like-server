package com.wutsi.like.dao

import com.wutsi.like.domain.LikeEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : CrudRepository<LikeEntity, Long> {
    fun countByUrlHash(urlHash: String): Long
    fun findByUrlHash(urlHash: String, pagination: Pageable): List<LikeEntity>
    fun findByUrlHashAndUserId(urlHash: String, userId: Long, pagination: Pageable): List<LikeEntity>
    fun findByUrlHashAndDeviceUUID(urlHash: String, deviceUUID: String, pagination: Pageable): List<LikeEntity>
}
