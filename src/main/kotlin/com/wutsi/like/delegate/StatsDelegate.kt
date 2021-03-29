package com.wutsi.like.`delegate`

import com.wutsi.like.dao.EventRepository
import com.wutsi.like.domain.EventEntity
import com.wutsi.like.model.GetStatsResponse
import com.wutsi.like.service.UrlNormalizer
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import java.util.Optional

@Service
public class StatsDelegate(
    private val dao: EventRepository,
    private val urlNormalizer: UrlNormalizer,
    private val cacheManager: CacheManager
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StatsDelegate::class.java)
    }

    fun invoke(canonicalUrl: String): GetStatsResponse {
        val urlHash = urlNormalizer.hash(canonicalUrl)
        val cache = cacheManager.getCache("default")

        val count = fromCache(urlHash, cache)
            .orElseGet {
                val result = fromDb(urlHash)
                toCache(urlHash, result, cache)
                result
            }

        return GetStatsResponse(
            canonicalUrl = canonicalUrl,
            count = count
        )
    }

    fun fromCache(urlHash: String, cache: Cache): Optional<Long> {
        try {
            return Optional.ofNullable(
                cache.get(urlHash, Long::class.java)
            )
        } catch (ex: Exception) {
            LOGGER.error("Unable to resolved data from cache", ex)
            return Optional.empty()
        }
    }

    fun fromDb(urlHash: String): Long {
        val events = dao.findByUrlHash(urlHash)
            .groupBy { it.generateUserOrDeviceKey() }

        return events.map { likeValue(it.value) }
            .sum()
            .toLong()
    }

    private fun likeValue(events: List<EventEntity>): Int =
        events.size % 2

    fun toCache(urlHash: String, count: Long, cache: Cache) =
        cache.put(urlHash, count)
}
