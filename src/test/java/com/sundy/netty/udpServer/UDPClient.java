package com.sundy.netty.udpServer;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

public class UDPClient {

	private void connect(String host,int port) throws InterruptedException {
		
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(eventLoopGroup)
			.channel(NioDatagramChannel.class)
			.handler(new UDPClientHandler());
			ChannelFuture future = bootstrap.bind(0).sync();
			future.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("谚语字典查询", CharsetUtil.UTF_8), new InetSocketAddress(host, port))).sync();
			if(!future.channel().closeFuture().await(150000)){
				System.out.println("查询超时");
			}
		} finally{
			eventLoopGroup.shutdownGracefully();
		}
		
		
	}
	
	
	public static void main(String[] args) throws InterruptedException{
		
		new UDPClient().connect("127.0.0.1",8080);
		
	}
	
}
