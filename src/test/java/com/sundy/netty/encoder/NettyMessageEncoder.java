package com.sundy.netty.encoder;

import java.util.List;

import com.sundy.netty.bean.NettyMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {


	@Override
	protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out)
			throws Exception {
		
		
	}

}
