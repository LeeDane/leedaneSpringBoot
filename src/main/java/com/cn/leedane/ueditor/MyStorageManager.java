package com.cn.leedane.ueditor;

 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;

public class MyStorageManager{
	public static final int BUFFER_SIZE = 8192;
	public static State saveBinaryFile(byte[] data, String path)
	{
		File file = new File(path);
	/*     */ 
	/*  25 */     State state = valid(file);
	/*     */ 
	/*  27 */     if (!state.isSuccess()) {
	/*  28 */       return state;
	/*     */     }
	/*     */     try
	/*     */     {
	/*  32 */       BufferedOutputStream bos = new BufferedOutputStream(
	/*  33 */         new FileOutputStream(file));
	/*  34 */       bos.write(data);
	/*  35 */       bos.flush();
	/*  36 */       bos.close();
	/*     */     } catch (IOException ioe) {
	/*  38 */       return new BaseState(false, 4);
	/*     */     }
	/*     */ 
	/*  41 */     state = new BaseState(true, file.getAbsolutePath());
	/*  42 */     state.putInfo("size", data.length);
	/*  43 */     state.putInfo("title", file.getName());
	/*  44 */     return state;
	/*     */   }
	/*     */ 
	/*     */   public static State saveFileByInputStream(InputStream is, String path, long maxSize)
	/*     */   {
	/*  49 */     State state = null;
	/*     */ 
	/*  51 */     File tmpFile = getTmpFile();
	/*     */ 
	/*  53 */     byte[] dataBuf = new byte[2048];
	/*  54 */     BufferedInputStream bis = new BufferedInputStream(is, 8192);
	/*     */     try
	/*     */     {
	/*  57 */       BufferedOutputStream bos = new BufferedOutputStream(
	/*  58 */         new FileOutputStream(tmpFile), 8192);
	/*     */ 
	/*  60 */       int count = 0;
	/*  61 */       while ((count = bis.read(dataBuf)) != -1) {
	/*  62 */         bos.write(dataBuf, 0, count);
	/*     */       }
	/*  64 */       bos.flush();
	/*  65 */       bos.close();
	/*     */ 
	/*  67 */       if (tmpFile.length() > maxSize) {
	/*  68 */         tmpFile.delete();
	/*  69 */         return new BaseState(false, 1);
	/*     */       }
	/*     */ 
	/*  72 */       state = saveTmpFile(tmpFile, path);
	/*     */ 
	/*  74 */       if (!state.isSuccess()) {
	/*  75 */         tmpFile.delete();
	/*     */       }
	/*     */ 
	/*  78 */       return state;
	/*     */     }
	/*     */     catch (IOException localIOException) {
	/*     */     }
	/*  82 */     return new BaseState(false, 4);
	/*     */   }
	/*     */ 
	/*     */   public static State saveFileByInputStream(InputStream is, String path) {
	/*  86 */     State state = null;
	/*     */ 
	/*  88 */     File tmpFile = getTmpFile();
	/*     */ 
	/*  90 */     byte[] dataBuf = new byte[2048];
	/*  91 */     BufferedInputStream bis = new BufferedInputStream(is, 8192);
	/*     */     try
	/*     */     {
	/*  94 */       BufferedOutputStream bos = new BufferedOutputStream(
	/*  95 */         new FileOutputStream(tmpFile), 8192);
	/*     */ 
	/*  97 */       int count = 0;
	/*  98 */       while ((count = bis.read(dataBuf)) != -1) {
	/*  99 */         bos.write(dataBuf, 0, count);
	/*     */       }
	/* 101 */       bos.flush();
	/* 102 */       bos.close();
	/*     */ 
	/* 104 */       state = saveTmpFile(tmpFile, path);
	/*     */ 
	/* 106 */       if (!state.isSuccess()) {
	/* 107 */         tmpFile.delete();
	/*     */       }
	/*     */ 
	/* 110 */       return state;
	/*     */     } catch (IOException localIOException) {
	/*     */     }
	/* 113 */     return new BaseState(false, 4);
	/*     */   }
	/*     */ 
	/*     */   private static File getTmpFile() {
	/* 117 */     File tmpDir = FileUtils.getTempDirectory();
	/* 118 */     String tmpFileName = String.valueOf(Math.random() * 10000).replace(".", "");
	/* 119 */     return new File(tmpDir, tmpFileName);
	/*     */   }
	/*     */ 
	/*     */   private static State saveTmpFile(File tmpFile, String path) {
	/* 123 */     State state = null;
	/* 124 */     File targetFile = new File(path);
	/*     */ 
	/* 126 */     if (targetFile.canWrite())
	/* 127 */       return new BaseState(false, 2);
	/*     */     try
	/*     */     {
	/* 130 */       FileUtils.moveFile(tmpFile, targetFile);
	/*     */     } catch (IOException e) {
	/* 132 */       return new BaseState(false, 4);
	/*     */     }
	/*     */ 
	/* 135 */     state = new BaseState(true);
	/* 136 */     state.putInfo("size", targetFile.length());
	/* 137 */     state.putInfo("title", targetFile.getName());
	/*     */ 
	/* 139 */     return state;
	/*     */   }
	/*     */ 
	/*     */   private static State valid(File file) {
	/* 143 */     File parentPath = file.getParentFile();
	/*     */ 
	/* 145 */     if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
	/* 146 */       return new BaseState(false, 3);
	/*     */     }
	/*     */ 
	/* 149 */     if (!parentPath.canWrite()) {
	/* 150 */       return new BaseState(false, 2);
	/*     */     }
	/*     */ 
	/* 153 */     return new BaseState(true);
	/*     */   }
	/*     */ }

	/* Location:           C:\Users\liyazhou\Downloads\a1b3ea95-60be-3e7c-a2b1-af921e4c4111.jar
	 * Qualified Name:     com.baidu.ueditor.upload.StorageManager
	 * JD-Core Version:    0.6.2
	 */