package com.sundy.netty.webSocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


public class WebSocketServer {

	private void start(int port) throws InterruptedException {
		
		EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
		EventLoopGroup workEventLoopGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossEventLoopGroup, workEventLoopGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline()
					.addLast("http-server-codec",new HttpServerCodec())
					.addLast("http-aggregator", new HttpObjectAggregator(65536))
					.addLast("http-chunked", new ChunkedWriteHandler())
					.addLast("handler", new WebSocketServerHandler());
					
				}
			});
			ChannelFuture future = serverBootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		} finally{
			workEventLoopGroup.shutdownGracefully();
			bossEventLoopGroup.shutdownGracefully();
		}
		
		
	}
	
	
	public static void main(String[] args) throws InterruptedException{
		
		
		new WebSocketServer().start(8080);
		
	}
	
}
