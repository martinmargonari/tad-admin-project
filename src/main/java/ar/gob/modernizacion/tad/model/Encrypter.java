package ar.gob.modernizacion.tad.model;

import org.apache.tomcat.util.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

/**
 * Created by MMargonari on 10/07/2017.
 */
public class Encrypter {
    private static String password = "Secret Passphrase";

    public static SecretKey generateKeyFromPassword(String password, byte[] saltBytes) throws GeneralSecurityException {

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), saltBytes, 100, 128);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    public static String decrypt(String encryptedData, String salt, String iv) throws Exception {
        byte[] saltBytes = DatatypeConverter.parseHexBinary(salt);
        byte[] ivBytes = DatatypeConverter.parseHexBinary(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec sKey = (SecretKeySpec) generateKeyFromPassword(password, saltBytes);

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, sKey, ivParameterSpec);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    public static String encrypt(String data, String salt, String iv) throws Exception {
        byte[] saltBytes = DatatypeConverter.parseHexBinary(salt);
        byte[] ivBytes = DatatypeConverter.parseHexBinary(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec sKey = (SecretKeySpec) generateKeyFromPassword(password, saltBytes);

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, sKey, ivParameterSpec);
        byte[] decValue = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeBase64String(decValue);
        return encryptedValue;
    }
}
