package com.cn.leedane.test;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.model.ChatBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.ChatService;
import net.sf.json.JSONObject;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 聊天相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:09:19
 * Version 1.0
 */
public class ChatTest extends BaseTest {

    @Resource
    private UserHandler userHandler;

    @Resource
    private ChatService<ChatBean> chatService;


    @Test
    public void sendChat(){
        UserBean user = userHandler.getUserBean(5);
        String str = "{\"toUserId\":\"1\",\"content\":\"锄禾日当午，汗滴禾下土，谁知盘中餐，粒粒皆辛苦。---李白\"}";
        JSONObject jo = JSONObject.fromObject(str);
        try {
            Map<String, Object> ls = chatService.send(jo, user, null);
            logger.info("总数:" +ls.size());
            for(Entry<String, Object> entry :ls.entrySet()){
                logger.info(entry.getKey() +":" +entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
