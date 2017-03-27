package com.sundy.netty.encoder;

import java.io.StringReader;
import java.nio.charset.Charset;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;



import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public abstract class AbstractHttpXmlDecoder<T> extends MessageToMessageDecoder<T> {

	private static IBindingFactory factory = null;
	private static StringReader reader = null;
	private Class<?> clazz;
	private boolean isPrint;
	private final Charset UTF_8 = Charset.forName("UTF-8");
	
	public AbstractHttpXmlDecoder(Class<?> clazz) {
		this(clazz, false);
	}
	public AbstractHttpXmlDecoder(Class<?> clazz, boolean isPrint) {
		this.clazz = clazz;
		this.isPrint = isPrint;
	}
	
	public Object decode0(ChannelHandlerContext ctx,ByteBuf byteBuf) throws Exception{
		Object result;
		try {
			byte[] buf = new byte[byteBuf.readableBytes()];
			byteBuf.readBytes(buf);
			String xmlStr = new String(buf, UTF_8);
			factory = BindingDirectory.getFactory(clazz);
			if(isPrint){
				System.out.println("the body is: "+xmlStr);
			}
			reader = new StringReader(xmlStr);
			IUnmarshallingContext uctx = factory.createUnmarshallingContext();
			result = uctx.unmarshalDocument(reader);
			return result;
		} finally{
			reader.close();
			reader = null;
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if(reader!=null){
			reader.close();
			reader = null;
		}
	}
	
	
	
	
}
