package com.wutsi.like.config

import com.wutsi.stream.Event
import com.wutsi.stream.EventHandler
import com.wutsi.stream.EventStream
import com.wutsi.stream.file.FileEventStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
@ConditionalOnProperty(
    value = ["rabbitmq.enabled"],
    havingValue = "false"
)
class MQueueLocalConfiguration(
    @Autowired private val eventPublisher: ApplicationEventPublisher
) {
    @Bean(destroyMethod = "close")
    fun stream(): EventStream = FileEventStream(
        name = "like",
        root = File(System.getProperty("home.directory") + File.separator + "like", "mqueue"),
        handler = object : EventHandler {
            override fun onEvent(event: Event) {
                eventPublisher.publishEvent(event)
            }
        }
    )
}
