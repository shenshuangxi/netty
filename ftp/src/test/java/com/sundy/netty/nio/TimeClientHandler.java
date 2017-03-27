package com.sundy.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandler implements Runnable {

	private String host;
	private int port;
	private Selector selector;
	private SocketChannel socketChannel;
	
	
	public TimeClientHandler(String host, int port) throws IOException {
		this.host = host==null?"127.0.0.1":host;
		this.port = port;
		selector = Selector.open();
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
	}

	@Override
	public void run() {
		try {
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			while(true){
				selector.select(1000);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				SelectionKey selectionKey = null;
				while(it.hasNext()){
					selectionKey = it.next();
					it.remove();
					if(selectionKey.isConnectable()){
						SocketChannel sc = (SocketChannel) selectionKey.channel();
						if(sc.finishConnect()){
							sc.register(selector, SelectionKey.OP_READ);
							doWrite(sc);
						}else{
							System.exit(1);
						}
					}
					if(selectionKey.isReadable()){
						ByteBuffer readBuffer = ByteBuffer.allocate(1024);
						SocketChannel sc = (SocketChannel) selectionKey.channel();
						int length = sc.read(readBuffer);
						if(length>0){
							readBuffer.flip();
							byte[] buffer = new byte[readBuffer.remaining()];
							readBuffer.get(buffer);
							String recvMsg = new String(buffer,"UTF-8");
							System.out.println(recvMsg);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void doConnect() throws IOException {
		if(socketChannel.connect(new InetSocketAddress(host, port))){
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		}else{
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
		
	}

	private void doWrite(SocketChannel socketChannel) throws IOException {
		byte[] sendBytes = "QUERY TIME ORDER".getBytes("UTF-8");
		ByteBuffer byteBuffer = ByteBuffer.allocate(sendBytes.length);
		byteBuffer.put(sendBytes);
		byteBuffer.flip();
		socketChannel.write(byteBuffer);
		if(!byteBuffer.hasRemaining()){
			System.out.println("send msg ok");
		}
		
	}

}
