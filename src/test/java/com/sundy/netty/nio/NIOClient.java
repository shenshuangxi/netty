package com.sundy.netty.nio;

import java.io.IOException;

public class NIOClient {

	public static void main(String[] args) throws IOException{
		TimeClientHandler timeClientHandler = new TimeClientHandler("127.0.0.1",8080);
		new Thread(timeClientHandler).start();
	}
	
}
