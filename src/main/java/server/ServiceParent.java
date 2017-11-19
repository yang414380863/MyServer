package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

abstract public class ServiceParent {
	private int PORT;
	ServerSocket serverSocket=null;
	ServiceParent(int port){
		PORT=port;
		System.out.println("service "+this.getClass().getName() +" running.PORT= "+PORT);
	}
	void initServer(){
		try {
			serverSocket=new ServerSocket(PORT);
		}catch (Exception e){
			System.out.println("server.ServiceParent start error");
			e.printStackTrace();
		}
	}
	abstract void startServer();
	void startReceiveServer(){
		ServiceParent serviceParent=this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true){
					try {
						Socket socket=serverSocket.accept();//监控端口 有新的连接就打开一个新线程
						System.out.println("Connect with IP: "+socket.getInetAddress());
						ServiceThread serverThread=new ServiceThread(socket,serviceParent);
						serverThread.start();
					}catch (IOException e){
						e.printStackTrace();
						break;
					}
				}
			}
		}) .start();
	}
	void startBackgroundServer(){}
	abstract String DoStatement(String statement,Socket socket);
}