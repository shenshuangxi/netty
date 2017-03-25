package com.sundy.netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import org.junit.Test;

public class BIOTest {

	@Test
	public void timeServer() throws IOException{
		ServerSocket serverSocker = null;
		try {
			serverSocker = new ServerSocket(8080);
			while(true){
				Socket socket = serverSocker.accept();
				new Thread(new TimerHandler(socket)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(serverSocker!=null){
				serverSocker.close();
				serverSocker = null;
			}
		}
	}
	
	@Test
	public void timeClient() throws UnknownHostException, IOException{
		Socket socket = new Socket("127.0.0.1", 8080);
		OutputStream os = socket.getOutputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		os.write("QUERY TIME ORDER\n".getBytes("utf-8"));
		os.flush();
		os.write("$end \n".getBytes("utf-8"));
		os.flush();
		String line ;
		while((line=br.readLine())!=null&&!line.equals("$end")){
			System.out.println(line);
		}
		
		os = socket.getOutputStream();
		os.write("hahaasdfqw\n".getBytes("utf-8"));
		os.flush();
		os.write("$end\n".getBytes("utf-8"));
		os.flush();
		while((line=br.readLine())!=null&&!line.equals("$end")){
			System.out.println(line);
		}
		os.write("$close\n".getBytes("utf-8"));
		os.flush();
		os.write("$end\n".getBytes("utf-8"));
		os.flush();
		
		socket.close();
		
	}
	
	public class TimerHandler implements Runnable{

		private Socket socket;
		
		public TimerHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			BufferedReader br = null;
			OutputStream os = null;
			try {
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				os = socket.getOutputStream();
				while(true){
					String line = null;
					StringBuilder sb = new StringBuilder();
					while((line=br.readLine())!=null&&!line.startsWith("$end")){
						sb.append(line);
					}
					String recvMsg = sb.toString();
					System.out.println(recvMsg);
					if(recvMsg.equals("$close")){
						break;
					}
					String sendMsg = (recvMsg.equals("QUERY TIME ORDER")?new Date().toString():"BAD ORDER") + "\n";
					os.write(sendMsg.getBytes("utf-8"));
					os.flush();
					os.write("$end\n".getBytes("utf-8"));
					os.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(br!=null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(os!=null){
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(socket!=null){
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					socket = null;
				}
			}
			
			
		}
		
	}
	
}
