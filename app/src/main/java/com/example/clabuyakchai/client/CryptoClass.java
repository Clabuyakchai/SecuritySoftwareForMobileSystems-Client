package com.example.clabuyakchai.client;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Clabuyakchai on 29.03.2018.
 */

public class CryptoClass {

    private static final String TAG = "CryptoClass";
    private static String SEED = "";
    private final static byte[] newIv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    public static void testKey(Context context){
        SEED = SaveKeyPreferences.getSecretKey(context);
        if(SEED == null){
            SEED = generateSecretKey();
            SaveKeyPreferences.setSecretKey(context, SEED);
            Log.d("encodeKey", SEED);
        }
        Log.d("encodeKey", SEED);
    }

    public static String generateSecretKey(){
        SecretKeySpec secretKey = null;

        try {
            //псевдорандомная последовательность по SHA1PRNG
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //генерируем ключ по алгоритму AES
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

            keyGenerator.init(192, secureRandom);
            secretKey = new SecretKeySpec((keyGenerator.generateKey()).getEncoded(), "AES");

        } catch (Exception e) {
            Log.e(TAG, "AES secret key spec error");
        }
        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT).toString().replaceAll("\n", "");
    }

    public static String hasString(String str){
        final String hashed = Hashing.sha256()
                .hashString(str, StandardCharsets.UTF_8)
                .toString().toUpperCase();

        return str + ",hash:" + hashed;
    }

    public static String encrypt(String cleartext) throws Exception {
        byte[] rawKey = SEED.getBytes();
        byte[] result = byteWork(rawKey, cleartext.getBytes());
        return Base64.encodeToString(result, Base64.DEFAULT);
    }

    public static String decrypt(String encrypted) throws Exception {
        byte[] rawKey = SEED.getBytes();
        byte[] enc = Base64.decode(encrypted, Base64.DEFAULT);
        byte[] result = byteWork(rawKey, enc);
        return new String(result);
    }

    private static byte[] byteWork(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(newIv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] mass = cipher.doFinal(clear);
        return mass;
    }
}
