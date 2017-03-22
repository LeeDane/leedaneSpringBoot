package com.cn.leedane.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.DateUtil;

/**
 * main方法相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:12:24
 * Version 1.0
 */
public class MainTest {

	public static void main(String[] args) throws IOException {
		//int distance = 112456399 + 11;
		//System.out.println(((distance/1000) > 0 ? ((distance/1000000) > 0 ? (distance/1000000)+ "千公里": (distance/1000) + "公里"): "") +  ((distance%1000) > 0 ? (distance%1000) +"米": ""));
		/*System.out.println(DateUtil.DateToString(new Date(), "yyyyMM"));
		System.out.println(DateUtil.DateToString(new Date(), "yyyy年MM月"));
		System.out.println(DateUtil.DateToString(new Date(), "MM月dd日"));
		System.out.println(DateUtil.DateToString(new Date(), "dd日HH时"));*/
		
		/*String timeString = "Thu Sep 03 23:51:25 CST 2015";
		String ssString = DateUtil.formatLocaleTime(timeString, DateUtil.DEFAULT_DATE_FORMAT);
	
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 1);
		map.put("name", "dane");
		
		for(Entry<String, Object> m: map.entrySet()){
			m.setValue("ss");
		}
		
		System.out.println(ssString);*/
		
		/*ChatSquareHandler chatSquareHandler = (ChatSquareHandler) SpringUtil.getBean("chatSquareHandler");
		chatSquareHandler.addChat("1UNION8788888");*/
		
		/*RedisUtil util = RedisUtil.getInstance();
		//util.clearAll();
		util.expire("aaa", null, 10);
		System.out.println(util.hasKey("aaa"));
		System.out.println(util.keys(null));*/
		
		//创建一个URL的实例
		//使用URL读取网页内容
		/*try {
			testStream();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		testIntAndInteger();
		//SqlSessionTemplate sqlSessionTemplate = (SqlSessionTemplate) SpringUtil.getBean("sqlSessionTemplate");
		//Object o = sqlSessionTemplate.selectOne("select id from t_user");
		//sqlSessionTemplate
		//System.out.println("sqlSessionTemplate object="+ o);
		/*UserService<UserBean> userService = (UserService<UserBean>) SpringUtil.getBean("userService");
		UserBean user = userService.findById(1);
		System.out.println(user.getAccount());*/
		UserBean user = new UserBean();
		user.setAccount("dane");
		
		/*UserBean user1 = new UserBean();
		user1.setAccount("dane");*/
		UserBean user1 = user;
		
		System.out.println(user.equals(user1));
		HashMap<String, Object> obj1 = new HashMap<String, Object>();
		obj1.put(null, null);
		obj1.put("hehe", "dd");
		System.out.println(obj1.containsValue("12"));
		Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
		hashtable.put("12", 12);
		hashtable.put("hehe", "122");
		System.out.println(hashtable.contains("12"));
		
	}
	private static void testIntAndInteger() {
		int i = 10;
		Integer i1 = 10;
		System.out.println(i == i1);
		Integer i2 = new Integer(10);
		System.out.println(i1 == i2);
		Integer i3 = new Integer(10);
		System.out.println(i3 == i2);
		Integer i4 = 12;
		Integer i5 = 12;
		System.out.println(i4 == i5);
	}
	public static void testStream() throws Exception{
		//创建一个URL实例
				URL url =new URL("http://www.cnblogs.com/rocomp/p/4790340.html");
				InputStream is = url.openStream();//通过openStream方法获取资源的字节输入流
				InputStreamReader isr =new InputStreamReader(is,"UTF-8");//将字节输入流转换为字符输入流,如果不指定编码，中文可能会出现乱码
				BufferedReader br =new BufferedReader(isr);//为字符输入流添加缓冲，提高读取效率
				String data = br.readLine();//读取数据
				while((data = br.readLine()) != null){
					System.out.println(data);//输出数据
				}
				br.close();
				isr.close();
				is.close();
				
				String inFile = "G:\\java.rar";
				String outFile = "G:\\aa.rar";
				
				//获取源文件和目标文件的输入输出流  
		        FileInputStream fin = new FileInputStream(inFile);  
		        FileOutputStream fos = new FileOutputStream(outFile);  
		        try{  
		            //获取输入输出通道  
		            FileChannel fcin = fin.getChannel();  
		            FileChannel fcout = fos.getChannel();  
		            //创建1024字节的缓存区  
		            ByteBuffer buffer =  ByteBuffer.allocate(1024);  
		            long start = System.currentTimeMillis();  
		            while(true){  
		                //重设此缓冲区，使它可以接受读入的数据。Buffer对象中的limit=capacity;  
		                buffer.clear();  
		                //从输入通道中将数据读入到缓冲区  
		                int temp = fcin.read(buffer);  
		                if(temp == -1){  
		                    break;  
		                }  
		                //让缓冲区将新入读的输入写入另外一个通道.Buffer对象中的limit=position  
		                buffer.flip();  
		                //从输出通道中将数据写入缓冲区  
		                fcout.write(buffer);  
		            }  
		            System.out.println("总共耗时:"+(System.currentTimeMillis()-start)+"ms");  
		        } finally {  
		            if(fin!=null) fin.close();  
		            if(fos != null) fos.close();  
		        }  
	}
	
	private static void testImages() {
		Date d1 = DateUtil.stringToDate("2016-11-04 10:53:35");
		Date d2 = DateUtil.stringToDate("2016-11-04 10:48:01");
		System.out.println(DateUtil.leftMinutes(d1, d2));
		
	}

	private static void testStringBuffer() {
		/*StringBuffer buffer = new StringBuffer("hhfhfhf可减肥咖啡");
		buffer.*/
	}

	private static int getInt(){
		int i = 80;
		if(i > 0)
			return 10;
		return 0;
		
	}
}