package ar.marquez.passme.database;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class PasswordUtil {
    private static final int ITERATIONS = 1000;

    public static byte[] passwordEncrypt(char[] password, byte[] plaintext) {
        byte[] salt = new byte[8];
        Random random = new Random();
        random.nextBytes(salt);

        PBEKeySpec keySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey key = null;
        try {
            key = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATIONS);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("PBEWithMD5AndDES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        byte[] ciphertext = new byte[0];
        try {
            ciphertext = cipher.doFinal(plaintext);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(salt);
            baos.write(ciphertext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static byte[] passwordDecrypt(char[] password, byte[] ciphertext) {
        byte[] salt = new byte[8];
        ByteArrayInputStream bais = new ByteArrayInputStream(ciphertext);
        bais.read(salt, 0, 8);

        byte[] remainingCiphertext = new byte[ciphertext.length - 8];
        bais.read(remainingCiphertext, 0, ciphertext.length - 8);

        PBEKeySpec keySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey secretKey = null;
        try {
            secretKey = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATIONS);

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("PBEWithMD5AndDES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        try {
            return cipher.doFinal(remainingCiphertext);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptWrapper(String key, String encryptedPass) {
        byte[] ciphertext = Base64.decode(encryptedPass, Base64.DEFAULT);
        byte[] plaintext = passwordDecrypt(key.toCharArray(), ciphertext);
        return new String(plaintext);
    }

    public static String encryptWrapper(String key, String plaintext) {
        byte[] ciphertext = passwordEncrypt(key.toCharArray(), plaintext.getBytes());
        return Base64.encodeToString(ciphertext, Base64.DEFAULT);
    }
}

