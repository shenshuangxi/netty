package com.sundy.netty.xml;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import com.sundy.netty.bean.Order;
import com.sundy.netty.encoder.HttpXmlRequestDecoder;
import com.sundy.netty.encoder.HttpXmlResponseEncoder;

public class HttpXmlServer {

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
					.addLast("http-request-decode",new HttpRequestDecoder())
					.addLast("http-aggregator", new HttpObjectAggregator(65536))
					.addLast("http-request-xml-decode",new HttpXmlRequestDecoder(Order.class,true))
					.addLast("http-response-encode", new HttpResponseEncoder())
					.addLast("http-response-xml-encode", new HttpXmlResponseEncoder())
					.addLast("xmlServerHandler", new HttpXmlServerHandler());
					
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
		
		
		new HttpXmlServer().start(8080);
		
	}
	
}
