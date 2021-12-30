package org.ffpy.portmux.config

object ForwardConfigManager {

    lateinit var forwardConfig: ForwardConfig
        private set

    fun init() {
        forwardConfig = ForwardConfig(ConfigManager.config)
        ConfigManager.addOnChangedListener {
            forwardConfig = ForwardConfig(it)
        }
    }
}