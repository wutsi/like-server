package com.wutsi.like.endpoint

import com.wutsi.like.delegate.DeleteDelegate
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
public class DeleteController(
    private val `delegate`: DeleteDelegate
) {
    @DeleteMapping("/v1/likes/{id}")
    public fun invoke(@PathVariable id: Long) {
        delegate.invoke(id)
    }
}
