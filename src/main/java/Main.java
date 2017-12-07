import anypick.FileUtil;
import basic.RSA;
import server.NewServer;
import server.ServiceAnypick;
import server.ServiceParent;
import server.ServiceSRP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class Main {
	public static ArrayList<ServiceParent> serviceList;
	public static void main(String[] args){
		boolean isTest=false;
		if (isTest){
			try{
				//File directory = new File("target\\classes\\website\\Qdaily.txt");//设定为当前文件夹
				String s=FileUtil.readFile("Qdaily.txt");
				System.out.println(s);
			}catch (Exception e){
				e.printStackTrace();
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

