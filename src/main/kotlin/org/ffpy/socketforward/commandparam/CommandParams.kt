package org.ffpy.socketforward.commandparam

import com.beust.jcommander.JCommander

/***
 * 命令行参数解析
 */
object CommandParams {
    /** 命令行参数对象 */
    val param: Param
        get() = param_ ?: throw IllegalStateException("还没有初始化")

    private var param_: Param? = null

    /**
     * 初始化
     * @param args 命令行参数
     */
    fun init(args: Array<out String>) {
        val param = Param()
        JCommander.newBuilder()
            .addObject(param)
            .build()
            .parse(*args)
        param_ = param
    }
}