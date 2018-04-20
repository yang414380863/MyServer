package sql;

import anypick.JsonUtils;
import anypick.WebsiteInit;

import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlAnyPick extends SqlParent {

	private static final String databaseName= "anypick";
	public SqlAnyPick(){
		super(databaseName);
	}

	public boolean refreshWebsite(String indexUrl,String link,String latestUpdate){
		resultSet=stateWithReturn("select * from websites where indexurl = '"+indexUrl+"';");
		try {
			if (resultSet.next()){
				state(" update websites set indexurl = '"+indexUrl+"', link = '"+link+"', latestupdate = '"+latestUpdate+"' where indexurl = '"+indexUrl+"';");
			}else {
				state("insert into websites values('"+indexUrl+"','"+link+"','"+latestUpdate+"');");
			}
			return true;
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public String judge(String statement, Socket socket){
		states=statement.split(" ");//暂时用空格分割
		String action=states[0];
		String res;
		switch (action){
			case "register":{//注册 成功返回:success,ID已存在:返回existed
				res = this.register(states[1],states[2]);//(register username password )
                break;
			}
			case "login":{//登录 成功返回:mark,登录失败:返回failed
				res =  this.login(states[1],states[2]);//(register username password)
                break;
			}
			case "updateMark":{//更新订阅
				updateMark(states[1],states[2],states[3]);
				res =  "";
                break;
			}
			case "updateLocal":{//更新已下载Website
				updateLocal(states[1],states[2],states[3]);
				res =  "";
				break;
			}
			case "getLocal":{//下载Website到本地
				res = getLocal(states[1],states[2]);
				break;
			}
			case "checkPush":{
				res =  checkPush(states[1]);
                break;
			}
			case "marketGetList":{
				res =  marketGetList();
				break;
			}
			case "marketGetDetail":{
				res =  marketGetDetail(states[1]);
				break;
			}
			default:{
				//无法识别
				res =  "statementError";//sql.stateWithReturn(statement).toString();
                break;
			}
		}
		disconnect();
		return res;
	}

	private String register(String userName, String password){
		resultSet=stateWithReturn("select * from users where username = "+userName+";");
		try {
			if (resultSet.next()){
				return "existed";
			}else {
				state("insert into users values('"+userName+"','"+password+"','','');");
				return "success";
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
	}

	private String login(String userName,String password){
		resultSet=stateWithReturn("select * from users where username = '"+userName+"'and password = '"+password+"';");
		try {
			if (resultSet.next()){
				String mark=resultSet.getString("mark");
				return "success "+mark;//返回已订阅信息
			}else {
				return "failed";
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
	}

	private void updateMark(String userName,String password,String mark){
		state(" update users set mark = '"+mark+"' where username = '"+userName+"'and password = '"+password+"';" );
	}

	private void updateLocal(String userName,String password,String local){
		state(" update users set local = '"+local+"' where username = '"+userName+"'and password = '"+password+"';" );
	}

	private String getLocal(String userName,String password){
		resultSet=stateWithReturn(" select * from users where username = '"+userName+"'and password = '"+password+"';" );
		try {
			if (resultSet.next()){
				String local=resultSet.getString("local");
				return "success "+local;//返回服务器储存的之前已下载内容
			}else {
				return "failed";
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
	}

	private String checkPush(String userName){
		resultSet=stateWithReturn("select * from users where username = '"+userName+"';");
		StringBuilder state=new StringBuilder("");
		StringBuilder res=new StringBuilder("");
		try {
			if (resultSet.next()){
				String[] websites=resultSet.getString("mark").split("!@#");
				for (int i=0;i<websites.length;i++){
					state.append(" indexurl = \"").append(websites[i]).append("\"");
					if (i==websites.length-1){
						state.append(";");
					}else {
						state.append(" or ");
					}
				}
				System.out.println(state);
				ResultSet resultSet=stateWithReturn("select * from websites where "+state);
				while (resultSet.next()){
					res.append(resultSet.getString("indexurl")).append("!@#").append(resultSet.getString("link")).append("!@#").append(resultSet.getString("latestupdate")).append("!@#!@#");
				}
			}else {
				return "error";
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
		return res.toString();
	}

	private String marketGetList(){
		String[] websiteNameList= WebsiteInit.getWebsiteNameList();
		if (websiteNameList.length==0){
			return "error";
		}
		StringBuilder res=new StringBuilder(websiteNameList[0]);
		for (int i=1;i<websiteNameList.length;i++){
			res.append(",").append(websiteNameList[i]);
		}
		return res.toString();
	}

	private String marketGetDetail(String name){
		String[] websiteNameList= WebsiteInit.getWebsiteNameList();
		if (websiteNameList.length==0){
			return "error";
		}
		for (int i=0;i<websiteNameList.length;i++){
			if (websiteNameList[i].equals(name)){
				return JsonUtils.ObjectToJson(WebsiteInit.getWebsiteList()[i]);
			}
		}
		return "error";
	}
}
