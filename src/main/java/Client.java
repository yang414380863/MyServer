import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private static final int PORT=10248;
	private static final String ServerIP="120.78.83.222";//211.66.11.224

	public static void main(String[] args) throws IOException{
		try (Socket socket = new Socket(ServerIP, PORT)){
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

			while (true){
				String s = keyboard.readLine();//客户端键盘输入
				out.println(s);//输出到服务器
				System.out.println(in.readLine());//打印服务器返回的内容
			}
		}
	}
}