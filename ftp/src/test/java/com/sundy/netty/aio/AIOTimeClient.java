package com.sundy.netty.aio;

import java.io.IOException;

public class AIOTimeClient {

	public static void main(String[] args) throws IOException{
		AsyncTimeClientHandler asyncTimeClientHandler = new AsyncTimeClientHandler("127.0.0.1",8080);
		new Thread(asyncTimeClientHandler, "AIO time client 0001").start();
	}
	
}
