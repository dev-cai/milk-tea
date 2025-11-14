package com.milktea.utils;

import java.util.regex.Pattern;

/**
 * 输入验证工具类
 * @author MilkTea Team
 */
public class ValidationUtils {
    
    // SQL注入关键词
    private static final String[] SQL_KEYWORDS = {
        "select", "insert", "update", "delete", "drop", "create", "alter", 
        "exec", "execute", "union", "script", "javascript", "vbscript"
    };
    
    // XSS攻击模式
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "<script[^>]*>.*?</script>|javascript:|vbscript:|onload=|onerror=|onclick=", 
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    // 用户名格式：4-20位字母数字下划线
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    
    // 密码格式：6-20位，至少包含字母和数字
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$");
    
    // 手机号格式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    /**
     * 检查SQL注入
     * @param input 输入字符串
     * @return 是否包含SQL注入
     */
    public static boolean containsSqlInjection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        for (String keyword : SQL_KEYWORDS) {
            if (lowerInput.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查XSS攻击
     * @param input 输入字符串
     * @return 是否包含XSS攻击代码
     */
    public static boolean containsXss(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return XSS_PATTERN.matcher(input).find();
    }
    
    /**
     * 清理XSS攻击代码
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    public static String cleanXss(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replaceAll("<script[^>]*>.*?</script>", "")
                    .replaceAll("javascript:", "")
                    .replaceAll("vbscript:", "")
                    .replaceAll("onload=", "")
                    .replaceAll("onerror=", "")
                    .replaceAll("onclick=", "")
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;")
                    .replaceAll("'", "&#x27;");
    }
    
    /**
     * 验证用户名格式
     * @param username 用户名
     * @return 是否有效
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 验证密码格式
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 验证手机号格式
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 安全的字符串检查
     * @param input 输入字符串
     * @return 是否安全
     */
    public static boolean isSafeInput(String input) {
        return !containsSqlInjection(input) && !containsXss(input);
    }
}
