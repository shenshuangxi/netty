package com.sundy.netty.fileServer;

import com.sundy.netty.fileServer.handler.FileServerHandler;
import com.sundy.netty.fileServer.handler.LogHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;


public class FileServer {

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
					.addLast("http-response-encode", new HttpResponseEncoder())
					.addLast("http-chunked", new ChunkedWriteHandler())
					.addLast("fileServerHandler", new FileServerHandler("/ftp"));
					
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
