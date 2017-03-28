package com.sundy.netty.udpServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UDPServer  {

	private void start(int port) throws InterruptedException {
		
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(eventLoopGroup)
			.channel(NioDatagramChannel.class)
			.handler(new UDPServerHandler());
			ChannelFuture future = bootstrap.bind(port).sync();
			future.channel().closeFuture().sync();
		} finally{
			eventLoopGroup.shutdownGracefully();
		}
		
		
	}
	
	
	public static void main(String[] args) throws InterruptedException{
		
		
		new UDPServer().start(8080);
		
	}
	
	
}
