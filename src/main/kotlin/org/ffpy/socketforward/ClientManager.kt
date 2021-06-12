package org.ffpy.socketforward

import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.slf4j.LoggerFactory
import java.net.SocketAddress

object ClientManager {

    private var clientBootstrap: Bootstrap? = null

    private var clientGroup: EventLoopGroup? = null

    fun init(config: Config) {
        val b = Bootstrap()
        val group = NioEventLoopGroup()

        b.group(group)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.connectTimeout)
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

class ClientHandler : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = LoggerFactory.getLogger(ClientHandler::class.java)
    }

    private lateinit var serverChannel: Channel

    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
        log.info("${ctx.channel().remoteAddress()}连接成功")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        super.channelInactive(ctx)
        log.info("${ctx.channel().remoteAddress()}连接断开")
        serverChannel.close()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        serverChannel.writeAndFlush(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("${serverChannel.remoteAddress()}发生错误: ${cause.message}", cause)
        ctx.close()
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any?) {
        if (evt is Channel) {
            serverChannel = evt
        }
    }
}