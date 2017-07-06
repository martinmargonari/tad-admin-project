package ar.gob.modernizacion.tad.controllers;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import static com.sun.javafx.font.FontResource.SALT;

/**
 * Created by MMargonari on 05/07/2017.
 */
public class Encrypter {

    private static String keyString = "adkj@#$02#@adflkj)(*jlj@#$#@LKjasdjlkj<.,mo@#$@#kljlkdsu343";
    private static String ENC_ALG = "AES/CBC/PKCS5Padding";
    private static String KEY_ALG = "AES";
    private static String PASSWORD = "adkj@#$02#@adflkj)(*jlj@#$#@LKjasdjlkj<.,mo@#$@#kljlkdsu343";

    private static String key = "This is a fairly long phrase used to encrypt";
    private static String instance = "DES";
    private static String algorithm = "DES";
    private static String charsetName = "UTF8";

    public static String encrypt(String strEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance(instance);
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance(instance).generateSecret(new DESKeySpec(key.getBytes(charsetName))));
            return new String(Base64.encodeBase64(cipher.doFinal(strEncrypt.getBytes(charsetName))));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String strDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance(instance);
            cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance(instance).generateSecret(new DESKeySpec(key.getBytes(charsetName))));
            return new String(cipher.doFinal(Base64.decodeBase64(strDecrypt.getBytes(charsetName))));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    public static String encrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(ENC_ALG);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALG);

        KeySpec keySpec = new PBEKeySpec(PASSWORD.toCharArray(), SALT, 65536, 256);
        SecretKey key = keyFactory.generateSecret(keySpec);

        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        PBEParameterSpec paramSpec = new PBEParameterSpec(SALT, 0,ivSpec);

        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        byte[] encrypted = cipher.doFinal(text.getBytes());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(ENC_ALG);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALG);

        KeySpec keySpec = new PBEKeySpec(PASSWORD.toCharArray(), SALT, 65536, 256);
        SecretKey key = keyFactory.generateSecret(keySpec);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        PBEParameterSpec paramSpec = new PBEParameterSpec(SALT, 0, ivSpec);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }*/

    /**
     * Encrypts and encodes the Object and IV for url inclusion
     * @param obj
     * @return
     * @throws Exception
     */
    public static String[] encryptObject(Object obj) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(stream);
        try {
            // Serialize the object
            out.writeObject(obj);
            byte[] serialized = stream.toByteArray();

            // Setup the cipher and Init Vector
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[cipher.getBlockSize()];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Hash the key with SHA-256 and trim the output to 128-bit for the key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(keyString.getBytes());
            byte[] key = new byte[16];
            System.arraycopy(digest.digest(), 0, key, 0, key.length);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            // encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            // Encrypt & Encode the input
            byte[] encrypted = cipher.doFinal(serialized);
            byte[] base64Encoded = Base64.encodeBase64(encrypted);
            String base64String = new String(base64Encoded);
            String urlEncodedData = URLEncoder.encode(base64String,"UTF-8");

            // Encode the Init Vector
            byte[] base64IV = Base64.encodeBase64(iv);
            String base64IVString = new String(base64IV);
            String urlEncodedIV = URLEncoder.encode(base64IVString, "UTF-8");

            return new String[] {urlEncodedData, urlEncodedIV};
        }finally {
            stream.close();
            out.close();
        }
    }

    /**
     * Decrypts the String and serializes the object
     * @param base64Data
     * @param base64IV
     * @return
     * @throws Exception
     */
    public static Object decryptObject(String base64Data, String base64IV) throws Exception {
        // Decode the data
        byte[] encryptedData = Base64.decodeBase64(base64Data.getBytes());

        // Decode the Init Vector
        byte[] rawIV = Base64.decodeBase64(base64IV.getBytes());

        // Configure the Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(rawIV);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(keyString.getBytes());
        byte[] key = new byte[16];
        System.arraycopy(digest.digest(), 0, key, 0, key.length);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        // Decrypt the data..
        byte[] decrypted = cipher.doFinal(encryptedData);

        // Deserialize the object
        ByteArrayInputStream stream = new ByteArrayInputStream(decrypted);
        ObjectInput in = new ObjectInputStream(stream);
        Object obj = null;
        try {
            obj = in.readObject();
        }finally {
            stream.close();
            in.close();
        }
        return obj;
    }



}
