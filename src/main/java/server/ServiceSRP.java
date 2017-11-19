package server;

import sql.SqlSRP;

import java.io.IOException;
import java.net.Socket;

public class ServiceSRP extends ServiceParent {
	private static int PORT=10248;
	public ServiceSRP(){
		super(PORT);
	}
	void initServer(){
		super.initServer();
	}
	void startServer(){
		startReceiveServer();
	}
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
	String DoStatement(String statement,Socket socket){
		SqlSRP sqlSRP=new SqlSRP();
		String res=sqlSRP.judge(statement,socket);
		return res;
	}
}
