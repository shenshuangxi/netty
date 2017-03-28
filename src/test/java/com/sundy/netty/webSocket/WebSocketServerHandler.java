package com.sundy.netty.webSocket;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

	private WebSocketServerHandshaker handshaker;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		//传统http接入
		if(msg instanceof FullHttpRequest){
			handleHttpRequest(ctx,(FullHttpRequest)msg);
		}
		//websocket接入
		else if(msg instanceof WebSocketFrame){
			handleWebSocketFrame(ctx,(WebSocketFrame)msg);
		}
	}
	
	

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}



	private void handleHttpRequest(ChannelHandlerContext ctx,
			FullHttpRequest request) {
		
		if(!request.decoderResult().isSuccess()||(!"websocket".equals(request.headers().get("Upgrade")))){
			sendHttpResponse(ctx,request,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
		handshaker = wsFactory.newHandshaker(request);
		if(handshaker==null){
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		}else{
			handshaker.handshake(ctx.channel(), request);
		}
		
	}
	
	private void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		
		//判断是否是关闭链路的指令
		if(frame instanceof CloseWebSocketFrame){
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		
		//判断是否为ping消息
		if(frame instanceof PingWebSocketFrame){
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		
		//支持文本消息，不支持二进制消息
		if(!(frame instanceof TextWebSocketFrame)){
			throw new UnsupportedOperationException(String.format("%s frame types not support",frame.getClass().getName()));
		}
		
		String request = ((TextWebSocketFrame)frame).text();
		ctx.channel().write(new TextWebSocketFrame(request+" ,欢迎使用Netty WebSocket 服务，现在时刻: "+new Date().toString()));
		
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, DefaultFullHttpResponse response) {
		if(response.status().code()==200){
			ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(),CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
			HttpUtil.setContentLength(response, response.content().readableBytes());
		}
		
		//非keep-alive关闭链接
		ChannelFuture future = ctx.channel().writeAndFlush(response);
		if(!HttpUtil.isKeepAlive(request)||response.status().code()!=200){
			future.addListener(ChannelFutureListener.CLOSE);
		}
		
	}

}
