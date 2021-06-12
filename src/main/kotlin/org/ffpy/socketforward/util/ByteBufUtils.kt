package org.ffpy.socketforward.util

import io.netty.buffer.ByteBuf

/**
 * [ByteBuf]工具类
 */
object ByteBufUtils {

    fun getBytes(buf: ByteBuf): ByteArray = getBytes(buf, buf.readableBytes())

    fun getBytes(buf: ByteBuf, size: Int): ByteArray {
        val bytes = ByteArray(size)
        buf.getBytes(0, bytes, 0, size)
        return bytes
    }
}