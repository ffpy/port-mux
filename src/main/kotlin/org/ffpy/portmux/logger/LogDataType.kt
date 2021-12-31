package org.ffpy.portmux.logger

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil

/**
 * 打印数据类型
 *
 * @param code 类型码
 * @param action ByteBuf转字符串的函数
 */
enum class LogDataType(
    val code: String,
    private val action: (ByteBuf) -> String
) {
    STRING("string", { it.toString(Charsets.UTF_8) }),

    BYTE("byte", { ByteBufUtil.getBytes(it).contentToString() }),

    HEX("hex", { ByteBufUtil.hexDump(it) }),

    PRETTY_HEX("pretty_hex", { "\n" + ByteBufUtil.prettyHexDump(it) }),
    ;

    companion object {

        /**
         * 类型码转 [LogDataType]
         *
         * @param code 类型码
         * @return 对应的[LogDataType]
         * @throws Exception 如果不支持此类型码
         */
        fun of(code: String): LogDataType {
            for (value in values()) {
                if (value.code == code) return value
            }
            throw Exception("log_data_type not support this type: $code")
        }
    }

    /**
     * 执行转换
     *
     * @param buf 要转换的ByteBuf
     * @return 转换后的字符串
     */
    fun apply(buf: ByteBuf) = action(buf)
}