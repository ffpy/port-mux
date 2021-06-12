package org.ffpy.socketforward.config

import org.ffpy.socketforward.util.JsonUtils
import java.nio.file.Path

object Configs {
    val config: Config
        get() = config_ ?: throw IllegalStateException("还没有初始化")

    private var config_: Config? = null

    /**
     * 加载配置文件
     *
     * @param path 文件路径
     * @return 配置对象，null代表找不到配置文件
     */
    @Throws(Exception::class)
    fun init(path: Path) {
        if (!path.toFile().exists()) {
            throw Exception("找不到配置文件")
        }
        // TODO 检查数据
        config_ = JsonUtils.parse(path, Config::class.java)
    }
}