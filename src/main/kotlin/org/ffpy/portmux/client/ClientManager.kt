package org.ffpy.portmux.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.ffpy.portmux.config.Configs
import java.net.SocketAddress

/**
 * 目标连接管理器
 */
object ClientManager {

    private var clientBootstrap: Bootstrap? = null

    private var clientGroup: EventLoopGroup? = null

    /**
     * 初始化
     */
    fun init() {
        val b = Bootstrap()
        val group = NioEventLoopGroup(2)

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

    /**
     * 连接到指定地址
     * @param address 连接地址
     * @return 对应的[ChannelFuture]
     */
    fun connect(address: SocketAddress): ChannelFuture {
        val b = clientBootstrap ?: throw IllegalStateException("还没有初始化")
        return b.connect(address)
    }

    /**
     * 停止服务
     */
    fun shutdown() {
        clientBootstrap = null
        clientGroup?.shutdownGracefully()
        clientGroup = null
    }
}