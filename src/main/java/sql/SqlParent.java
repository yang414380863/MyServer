package sql;

import java.sql.*;

public class SqlParent {

	String DATABASENAME;
	private static final String URL="jdbc:mysql://localhost:3306/?useSSL=false";
	private static final String user = "root";
	private static final String password = "5818111";
	private Connection connection = null;
	private PreparedStatement statement = null;//用来执行SQL语句
	ResultSet resultSet=null;//SQL返回结果
	String[] states=null;
	//String result=null;

	SqlParent(String databaseName){
		DATABASENAME =databaseName;
		String driver = "com.mysql.jdbc.Driver";
		try {
			Class.forName(driver);
			//System.out.println("驱动程序加载成功");
		} catch (ClassNotFoundException e) {
			//System.out.println("驱动程序加载失败:");
			e.printStackTrace();
		}
		//链接数据库
		try {
			connection = DriverManager.getConnection(URL, user, password);
			if (!connection.isClosed()) {
				//System.out.println("连接数据库成功");
			}
		} catch (SQLException e) {
			System.out.println("连接数据库失败: " + e.getMessage());
		}
		state("USE "+ DATABASENAME +";");
	}
	//断开
	public void disconnect(){
		try {
			if (connection != null)
				connection.close();
		} catch (Exception e) {
			System.out.println("关闭数据库问题 ：");
			e.printStackTrace();
		}
	}
	//执行SQL语句 增删改
	public boolean state(String sql) {
		try {
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	//执行带返回的SQL语句 查找
	public ResultSet stateWithReturn(String sql) {
		ResultSet res = null;
		try {
			statement = connection.prepareStatement(sql);
			res = statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
}
