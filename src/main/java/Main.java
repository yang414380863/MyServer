import server.NewServer;
import server.ServiceAnypick;
import server.ServiceParent;
import server.ServiceSRP;

import java.util.ArrayList;

public class Main {
	public static ArrayList<ServiceParent> serviceList;
	public static void main(String[] args){
		if (System.getProperty ("os.name").startsWith("Win")){
			serviceList=new ArrayList<>();
			serviceList.add(new ServiceAnypick());
			serviceList.add(new ServiceSRP());
			for (int i=0;i<serviceList.size();i++){
				new NewServer(serviceList.get(i)).start();
			}
		}else {
			serviceList=new ArrayList<>();
			serviceList.add(new ServiceAnypick());
			serviceList.add(new ServiceSRP());
			for (int i=0;i<serviceList.size();i++){
				new NewServer(serviceList.get(i)).start();
			}
		}
	}
}

