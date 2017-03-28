package com.sundy.netty.udpServer;

import java.util.concurrent.ThreadLocalRandom;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	private static final String[] DICTIONARY = {"只要功夫深，铁杵磨成针。",
		"旧时王谢堂前燕，飞入寻常百姓家。",
		"洛阳亲友如相问，一片冰心在玉壶。",
		"一寸观音一寸金，寸金难买寸光阴。",
		"老骥伏枥，志在千里。烈士暮年，壮心不已。"};
	
	private String nextQuote(){
		int point = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
		return DICTIONARY[point];
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet)
			throws Exception {
		String reqMsg = packet.content().toString(CharsetUtil.UTF_8);
		if("谚语字典查询".equals(reqMsg)){
			ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(nextQuote(), CharsetUtil.UTF_8), packet.sender()));
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	

}
