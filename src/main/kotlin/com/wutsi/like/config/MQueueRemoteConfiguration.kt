package com.wutsi.like.config

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.wutsi.stream.Event
import com.wutsi.stream.EventHandler
import com.wutsi.stream.EventStream
import com.wutsi.stream.rabbitmq.RabbitMQEventStream
import com.wutsi.stream.rabbitmq.RabbitMQHealthIndicator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
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
    @Bean
    fun connectionFactory(): ConnectionFactory {
        val factory = ConnectionFactory()
        factory.setUri(url)
        return factory
    }

    @Bean(destroyMethod = "shutdown")
    fun executorService(): ExecutorService =
        Executors.newFixedThreadPool(threadPoolSize)

    @Bean(destroyMethod = "close")
    fun channel(): Channel {
        val factory = connectionFactory()
        val connection = factory.newConnection(executorService())
        return connection.createChannel()
    }

    @Bean(destroyMethod = "close")
    fun stream(): EventStream = RabbitMQEventStream(
        name = "like",
        channel = channel(),
        handler = object : EventHandler {
            override fun onEvent(event: Event) {
                eventPublisher.publishEvent(event)
            }
        }
    )

    @Bean
    fun rabbitMQHealthIndicator(): HealthIndicator =
        RabbitMQHealthIndicator(channel = channel())
}
