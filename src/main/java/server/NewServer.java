package server;

public class NewServer extends Thread{
	private ServiceParent serviceParent;
	public NewServer(ServiceParent serviceParent){
		this.serviceParent=serviceParent;
	}
	public void run(){
		serviceParent.initServer();
		serviceParent.startServer();
	}
}
