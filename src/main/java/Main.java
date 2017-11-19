import basic.RSA;
import server.NewServer;
import server.ServiceAnypick;
import server.ServiceParent;
import server.ServiceSRP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class Main {
	public static ArrayList<ServiceParent> serviceList;
	public static void main(String[] args){
		boolean isTest=false;
		if (isTest){
			try{

				System.out.println("Test RSA :");
				String input="222 111 1000000000000 10000 content";
				System.out.println("输入String: "+input);
				byte[] inputs=input.getBytes();
				System.out.println("String->byte[]: "+Arrays.toString(inputs));
				RSA rsa=new RSA();
				String publicKey=rsa.getPublicKey();
				//publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDE8eNvBliPL0+Q5a9PiIRbO/ZQ1GV9uqlaNdvzFqaxV4RD4rd2FstCLc+lHJ4dJFqQpfkM9OO1+En+YcY1viAdiFVqRpcwCafFHcB5Sj3kUzeKwnlv5sGIy4Few/xz3HsQDA3uVcnIdAFil8glCJ7kmfhKynhLijR7mQpWRg8wawIDAQAB";
				System.out.println("publicKey "+publicKey);
				String privateKey=rsa.getPrivateKey();
				//privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMTx428GWI8vT5Dlr0+IhFs79lDUZX26qVo12/MWprFXhEPit3YWy0Itz6Ucnh0kWpCl+Qz047X4Sf5hxjW+IB2IVWpGlzAJp8UdwHlKPeRTN4rCeW/mwYjLgV7D/HPcexAMDe5Vych0AWKXyCUInuSZ+ErKeEuKNHuZClZGDzBrAgMBAAECgYBE9HQvEUe8ilIPZ3vkGuZMk0gAsPz/8nUNEsJ58DpY6U1z/1NA02ZDO4ryQnzRs0LKVnJGqQ95TP+LQ2yEsJbJYodC50mICT1ghyBIUBTfLg45siD73gp8ticFliZXmcvNm+bM9yzq58TJBZ5YpFugQpzkWYwC5HxjX0neKUYM8QJBAPPbEtaNMuDU2zgqahC96iGyM0DRatSO4BrcIejIV2xUlufx+zX8sRCaTT+NaPWvZaM6aoqHC3eIgv9O/t26RyUCQQDOwL8IMDJeNChNJ7+9oDS19OkvjurdJ8L9fst96aHY9lmjVbWEgI5VJeAOW3cePtAMLADjoN9+4HQV75U5j4xPAkAHJsucozN0yIlNUmEd8JPnPRSnt/yME0ybPQ15iDaYJLrwPnaBlNGUjRHq5TjIy8YNTW9GdMNvU/+TgTuUQRxJAkB4OkFcI5rGiRH+e+TWAjlkrfyDuVn+LMcI3hi7KYcCQY8ymf6qBI/AF/xInQuRvPkCevwxYjVU9HTM5Lsj88OlAkEArMoeMyVYK72b+PUwPmdnMm8F4xsBUaZ25ipSv02NoA/O8HL3UDkeIZ2+YoMMMPsPfHPLISWuHGF2vr5USQlh5g==";
				System.out.println("privateKey "+privateKey);
				byte[] byte1=RSA.encrypt(inputs,RSA.getPublicKey(publicKey));
				System.out.println("RSA加密(byte[]->byte[]): "+Arrays.toString(byte1));
				String str = Base64.getEncoder().encodeToString(byte1);
				System.out.println("Base64加密(byte[]->String): "+str);
				System.out.println("------------------------网络传输-------------------------");
				//str="sE+hwJ6WDmEqn44fRyj07+9wPOGgLBvrj4tArPXKHOMU/UQrtuVLz8+FuArcHEXoEM8qlcVBRXOOYW0I14KU9UBz+VSgwVJkIejtfqDDkNe11gRARlkMi2o79EicjxTOpGOkAdnFq6vr5zBRQt14CKvf8G8zVj4vWGbgVThEv8s=";
				byte[] byte2=Base64.getDecoder().decode(str);
				System.out.println("Base64解密String->byte[]: "+Arrays.toString(byte2));
				byte[] byte3=RSA.decrypt(byte2,RSA.getPrivateKey(privateKey));
				System.out.println("RSA解密(byte[]->byte[]): "+Arrays.toString(byte3));
				String output=new String(byte3);
				System.out.println("输出(byte[]->String): "+output);
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

