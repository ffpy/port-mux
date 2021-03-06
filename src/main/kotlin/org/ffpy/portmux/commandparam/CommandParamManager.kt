package org.ffpy.portmux.commandparam

import com.beust.jcommander.JCommander
import kotlin.system.exitProcess

/**
 * 命令行参数解析管理器
 */
object CommandParamManager {
    /** 命令行参数对象 */
    val param: Param
        get() = param_ ?: throw IllegalStateException("Not initialized yet")

    private var param_: Param? = null

    /**
     * 初始化
     * @param args 命令行参数
     */
    fun init(args: Array<out String>) {
        val param = Param()
        val jCommander = JCommander.newBuilder()
            .addObject(param)
            .build()
        jCommander.parse(*args)

        if (param.help) {
            jCommander.usage()
            exitProcess(0)
        }

        param_ = param
    }
}