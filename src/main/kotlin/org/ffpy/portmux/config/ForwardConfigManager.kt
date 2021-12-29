package org.ffpy.portmux.config

object ForwardConfigManager {

    var forwardConfig: ForwardConfig = ForwardConfig(ConfigManager.config)

    fun init() {
        ConfigManager.addOnChangedListener {
            forwardConfig = ForwardConfig(it)
        }
    }
}