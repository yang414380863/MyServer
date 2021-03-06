package anypick;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by YanGGGGG on 2017/12/7.
 */

public class FileUtil {
	private static final String pathWin="src/main/website/";
	private static final String pathLinux="/home/yang/java/src/website/";
        public static String readFile(String fileName){
            File directory;
            if (System.getProperty ("os.name").startsWith("Win")){
	            directory = new File(pathWin+fileName);//设定当前文件夹
            }else {
	            directory = new File(pathLinux+fileName);//设定为当前文件夹
            }
            StringBuilder result = new StringBuilder();
            try{
                BufferedReader br = new BufferedReader(new FileReader(directory.getCanonicalPath()));//构造一个BufferedReader类来读取文件
                String s = null;
                while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                    result.append(System.lineSeparator()+s);
                }
                br.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            return result.toString();
        }
    public static String[] getList(){
	    File directory;
	    if (System.getProperty ("os.name").startsWith("Win")){
		    directory = new File(pathWin);//设定当前文件夹
	    }else {
		    directory = new File(pathLinux);//设定为当前文件夹
	    }
        String[] tempList = directory.list();
        return tempList;
    }
}


