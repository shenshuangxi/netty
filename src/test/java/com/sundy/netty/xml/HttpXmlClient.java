package com.sundy.netty.xml;

import com.sundy.netty.bean.Order;
import com.sundy.netty.encoder.HttpXmlRequestEncoder;
import com.sundy.netty.encoder.HttpXmlResponseDecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

public class HttpXmlClient {

	public void connect(String host, int port) throws InterruptedException{
		
		EventLoopGroup workEventLoopGroup = new NioEventLoopGroup();
		
		try {
			Bootstrap bootstrap = new Bootstrap();
			
			bootstrap.group(workEventLoopGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("http-decode",new HttpResponseDecoder())
					.addLast("http-aggregator", new HttpObjectAggregator(65536))
					.addLast("xml-decoder", new HttpXmlResponseDecoder(Order.class, true))
					.addLast("http-encoder", new HttpRequestEncoder())
					.addLast("xml-encoder", new HttpXmlRequestEncoder())
					.addLast("xmlClentHandler", new HttpXmlClientHandler());
				}
			});
			ChannelFuture future = bootstrap.connect(host, port).sync();
			future.channel().closeFuture().sync();
		}finally{
			workEventLoopGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args){
		try {
			new HttpXmlClient().connect("127.0.0.1", 8080);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
