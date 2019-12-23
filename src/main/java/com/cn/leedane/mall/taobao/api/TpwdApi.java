package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.mall.taobao.CommUtil;
import com.cn.leedane.mall.taobao.other.TbkRebateOrderGetResponse;
import com.cn.leedane.utils.StringUtil;
import com.taobao.api.*;
import com.taobao.api.internal.util.TaobaoHashMap;
import com.taobao.api.request.TbkTpwdCreateRequest;
import com.taobao.api.response.TbkTpwdCreateResponse;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * 链接转成淘口令的api
 * @link {https://open.taobao.com/api.htm?spm=a2e0r.13193907.0.0.178c24ad926XS3&docId=31127&docType=2}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class TpwdApi {

    /**
     * 转淘口令
     * @param link
     * @param img
     * @param title
     * @return
     * @throws ApiException
     */
    public static String toTpwd(String link, String img, String title) throws ApiException {
        if(StringUtil.isNull(link))
            return null;

        //对没有http开始的，补上
        if(!link.startsWith("http"))
            link = "http:" + link;

        TaobaoClient client = new DefaultTaobaoClient(CommUtil.url, CommUtil.appkey, CommUtil.secret);
        TbkTpwdCreateRequest req = new TbkTpwdCreateRequest();
//		req.setUserId("123");
        if(StringUtil.isNotNull(title))
            req.setText(title);
        req.setUrl(link);

        if(StringUtil.isNotNull(img))
            req.setLogo(img);
//        req.setExt("{}");
        TbkTpwdCreateResponse rsp = client.execute(req);
        String rspBody = rsp.getBody();
        if (StringUtil.isNull(rspBody))
            throw new ApiException("调用淘宝客api，结果返回空串");
        return JSONObject.fromObject(rspBody).optJSONObject("tbk_tpwd_create_response").optJSONObject("data").optString("model");
    }

    public static class TbkRebateOrderGetRequest extends BaseTaobaoRequest<TbkRebateOrderGetResponse> {

        private String fields; //查询的字段

        private Date startTime; //开始时间
        private long span;
        private long pageNo;
        private long pageSize;
        private Long taskId;

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        public Long getTaskId() {
            return this.taskId;
        }

        public String getApiMethodName() {
            return "taobao.tbk.rebate.order.get";
        }

        public Map<String, String> getTextParams() {
            TaobaoHashMap txtParams = new TaobaoHashMap();
            txtParams.put("fields", this.fields);
            txtParams.put("start_time", this.startTime);
            txtParams.put("span", this.span);
            txtParams.put("page_no", this.pageNo);
            txtParams.put("page_size", this.pageSize);
            if(this.udfParams != null) {
                txtParams.putAll(this.udfParams);
            }
            return txtParams;
        }

        public Class<TbkRebateOrderGetResponse> getResponseClass() {
            return TbkRebateOrderGetResponse.class;
        }

        public void check() throws ApiRuleException {
            //RequestCheckUtils.checkNotEmpty(taskId, "taskId");
        }

        public void setFields(String fields) {
            this.fields = fields;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public void setSpan(long span) {
            this.span = span;
        }

        public void setPageNo(long pageNo) {
            this.pageNo = pageNo;
        }

        public void setPageSize(long pageSize) {
            this.pageSize = pageSize;
        }


    }
}