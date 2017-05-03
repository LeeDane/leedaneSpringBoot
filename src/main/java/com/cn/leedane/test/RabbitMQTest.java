package com.cn.leedane.test;

import java.util.Date;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.rabbitmq.RecieveMessage;
import com.cn.leedane.rabbitmq.SendAndRecieveObject;
import com.cn.leedane.rabbitmq.SendMessage;

public class RabbitMQTest {
	//public static void main(String[] args) {
		/*SendAndRecieveObject object = new SendAndRecieveObject();
		object.setCreateTime(new Date());
		object.setFromUserID("12344");
		object.setMsg("hello, every oneww !");
		object.setToUserID("123");
		for(int i = 0; i < 10; i++){
			try {
				SendMessage sendMessage = new SendMessage("abc"+ SendAndRecieveObject.DEFAULT_SUFFIX,object);
				sendMessage.sendMsg();
				System.out.println("i="+i);
			} catch (ErrorException e) {
				e.printStackTrace();
			}
		}*/
		
		
		
		/*RecieveMessage recieveMessage = new RecieveMessage("abc"+SendAndRecieveObject.DEFAULT_SUFFIX);
		try {
			recieveMessage.getMsg();
			///System.out.println(recieveMessage.getMsg().getMsg());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	//}

}
