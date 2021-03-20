package com.wutsi.like

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.transaction.annotation.EnableTransactionManagement
import kotlin.String

@SpringBootApplication
@EnableTransactionManagement
public class Application

fun main(args: Array<String>) {
    org.springframework.boot.runApplication<Application>(*args)
}
