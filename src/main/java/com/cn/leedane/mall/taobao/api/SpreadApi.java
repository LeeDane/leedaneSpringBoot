package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.mall.taobao.CommUtil;
import com.cn.leedane.utils.StringUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkSpreadGetRequest;
import com.taobao.api.response.TbkSpreadGetResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 长链转短链接的api
 * @link {https://open.taobao.com/api.htm?spm=a2e0r.13193907.0.0.178c24ad4jd40j&docId=27832&docType=2}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class SpreadApi {

    /**
     * 转短链接
     * @param longLinks
     * @return
     * @throws ApiException
     */
    public static ShortLinkResult toShort(String[] longLinks) throws ApiException {
        ShortLinkResult shortLinkResult = new ShortLinkResult();
        if(longLinks == null || longLinks.length == 0)
            return shortLinkResult;

        TaobaoClient client = new DefaultTaobaoClient(CommUtil.url, CommUtil.appkey, CommUtil.secret);
        TbkSpreadGetRequest req = new TbkSpreadGetRequest();
        List<TbkSpreadGetRequest.TbkSpreadRequest> spreadRequests = new ArrayList<TbkSpreadGetRequest.TbkSpreadRequest>();
        TbkSpreadGetRequest.TbkSpreadRequest spreadRequest;
        for(int i = 0; i < longLinks.length; i++){
            String longLink = longLinks[i];

            //对没有http开始的，补上
            if(StringUtil.isNotNull(longLink) && !longLink.startsWith("http"))
                longLink = "http:" + longLink;

            spreadRequest = new TbkSpreadGetRequest.TbkSpreadRequest();
            spreadRequest.setUrl(longLink);
            spreadRequests.add(spreadRequest);
        }
        req.setRequests(spreadRequests);
        TbkSpreadGetResponse rsp = client.execute(req);
        String rspBody = rsp.getBody();
        if (StringUtil.isNull(rspBody))
            throw new ApiException("调用淘宝客api，结果返回空串");

        JSONObject resultObject = JSONObject.fromObject(rspBody);
        JSONObject responseObject = resultObject.optJSONObject("tbk_spread_get_response");

        //设置总数
        shortLinkResult.setTotal(responseObject.optInt("total_results"));

        JSONObject resultListObject = responseObject.optJSONObject("results");
        JSONArray tbk_spread = resultListObject.optJSONArray("tbk_spread");

        List<Result> results = new ArrayList<Result>();
        for (int i = 0; i < tbk_spread.size(); i++) {
            Result result = new Result();
            result.setContent(tbk_spread.optJSONObject(i).optString("content"));
            result.setErrCode(tbk_spread.optJSONObject(i).optString("err_msg"));
            results.add(result);
        }
        //设置结果集
        shortLinkResult.setResults(results);
        return shortLinkResult;
    }
    /**
     * 短链接转化结果
     */
    public static class ShortLinkResult{
        private int total;
        private List<Result> results = new ArrayList<Result>();

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotal() {
            return total;
        }

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }
    }

    /**
     * 短链接转化结果
     */
    public static class Result{
        private String content;
        private String errCode;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getErrCode() {
            return errCode;
        }

        public void setErrCode(String errCode) {
            this.errCode = errCode;
        }
    }
}