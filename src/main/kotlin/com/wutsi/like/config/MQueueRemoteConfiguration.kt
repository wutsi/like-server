package com.wutsi.like.config

import com.wutsi.stream.Event
import com.wutsi.stream.EventHandler
import com.wutsi.stream.EventStream
import com.wutsi.stream.rabbitmq.RabbitMQEventStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI
import java.util.concurrent.Executors

@Configuration
@ConditionalOnProperty(
    value = ["rabbitmq.enabled"],
    havingValue = "true"
)
class MQueueRemoteConfiguration(
    @Autowired private val eventPublisher: ApplicationEventPublisher,
    @Value(value = "\${rabbitmq.url}") private val url: String,
    @Value("\${rabbitmq.thread-pool-size}") val threadPoolSize: Int
) {
    @Bean(destroyMethod = "close")
    fun stream(): EventStream = RabbitMQEventStream(
        name = "like",
        uri = URI(url),
        executorService = Executors.newFixedThreadPool(threadPoolSize),
        handler = object : EventHandler {
            override fun onEvent(event: Event) {
                eventPublisher.publishEvent(event)
            }
        }
    )
}
