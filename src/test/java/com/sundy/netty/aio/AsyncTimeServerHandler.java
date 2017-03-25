package com.sundy.netty.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable {

	private AsynchronousServerSocketChannel asynchronousServerSocketChannel;
	private CountDownLatch countDownLatch;
	
	public AsyncTimeServerHandler(int port) throws IOException {
		asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port), 1024);
	}

	@Override
	public void run() {
		this.countDownLatch = new CountDownLatch(1);
		doAccept();
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void doAccept() {
		asynchronousServerSocketChannel.accept(this, new CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler>() {
			
			@Override
			public void completed(final AsynchronousSocketChannel channel,
					AsyncTimeServerHandler attachment) {
				attachment.asynchronousServerSocketChannel.accept(attachment, this);
				ByteBuffer readByteBuffer = ByteBuffer.allocate(1024);
				channel.read(readByteBuffer, readByteBuffer, new CompletionHandler<Integer, ByteBuffer>() {

					@Override
					public void failed(Throwable exc, ByteBuffer attachment) {
						try {
							channel.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void completed(Integer result, ByteBuffer byteBuffer) {
						byteBuffer.flip();
						byte[] buffer = new byte[byteBuffer.remaining()];
						byteBuffer.get(buffer);
						try {
							String recvMsg = new String(buffer,"UTF-8");
							System.out.println(recvMsg);
							String sendMsg = "QUERY TIME ORDER".equals(recvMsg)?new Date().toString():"BAD QUERY";
							byte[] sendByte = sendMsg.getBytes("UTF-8");
							ByteBuffer writeBuffer = ByteBuffer.allocate(sendByte.length);
							writeBuffer.put(sendByte);
							writeBuffer.flip();
							channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {

								@Override
								public void completed(Integer result,
										ByteBuffer byteBuffer) {
									
									if(byteBuffer.hasRemaining()){
										channel.write(byteBuffer, byteBuffer, this);
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
								}
							});
							
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
				});
			}

			@Override
			public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
				try {
					attachment.asynchronousServerSocketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	}

}
