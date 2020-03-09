package com.cn.leedane.utils;
import java.security.Key;
import java.util.Map;

/**
 * http://blog.csdn.net/dounine/article/details/70160411
 * @author LeeDane
 * 2017年7月14日 下午5:42:08
 * version 1.0
 */
public class RSAKeyUtil {

	private static volatile RSAKeyUtil instance;
	
	private String publicKey = null;
	private String privateKey = null;
	
    private RSAKeyUtil() throws Exception {
    	Map<String, Key> keyMap = RSACoder.initKey();
    	publicKey  = RSACoder.getPublicKey(keyMap);
    	privateKey = RSACoder.getPrivateKey(keyMap);
    }

    public static synchronized RSAKeyUtil getInstance() {
        if (instance == null) {
            synchronized (RSAKeyUtil.class) {
                if (instance == null) {
                    try {
						instance = new RSAKeyUtil();
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
            }
        }
        return instance;
    }
    
    public String getPublicKey(){
    	return publicKey;
    }
    
    public String getPrivateKey(){
    	return privateKey;
    }
}