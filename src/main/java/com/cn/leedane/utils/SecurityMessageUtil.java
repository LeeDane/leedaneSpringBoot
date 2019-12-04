package com.cn.leedane.utils;

/**
 * 安全信息的处理工具
 *  @author LeeDane
 *  2019年6月28日 下午6:11:21
 *  version 1.0
 */
public class SecurityMessageUtil {
    /**
     * 定义所有常量
     */
    public static final String EMPTY = "";
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;

    /**
     * @Description 字符串向左截取
     * @date 2018/7/4
     * @param str
     * @param len
     * @return java.lang.String
     */
    public static String left(String str, int len) {
        if (StringUtil.isNull(str)) {
            return EMPTY;
        }
        if (len < ZERO) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(ZERO, len);

    }

    /**
     * @Description 字符串向右截取
     * @date 2018/7/4
     * @param str
     * @param len
     * @return java.lang.String
     */
    public static String right(String str, int len) {
        if (StringUtil.isNull(str)) {
            return EMPTY;
        }

        if (len < ZERO) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);

    }

    /**
     * @Description 根据不同名字的长度返回不同的显示数据
     * @author ShengLiu
     * @date 2018/7/4
     * @param str
     * @return java.lang.String
     */
    public static String checkNameLength(String str){
        if (StringUtil.isNull(str)) {
            return EMPTY;
        }
        if(str.length() == ONE) {
            return str;
        }else if(str.length() == TWO){
            return "*" + SecurityMessageUtil.right(str, ONE);
        }else if(str.length() == THREE){
            return SecurityMessageUtil.left(str, ONE) + "*" + SecurityMessageUtil.right(str, ONE);
        }else if(str.length() == FOUR){
            return SecurityMessageUtil.left(str, ONE) + "**" + SecurityMessageUtil.right(str, ONE);
        }
        return str;
    }
}
