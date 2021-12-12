package org.ffpy.portmux.util

import io.netty.buffer.ByteBuf

/**
 * [ByteBuf]工具类
 */
object ByteBufUtils {

    /**
     * 读取[ByteBuf]的所有可读数据，转为[ByteArray]
     *
     * @param buf [ByteBuf]
     * @return 对应的[ByteArray]
     */
    fun getBytes(buf: ByteBuf): ByteArray = getBytes(buf, buf.readableBytes())

    /**
     * 读取[ByteBuf]的指定大小的数据，转为[ByteArray]
     *
     * @param buf [ByteBuf]
     * @param size 读取的大小，应注意不能超过buf的最大可读大小，否则会抛异常
     * @return 对应的[ByteArray]
     */
    fun getBytes(buf: ByteBuf, size: Int): ByteArray {
        val bytes = ByteArray(size)
        buf.getBytes(0, bytes, 0, size)
        return bytes
    }
}