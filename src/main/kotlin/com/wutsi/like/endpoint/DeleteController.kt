package com.wutsi.like.endpoint

import com.wutsi.like.`delegate`.DeleteDelegate
import kotlin.Long
import kotlin.Unit
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class DeleteController(
  private val `delegate`: DeleteDelegate
) {
  @DeleteMapping("/v1/likes/{id}")
  public fun invoke(@PathVariable(name="id") id: Long): Unit {
    delegate.invoke(id)
  }
}
