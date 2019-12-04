package com.cn.leedane.utils;

/**
 * emoji表情的过滤的公共类
 * 感谢：http://www.qiqiworld.tk/2016/04/02/AndroidEmojiFilter/
 * @author LeeDane
 * 2016年7月12日 上午10:27:34
 * Version 1.0
 */
public class EmojiUtil {
	private static boolean isNotEmojiCharacter(char codePoint){
		return (codePoint == 0x0) ||
			(codePoint == 0x9) ||
			(codePoint == 0xA) ||
			(codePoint == 0xD) ||
			((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
			((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
			((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}

	/**
	 * 过滤emoji 或者 其他非文字类型的字符
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source){
		int len = source.length();
		StringBuilder buf = new StringBuilder(len);
		for (int i = 0; i < len; i++){
			char codePoint = source.charAt(i);
			if (isNotEmojiCharacter(codePoint)){
				buf.append(codePoint);
			}
		}
		return buf.toString();
	}
}
