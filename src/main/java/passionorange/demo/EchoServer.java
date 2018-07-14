package passionorange.demo;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * A server which accepts client connections and echoes back the request.
 * Run EchoServer and EchoCLient at same time to test
 */
public class EchoServer extends NettyServerDemo {

	@Sharable
	class EchoServerInboundHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			ByteBuf in = (ByteBuf) msg;
			System.out.println("Received from client: " + in.toString(CharsetUtil.UTF_8));
			ctx.write(in);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			ctx.writeAndFlush(Unpooled.copiedBuffer("\r\nBye".getBytes())).addListener(ChannelFutureListener.CLOSE);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}
	}

	public ChannelInitializer<SocketChannel> getHandler() {
		final EchoServerInboundHandler serverHandler = new EchoServerInboundHandler();
		final ByteBuf buf = Unpooled
				.unreleasableBuffer(Unpooled.copiedBuffer("Hello World!!\r\n", Charset.forName("UTF-8")));

		ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						ctx.channel().pipeline().addLast(serverHandler);
					}
				});
			}
		};
		return channelInitializer;
	}
	
	public static void main(String[] args) {
		EchoServer echoServer = new EchoServer();
		echoServer.bootstrap();
	}

}
