package org.ffpy.portmux.config

import org.ffpy.portmux.logger.LogDataType
import org.ffpy.portmux.protocol.Protocols
import org.ffpy.portmux.util.AddressUtils
import org.ffpy.portmux.util.JsonUtils
import java.nio.file.Path
import java.util.*

typealias OnChangedListener = (Config) -> Unit

/**
 * 配置文件管理器
 */
object ConfigManager {

    /** 配置信息对象 */
    val config: Config
        get() = config_ ?: throw IllegalStateException("还没有初始化")

    private var config_: Config? = null

    private val onChangedListeners: MutableList<OnChangedListener> = Collections.synchronizedList(ArrayList())

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
        config_ = check(parseConfig(path))
        onChangedListeners.forEach { it(config) }
    }

    private fun parseConfig(path: Path): Config {
        try {
            return JsonUtils.parse(path, Config::class.java)
        } catch (e: Exception) {
            throw Exception("配置文件解析失败: ${e.message}", e)
        }
    }

    /**
     * 添加配置文件改变监听器
     */
    fun addOnChangedListener(listener: OnChangedListener) {
        onChangedListeners.add(listener)
    }

    /**
     * 检查配置信息是否有效，如果无效则打印错误信息，并且结束程序
     *
     * @param config 要检查的配置信息
     * @return 原样放回config
     */
    private fun check(config: Config): Config {
        if (!AddressUtils.validAddress(config.listen)) {
            throw Exception("listen地址格式不正确: ${config.listen}")
        }
        if (config.threadNum < 1) {
            throw Exception("thread_num不能小于1")
        }
        if (config.logDataType.isNotEmpty()) {
            LogDataType.of(config.logDataType)
        }
        if (config.logDataLen < 1) {
            throw Exception("log_data_len不能小于1")
        }
        if (config.default.isNotEmpty() && !AddressUtils.validAddress(config.default)) {
            throw Exception("default地址格式不正确: ${config.default}")
        }
        if (config.connectTimeout <= 0) {
            throw Exception("connectTimeout不能小于1")
        }
        if (config.readTimeout <= 0) {
            throw Exception("readTimeout不能小于1")
        }
        if (config.readTimeoutAddress.isNotEmpty() && !AddressUtils.validAddress(config.readTimeoutAddress)) {
            throw Exception("read_timeout_address地址格式不正确")
        }

        config.protocols.forEachIndexed { index, protocol -> checkProtocol(index, protocol) }

        return config
    }

    private fun checkProtocol(index: Int, protocol: ProtocolConfig) {
        if (protocol.name.isEmpty()) {
            throw Exception("protocol[${index}].name不能为空")
        }
        if (protocol.type.isEmpty()) {
            throw Exception("protocol[${index}].type不能为空")
        }
        val type = Protocols.values().asSequence()
            .filter { it.type == protocol.type }
            .firstOrNull() ?: throw Exception("未知的protocol[${index}].type: ${protocol.type}")
        type.check(protocol, index)

        if (!AddressUtils.validAddress(protocol.addr)) {
            throw Exception("protocol[${index}].addr地址格式不正确: ${protocol.addr}")
        }

        if (protocol.patterns.isEmpty()) {
            throw Exception("protocol[${index}].patterns不能为空")
        }
    }
}