package com.sundy.netty.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ProtobufUtil {

	public static void main(String[] args) throws IOException{
		URL sourceFielUrl = ProtobufUtil.class.getClassLoader().getResource("protobuf/person.proto");
		URL sourceDirUrl = ProtobufUtil.class.getClassLoader().getResource("protobuf");
		URL protocExeUrl = ProtobufUtil.class.getClassLoader().getResource("protobuf/protoc.exe");
		URL protocBatUrl = ProtobufUtil.class.getClassLoader().getResource("protobuf/protoc.bat");
		URL targetJsDirUrl = ProtobufUtil.class.getClassLoader().getResource("js");
		URL targetProtosDirUrl = ProtobufUtil.class.getClassLoader().getResource("protos");
		String[] exec = new String[]{new File(protocBatUrl.getFile()).getPath(),
				"java",
				new File(protocExeUrl.getFile()).getPath(),
				new File(sourceDirUrl.getFile()).getPath(),
				new File(targetProtosDirUrl.getFile()).getPath(),
				new File(sourceFielUrl.getFile()).getPath()};
		compaile(exec);
	}
	
	public static void compaile(String[] exec){
		try {
			Process process = Runtime.getRuntime().exec(exec);
			InputStream is = process.getInputStream();
			byte[] buf = new byte[is.available()];
			process.getErrorStream().read(buf);
			System.out.println(new String(buf));
			
			InputStream errIs = process.getErrorStream();
			byte[] errBuf = new byte[errIs.available()];
			process.getErrorStream().read(errBuf);
			System.out.println(new String(errBuf,"gbk"));
			
			is.close();
			errIs.close();
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
