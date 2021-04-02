package com.wutsi.like.`delegate`

import com.wutsi.like.dao.EventRepository
import com.wutsi.like.dto.GetStatsResponse
import com.wutsi.like.entity.EventEntity
import com.wutsi.like.event.EventType.LIKED
import com.wutsi.like.service.UrlNormalizer
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import java.util.Optional

@Service
public class StatsDelegate(
    private val dao: EventRepository,
    private val urlNormalizer: UrlNormalizer,
    private val cache: Cache
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StatsDelegate::class.java)
    }

    fun invoke(canonicalUrl: String, userId: Long? = null, deviceUuid: String? = null): GetStatsResponse {
        val urlHash = urlNormalizer.hash(canonicalUrl)

        return fromCache(urlHash, cache)
            .orElseGet {
                val result = fromDb(urlHash, canonicalUrl, userId, deviceUuid)
                cache.put(urlHash, result)
                result
            }
    }

    fun fromCache(urlHash: String, cache: Cache): Optional<GetStatsResponse> {
        try {
            return Optional.ofNullable(
                cache.get(urlHash, GetStatsResponse::class.java)
            )
        } catch (ex: Exception) {
            LOGGER.error("Unable to resolved data from cache", ex)
            return Optional.empty()
        }
    }

    fun fromDb(urlHash: String, canonicalUrl: String, userId: Long? = null, deviceUuid: String? = null): GetStatsResponse {
        val events = dao.findByUrlHashAndType(urlHash, LIKED)
            .groupBy { it.generateUserOrDeviceKey() }

        val count = events.map { likeValue(it.value) }
            .sum()
            .toLong()

        var keys = events.filter { likeValue(it.value) == 1 }
            .map { it.key }

        return GetStatsResponse(
            canonicalUrl = canonicalUrl,
            count = count,
            liked = keys.contains(userId?.toString()) || keys.contains(deviceUuid)
        )
    }

    private fun likeValue(events: List<EventEntity>): Int =
        events.size % 2
}
