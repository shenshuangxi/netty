package com.sundy.netty.tcpFileServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


public class FileServer {

	private void start(int port) throws InterruptedException {
		
		
		EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
		EventLoopGroup workEventLoopGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossEventLoopGroup, workEventLoopGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline()
					.addLast("string-encode",new StringEncoder())
					.addLast("line-decode", new LineBasedFrameDecoder(1024))
					.addLast("string-decode", new StringDecoder())
					.addLast("fileServerHandler", new FileServerHandler());
					
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
		
		
		new FileServer().start(8080);
		
	}
	
}
