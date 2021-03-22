package com.wutsi.like.dao

import com.wutsi.like.domain.Like
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : CrudRepository<Like, Long>
