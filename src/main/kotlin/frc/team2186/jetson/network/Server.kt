package frc.team2186.jetson.network

import com.google.gson.JsonObject
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.util.logging.Logger

object Server {
    private const val PORT = 5800
    private val handler = ServerHandler()

    val connected get() = handler.connected
    val input get() = handler.recv()

    init {
        Thread {
            run()
        }.start()
    }

    fun send(o: JsonObject) {
        handler.send(o)
    }

    fun run() {
        Logger.getLogger("Server").info("Starting server thread")

        val masterGroup = NioEventLoopGroup(1)
        val slaveGroup = NioEventLoopGroup(1)

        try {
            val b = ServerBootstrap()
            b.group(
                    masterGroup,
                    slaveGroup
            ).channel(NioServerSocketChannel::class.java).childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel?) {
                    ch?.pipeline()?.addLast(handler)
                }
            }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)

            val f = b.bind(PORT).sync()
            f?.channel()?.closeFuture()?.sync()
        } finally {
            masterGroup.shutdownGracefully()
            slaveGroup.shutdownGracefully()
        }
    }
}