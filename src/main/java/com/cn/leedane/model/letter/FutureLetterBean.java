package com.cn.leedane.model.letter;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.utils.StringUtil;

import java.util.Date;

/**
 * 未来的信件实体类
 * @author LeeDane
 * 2020年5月22日 下午22:39:24
 * Version 1.0
 */
public class FutureLetterBean extends RecordTimeBean{

    /**
     * 对收信人的称呼，可以为空
     */
    private String calla;

    /**
     * 信件的标题，如：给未来的自己，建议不为空
     */
    private String subject;

    /**
     * 信件的内容，大文本字段，不能为空，未到时间的信件将进行加密
     */
    private String content;

    /**
     * 信件的署名, 不能为空，为空将获取用户的ChinaName字段填充
     */
    private String sign;

    /**
     * 11位的手机号码，用在到期的时候短信进行提醒用户，可以为空
     */
    private long phone;

    /**
     * 信件到期时间。不能为空
     */
    private Date end;

    /**
     * 用于短信提醒时候的提示信息，为空将使用系统默认的，只有在phone字段不为空的时候才有效
     */
//    private String phoneMsg;

    /**
     * 未到期信件的密钥，不能为空
     */
    private String pwd;

    /**
     * 接收人的电子邮箱，不能为空，可以不是自己的电子邮箱，默认是自己的电子邮箱
     */
    private String email;

    /**
     * 通知方式，如短信，站内通知，邮件等
     */
    private String way;

    /**
     * 是否公开
     */
    private boolean publica;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getCalla() {
        return calla;
    }

    public void setCalla(String calla) {
        this.calla = calla;
    }

    public boolean isPublica() {
        return publica;
    }

    public void setPublica(boolean publica) {
        this.publica = publica;
    }
}
