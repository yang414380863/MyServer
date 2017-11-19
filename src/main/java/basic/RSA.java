package basic;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSA {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    private Map<String, Object> keyMap;

    public RSA() {
        try {
            initKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获得公钥
    public String getPublicKey() {
        try {
            //获得map中的公钥对象 转为key对象
            Key key = (Key) keyMap.get(PUBLIC_KEY);
            //byte[] publicKey = key.getEncoded();
            //编码返回字符串
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    //获得私钥
    public String getPrivateKey() {
        try {
            //获得map中的私钥对象 转为key对象
            Key key = (Key) keyMap.get(PRIVATE_KEY);
            //byte[] privateKey = key.getEncoded();
            //编码返回字符串
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    //map对象中存放公私钥
    private void initKey() throws Exception {
        //获得对象 KeyPairGenerator 参数 basic.RSA 1024个字节
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        //通过对象 KeyPairGenerator 获取对象KeyPair
        KeyPair keyPair = keyPairGen.generateKeyPair();

        //通过对象 KeyPair 获取RSA公私钥对象RSAPublicKey RSAPrivateKey
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //公私钥对象存入map中
        keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
    }


    //将base64编码后的公钥字符串转成PublicKey实例
    public static PublicKey getPublicKey(String publicKey) throws Exception{
        byte[ ] keyBytes=Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec=new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    //将base64编码后的私钥字符串转成PrivateKey实例
    public static PrivateKey getPrivateKey(String privateKey) throws Exception{
        byte[ ] keyBytes= Base64.getDecoder().decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    //公钥加密
    public static byte[] encrypt(byte[] content, PublicKey publicKey) throws Exception{
        Cipher cipher=Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding" android "RSA/ECB/NoPadding"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }
    //String->String版
	public static String encrypt(String content, PublicKey publicKey) throws Exception{
		return Base64.getEncoder().encodeToString(encrypt(content.getBytes(),publicKey));
	}
    //私钥解密
    public static byte[] decrypt(byte[] content, PrivateKey privateKey) throws Exception{
        Cipher cipher=Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }
	//String->String版
	public static String decrypt(String content, PrivateKey privateKey) throws Exception{
		return new String(decrypt(Base64.getDecoder().decode(content),privateKey));
	}
}