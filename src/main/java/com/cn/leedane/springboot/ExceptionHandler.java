package com.cn.leedane.springboot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.ResponseCode;

/**
 * sprngmvc全局异常的处理类
 * @author LeeDane
 * 2017年3月30日 上午10:14:17
 * version 1.0
 */
@Component
public class ExceptionHandler implements HandlerExceptionResolver {
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception) {
		
		
		boolean isPageRequest = CommonUtil.isPageRequest(request, response);
		
		Map<String, Object> message = new HashMap<String, Object>();
        message.put("isSuccess", false);
        
		if(exception instanceof org.apache.shiro.authz.UnauthorizedException){//没有授权异常
			if(isPageRequest)
				return new ModelAndView("/403");
			 message.put("message", EnumUtil.getResponseValue(ResponseCode.没有操作权限.value));
			 message.put("responseCode", ResponseCode.没有操作权限.value);
		}else if(exception instanceof UnsupportedTokenException){//不支持token异常
			if(isPageRequest)
				return new ModelAndView("/lg");
			message.put("message", EnumUtil.getResponseValue(ResponseCode.请先登录.value));
			 message.put("responseCode", ResponseCode.请先登录.value);
			
		}else if(exception instanceof RE404Exception){//404异常
			if(isPageRequest)
				return new ModelAndView("/404");
			
			 message.put("message", exception.getMessage());
			 message.put("responseCode", ResponseCode.资源不存在.value);
		}else{
			StringPrintWriter strintPrintWriter = new StringPrintWriter();  
	        exception.printStackTrace(strintPrintWriter);
	        logger.info(strintPrintWriter.getString());
	        message.put("message", /*"服务器异常"*/strintPrintWriter.getString());//将错误信息传递给view  
		}
		
		ModelAndView mav = new ModelAndView();
		
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setAttributesMap(message);
		mav.setView(view);
		return mav;
        /*JSONObject jsonObject = JSONObject.fromObject(message);
		response.setCharacterEncoding("utf-8");
		//System.out.println("服务器返回:"+jsonObject.toString());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.append(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer != null)
				writer.close();
		}
		return null;*/
	}  
	
	
}

class StringPrintWriter extends PrintWriter{  
	  
    public StringPrintWriter(){  
        super(new StringWriter());  
    }  
     
    public StringPrintWriter(int initialSize) {  
          super(new StringWriter(initialSize));  
    }  
     
    public String getString() {  
          flush();  
          return ((StringWriter) this.out).toString();  
    }  
     
    @Override  
    public String toString() {  
        return getString();  
    }  
}  
