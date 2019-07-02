package net.server.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.List;

public class ChatServerSocketThread14 extends Thread {
	
	///Field
	private BufferedReader fromClient;
	private PrintWriter toClient;
	private Socket socket;
	private List<ChatServerSocketThread14> list;
	boolean loopFlag;
	private SocketAddress socketAddress;
	private String clientName;
	
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	///Constructor
	public ChatServerSocketThread14() {
		
	}
	
	public ChatServerSocketThread14(Socket socket, List<ChatServerSocketThread14> list) {
		
		this.socket=socket;
		this.socketAddress=socket.getRemoteSocketAddress();
		this.list=list;
		
		try {
			fromClient=new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			toClient=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			loopFlag=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	///Method
	public void run() {
		
		System.out.println("\n[ChatServerSocketThread ("+socketAddress+")] : data를 수신, 송신 Loop Start");
		
		String fromClientData=null;
		
		while (loopFlag) {
			
			try {					
				if((fromClientData=fromClient.readLine())!=null && !fromClientData.equals("Quit")) {
					System.out.println("\n[ChatServerSocketThread ("+socketAddress+")] : Client 전송받은 Data ==>"+fromClientData);
					execute(fromClientData.substring(0,3), fromClientData.substring(4));				
				} else {
					break;
				}
			} catch (SocketException se) {
				se.printStackTrace();
				loopFlag=false;
			} catch (Exception e) {
				e.printStackTrace();
				loopFlag=false;
			}
		}

		System.out.println("\n[ChatServerSocketThread ("+socketAddress+")] : Data를 수신,송신 Loop End");
		this.close();
	}
	
	public synchronized void toAllClient(String message) {
		for(ChatServerSocketThread14 chatServerSocketThread : list) {
			chatServerSocketThread.getWriter().println(message);
		}
	}
	
	public PrintWriter getWriter() {
		return toClient;
	}
	
	public synchronized boolean hasName(String clientName) {
		for(ChatServerSocketThread14 chatServerSocketThread : list) {
			if(chatServerSocketThread!=this&&clientName.equals(chatServerSocketThread.getClientName())) {
				return true;
			}
		}
		return false;
	}
	
	public void execute(String protocol, String message) {
		if(protocol.equals("100")) {
			this.clientName=message;
			if(this.hasName(message)) {
				System.out.println(" ["+message+"] 대화명 중복");
				toClient.println(" ["+message+"] 대화명 중복");
				loopFlag=false;
			} else {
				this.toAllClient("[ "+message+" ] 님 입장");
			}
			
		} else if(protocol.equals("200")) {
			this.toAllClient("["+clientName+"] : "+message);
		} else if(protocol.equals("400")) {
			this.toAllClient("[ "+clientName+" ] 님 퇴실");
		}
	}

	public void close() {
		System.out.println(":: close() start......");
		
		try {
			if(toClient!=null) {
				toClient.close();
				toClient=null;
			}
			if(fromClient!=null) {
				fromClient.close();
				fromClient=null;
			}
			if(socket!=null) {
				socket.close();
				socket=null;
			}

			list.remove(this);
			System.out.println("접속자 수 : "+list.size());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(":: close() end......");
	}

	public void setLoopFlag(boolean loopFlag) {
		this.loopFlag = loopFlag;
	}
		
}
