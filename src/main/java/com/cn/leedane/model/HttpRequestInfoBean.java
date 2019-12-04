package com.cn.leedane.model;

import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.IpLocationUtil;
import com.cn.leedane.utils.StringUtil;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Http网络请求封装的bean
 */
public class HttpRequestInfoBean implements Serializable {
    private Logger logger = Logger.getLogger(getClass());

    private HttpServletRequest request;

    private String ip;

    private String browerInfo;

    private String location;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getIp() {
        if(StringUtil.isNull(ip) && request != null)
            ip = CommonUtil.getIPAddress(request);
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowerInfo() {
        if(StringUtil.isNull(browerInfo) && request != null)
            browerInfo = CommonUtil.getIPAddress(request);

        return browerInfo;
    }

    public void setBrowerInfo(String browerInfo) {
        this.browerInfo = browerInfo;
    }

    public String getLocation() {
        logger.info("获取到IP地址："+ getIp());
        if(StringUtil.isNotNull(getIp())){
            IpLocationUtil util = new IpLocationUtil();
            location = util.getLocaltion(getIp());
            logger.info("根据IP"+ getIp()+ "获取到地址："+ location);
        }
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
