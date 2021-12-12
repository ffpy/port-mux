package org.ffpy.portmux.config

import org.ffpy.portmux.protocol.Protocols
import org.ffpy.portmux.util.AddressUtils
import org.ffpy.portmux.util.JsonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.system.exitProcess

/**
 * 配置文件管理器
 */
object Configs {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    /** 配置信息对象 */
    val config: Config
        get() = config_ ?: throw IllegalStateException("还没有初始化")

    private var config_: Config? = null

    /**
     * 加载配置文件
     *
     * @param path 文件路径
     */
    @Throws(Exception::class)
    fun init(path: Path) {
        if (!path.toFile().exists()) {
            throw Exception("找不到配置文件")
        }
        config_ = check(JsonUtils.parse(path, Config::class.java))
    }

    /**
     * 检查配置信息是否有效，如果无效则打印错误信息，并且结束程序
     *
     * @param config 要检查的配置信息
     * @return 原样放回config
     */
    private fun check(config: Config): Config {
        if (!AddressUtils.validAddress(config.listen)) {
            log.error("listen地址格式不正确: {}", config.listen)
            exitProcess(-1)
        }
        if (config.default.isNotEmpty() && !AddressUtils.validAddress(config.default)) {
            log.error("default地址格式不正确: {}", config.default)
            exitProcess(-1)
        }
        if (config.connectTimeout <= 0) {
            log.error("connectTimeout不能小于1")
            exitProcess(-1)
        }
        if (config.readTimeout <= 0) {
            log.error("readTimeout不能小于1")
            exitProcess(-1)
        }
        if (config.readTimeoutAddress.isNotEmpty() && !AddressUtils.validAddress(config.readTimeoutAddress)) {
            log.error("read_timeout_address地址格式不正确")
            exitProcess(-1)
        }

        config.protocols.forEachIndexed { index, protocol -> checkProtocol(index, protocol) }

        return config
    }

    private fun checkProtocol(index: Int, protocol: ProtocolConfig) {
        if (protocol.name.isEmpty()) {
            log.error("protocol[${index}].name不能为空")
            exitProcess(-1)
        }
        if (protocol.type.isEmpty()) {
            log.error("protocol[${index}].type不能为空")
            exitProcess(-1)
        }
        val type = Protocols.values().asSequence()
            .filter { it.type == protocol.type }
            .firstOrNull()
        if (type == null) {
            log.error("未知的protocol[${index}].type: {}", protocol.type)
            exitProcess(-1)
        }

        if (!AddressUtils.validAddress(protocol.addr)) {
            log.error("protocol[${index}].addr地址格式不正确: {}", protocol.addr)
            exitProcess(-1)
        }

        if (type == Protocols.REGEX) {
            if (protocol.minLen <= 0) {
                log.error("protocol[${index}].min_len不能小于1")
                exitProcess(-1)
            }
            if (protocol.maxLen <= 0) {
                log.error("protocol[${index}].max_len不能小于1")
                exitProcess(-1)
            }
        }

        if (protocol.patterns.isEmpty()) {
            log.error("protocol[${index}].patterns不能为空")
            exitProcess(-1)
        }
    }
}