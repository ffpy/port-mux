package org.ffpy.socketforward.server

import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.util.HashedWheelTimer
import io.netty.util.Timeout
import org.ffpy.socketforward.client.ClientManager
import org.ffpy.socketforward.config.Configs.config
import org.ffpy.socketforward.protocol.Protocols
import org.ffpy.socketforward.util.AddressUtils
import org.ffpy.socketforward.util.DebugUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * 转发处理器
 */
class ServerHandler : ChannelInboundHandlerAdapter() {
    companion object {
        private val log = LoggerFactory.getLogger(ServerHandler::class.java)
        private val timer = HashedWheelTimer(500, TimeUnit.MILLISECONDS, 64)
    }

    /** 转发目标连接 */
    private var clientChannel: Channel? = null

    /** 转发协议列表 */
    private val protocols = config.protocols.map { Protocols.create(it) }

    /** 首次读取超时检查定时器 */
    private var firstReadTimeout: Timeout? = null

    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)

        log.info("${ctx.channel().remoteAddress()}新连接")

        // 连接后一段时间内没有数据则直接转发到默认地址
        firstReadTimeout = timer.newTimeout({
            connect(config.default, null, ctx.channel())
        }, config.readTimeout.toLong(), TimeUnit.MILLISECONDS)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        cancelFirstTimeout()

        DebugUtils.logData(log, msg as ByteBuf, ctx)

        val c = clientChannel
        if (c == null) {
            val address = matchProtocol(msg, ctx)
            connect(address, msg, ctx.channel())
        } else {
            c.writeAndFlush(msg)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.info("${ctx.channel().remoteAddress()}连接断开")
        clientChannel?.close()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("${ctx.channel().remoteAddress()}发生错误", cause)
        ctx.close()
    }

    /**
     * 连接转发地址
     * @param address 转发地址
     * @param msg 首次读取数据
     * @param serverChannel 源连接
     */
    private fun connect(address: String, msg: Any?, serverChannel: Channel) {
        if (address.isEmpty()) {
            log.info("找不到转发地址")
            serverChannel.close()
            return
        }

        log.info("${serverChannel.remoteAddress()} => $address")

        ClientManager.connect(AddressUtils.parseAddress(address)).addListener(object : ChannelFutureListener {
            override fun operationComplete(future: ChannelFuture) {
                if (future.isSuccess) {

                    val channel = future.channel()
                    clientChannel = channel
                    channel.pipeline().fireUserEventTriggered(serverChannel)
                    if (msg != null) {
                        channel.writeAndFlush(msg)
                    }
                } else {
                    log.error("连接${address}失败")
                    serverChannel.close()
                }
            }
        })
    }

    /**
     * 关闭首次读取超时检查定时器
     */
    private fun cancelFirstTimeout() {
        firstReadTimeout?.let {
            it.cancel()
            firstReadTimeout = null
        }
    }

    /**
     * 匹配转发协议
     */
    private fun matchProtocol(buf: ByteBuf, ctx: ChannelHandlerContext): String {
        for (protocol in protocols) {
            if (protocol.match(buf)) {
                log.info("{}匹配协议: {}", ctx.channel().remoteAddress(), protocol.config.name)
                return protocol.config.addr
            }
        }
        log.info("{}匹配失败，转发到默认地址", ctx.channel().remoteAddress())
        return config.default
    }
}