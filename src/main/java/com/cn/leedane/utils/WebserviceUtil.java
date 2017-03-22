 package com.cn.leedane.utils;

import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
/**
 * webservice相关的工具类
 * @author LeeDane
 * 2016年7月12日 上午10:33:39
 * Version 1.0
 */
public class WebserviceUtil {

	private Call call;
	
	private static Service service = null;
		
	/**
	 * 初始化构造
	 * @param url webservice地址
	 */
	public WebserviceUtil(String url){
		service = WebserviceUtil.getInstance();
		try {
			call = (Call) service.createCall();
			call.setTargetEndpointAddress(url);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 调用 webservice
	 * @param method 调用方法
	 * @param args 参数
	 * @return
	 */
	public String call(String method,Object[] args) {
		try{
			if(call!=null){
				call.setOperationName(method);
				/*Object[] args2=new Object[args.length];
				for(int i=0;i<args.length;i++){
					args2[i]=args[i];
				}*/
				return (String)call.invoke(args);
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		return "";
	}
	
	/**
	 * 调用 webservice
	 * @param namespace 命名空间
	 * @param method  调用方法
	 * @param args 参数
	 * @return
	 */
	public String call(String namespace, String method,Object[] args) {
		try{
			if(call!=null){
				call.setOperationName(method);
				return (String)call.invoke(namespace, method, args);
			}
		}catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}
		return null;	
		
	}
	
	/**
	 * 调用 webservice
	 * @param method 方法名称
	 * @return
	 */
	public String call(String method) {
		try{
			if(call!=null){
				call.setOperationName(method);
				return (String)call.invoke(new Object[] {});
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		return "";
	}
	
	/**
	 * 调用 webservice
	 * @param method 方法名称
	 * @return
	 */
	public void invokeOneWay(String method,Object[] args) {		
		try{			
			if(call!=null){
				call.setOperationName(method);
				call.invokeOneWay(args);
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * 获得service对象
	 * @return
	 */
	public static synchronized Service getInstance() {
		if(service == null)
			service = new Service();
		return service;
	}
	
	public static void main(String[] args) {
		WebserviceUtil util = new WebserviceUtil("http://192.168.12.200:8093/services/EngineAPIWebService");
		String xmlString = "<?xml version='1.0' encoding='UTF-8'?>"+
								"<business>"+
									"<baseInfo>"+
										"<funcCode>PeerRelationship</funcCode>"+
										"<version>1.0</version>"+
									"</baseInfo>"+
									"<sendData>"+
										"<idCard>269940129682984412</idCard>"+
										"<ip></ip>"+
										"<userIdCard>123456</userIdCard>"+
										"<xm>小李</xm>"+
									"</sendData>"+
								"</business>";
		
		System.out.println(util.call("execute", new Object[]{xmlString}));
		//System.out.println(getTime("2015-08-11 18:23:22","yyyyMMddHHmmss"));
	}
}
