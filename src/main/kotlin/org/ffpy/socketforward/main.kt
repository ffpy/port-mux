package org.ffpy.socketforward

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.ffpy.socketforward.util.AddressUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path


val log: Logger = LoggerFactory.getLogger("org.ffpy.socketforward.MainKt")

fun main(vararg args: String) {
    try {
        startServe(*args)
    } catch (e: Exception) {
        log.error(e.message, e)
    }
}

@Throws(Exception::class)
private fun startServe(vararg args: String) {
    val param = getCommandParam(args)
    val config = getConfig(Path.of(param.config))

    val boosGroup = NioEventLoopGroup(1)
    val workerGroup = NioEventLoopGroup()
    try {
        val bootstrap = ServerBootstrap()
        bootstrap.group(boosGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .option(ChannelOption.SO_BACKLOG, 128)
//            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(ServerHandler(config))
                }
            })

        val future = bootstrap.bind(AddressUtils.parseAddress(config.listen)).sync()

        log.info("启动转发服务在${config.listen}")

        ClientManager.init(config)

        future.channel().closeFuture().sync()
    } finally {
        boosGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
        ClientManager.shutdown()
    }
}