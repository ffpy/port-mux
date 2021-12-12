package org.ffpy.portmux.util

import java.util.*

/**
 * 数组工具类
 */
object ArraysUtils {

    fun startWith(arr: ByteArray, prefix: ByteArray): Boolean {
        return arr.size >= prefix.size && Arrays.equals(arr, 0, prefix.size, prefix, 0, prefix.size)
    }
}