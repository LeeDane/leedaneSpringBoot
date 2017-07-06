package com.cn.leedane.utils;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * DES3重加密和解密工具,可以对字符串进行加密和解密操作
 * @author LeeDane
 * 2017年7月3日 下午2:52:36
 * version 1.0
 */
public class Des3Utils {
  
	 //字符串默认键值
	  private static String strDefaultKey = ConstantsUtil.DES_SECRET_KEY;

	  /**
	   * 3重DES加密
	   * @param src
	   * @param DES_KEY 密钥长度不少于24的倍数位
	   * @return
	   */
	  public static String EncryptBy3DES(String src,String DES_KEY){
	      String result=null;
	      try {
	          SecureRandom secureRandom=new SecureRandom();
	          DESedeKeySpec sedeKeySpec=new DESedeKeySpec(DES_KEY.getBytes());

	          SecretKeyFactory secretKeyFactory=SecretKeyFactory.getInstance("DESede");
	          SecretKey key=secretKeyFactory.generateSecret(sedeKeySpec);

	          Cipher cipher=Cipher.getInstance("DESede/ECB/PKCS5Padding");
	          cipher.init(Cipher.ENCRYPT_MODE,key,secureRandom);

	          byte[] bytesresult=cipher.doFinal(src.getBytes());
	          result=new sun.misc.BASE64Encoder().encode(bytesresult);
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
	      return  result;
	  }

	  /**
	   * 3重DES解密
	   * @param src
	   * @param DES_KEY
	   * @return
	   */

	  public static String decryptBy3DES(String src,String DES_KEY){
	      String deresult=null;
	      try {
	          SecureRandom secureRandom=new SecureRandom();
	          DESedeKeySpec sedeKeySpec=new DESedeKeySpec(DES_KEY.getBytes());

	          SecretKeyFactory secretKeyFactory=SecretKeyFactory.getInstance("DESede");
	          SecretKey key = secretKeyFactory.generateSecret(sedeKeySpec);

	          Cipher cipher=Cipher.getInstance("DESede/ECB/PKCS5Padding");
	          cipher.init(Cipher.DECRYPT_MODE,key,secureRandom);

	          byte[] bytesresult=cipher.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(src));
	          deresult=new String(bytesresult);
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
	      return deresult;
	  }

	  /**
	   * main方法
	   * @param args
	   */
	  /*public static void main(String[] args) {
	    try {
	    	String sss = UUID.randomUUID().toString();
	    	System.out.println(UUID.randomUUID().toString().length());
	    	String test = "{name: \"leedane\", value: \"test回复时{}《》<>\".,;+=%$#@*&是否覆盖\", total: 123}";
	    	String af = EncryptBy3DES(test, sss);
	    	//调用加密的方法并打印查看结果   	
	    	System.out.println(af);
	    	System.out.println("长度是" +af.length());
	        //将加密后的结果放入解密的方法，由于是对称加密，因此加解密的密钥都是同一个        
	    	System.out.println(decryptBy3DES(af,sss));    
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	  }*/
	  
}