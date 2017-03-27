package com.sundy.netty.encoder;

import java.io.StringWriter;
import java.nio.charset.Charset;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public abstract class AbstractHttpXmlEncoder<T> extends MessageToMessageEncoder<T> {

	private IBindingFactory factory;
	private StringWriter writer = null;
	private final static String CHARSET_NAME = "UTF-8";
	private final Charset UTF_8 = Charset.forName("UTF-8");
	
	public ByteBuf encode0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		try {
			factory = BindingDirectory.getFactory(msg.getClass());
			writer = new StringWriter();
			IMarshallingContext mctx = factory.createMarshallingContext();
			mctx.setIndent(2);
			mctx.marshalDocument(msg, CHARSET_NAME, null,writer);
			String xmlStr = writer.toString();
			ByteBuf encodeBuf = Unpooled.copiedBuffer(xmlStr, UTF_8);
			return encodeBuf;
		} finally{
			writer.close();
			writer = null;
		}
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if(writer!=null){
			writer.close();
			writer = null;
		}
	}
	

}
