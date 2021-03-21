package com.wutsi.like.config

import com.wutsi.spring.memcached.MemcachedCache
import com.wutsi.spring.memcached.MemcachedClientBuilder
import com.wutsi.spring.memcached.MemcachedHealthIndicator
import net.rubyeye.xmemcached.MemcachedClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(name = ["memcached.enabled"], havingValue = "true")
class MemcachedConfiguration(
    @Value("\${memcached.username}") private val username: String,
    @Value("\${memcached.password}") private val password: String,
    @Value("\${memcached.servers}") private val servers: String,
    @Value("\${memcached.ttl}") private val ttl: Int
) {
    @Bean
    fun memcachedClient(): MemcachedClient =
        MemcachedClientBuilder()
            .withServers(servers)
            .withPassword(password)
            .withUsername(username)
            .build()

    @Bean
    fun memcachedHealthIndicator(): HealthIndicator =
        MemcachedHealthIndicator(memcachedClient())

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            listOf(
                MemcachedCache("default", ttl, memcachedClient())
            )
        )
        return cacheManager
    }
}
