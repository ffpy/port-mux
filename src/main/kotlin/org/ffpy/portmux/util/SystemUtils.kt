package org.ffpy.portmux.util

object SystemUtils {

    /**  当前系统是不是Linux */
    val isLinux by lazy {
        System.getProperty("os.name").lowercase().contains("linux");
    }
}