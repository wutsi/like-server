package com.wutsi.like.endpoint

import com.wutsi.like.`delegate`.CreateDelegate
import com.wutsi.like.dto.CreateLikeRequest
import com.wutsi.like.dto.CreateLikeResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateController(
    private val `delegate`: CreateDelegate
) {
    @PostMapping("/v1/likes")
    public fun invoke(@Valid @RequestBody request: CreateLikeRequest): CreateLikeResponse =
        delegate.invoke(request)
}
