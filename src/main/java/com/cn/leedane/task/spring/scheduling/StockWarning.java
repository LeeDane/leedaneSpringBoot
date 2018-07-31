package com.cn.leedane.task.spring.scheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 股票预警的任务
 * @author LeeDane
 * 2018年2月6日 下午1:55:57
 * version 1.0
 */
@Component("stockWarning")
public class StockWarning extends AbstractScheduling{
	private Logger logger = Logger.getLogger(getClass());
	
	private RedisUtil redis = new RedisUtil();
	/**
	 * 执行爬取方法
	 */
	@Override
	public void execute() throws SchedulerException {
		try {
			System.out.println("坎坎坷坷扩=="+DateUtil.DateToString(new Date()));
			JSONObject params = getParams();
			//System.out.println("参数："+ params);
			//String codes = ; 
			//double top = JsonUtil.getFloatValue(params, "top");
			//double low = JsonUtil.getFloatValue(params, "low");
			JSONArray codes = JSONArray.fromObject(JsonUtil.getStringValue(params, "codes"));//股票代码,支持多个
			List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
			ExecutorService threadpool = Executors.newCachedThreadPool();
			SingleTask dealTask;
			if(codes != null && codes.size() > 0){
				for(int i = 0; i < codes.size(); i++){
					dealTask = new SingleTask(codes.getJSONObject(i));
					futures.add(threadpool.submit(dealTask));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("股票预警的任务出现异常：execute()");
		}
	}
	
	
	private class SingleTask implements Callable<Boolean>{
		private JSONObject mJSONObject;
		private String code;
		
		public SingleTask(JSONObject jsonObject){
			this.mJSONObject = jsonObject;
		}
		@Override
		public Boolean call() throws Exception {
			
			Float top = JsonUtil.getFloatValue(mJSONObject, "top"); //获取最高预警值
			Float low = JsonUtil.getFloatValue(mJSONObject, "low");  //获取最低预警值
			code = JsonUtil.getStringValue(mJSONObject, "code");// 获取股票代码
			
			//获取请求连接
	        Connection con = Jsoup.connect("http://q.jrjimg.cn/?q=cn|s&i="+ code +"&n=hqs1&c=l&n=ahq&d=135944");
	        //请求头设置，特别是cookie设置
	        con.header("Accept", "text/html, application/xhtml+xml, */*"); 
	        con.header("Content-Type", "application/x-www-form-urlencoded");
	        con.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0))"); 
	        //con.header("Cookie", cookie);
	        //解析请求结果
			Document doc=con.get();
			//System.out.println(doc.toString());
			JSONObject json = JSONObject.fromObject(doc.body().text().replaceAll("var ahq=", ""));
			JSONArray array = json.getJSONArray("HqData").getJSONArray(0);
			//System.out.println(array);
			StringBuffer buffer = new StringBuffer();
			//第一个是完整的代码
			buffer.append("完整代码："+ array.getString(0) +"\n");
			//第二个是股票代码
			buffer.append("股票代码："+ array.getString(1) +"\n");
			//带三个是股票名称
			buffer.append("股票名称："+ array.getString(2) +"\n");
			//第四个是涨停价
			buffer.append("涨停价："+ array.getString(3) +"\n");
			//第五个是跌停价
			buffer.append("跌停价："+ array.getString(4) +"\n");
			//第六个是昨日收盘价
			buffer.append("昨日收盘价："+ array.getString(5) +"\n");
			//第七个是
			
			//第八个是
			
			//第九个是今日开盘价
			buffer.append("今日开盘价："+ array.getString(8) +"\n");
			//第10个是今日最高价
			buffer.append("最高价："+ array.getString(9) +"\n");
			//第11个是今日最低价
			buffer.append("最低价："+ array.getString(10) +"\n");
			//第12个是现价
			double now = array.getDouble(11);
			buffer.append("现价："+ array.getDouble(11) +"\n");
			//第13个是成交手数
			buffer.append("成交手："+ array.getString(12) +"手\n");
			//第14个是成交额万
			buffer.append("成交额："+ array.getString(13) +"万\n");
			//第15个
			
			//第16个
			
			//第17个是流通市值
			buffer.append("流通市值："+ array.getString(16) +"万\n");
			
			//第18个是总市值
			buffer.append("总市值："+ array.getString(17) +"万\n");
			//第19个
			
			//第20个当前的涨跌幅
			buffer.append("涨跌幅："+ array.getString(19) +"\n");
			//第21个是
			
			//第22个是
			
			//第23个是量比
			buffer.append("量比："+ array.getString(22) +"\n");
			//第25个是市净
			buffer.append("市净："+ array.getString(24) +"\n");
			logger.info(buffer.toString());
			
			//超过这个值就开始预警
			if(now >= top || now <= low ){
				long lastTime = 0;
				//获取上一次的预警时间
				if(redis.hasKey(code)){
					lastTime = Long.parseLong(redis.getString(code));
				}else{
					lastTime = System.currentTimeMillis();
				}
				//两次预警相隔的时间，分钟算,默认是30分钟
				int interval = JsonUtil.getIntValue(mJSONObject, "interval", 30);
				long nowTime = System.currentTimeMillis();
				//时间间隔太短
				System.out.println(nowTime - lastTime < 1000 * 60 * interval);
				System.out.println(nowTime - lastTime);
				if(nowTime - lastTime < 1000 * 60 * interval){
					return false;
				}
				
				String phone = JsonUtil.getStringValue(mJSONObject, "phone");
				StringBuffer msg = new StringBuffer(); //短信内容
				//发送短信通知
				if(StringUtil.isNotNull(phone) && phone.length() == 11){
					msg.append(array.getString(2)+(now >= top? "已经涨到您的预警值:"+ top: "已经跌破您的预警值:"+ low )+ ",当前股价是:"+ array.getDouble(11) +"元");
					/*TaobaoClient client = new DefaultTaobaoClient(SendNotificationImpl.URL, SendNotificationImpl.APPKEY, SendNotificationImpl.SECRET);
					AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
					req.setExtend("123456");
					req.setSmsType("normal");
					req.setSmsFreeSignName("LeeDane官方");
					req.setSmsParamString("{\"gupiao\":\""+ msg.toString()+ "\"}");
					req.setRecNum(phone);
					req.setSmsTemplateCode("SMS_124400087");
					AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
					logger.info(rsp.getBody());*/
					System.out.println("发送消息："+ DateUtil.DateToString(new Date()) + ", 内容是："+ msg.toString());
				}
				
				redis.addString(code, System.currentTimeMillis()+"");
			}
			return true;
		}
	}
	
	/*public static void main(String[] args) {
		TaobaoClient client = new DefaultTaobaoClient(SendNotificationImpl.URL, SendNotificationImpl.APPKEY, SendNotificationImpl.SECRET);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend("123456");
		req.setSmsType("normal");
		req.setSmsFreeSignName("LeeDane官方");
		req.setSmsParamString("{\"gupiao\":\"国盛金控已经涨到您的预警值当前股价是元\"}");
		req.setRecNum("13763059195");
		req.setSmsTemplateCode("SMS_124400087");
		AlibabaAliqinFcSmsNumSendResponse rsp;
		try {
			rsp = client.execute(req);
			System.out.println(rsp.getBody());
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
}
