package com.cn.leedane.mall.model;

import com.cn.leedane.model.IDBean;

/**
 * 淘宝共享链接的实体bean
 * @author LeeDane
 * 2017年12月7日 下午12:16:36
 * version 1.0
 */
public class ProductPromotionLinkBean extends IDBean {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String shortLinkUrl; //短链接

    private String couponShortLinkUrl; //领券短链接

    private String qrCodeUrl; //二维码地址
    private String qrCouponCodeUrl; //二维码领券地址

    private String clickUrl; //长链接的地址

    private String couponLink; //领券长链接地址

    private String taoToken; //淘口令

    private String couponLinkTaoToken; //领券淘口令

    public String getShortLinkUrl() {
        return shortLinkUrl;
    }

    public void setShortLinkUrl(String shortLinkUrl) {
        this.shortLinkUrl = shortLinkUrl;
    }

    public String getCouponShortLinkUrl() {
        return couponShortLinkUrl;
    }

    public void setCouponShortLinkUrl(String couponShortLinkUrl) {
        this.couponShortLinkUrl = couponShortLinkUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public String getCouponLink() {
        return couponLink;
    }

    public void setCouponLink(String couponLink) {
        this.couponLink = couponLink;
    }

    public String getTaoToken() {
        return taoToken;
    }

    public void setTaoToken(String taoToken) {
        this.taoToken = taoToken;
    }

    public String getCouponLinkTaoToken() {
        return couponLinkTaoToken;
    }

    public void setCouponLinkTaoToken(String couponLinkTaoToken) {
        this.couponLinkTaoToken = couponLinkTaoToken;
    }

    public String getQrCouponCodeUrl() {
        return qrCouponCodeUrl;
    }

    public void setQrCouponCodeUrl(String qrCouponCodeUrl) {
        this.qrCouponCodeUrl = qrCouponCodeUrl;
    }
}