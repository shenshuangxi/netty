package com.sundy.netty.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeClientHandler implements Runnable,
		CompletionHandler<Void, AsyncTimeClientHandler> {

	private String host;
	private int port;
	private AsynchronousSocketChannel channel;
	private CountDownLatch countDownLatch;
	
	public AsyncTimeClientHandler(String host, int port) throws IOException {
		this.channel = AsynchronousSocketChannel.open();
		this.host = host;
		this.port = port;
	}

	@Override
	public void run() {
		countDownLatch = new CountDownLatch(1);
		channel.connect(new InetSocketAddress(host, port), this, this);
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void completed(Void result, AsyncTimeClientHandler attachment) {
		try {
			byte[] sendBytes = "QUERY TIME ORDER".getBytes("UTF-8");
			ByteBuffer writeBuffer = ByteBuffer.allocate(sendBytes.length);
			writeBuffer.put(sendBytes);
			writeBuffer.flip();
			channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {

				@Override
				public void completed(Integer result, ByteBuffer byteBuffer) {
					if(byteBuffer.hasRemaining()){
						channel.write(byteBuffer, byteBuffer, this);
					}else{
						ByteBuffer readBuffer = ByteBuffer.allocate(1024);
						channel.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {

							@Override
							public void completed(Integer result,ByteBuffer byteBuffer) {
								byteBuffer.flip();
								byte[] buffer = new byte[byteBuffer.remaining()];
								byteBuffer.get(buffer);
								try {
									String recvMsg = new String(buffer,"UTF-8");
									System.out.println(recvMsg);
									countDownLatch.countDown();
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void failed(Throwable exc,
									ByteBuffer attachment) {
								try {
									channel.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								countDownLatch.countDown();
							}
						});
					}
					
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try {
						channel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					countDownLatch.countDown();
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			try {
				channel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			countDownLatch.countDown();
		}
		
	}

	@Override
	public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
		
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		countDownLatch.countDown();
		
	}

}
