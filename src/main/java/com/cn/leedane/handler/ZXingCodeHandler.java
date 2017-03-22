package com.cn.leedane.handler;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

import okio.Buffer;

import com.cn.leedane.enums.FileType;
import com.cn.leedane.utils.Base64ImageUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * zxing生成二维码
 * @author LeeDane
 * 2016年11月28日 上午11:04:17
 * Version 1.0
 */
public class ZXingCodeHandler {
	
	public static String IMAGETYPE = "png";
	
	private static final int BLACK = 0xff000000;
	/** 
     * 生成二维码(QRCode)图片 
     * @param content 
     * @param imgPath 
     */  
    public static String createQRCode(String str,int widthAndHeight) throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = BLACK;
				}
			}
		}
		
		String base64 = null;
		StringBuffer buffer = new StringBuffer();
		buffer.append(ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER);
		buffer.append(FileType.TEMPORARY.value);
		buffer.append(File.separator);
		buffer.append(DateUtil.DateToString(new Date(), "yyyyMMddHHmmss"));
		buffer.append("_");
		buffer.append(UUID.randomUUID().toString());
		buffer.append("_");
		buffer.append("login.jpg");
		File file = new File(buffer.toString());
		try {
			file.createNewFile();
			MatrixToImageWriter.writeToFile(matrix, "jpg", file);			
			base64 = Base64ImageUtil.convertImageToBase64(file, null);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			file.deleteOnExit();
		}
		return base64;
	}
}
