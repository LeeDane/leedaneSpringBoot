package com.cn.leedane.utils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class ThumbnailConvert {
	private String CMYK_COMMAND = "mogrify -colorspace RGB -quality 80 file1";//转换cmyk格式   
	  
	public void setCMYK_COMMAND(String file1) {   
	exeCommand(CMYK_COMMAND.replace("file1", file1));   
	}   
	  
	public boolean exeCommand(String cmd){   
	InputStreamReader ir = null;   
	LineNumberReader input = null;   
	try   
	{   
	//linux下java执行指令：Runtime.getRuntime().exec (String str);   
	Process process = Runtime.getRuntime().exec (cmd);   
	ir=new InputStreamReader(process.getInputStream());   
	input = new LineNumberReader (ir);   
	while ((input.readLine ()) != null){   
	}   
	ir.close();   
	input.close();   
	}   
	catch (java.io.IOException e){   
	System.err.println ("IOException " + e.getMessage());   
	return false;   
	}   
	return true;   
	}   

}
