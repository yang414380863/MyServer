package sql;

import java.net.Socket;
import java.sql.SQLException;

public class SqlAnyPick extends SqlParent {

	private static final String databaseName= "anypick";
	public SqlAnyPick(){
		super(databaseName);
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
			case "checkPush":{
				res =  "";
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
				state("insert into users values('"+userName+"','"+password+"','');");
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
	//todo:String
	private String checkPush(String userName){
		resultSet=stateWithReturn("select * from users where username = "+userName+";");
		try {
			if (resultSet.next()){
				String mark=resultSet.getString("mark");
				return "";
			}else {
				return "error";
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
	}
}
