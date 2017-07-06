package com.cn.leedane.utils;


/**
 * http://blog.csdn.net/sinat_29508201/article/details/51648572
 * @author LeeDane
 * 2017年7月4日 下午6:11:21
 * version 1.0
 */
public class RSAUtils {
	/*private static final KeyPair keyPair = initKey();

    private static KeyPair initKey(){
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            SecureRandom random = new SecureRandom();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
            generator.initialize(1024, random);
            return generator.generateKeyPair();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    *//**
     * 生成public key
     * @return
     *//*
    public static String generateBase64PublicKey(){
        RSAPublicKey key = (RSAPublicKey)keyPair.getPublic();
        return new String(Base64Util.encode(key.getEncoded()));
    }

    *//**
     * 解密
     * @param string
     * @return
     *//*
    public static String decryptBase64(String string) {
        return new String(decrypt(Base64Util.decode(string.toCharArray())));
    }

    private static byte[] decrypt(byte[] string) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
            RSAPrivateKey pbk = (RSAPrivateKey)keyPair.getPrivate();
            cipher.init(Cipher.DECRYPT_MODE, pbk);
            byte[] plainText = cipher.doFinal(string);
            return plainText;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/
}
