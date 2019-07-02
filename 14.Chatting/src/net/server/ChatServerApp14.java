package net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import net.server.thread.ChatServerSocketThread14;

public class ChatServerApp14 {
	
	///Main Method
	public static void main(String[] args) {
		
		System.out.println("======================================");
		System.out.println("[ChatServerApp Main Thread] : STARTUP......\n");
		
		List<ChatServerSocketThread14> list=new Vector<ChatServerSocketThread14>(10,10);
		
		ServerSocket serverSocket=null;
		Socket socket=null;
		ChatServerSocketThread14 serverSocketThread=null;
		boolean loopFlag=false;
		
		try {
			serverSocket=new ServerSocket(7000);
			loopFlag=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(loopFlag) {
			try {
				System.out.println("\n\t\t\t\t/////////////////////////////////////////////////////////");
				System.out.println("\t\t\t\t[ChatServerApp Main Thread] : Client Connection Wait");
				
				socket=serverSocket.accept();
				
				System.out.println("\n\t\t\t\t[Host Main Thread] : client"+socket.getRemoteSocketAddress()+" 연결");
				
				serverSocketThread=new ChatServerSocketThread14(socket, list);
				list.add(serverSocketThread);
				System.out.println(serverSocketThread.getClientName());
				
				System.out.println("\n\t\t\t\t[ChatServerApp Main Thread] : 현재 접속자 수"+list.size()+"\n");
				
				serverSocketThread.start();				
			} catch (IOException ie) {
				ie.printStackTrace();
				loopFlag=false;
			}
		}
		
		System.out.println("\t\t\t\t[ChatServerApp Main Thread] : Client Connection Wait END");
		System.out.println("\n\t\t\t\t/////////////////////////////////////////////////////////");
		
		
		synchronized (list) {
			for(ChatServerSocketThread14 thread : list) {
				thread.setLoopFlag(false);
			}
		}
		
		while(true) {
			if(list.size()!=0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			} else {
				break;
			}
		}
		
		try {
			if(serverSocket!=null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n[ChatServerApp Main Thread] : SHUTDOWN....");
		System.out.println("======================================");
		
	}

}
