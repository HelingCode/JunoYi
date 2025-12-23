package com.junoyi.framework.web.sql;

import com.junoyi.framework.core.utils.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * SQL 注入检测工具类
 *
 * @author Fan
 */
public class SqlInjectionUtils {

    private SqlInjectionUtils() {}

    /**
     * SQL 关键词（小写）
     */
    private static final Set<String> SQL_KEYWORDS = new HashSet<>(Arrays.asList(
            "select", "insert", "update", "delete", "drop", "truncate", "alter",
            "create", "exec", "execute", "xp_", "sp_", "0x", "union", "join",
            "declare", "cast", "convert", "char", "nchar", "varchar", "nvarchar",
            "waitfor", "delay", "shutdown", "grant", "revoke"
    ));

    /**
     * 危险字符和模式
     */
    private static final Pattern[] DANGEROUS_PATTERNS = {
            // SQL 注释
            Pattern.compile("--"),
            Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL),
            // 单引号攻击
            Pattern.compile("'\\s*(or|and)\\s*'", Pattern.CASE_INSENSITIVE),
            Pattern.compile("'\\s*(or|and)\\s+\\d+\\s*=\\s*\\d+", Pattern.CASE_INSENSITIVE),
            Pattern.compile("'\\s*(or|and)\\s+\\w+\\s*=\\s*\\w+", Pattern.CASE_INSENSITIVE),
            // 1=1 / 1'='1 类型
            Pattern.compile("\\d+\\s*=\\s*\\d+"),
            Pattern.compile("'\\d+'\\s*=\\s*'\\d+'"),
            // UNION 注入
            Pattern.compile("union\\s+(all\\s+)?select", Pattern.CASE_INSENSITIVE),
            // 堆叠查询
            Pattern.compile(";\\s*(select|insert|update|delete|drop|truncate)", Pattern.CASE_INSENSITIVE),
            // 时间盲注
            Pattern.compile("sleep\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("benchmark\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("waitfor\\s+delay", Pattern.CASE_INSENSITIVE),
            // 报错注入
            Pattern.compile("extractvalue\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("updatexml\\s*\\(", Pattern.CASE_INSENSITIVE),
            // 系统函数
            Pattern.compile("load_file\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("into\\s+(outfile|dumpfile)", Pattern.CASE_INSENSITIVE),
            // 十六进制编码
            Pattern.compile("0x[0-9a-fA-F]+"),
            // 特殊字符组合
            Pattern.compile("\\|\\|"),
            Pattern.compile("&&")
    };

    /**
     * 自定义关键词（可通过配置追加）
     */
    private static Set<String> customKeywords = new HashSet<>();

    /**
     * 设置自定义关键词
     */
    public static void setCustomKeywords(Set<String> keywords) {
        customKeywords = keywords;
    }

    /**
     * 检测是否包含 SQL 注入
     *
     * @param value 待检测内容
     * @return true 包含 SQL 注入
     */
    public static boolean containsSqlInjection(String value) {
        if (StringUtils.isBlank(value)) return false;

        String lowerValue = value.toLowerCase();

        // 检测危险模式
        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(value).find()) return true;
        }

        // 检测 SQL 关键词组合
        if (containsSqlKeywordCombination(lowerValue)) return true;

        // 检测自定义关键词
        for (String keyword : customKeywords) {
            if (lowerValue.contains(keyword.toLowerCase())) return true;
        }

        return false;
    }

    /**
     * 检测是否包含危险的 SQL 关键词组合
     */
    private static boolean containsSqlKeywordCombination(String value) {
        // 检测多个 SQL 关键词组合出现
        int keywordCount = 0;
        for (String keyword : SQL_KEYWORDS) {
            // 使用单词边界检测，避免误判
            if (Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
                keywordCount++;
                if (keywordCount >= 2) return true;
            }
        }

        // 单个危险关键词 + 特殊字符
        if (keywordCount >= 1) {
            if (value.contains("'") || value.contains("\"") || value.contains(";") || value.contains("--")) {
                return true;
            }
        }

        return false;
    }

    /**
     * 清理 SQL 注入内容
     *
     * @param value 待处理内容
     * @return 清理后的内容
     */
    public static String clean(String value) {
        if (StringUtils.isBlank(value)) return value;

        // 移除 SQL 注释
        value = value.replaceAll("--.*", "");
        value = value.replaceAll("/\\*.*?\\*/", "");

        // 转义单引号
        value = value.replace("'", "''");

        // 移除分号（防止堆叠查询）
        value = value.replace(";", "");

        // 移除危险函数调用
        value = value.replaceAll("(?i)sleep\\s*\\([^)]*\\)", "");
        value = value.replaceAll("(?i)benchmark\\s*\\([^)]*\\)", "");
        value = value.replaceAll("(?i)load_file\\s*\\([^)]*\\)", "");

        return value;
    }

    /**
     * 获取检测到的危险模式描述
     *
     * @param value 待检测内容
     * @return 危险模式描述，未检测到返回 null
     */
    public static String getDetectedPattern(String value) {
        if (StringUtils.isBlank(value)) return null;

        for (Pattern pattern : DANGEROUS_PATTERNS) {
            if (pattern.matcher(value).find()) {
                return "匹配模式: " + pattern.pattern();
            }
        }

        String lowerValue = value.toLowerCase();
        for (String keyword : SQL_KEYWORDS) {
            if (Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE).matcher(value).find()) {
                if (value.contains("'") || value.contains(";")) {
                    return "SQL关键词 + 特殊字符: " + keyword;
                }
            }
        }

        return null;
    }
}
