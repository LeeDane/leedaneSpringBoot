package com.cn.leedane.utils;

import javax.servlet.http.HttpServletRequest;

public class CodeUtil {
    /**
     * 验证码校验
     * @param request
     * @param inputCode 获取用户输入的验证码
     * @return
     */
    public static boolean checkVerifyCode(HttpServletRequest request, String inputCode) {
        //获取生成的验证码
        String verifyCodeExpected = (String) request.getSession().getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
        if(StringUtil.isNull(inputCode) || StringUtil.isNull(verifyCodeExpected) || !inputCode.equals(verifyCodeExpected)) {
            return false;
        }
        return true;
    }
}
