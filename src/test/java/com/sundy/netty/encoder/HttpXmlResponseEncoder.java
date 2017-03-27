package com.sundy.netty.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

import java.util.List;

import com.sundy.netty.bean.HttpXmlResponse;

public class HttpXmlResponseEncoder extends AbstractHttpXmlEncoder<HttpXmlResponse> {

	@Override
	protected void encode(ChannelHandlerContext ctx, HttpXmlResponse msg, List<Object> out) throws Exception {
		ByteBuf byteBuf = encode0(ctx, msg.getResult());
		FullHttpResponse response = msg.getResponse();
		if(response==null){
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,byteBuf);
		}else{
			response = new DefaultFullHttpResponse(msg.getResponse().protocolVersion(), msg.getResponse().status(),byteBuf);
		}
		response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/xml");
		HttpUtil.setContentLength(response, byteBuf.readableBytes());
		out.add(response);
	}

}
