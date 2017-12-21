package com.cn.leedane.utils;import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.xerial.snappy.Snappy;

/**
 * 压缩工具类
 * @author LeeDane
 * 2017年8月25日 下午5:10:44
 * version 1.0
 */
public class ZipUtil {
 
	/**
	 * 压缩字符
	 * @param html
	 * @return
	 */
	public static byte[] compressHtml(String source) {
		try {
			return Snappy.compress(source.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
 
	/**
	 * 解压字符
	 * @param bytes
	 * @return
	 */
	public static String decompressHtml(byte[] bytes) {
		try {
			return new String(Snappy.uncompress(bytes));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}