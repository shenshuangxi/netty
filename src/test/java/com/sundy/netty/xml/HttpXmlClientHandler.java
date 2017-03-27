package com.sundy.netty.xml;

import com.sundy.netty.bean.Address;
import com.sundy.netty.bean.Customer;
import com.sundy.netty.bean.HttpXmlRequest;
import com.sundy.netty.bean.HttpXmlResponse;
import com.sundy.netty.bean.Order;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpXmlClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Order order = new Order();
		Address billTo = new Address();
		billTo.setStreet1("南山区");
		billTo.setStreet2("深南香蜜立交");
		billTo.setCity("深圳市");
		billTo.setCountry("中国");
		billTo.setState("广东省");
		billTo.setPostCode("00000135");
		order.setBillTo(billTo);
		
		Customer customer = new Customer();
		customer.setCustomerNumber(123421l);
		customer.setFirstName("申");
		customer.setMiddleName("双");
		customer.setLastName("喜");
		order.setCustomer(customer);
		
		order.setOrderNumber(187234612l);
		
		
		Address shipTo = new Address();
		shipTo.setStreet1("南山区1");
		shipTo.setStreet2("深南香蜜立交1");
		shipTo.setCity("深圳市1");
		shipTo.setCountry("中国1");
		shipTo.setState("广东省1");
		shipTo.setPostCode("00001351");
		order.setShipTo(shipTo);
		order.setTotal(101.54f);
		HttpXmlRequest request = new HttpXmlRequest(null, order);
		ctx.writeAndFlush(request);
		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		HttpXmlResponse xmlResponse = (HttpXmlResponse) msg;
		System.out.println("the client receive response of http header is: "+xmlResponse.getResponse().headers().names());
		Object obj = xmlResponse.getResult();
		System.out.println("the client receive response of http body is: "+obj);
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}


	
	
}
