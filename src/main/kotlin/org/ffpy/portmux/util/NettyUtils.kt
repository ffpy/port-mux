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

object NettyUtils {

    fun createEventLoopGroup(nThreads: Int): EventLoopGroup {
        return if (SystemUtils.isLinux) EpollEventLoopGroup(nThreads) else NioEventLoopGroup(nThreads)
    }

    fun getServerSocketChannelClass(): Class<out ServerChannel> {
        return if (SystemUtils.isLinux) EpollServerSocketChannel::class.java else NioServerSocketChannel::class.java
    }

    fun getSocketChannelClass(): Class<out Channel> {
        return if (SystemUtils.isLinux) EpollSocketChannel::class.java else NioSocketChannel::class.java
    }
}