package com.sundy.netty.aio;

import java.io.IOException;

public class AIOTimeServer {

	public static void main(String[] args) throws IOException{
		AsyncTimeServerHandler asyncTimeServerHandler = new AsyncTimeServerHandler(8080);
		new Thread(asyncTimeServerHandler, "AIO SERVER-001").start();
	}
	
}
