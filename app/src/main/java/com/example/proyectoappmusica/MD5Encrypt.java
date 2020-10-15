package com.example.proyectoappmusica;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Encrypt {
    public static String generator(String data) throws Exception {
        byte [] md5Input = data.getBytes();

        BigInteger md5Data = null;
        md5Data = new BigInteger(1, MD5Encrypt.encryptMD5(md5Input));
        String md5Str = md5Data.toString(16);
        if(md5Str.length() < 32){
            md5Str = 0 + md5Str;
        }
        return md5Str;
    }

    private static byte[] encryptMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }
}
