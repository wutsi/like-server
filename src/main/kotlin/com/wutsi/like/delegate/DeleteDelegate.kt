package com.wutsi.like.`delegate`

import com.wutsi.like.service.LikeService
import org.springframework.stereotype.Service

@Service
public class DeleteDelegate(private val service: LikeService) {
    public fun invoke(id: Long) {
        service.delete(id)
    }
}
