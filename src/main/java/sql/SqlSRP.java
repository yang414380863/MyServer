package sql;

import basic.MD5;
import basic.RSA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlSRP extends SqlParent {
	private static final String databaseName= "srp";

	public SqlSRP(){
		super(databaseName);
	}

	String register(String userID, String loginPassword,String payPassword){
		resultSet=stateWithReturn("select * from users where userid = "+userID+";");
		try {
			if (resultSet.next()){
				return "userIDExisted";
			}else {
				//这里应该是实时生成然后写入数据库并返回公钥的
				RSA rsa=new RSA();
				String publicKey=rsa.getPublicKey();
				String privateKey=rsa.getPrivateKey();
				state("insert into users values('"+userID+"','"+loginPassword+"','"+payPassword+"',"+ (int)(Math.random()*10000)+",'"+publicKey+"','"+privateKey+"');");//先随机给个初始money值
				return publicKey;//返回公钥
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
	}
	String login(String userID,String loginPassword){
		resultSet=stateWithReturn("select * from users where userid = '"+userID+"'and loginpass = '"+loginPassword+"';");
		try {
			if (resultSet.next()){
				String publicKey=resultSet.getString("publickey");
				return publicKey;//返回公钥
			}else {
				return "loginFailed";
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
	}
	String queryBalance(String userID,String loginPassword){
		resultSet=stateWithReturn("select * from users where userid = '"+userID+"'and loginpass = '"+loginPassword+"';");
		try {
			if (resultSet.next()){
				String balance=resultSet.getString("balance");
				return balance;//返回余额
			}else {
				return "queryFailed";
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
	}

	String tradeFromPayee(String payee,String payer,String timeStamp,double value,String content){
		state("insert into temp values('"+payee+"','"+payer+"','"+timeStamp+"','"+ value+"','"+content+"');");
		return "insert success";
	}

	String readyForTrade(String payer, Socket socket){
		try {
			resultSet=stateWithReturn("select * from temp where payer = '"+payer+"';");
			while (true){
				if (resultSet.next()){
					String payee=resultSet.getString("payee");
					Double value=resultSet.getDouble("value");
					String timestamp=resultSet.getString("timestamp");
					state("delete from temp where payer = '"+payer+"';");
					String result=payee+" "+value+" "+timestamp;
					return result;
				}else {
					resultSet=stateWithReturn("select * from temp where payer = '"+payer+"';");
				}
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 *
	 * @param payee 收款人ID
	 * @param payer 付款方ID
	 * @param timeStamp 时间戳
	 * @param value 交易金额
	 * @param md5 (支付密码+时间戳+userID)的MD5
	 * @return
	 */
	String tradeFromPayer(String payee,String payer,String timeStamp,double value, String md5){
		resultSet=stateWithReturn("select * from record where payee = '"+payee+"'and payer = '"+payer+"'and timestamp = '"+timeStamp+"';");
		try {
			if (resultSet.next()){
				return "tradeExisted";
			}
			//这里需要对比MD5
			resultSet=stateWithReturn("select * from users where userid = '"+payer+"' ;");
			if (!resultSet.next()){
				return "payer id not find";
			}
			String payPassword=resultSet.getString("paypass");
			String loginPassword=resultSet.getString("loginpass");
			String res= MD5.getMD5(payer+loginPassword+payPassword+timeStamp);
			System.out.println("Receive MD5: "+md5);
			System.out.println("Server  MD5: "+res);
			if (res.equals(md5)){
				resultSet=stateWithReturn("select * from users where userid = '"+payer+"';");
				resultSet.next();
				Double payerBalance=resultSet.getDouble("balance");
				if (payerBalance<value){
					return "balanceNotEnough";
				}
				state("insert into record values('"+payee+"','"+payer+"','"+timeStamp+"',"+ value+");");
				state("update users set balance = '"+(payerBalance-value)+"' where userid ='"+payer+"';");
				resultSet=stateWithReturn("select * from users where userid = '"+payee+"';");
				resultSet.next();
				Double payeeBalance=resultSet.getDouble("balance");
				state("update users set balance = '"+(payeeBalance+value)+"' where userid ='"+payee+"';");
				return "tradeFromPayerSuccess";
			}else {
				return "tradeFailed(MD5)";
			}
		}catch (SQLException e){
			e.printStackTrace();
			return "error";
		}
	}

	public String judge(String statement, Socket socket){
		states=statement.split(" ");//暂时用空格分割
		String action=states[0];
		String res;
		switch (action){
			case "register":{//注册 成功返回:RSA公钥,ID已存在:返回userIDExisted
                res = this.register(states[1],states[2],states[3]);//(userid loginpass paypassMD5)
                break;
			}
			case "login":{//登录 成功返回:RSA公钥,登录失败:返回loginFailed
                res = this.login(states[1],states[2]);//(userid loginpass)
                break;
			}
			case "queryBalance":{//查询 成功返回:余额,服务器端密码已经被修改导致登录,查询失败:返回queryFailed
                res = this.queryBalance(states[1],states[2]);//(userid loginpass)
                break;
			}
			case "tradeFromPayee":{
				String userID=states[1];
				String express=decrypt(userID,states[2]);
				System.out.println("express: "+express);
				String[] array=express.split(" ");//暂时用空格分割
                res = tradeFromPayee(array[0],array[1],array[2],Double.parseDouble(array[3]),array[4]);//收款人ID 付款方ID 时间戳 交易金额 物品清单
                break;
			}
			case "readyForTrade":{
				//付款方交易时第一次发送 失败返回error
				//服务器返回 userID2+" "+value+" "+time
                res = readyForTrade(states[1],socket);//(userid)
                break;
			}
			//然后付款方发送 收款人ID 付款方ID 时间戳 交易金额 (支付密码+时间戳+userID)的MD5
			case "tradeFromPayer":{
				res=tradeFromPayer(states[1],states[2],states[3],Double.parseDouble(states[4]),states[5]);//(收款人ID 付款方ID 时间戳 交易金额 (支付密码+时间戳+userID)的MD5)
				break;
			}
			default:{
				//无法识别
                res = "statementError";//sql.stateWithReturn(statement).toString();
                break;
			}
		}
		System.out.println("disconnect SQL");
		disconnect();
		return res;
	}

	String decrypt(String userID,String statement){
		try {
			ResultSet resultSet=stateWithReturn("select * from users where userid = "+userID+";");
			if (resultSet.next()){
				String privateKey=resultSet.getString("privatekey");
                System.out.println("get privateKey: "+privateKey);
				return RSA.decrypt(statement,RSA.getPrivateKey(privateKey));
			}else {
				return "decryptError";
			}
		}catch (Exception e){
			e.printStackTrace();
			return "error";
		}
	}

}
