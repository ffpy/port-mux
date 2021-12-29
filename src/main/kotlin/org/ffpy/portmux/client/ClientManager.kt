package org.ffpy.portmux.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.SocketAddress

/**
 * 客户端管理器
 */
object ClientManager {

    /**
     * 连接客户端
     *
     * @param serverChannel 服务端的Channel
     * @param address 客户端地址
     * @param connectTimeout 连接超时时间（毫秒）
     */
    fun connect(serverChannel: Channel, address: SocketAddress, connectTimeout: Int) = Bootstrap()
        .channel(NioSocketChannel::class.java)
        .group(serverChannel.eventLoop())
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .handler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                ch.pipeline().addLast(ClientHandler(serverChannel))
            }
        })
        .connect(address)
}