package com.cn.leedane.ueditor;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;

/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public final class MyConfigManager
/*     */ {
/*     */   private final String rootPath;
/*     */   private final String originalPath;
/*     */   //private static final String configFileName = "config.json";
/*  29 */   private String parentPath = null;
/*  30 */   private JSONObject jsonConfig = null;
/*     */   //private static final String SCRAWL_FILE_NAME = "scrawl";
/*     */  // private static final String REMOTE_FILE_NAME = "remote";
/*     */ 
/*     */   private MyConfigManager(String rootPath, String contextPath, String uri)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/*  41 */     rootPath = rootPath.replace("\\", "/");
/*     */ 
/*  43 */     this.rootPath = rootPath;
/*     */ 
/*  46 */     if (contextPath.length() > 0)
/*  47 */       this.originalPath = (this.rootPath + uri.substring(contextPath.length()));
/*     */     else {
/*  49 */       this.originalPath = (this.rootPath + uri);
/*     */     }
/*     */ 
/*  52 */     initEnv();
/*     */   }

			private MyConfigManager(String rootPath, String configJspPath)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/*  41 */     rootPath = rootPath.replace("\\", "/");
/*  43 */     this.rootPath = rootPath;
/*  49 */     this.originalPath = configJspPath;
/*  52 */     initEnv();
/*     */   }
/*     */ 
/*     */   public static MyConfigManager getInstance(String rootPath, String contextPath, String uri)
/*     */   {
/*     */     try
/*     */     {
/*  66 */       return new MyConfigManager(rootPath, contextPath, uri); } catch (Exception e) {
/*     */     }
/*  68 */     return null;
/*     */   }

			public static MyConfigManager getInstance(String rootPath, String configJspPath)
/*     */   {
/*     */     try
/*     */     {
/*  66 */       return new MyConfigManager(rootPath, configJspPath); } catch (Exception e) {
/*     */     }
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean valid()
/*     */   {
/*  75 */     return this.jsonConfig != null;
/*     */   }
/*     */ 
/*     */   public JSONObject getAllConfig()
/*     */   {
/*  80 */     return this.jsonConfig;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getConfig(int type)
/*     */   {
/*  86 */     Map<String, Object> conf = new HashMap<String, Object>();
/*  87 */     String savePath = null;
/*     */ 
/*  89 */     switch (type)
/*     */     {
/*     */     case 4:
/*  92 */       conf.put("isBase64", "false");
/*  93 */       conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("fileMaxSize")));
/*  94 */       conf.put("allowFiles", getArray("fileAllowFiles"));
/*  95 */       conf.put("fieldName", this.jsonConfig.getString("fileFieldName"));
/*  96 */       savePath = this.jsonConfig.getString("filePathFormat");
/*  97 */       break;
/*     */     case 1:
/* 100 */       conf.put("isBase64", "false");
/* 101 */       conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("imageMaxSize")));
/* 102 */       conf.put("allowFiles", getArray("imageAllowFiles"));
/* 103 */       conf.put("fieldName", this.jsonConfig.getString("imageFieldName"));
/* 104 */       savePath = this.jsonConfig.getString("imagePathFormat");
/* 105 */       break;
/*     */     case 3:
/* 108 */       conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("videoMaxSize")));
/* 109 */       conf.put("allowFiles", getArray("videoAllowFiles"));
/* 110 */       conf.put("fieldName", this.jsonConfig.getString("videoFieldName"));
/* 111 */       savePath = this.jsonConfig.getString("videoPathFormat");
/* 112 */       break;
/*     */     case 2:
/* 115 */       conf.put("filename", "scrawl");
/* 116 */       conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("scrawlMaxSize")));
/* 117 */       conf.put("fieldName", this.jsonConfig.getString("scrawlFieldName"));
/* 118 */       conf.put("isBase64", "true");
/* 119 */       savePath = this.jsonConfig.getString("scrawlPathFormat");
/* 120 */       break;
/*     */     case 5:
/* 123 */       conf.put("filename", "remote");
/* 124 */       conf.put("filter", getArray("catcherLocalDomain"));
/* 125 */       conf.put("maxSize", Long.valueOf(this.jsonConfig.getLong("catcherMaxSize")));
/* 126 */       conf.put("allowFiles", getArray("catcherAllowFiles"));
/* 127 */       conf.put("fieldName", this.jsonConfig.getString("catcherFieldName") + "[]");
/* 128 */       savePath = this.jsonConfig.getString("catcherPathFormat");
/* 129 */       break;
/*     */     case 7:
/* 132 */       conf.put("allowFiles", getArray("imageManagerAllowFiles"));
/* 133 */       conf.put("dir", this.jsonConfig.getString("imageManagerListPath"));
/* 134 */       conf.put("count", Integer.valueOf(this.jsonConfig.getInt("imageManagerListSize")));
/* 135 */       break;
/*     */     case 6:
/* 138 */       conf.put("allowFiles", getArray("fileManagerAllowFiles"));
/* 139 */       conf.put("dir", this.jsonConfig.getString("fileManagerListPath"));
/* 140 */       conf.put("count", Integer.valueOf(this.jsonConfig.getInt("fileManagerListSize")));
/*     */     }
/*     */ 
/* 145 */     conf.put("savePath", savePath);
/* 146 */     conf.put("rootPath", this.rootPath);
/*     */ 
/* 148 */     return conf;
/*     */   }
/*     */ 
/*     */   private void initEnv()
/*     */     throws FileNotFoundException, IOException
/*     */   {
/* 154 */     File file = new File(this.originalPath);
/*     */ 
/* 156 */     if (!file.isAbsolute()) {
/* 157 */       file = new File(file.getAbsolutePath());
/*     */     }
/*     */ 
/* 160 */     this.parentPath = file.getParent();
/*     */ 
/* 162 */     String configContent = readFile(getConfigPath());
/*     */     try
/*     */     {
/* 165 */       JSONObject jsonConfig = new JSONObject(configContent);
/* 166 */       this.jsonConfig = jsonConfig;
/*     */     } catch (Exception e) {
/* 168 */       this.jsonConfig = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getConfigPath()
/*     */   {
/* 174 */     return this.parentPath + File.separator + "config.json";
/*     */   }
/*     */ 
/*     */   private String[] getArray(String key)
/*     */   {
/* 179 */     JSONArray jsonArray = this.jsonConfig.getJSONArray(key);
/* 180 */     String[] result = new String[jsonArray.length()];
/*     */ 
/* 182 */     int i = 0; for (int len = jsonArray.length(); i < len; i++) {
/* 183 */       result[i] = jsonArray.getString(i);
/*     */     }
/*     */ 
/* 186 */     return result;
/*     */   }
/*     */ 
/*     */   private String readFile(String path)
/*     */     throws IOException
/*     */   {
/* 192 */     StringBuilder builder = new StringBuilder();
/*     */     try
/*     */     {
/* 196 */       InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
/* 197 */       BufferedReader bfReader = new BufferedReader(reader);
/*     */ 
/* 199 */       String tmpContent = null;
/*     */ 
/* 201 */       while ((tmpContent = bfReader.readLine()) != null) {
/* 202 */         builder.append(tmpContent);
/*     */       }
/*     */ 
/* 205 */       bfReader.close();
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */     {
/*     */     }
/*     */ 
/* 211 */     return filter(builder.toString());
/*     */   }
/*     */ 
/*     */   private String filter(String input)
/*     */   {
/* 218 */     return input.replaceAll("/\\*[\\s\\S]*?\\*/", "");
/*     */   }
/*     */ }

/* Location:           C:\Users\liyazhou\Downloads\a1b3ea95-60be-3e7c-a2b1-af921e4c4111.jar
 * Qualified Name:     com.baidu.ueditor.ConfigManager
 * JD-Core Version:    0.6.2
 */