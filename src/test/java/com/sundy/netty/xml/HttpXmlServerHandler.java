package com.sundy.netty.xml;

import com.sundy.netty.bean.Customer;
import com.sundy.netty.bean.HttpXmlRequest;
import com.sundy.netty.bean.HttpXmlResponse;
import com.sundy.netty.bean.Order;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpXmlServerHandler extends SimpleChannelInboundHandler<HttpXmlRequest> {

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, HttpXmlRequest msg)
			throws Exception {
		
		HttpRequest request = msg.getRequest();
		Order order = (Order) msg.getBody();
		System.out.println("http server receive request: "+order);
		doBusiness(order);
		ChannelFuture future = ctx.writeAndFlush(new HttpXmlResponse(null, order));
		if(!HttpUtil.isKeepAlive(request)){
			future.addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future)
						throws Exception {
					ctx.close();
				}
			});
		}
		
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		if(ctx.channel().isActive()){
			sendError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void sendError(ChannelHandlerContext ctx,
			HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("Failure: "+status, CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html;charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}





	private void doBusiness(Order order) {
		Customer customer = order.getCustomer();
		customer.setFirstName("狄");
		customer.setMiddleName("仁");
		customer.setLastName("杰");
	}

}
