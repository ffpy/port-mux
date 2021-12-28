package org.ffpy.portmux.protocol

import java.net.SocketAddress

data class MatchResult(
    val finish: Boolean,
    val address: SocketAddress?
)
