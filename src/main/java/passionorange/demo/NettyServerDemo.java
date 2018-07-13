package passionorange.demo;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Demo to show the API for netty based server endpoints.
 * Test using telnet localhost 3002
 */
public class NettyServerDemo {

	public void bootstrap() {
		// starts number_of_processors * 2 threads.
		// uses default provider = sun.nio.ch.KQueueSelectorProvider
		// io.netty.util.concurrent.ThreadPerTaskExecutor
		// spwans a event loop for each thread
		// uses io.netty.channel.DefaultSelectStrategy
		// uses io.netty.util.concurrent.RejectedExecutionHandlers
		// event loop is single threaded event executor
		// task q used by event loop is
		// io.netty.util.internal.shaded.org.jctools.queues.MpscChunkedArrayQueue
		NioEventLoopGroup group = new NioEventLoopGroup();
		// allows easy bootstrap of
		ServerBootstrap b = new ServerBootstrap();
		try {
			b.group(group);
			// set the class to be used to create, NioServerSocketChannel has NIO selector
			// based implementation to accept new connections.
			b.channel(NioServerSocketChannel.class);
			// set the child handler to be used for serving requests for this channel
			b.childHandler(getHandler());
			//set the local address and port
			b.localAddress(new InetSocketAddress(3002));
			// Create a new channel and bind it to the declared port.
			b.bind().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		NettyServerDemo nettyServerDemo = new NettyServerDemo();
		nettyServerDemo.bootstrap();
	}

	public ChannelInitializer<SocketChannel> getHandler() {
		final ByteBuf buf = Unpooled
				.unreleasableBuffer(Unpooled.copiedBuffer("Hello World!!\r\n", Charset.forName("UTF-8")));
		ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
					}
				});
			}
		};
		return channelInitializer;
	}

}
