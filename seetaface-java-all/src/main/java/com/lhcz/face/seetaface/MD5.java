package com.lhcz.face.seetaface;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class MD5 {

    private static final char[] hexCode = new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};


    public static byte[] md5(byte[] data) throws NoSuchAlgorithmException {
        byte[] secretBytes = null;
            secretBytes = MessageDigest.getInstance("MD5").digest(data);
            return secretBytes;
    }

    public static String md5ToString(byte[] data) throws NoSuchAlgorithmException {
        byte[] bytes = md5(data);
        return toHexString(bytes);

    }

    public static String md5ToString(String str) throws NoSuchAlgorithmException {
        byte[] bytes = md5(str.getBytes());
        return toHexString(bytes);

    }

    private static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }


}