package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.Socket;

public class ServiceThread extends Thread {
	private Socket socket = null;
	private ServiceParent serviceParent;
	ServiceThread(Socket socket, ServiceParent serviceParent) {
		this.socket = socket;
		this.serviceParent=serviceParent;
	}

	public void run(){
		printProcessState();
		BufferedReader in;
		PrintWriter out;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			String s=in.readLine();
			System.out.println("get: "+s);//本地打印
			String result=serviceParent.DoStatement(s,socket);
			System.out.println("send: "+result);//本地打印
			out.println(result);//输出到客户端
			socket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	private void printProcessState(){
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String pid = name.split("@")[0];
		System.out.println("Process name= "+name+" ProcessID= "+pid);
	}
}
