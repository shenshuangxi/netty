package com.sundy.netty.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;

import org.jboss.marshalling.Marshaller;

import com.google.protobuf.compiler.PluginProtos;


public class MarshallingEncoder {

	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	
	private Marshaller marshaller;
	
	public MarshallingEncoder(){
		
	}
	
	public void encode(Object msg, ByteBuf out){
	}
	
}
