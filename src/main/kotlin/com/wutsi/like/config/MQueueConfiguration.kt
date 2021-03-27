package com.wutsi.like.config

import com.wutsi.stream.EventStream
import com.wutsi.stream.EventSubscription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MQueueConfiguration(@Autowired private val stream: EventStream) {
    @Bean
    fun legacySubscription() = EventSubscription("wutsi-legacy", stream)
}
