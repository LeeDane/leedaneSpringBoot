package com.cn.leedane.test;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import com.cn.leedane.handler.UserHandler;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.cn.leedane.mapper.MoodMapper;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.observer.ConcreteWatched;
import com.cn.leedane.observer.ConcreteWatcher;
import com.cn.leedane.observer.Watched;
import com.cn.leedane.observer.Watcher;
import com.cn.leedane.observer.template.UpdateMoodTemplate;
import com.cn.leedane.service.MoodService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.Base64ImageUtil;
import com.cn.leedane.utils.ConstantsUtil;

/**
 * 心情相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:12:31
 * Version 1.0
 */
public class MoodTest extends BaseTest {
    private Logger logger = Logger.getLogger(getClass());

    @Resource
    private OperateLogService<OperateLogBean> operateLogService;

    @Resource
    private UserHandler userHandler;

    @Resource
    private MoodService<MoodBean> moodService;

    @Resource
    private MoodMapper moodMapper;

    @Test
    public void save() throws Exception{
        long start = System.currentTimeMillis();
        MoodBean moodBean = new MoodBean();
        moodBean.setStatus(ConstantsUtil.STATUS_NORMAL);
        moodBean.setCreateUserId(1);
        moodBean.setModifyUserId(1);
        moodBean.setContent("hahaee");
        moodBean.setLatitude(0.00);
        moodBean.setLongitude(0.00);
        logger.info("数量："+moodMapper.save(moodBean));

        long end = System.currentTimeMillis();
        logger.info("最终总耗时：" +(end - start) +"毫秒");
    }

    @Test
    public void sendDraft() throws Exception{
        long start = System.currentTimeMillis();
        for(int i = 0; i < 1000; i++){
            UserBean user = userHandler.getUserBean(1);
            String filePath = ConstantsUtil.getDefaultSaveFileFolder() +"liudehua.jpg";
            String base64 = Base64ImageUtil.convertImageToBase64(filePath, null);
            StringBuffer buffer = new StringBuffer();
            buffer.append(base64);
            String str = "{\"froms\":\"android客户端\", \"content\":\"今天天气不错哦!!!\", \"base64\":\""+buffer.toString()+"\"}";
            JSONObject jsonObject = JSONObject.fromObject(str);
            moodService.saveMood(jsonObject, user, ConstantsUtil.STATUS_NORMAL, null);
        }
        long end = System.currentTimeMillis();
        logger.info("最终总耗时：" +(end - start) +"毫秒");
    }

    @Test
    public void notifyMoodUpdate(){
        Watched watched = new ConcreteWatched();
        Watcher watcher = new ConcreteWatcher();
        watched.addWatcher(watcher);
        //int i = 10/0;
        watched.notifyWatchers(userHandler.getUserBean(1), new UpdateMoodTemplate());
    }
    @Test
    public void getMoodByLimit(){
        //MoodBean moodBean = moodMapper.findById(MoodBean.class, 279);
        UserBean user = userHandler.getUserBean(1);
        //String str = "{\"uid\": 1, \"pageSize\": 10, \"method\":\"lowloading\", \"last_id\":19, \"pic_size\":\"30x30\"}";
        String str = "{\"account\":\"leedane\",\"uid\":2,\"method\":\"firstloading\",\"login_mothod\":\"android\",\"no_login_code\":\"14480951808066e31568670e51be42bc7978cc2066ea060.521926355594616\",\"pageSize\":10}";
        JSONObject jo = JSONObject.fromObject(str);
        try {
            Map<String, Object> ls = moodService.rolling(jo, user, null);
            logger.info("总数:" +ls.size());
            for(Entry<String, Object> entry :ls.entrySet()){
                logger.info(entry.getKey() +":" +entry.getValue());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void updateMoodStatus(){
        UserBean user = userHandler.getUserBean(1);
        String str = "{\"mid\":1037, \"login_mothod\":\"android\",\"no_login_code\":\"14480951808066e31568670e51be42bc7978cc2066ea060.521926355594616\",\"pageSize\":10}";
        JSONObject jo = JSONObject.fromObject(str);
        try {
            Map<String, Object> message = moodService.updateMoodStatus(jo, 1, null, user);
            logger.info("isUpdate:" +message);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void query(){

    }
}
