package passionorange.demo;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class EchoClient {

	private class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Server", CharsetUtil.UTF_8));
		}

		@Override
		public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
			System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}
	}

	public void client_bootstrap() {
		
		EchoClientHandler echoClientHandler = new EchoClientHandler();
		
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group);
		b.channel(NioSocketChannel.class);
		b.remoteAddress(new InetSocketAddress(3002));
		
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(echoClientHandler);
			}
		});
		ChannelFuture f;
		try {
			f = b.connect().sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// no-op
		} finally {
			try {
				group.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				// no-op
			}
		}
	}
	
	public static void main(String[] args) {
		EchoClient echoClient = new EchoClient();
		echoClient.client_bootstrap();
	}

}
