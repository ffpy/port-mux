package org.ffpy.socketforward.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.ffpy.socketforward.config.Configs
import java.net.SocketAddress

object ClientManager {

    private var clientBootstrap: Bootstrap? = null

    private var clientGroup: EventLoopGroup? = null

    fun init() {
        val b = Bootstrap()
        val group = NioEventLoopGroup()

        b.group(group)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Configs.config.connectTimeout)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(ClientHandler())
                }
            })

        clientBootstrap = b
        clientGroup = group
    }

    fun connect(address: SocketAddress): ChannelFuture {
        val b = clientBootstrap ?: throw IllegalStateException("还没有初始化")
        return b.connect(address)
    }

    fun shutdown() {
        clientBootstrap = null
        clientGroup?.shutdownGracefully()
        clientGroup = null
    }
}