package com.cn.leedane.utils;
import java.security.Key;    

import javax.crypto.Cipher;    
import javax.crypto.KeyGenerator;    
import javax.crypto.SecretKey;    
import javax.crypto.SecretKeyFactory;    
import javax.crypto.spec.DESKeySpec;    
import javax.crypto.spec.IvParameterSpec;    
    
/**  
 * DES 算法       1972美国IBM研制，对称加密算法  
 * @author stone  
 * @date 2014-03-10 04:47:05  
 */    
public class DES {      
    // 算法名称    
    public static final String KEY_ALGORITHM = "DES";    
    // 算法名称/加密模式/填充方式    
    public static final String CIPHER_ALGORITHM_ECB = "DES/ECB/PKCS5Padding";    
    public static final String CIPHER_ALGORITHM_CBC = "DES/CBC/PKCS5Padding";    
        
    public static void main(String[] args) throws Exception {    
        /*  
         * 使用 ECB mode  
         * 密钥生成器 生成密钥  
         * ECB mode cannot use IV  
         */    
        byte[] key = generateKey();     
        byte[] encrypt = encrypt("effrdd".getBytes(), key);
        System.out.println("加密后："+new String(encrypt, "UTF8"));
        System.out.println("解密后：" +new String(decrypt(encrypt, key)));    
            
            
        /*  
         * 使用CBC mode  
         * 使用密钥工厂生成密钥，加密 解密  
         * iv: DES in CBC mode and RSA ciphers with OAEP encoding operation.  
         */    
        DESKeySpec dks = new DESKeySpec(key);    
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);    
        SecretKey secretKey = factory.generateSecret(dks);    
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);    
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(getIV()));    
        byte[] enc = cipher.doFinal("ggggg".getBytes()); //加密    
        System.out.println("加密后1："+new String(enc, "gb2312"));   
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(getIV()));    
        byte[] dec = cipher.doFinal(enc); // 解密    
        //System.out.println(new String(dec));   
        System.out.println("解密后1：" +new String(dec));    
    }    
        
    static byte[] getIV() {    
        String iv = "asdfivh7"; //IV length: must be 8 bytes long    
        return iv.getBytes();    
    }    
    
    /**  
     * 生成密钥  
     *   
     * @return  
     * @throws Exception  
     */    
    private static byte[] generateKey() throws Exception {    
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);    
        keyGenerator.init(56); //des 必须是56, 此初始方法不必须调用    
        SecretKey secretKey = keyGenerator.generateKey();    
        return secretKey.getEncoded();    
    }    
    
    /**  
     * 还原密钥  
     *   
     * @param key  
     * @return  
     * @throws Exception   
     */    
    private static Key toKey(byte[] key) throws Exception {    
        DESKeySpec des = new DESKeySpec(key);    
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);    
        SecretKey secretKey = keyFactory.generateSecret(des);    
        return secretKey;    
    }    
        
    /**  
     * 加密   
     * @param data 原文  
     * @param key  
     * @return 密文  
     * @throws Exception  
     */    
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {    
        Key k = toKey(key);    
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);    
        cipher.init(Cipher.ENCRYPT_MODE, k);    
        return cipher.doFinal(data);    
    }    
    /**  
     * 解密  
     * @param data 密文  
     * @param key  
     * @return 明文、原文  
     * @throws Exception  
     */    
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {    
        Key k = toKey(key);    
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);    
        cipher.init(Cipher.DECRYPT_MODE, k);    
        return cipher.doFinal(data);    
    }    
}  