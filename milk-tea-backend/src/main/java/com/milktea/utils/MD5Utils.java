package com.milktea.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 * @author MilkTea Team
 */
public class MD5Utils {
    
    /**
     * MD5加密
     * @param input 原始字符串
     * @return MD5哈希值
     */
    public static String encode(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    /**
     * 验证密码
     * @param plaintext 明文密码
     * @param hashed 哈希密码
     * @return 是否匹配
     */
    public static boolean matches(String plaintext, String hashed) {
        return encode(plaintext).equals(hashed);
    }
}
