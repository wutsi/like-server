package com.wutsi.like.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
public class CacheConfiguration(
    @Autowired private val cacheManager: CacheManager
) {
    @Bean
    public fun getCache(): Cache =
        cacheManager.getCache("default")
}
