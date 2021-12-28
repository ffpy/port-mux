package org.ffpy.portmux.config

object ForwardConfigs {

    var forwardConfig = ForwardConfig(Configs.config)

    fun refreshForwardConfig() {
        forwardConfig = ForwardConfig(Configs.config)
    }
}