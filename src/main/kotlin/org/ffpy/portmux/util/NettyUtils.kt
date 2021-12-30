package org.ffpy.portmux.util

import io.netty.channel.Channel
import io.netty.channel.EventLoopGroup
import io.netty.channel.ServerChannel
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.ffpy.portmux.commandparam.CommandParamManager

object NettyUtils {

    fun createEventLoopGroup(nThreads: Int): EventLoopGroup {
        return if (CommandParamManager.param.epoll) EpollEventLoopGroup(nThreads) else NioEventLoopGroup(nThreads)
    }

    fun getServerSocketChannelClass(): Class<out ServerChannel> {
        return if (CommandParamManager.param.epoll) EpollServerSocketChannel::class.java else NioServerSocketChannel::class.java
    }

    fun getSocketChannelClass(): Class<out Channel> {
        return if (CommandParamManager.param.epoll) EpollSocketChannel::class.java else NioSocketChannel::class.java
    }

    fun getMode() = if (CommandParamManager.param.epoll) "epoll" else "nio"
}