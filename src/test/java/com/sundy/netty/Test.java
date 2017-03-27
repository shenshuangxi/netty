package com.sundy.netty;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.tools.ant.taskdefs.XSLTProcess.Factory;
import org.jibx.binding.BindingGenerator;
import org.jibx.binding.Compile;
import org.jibx.binding.SchemaGenerator;
import org.jibx.binding.ant.CompileTask;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import com.sun.org.apache.regexp.internal.recompile;
import com.sundy.netty.bean.Address;
import com.sundy.netty.bean.Customer;
import com.sundy.netty.bean.Order;
import com.sundy.netty.bean.Shipping;


public class Test {
	
	private static IBindingFactory factory = null;
	private static StringWriter writer = null;
	private static StringReader reader = null;
	private final static String CHARSET_NAME = "UTF-8";
	
	private String encode2Xml(Order order) throws Exception{
		factory = BindingDirectory.getFactory(Order.class);
		writer = new StringWriter();
		IMarshallingContext mctx = factory.createMarshallingContext();
		mctx.setIndent(2);
		mctx.marshalDocument(order, CHARSET_NAME, null, writer);
		String xmlStr = writer.toString();
		writer.close();
		System.out.println(xmlStr);
		return xmlStr;
	}
	
	public Order decode2Order(String xmlBody) throws Exception{
		reader = new StringReader(xmlBody);
		IUnmarshallingContext uctx = factory.createUnmarshallingContext();
		Order order = (Order) uctx.unmarshalDocument(reader);
		return order;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("user.dir"));
		
		
//		BindingGenerator.main(new String[]{"-v","-f", "bind.xml",
//		"-b","com.sundy.netty.bean.Shipping=STANDARD_MAIL,PRIORITY_MAIL,INTERNATIONAL_MAIL,DOMESTIC_EXPRESS,INTERNATIONAL_EXPRESS",
//		"com.sundy.netty.bean.Shipping"});
		
		
//		BindingGenerator.main(new String[]{"-v","-f", "bind.xml", "com.sundy.netty.bean.Shipping", "com.sundy.netty.bean.Order"});
		BindingGenerator.main(new String[]{"-v","-f", "bind.xml", "com.sundy.netty.bean.Order"});
		Compile.main(new String[]{"-v","bind.xml"});
		SchemaGenerator.main(new String[]{"-v","bind.xml"});
		
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
		
//		order.setShipping(Shipping.DOMESTIC_EXPRESS);
		
		Address shipTo = new Address();
		shipTo.setStreet1("南山区1");
		shipTo.setStreet2("深南香蜜立交1");
		shipTo.setCity("深圳市1");
		shipTo.setCountry("中国1");
		shipTo.setState("广东省1");
		shipTo.setPostCode("00001351");
		order.setShipTo(shipTo);
		order.setTotal(101.54f);
		
		String xmlOrder = new Test().encode2Xml(order);
		Order order2 = new Test().decode2Order(xmlOrder);
		System.out.println(order2);
		
		
	
	}

}
