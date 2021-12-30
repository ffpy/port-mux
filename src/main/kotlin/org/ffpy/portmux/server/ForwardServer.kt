package org.ffpy.portmux.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.SocketChannel
import org.ffpy.portmux.config.ConfigManager
import org.ffpy.portmux.config.ForwardConfigManager
import org.ffpy.portmux.util.AddressUtils
import org.ffpy.portmux.util.NettyUtils
import org.slf4j.LoggerFactory

/**
 * 转发服务
 */
class ForwardServer {
    companion object {
        private val log = LoggerFactory.getLogger(ForwardServer::class.java)
    }

    /**
     * 启动服务
     */
    fun start() {
        val boosGroup = NettyUtils.createEventLoopGroup(1)
        val workerGroup = NettyUtils.createEventLoopGroup(ConfigManager.config.threadNum)
        try {
            val future = ServerBootstrap()
                .group(boosGroup, workerGroup)
                .channel(NettyUtils.getServerSocketChannelClass())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast("matchHandler", MatchHandler(ForwardConfigManager.forwardConfig))
                    }
                })
                .bind(AddressUtils.parseAddress(ConfigManager.config.listen))
                .sync()

            log.info("当前模式为: " + NettyUtils.getMode())
            log.info("启动转发服务: ${ConfigManager.config.listen}")

            future.channel().closeFuture().sync()
        } finally {
            boosGroup.shutdownGracefully().syncUninterruptibly()
            workerGroup.shutdownGracefully().syncUninterruptibly()
        }
    }
}