package server;

import anypick.Browser;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import sql.SqlAnyPick;

import java.net.Socket;

public class ServiceAnypick extends ServiceParent {
	private static int PORT=26975;

	public ServiceAnypick(){
		super(PORT);
	}

	void initServer(){
		super.initServer();
	}

	void startServer(){
		startReceiveServer();
		startBackgroundServer();
	}

	void startBackgroundServer(){
		EventBus.getDefault().register(this);
		Browser.getInstance().sendRequest();
	}
	void startReceiveServer(){
		super.startReceiveServer();
	}

	String DoStatement(String statement,Socket socket){
		SqlAnyPick sqlAnyPick=new SqlAnyPick();
		return sqlAnyPick.judge(statement,socket);
	}

	@Subscribe
	public void nextWebsite(String s){
		if (s.equals("nextWebsite")){
			Browser.getInstance().sendRequest();
		}
	}
}
