package frc.team2186.jetson.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.CharsetUtil

class ServerHandler : ChannelInboundHandlerAdapter() {
    private lateinit var currentInput: JsonElement
    private lateinit var currentOutput: JsonObject

    private var isConnected = false
    private val parser = JsonParser()

    val connected get() = synchronized(this) {isConnected}

    fun send(o: JsonObject) {
        synchronized(this) {
            currentInput = o
        }
    }

    fun recv(): JsonElement? {
        synchronized(this) {
            return currentOutput
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        val byteBuf = msg as ByteBuf
        var out = JsonObject()

        synchronized(this) {
            currentInput = parser.parse(byteBuf.toString(CharsetUtil.US_ASCII))
            out = currentOutput
            isConnected = true
        }

        val s = ctx?.alloc()?.buffer()
        s?.writeBytes((out.toString() + '\n').toByteArray(CharsetUtil.US_ASCII))

        val f = ctx?.writeAndFlush(s)
        f?.addListener {
            assert(f == it)
            synchronized(this) {
                isConnected = false
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        cause?.printStackTrace()
        ctx?.close()
    }
}