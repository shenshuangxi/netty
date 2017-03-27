package com.sundy.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MulitplexerTimerServer implements Runnable {

	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	
	public MulitplexerTimerServer(int port) throws IOException{
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	@Override
	public void run() {
		try {
			while(true){
				selector.select(1000);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				SelectionKey selectionKey = null;
				Iterator<SelectionKey> it = selectionKeys.iterator();
				while(it.hasNext()){
					selectionKey = it.next();
					it.remove();
					if(selectionKey.isAcceptable()){
						Selector selector = selectionKey.selector();
						ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
						SocketChannel sc = ssc.accept();
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
					}
					if(selectionKey.isReadable()){
						SocketChannel sc = (SocketChannel) selectionKey.channel();
						ByteBuffer readBuffer = ByteBuffer.allocate(1024);
						int length = sc.read(readBuffer);
						if(length>0){
							readBuffer.flip();
							byte[] buffer = new byte[readBuffer.remaining()];
							readBuffer.get(buffer);
							String recvMsg = new String(buffer,"UTF-8");
							System.out.println(recvMsg);
							String sendMsg = recvMsg.equals("QUERY TIME ORDER")?new Date().toString():"BAD QUERY";
							ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
							writeBuffer.put(sendMsg.getBytes("UTF-8"));
							writeBuffer.flip();
							sc.write(writeBuffer);
						}else if(length<0){
							selectionKey.cancel();
							sc.close();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(selector!=null){
				try {
					selector.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			selector = null;
		}
	}

}
