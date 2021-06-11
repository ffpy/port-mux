package org.ffpy.socketforward

data class Config(
    var listen: String = "",
    var default: String = "",
    var timeout: Int = 0,
    var connectTimeout: Int = 0,
    var protocols: List<Protocol> = emptyList(),
)

data class Protocol(
    var name: String = "",
    var service: String = "",
    var addr: String = "",
    var minLen: Int = 0,
    var maxLen: Int = 0,
    var patterns: List<String> = emptyList(),
)