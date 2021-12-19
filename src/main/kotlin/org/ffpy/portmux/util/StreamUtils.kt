package org.ffpy.portmux.util

import java.time.Duration
import java.time.Instant

object StreamUtils {

    /**
     * 过滤掉距离上一个元素时间太短的元素
     *
     * @param time 距离上一个元素的最短时间（毫秒），小于这个时间的元素会被过滤掉
     */
    fun <T> filterByShortTime(time: Int): (T) -> Boolean {
        var lastTime = Instant.now().minusMillis(time.toLong())
        return {
            val now = Instant.now()
            val between = Duration.between(lastTime, now).toMillis()
            lastTime = now
            between >= time
        }
    }
}