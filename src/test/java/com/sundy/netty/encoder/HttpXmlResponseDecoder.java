package com.sundy.netty.encoder;

import java.util.List;

import com.sundy.netty.bean.HttpXmlResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<FullHttpResponse> {

	public HttpXmlResponseDecoder(Class<?> clazz) {
		this(clazz,false);
	}

	public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrint) {
		super(clazz, isPrint);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpResponse msg, List<Object> out) throws Exception {
		Object obj = decode0(ctx, msg.content());
		HttpXmlResponse response = new HttpXmlResponse(msg, obj);
		out.add(response);
	}

}
