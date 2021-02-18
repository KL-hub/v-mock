package com.vmock.biz.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static final String DEFAULT_CHARSET = "utf-8";

    public AESUtil() {
    }

    public static String encrypt(String sSrc, String sKey) throws Exception {
        byte[] srcIv = new byte[16];
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(srcIv);
        cipher.init(1, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return new String(Base64.encodeBase64(encrypted, false), "utf-8");
    }

    public static String decrypt(String sSrc, String sKey) throws Exception {
        byte[] srcIv = new byte[16];
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(srcIv);
        cipher.init(2, skeySpec, iv);
        byte[] sSrcB = sSrc.getBytes("utf-8");
        sSrcB = Base64.decodeBase64(sSrcB);
        byte[] encrypted = cipher.doFinal(sSrcB);
        return new String(encrypted, "utf-8");
    }
}
