package org.ffpy.portmux

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import org.ffpy.portmux.commandparam.CommandParams
import org.ffpy.portmux.config.Configs
import org.ffpy.portmux.config.ForwardConfigs
import org.ffpy.portmux.protocol.FastMatcher
import org.ffpy.portmux.protocol.MatchResult
import org.ffpy.portmux.protocol.Matcher
import org.ffpy.portmux.util.AddressUtils
import java.net.InetSocketAddress
import java.nio.file.Path

class MatcherTest {

    private lateinit var buf: ByteBuf

    private val remoteAddress = InetSocketAddress(8888)

    private lateinit var defaultAddress: InetSocketAddress

    fun setup() {
        CommandParams.init(emptyArray())
        val configPath = Path.of(CommandParams.param.config)

        Configs.init(configPath)


        buf = Unpooled.wrappedBuffer("GET 11111".toByteArray(CharsetUtil.UTF_8))

        defaultAddress = AddressUtils.parseAddress(Configs.config.default)
    }

    fun old(): MatchResult {
        return Matcher(ForwardConfigs.forwardConfig.protocols).match(buf, remoteAddress, defaultAddress)
    }

    fun fast(): MatchResult {
        return FastMatcher().match(buf, remoteAddress, defaultAddress)
    }
}
