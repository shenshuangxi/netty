package com.sundy.netty.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

import java.net.InetAddress;
import java.util.List;

import com.sundy.netty.bean.HttpXmlRequest;

public class HttpXmlRequestEncoder extends AbstractHttpXmlEncoder<HttpXmlRequest> {

	@Override
	protected void encode(ChannelHandlerContext ctx, HttpXmlRequest msg, List<Object> out)
			throws Exception {
		
		ByteBuf body = encode0(ctx, msg.getBody());
		FullHttpRequest request = msg.getRequest();
		if(request == null){
			request =  new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/do", body);
			HttpHeaders httpHeaders = request.headers();
			httpHeaders.set(HttpHeaderNames.HOST, InetAddress.getLocalHost());
			httpHeaders.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
			httpHeaders.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP.toString()+","+HttpHeaderValues.DEFLATE.toString());
			httpHeaders.set(HttpHeaderNames.ACCEPT_CHARSET, "ISO-8859-1,utf-8,q=0.7.*;q=0.7");
			httpHeaders.set(HttpHeaderNames.ACCEPT_LANGUAGE, "zh");
			httpHeaders.set(HttpHeaderNames.USER_AGENT, "netty xml http client side");
			httpHeaders.set(HttpHeaderNames.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		}
		HttpUtil.setContentLength(request, body.readableBytes());
		out.add(request);
	}

}
