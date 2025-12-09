package com.Bisag.QuizApp.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class AesUtil {
    private final int keySize;
    private final int iterationCount;
    private final Cipher cipher;
    public  String salt;
    public  String iv;
    
    public AesUtil(int keySize, int iterationCount) {
        this.keySize = keySize;
        this.iterationCount = iterationCount;
        this.salt=randomHex();
        this.iv=randomHex();
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw fail(e);
        }
    }
    
//    public  String encrypt(String salt,String iv, String passphrase,String input) throws NoSuchPaddingException, NoSuchAlgorithmException,
//    	    InvalidAlgorithmParameterException, InvalidKeyException,
//    	    BadPaddingException, IllegalBlockSizeException {
//    	    SecretKey key = generateKey(salt, passphrase);
//    	    cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(hex(iv)));
//    	    byte[] cipherText = cipher.doFinal(input.getBytes());
//    	    return Base64.encodeBase64String(cipherText);  
//    	}
    
    public  String encrypt(String salt,String iv, String passphrase,String input) throws NoSuchPaddingException, NoSuchAlgorithmException,
    InvalidAlgorithmParameterException, InvalidKeyException,
    BadPaddingException, IllegalBlockSizeException {
    SecretKey key = generateKey(salt, passphrase);
    cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(hex(iv)));
    byte[] cipherText = cipher.doFinal(input.getBytes());
    return java.util.Base64.getEncoder().encodeToString((iv + "::" + salt + "::" + Base64.encodeBase64String(cipherText)).getBytes());
   

}

public  String encryptwithtime(String salt,String iv, String passphrase,String input) throws NoSuchPaddingException, NoSuchAlgorithmException,
InvalidAlgorithmParameterException, InvalidKeyException,
BadPaddingException, IllegalBlockSizeException {
SecretKey key = generateKey(salt, passphrase);
cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(hex(iv)));
LocalDateTime currentDateTime = LocalDateTime.now();
long timestamp = currentDateTime.toEpochSecond(java.time.OffsetDateTime.now().getOffset());
input=input+"::"+String.valueOf(timestamp);
byte[] cipherText = cipher.doFinal(input.getBytes());
return java.util.Base64.getEncoder().encodeToString((iv + "::" + salt + "::" + Base64.encodeBase64String(cipherText)).getBytes());
}
    
    public String decrypt(String salt, String iv, String passphrase, String ciphertext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, base64(ciphertext));
            return new String(decrypted, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }catch (Exception e){
            return null;
        }
    }
    
    private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
        try {
            cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
            return cipher.doFinal(bytes);
        }
        catch (InvalidKeyException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            return null;
        }
    }
    
    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), iterationCount, keySize);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return key;
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
    }

    public static byte[] base64(String str) {
        return Base64.decodeBase64(str);
    }
    
    public static byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        }
        catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private IllegalStateException fail(Exception e) {
        return null;
    }
    
    public IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    
    public String randomHex() {
    	byte[] randombyte = new byte[128/8];
		new SecureRandom().nextBytes(randombyte);
		return Hex.encodeHexString(randombyte);
    }

    public static String encode(String input) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        AesUtil aesUtil = new AesUtil(128, 1000);
        String encodedtext = aesUtil.encrypt(aesUtil.salt,aesUtil.iv,"CD0M7ZMGXIphBqB3USmt13h7pxHtaMgL",input);
        return encodedtext;
    }
    public static String encodewithtime(String input) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        AesUtil aesUtil = new AesUtil(128, 1000);
        String encodedtext = aesUtil.encryptwithtime(aesUtil.salt,aesUtil.iv,"CD0M7ZMGXIphBqB3USmt13h7pxHtaMgL",input);
        return encodedtext;
    }
}

