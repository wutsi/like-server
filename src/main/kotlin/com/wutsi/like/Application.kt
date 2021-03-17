package com.wutsi.like

import kotlin.String
import kotlin.Unit
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
public class Application

public fun main(vararg args: String): Unit {
  org.springframework.boot.runApplication<Application>(*args)
}
