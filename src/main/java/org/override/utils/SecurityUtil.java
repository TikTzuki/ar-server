package org.override.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Log4j2
@Service
public class SecurityUtil {
    public static final String SECRECT_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    public static final String CRYPT_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String ALGORITHM = "AES";
    public static final int KEY_SIZE = 128;

    public static String encrypt(String input, String keyString, String ivString)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        SecretKey key = convertStringToSecretKey(keyString);
        IvParameterSpec iv = convertStringToParameterSpec(ivString);

        Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String cipherText, String keyString, String ivString)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        SecretKey key = convertStringToSecretKey(keyString);
        IvParameterSpec iv = convertStringToParameterSpec(ivString);

        Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }

    @Deprecated
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }

    public static String generateKey(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRECT_KEY_FACTORY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, KEY_SIZE);
        return convertSecretKeyToString(new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM));
    }

    public static String generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return convertParameterSpecToString(new IvParameterSpec(iv));
    }

    private static String convertSecretKeyToString(SecretKey secretKey) throws NoSuchAlgorithmException {
        byte[] rawData = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(rawData);
    }

    private static SecretKey convertStringToSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    private static String convertParameterSpecToString(IvParameterSpec parameterSpec) {
        byte[] rawData = parameterSpec.getIV();
        return Base64.getEncoder().encodeToString(rawData);
    }

    private static IvParameterSpec convertStringToParameterSpec(String encodedParameterSpec) {
        byte[] decodedParameterSpec = Base64.getDecoder().decode(encodedParameterSpec);
        return new IvParameterSpec(decodedParameterSpec);
    }

}
