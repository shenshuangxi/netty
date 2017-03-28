package com.sundy.netty.tcpFileServer;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

public class FileServerHandler extends SimpleChannelInboundHandler<String>{

	private final static String CR = System.getProperty("line.separator");

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String request)
			throws Exception {
		File file = new File(request);
		if(!file.exists()){
			ctx.writeAndFlush("file not find : "+file+CR);
			return;
		}else{
			if(!file.isFile()){
				ctx.writeAndFlush("not a file: "+file+CR);
				return;
			}
			ctx.writeAndFlush(file+" "+file.length()+CR);
			RandomAccessFile randomAccessFile = new RandomAccessFile(request,"r");
			FileRegion fileRegion = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
			ctx.writeAndFlush(fileRegion);
			ctx.writeAndFlush(CR);
			randomAccessFile.close();
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}	
	

}
