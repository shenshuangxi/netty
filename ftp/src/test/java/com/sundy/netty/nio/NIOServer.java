package com.sundy.netty.nio;

import java.io.IOException;

public class NIOServer {

	public static void main(String[] args) throws IOException{
		MulitplexerTimerServer mulitplexerTimerServer = new MulitplexerTimerServer(8080);
		new Thread(mulitplexerTimerServer).start();
	}
	
}
