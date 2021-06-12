package org.ffpy.socketforward.commandparam

import com.beust.jcommander.JCommander

object CommandParams {
    val param: Param
        get() = param_ ?: throw IllegalStateException("还没有初始化")

    private var param_: Param? = null

    fun init(args: Array<out String>) {
        val param = Param()
        JCommander.newBuilder()
            .addObject(param)
            .build()
            .parse(*args)
        param_ = param
    }
}