package com.cn.leedane.handler;


import com.cn.leedane.mapper.util.CityMapper;
import com.cn.leedane.mapper.util.CountyMapper;
import com.cn.leedane.mapper.util.ProvinceMapper;
import com.cn.leedane.model.util.CityBean;
import com.cn.leedane.model.util.CountyBean;
import com.cn.leedane.model.util.ProvinceBean;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 省市县处理类（通过解析国家统计局网站实现）
 * http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/index.html
 * @author LeeDane
 * 2016年8月12日 上午10:25:38
 * Version 1.0
 */
@Component
public class CitysHandle {

    @Autowired
	private ProvinceMapper provinceMapper;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private CountyMapper countyMapper;
    /**
     * 处理省数据入口
     * @return
     */
	public boolean parseProvince(){
        String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/index.html";
        try {
            Document html = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
                    .timeout(60000)
                    .get();
            Elements trs = html.select(".provincetr");
            List<ProvinceBean> provinceBeanList = new ArrayList<>();
            for(int i = 0; i < trs.size(); i++){
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                for(int j = 0; j < tds.size(); j++){
                    ProvinceBean province = new ProvinceBean();
                    Element td = tds.get(j);
                    province.setCreateUserId(1);
                    province.setStatus(ConstantsUtil.STATUS_NORMAL);
                    province.setCreateTime(new Date());
                    province.setName(td.select("a").get(0).text());
                    province.setCode(td.select("a").get(0).attr("href").replace(".html", ""));
                    provinceBeanList.add(province);
                }
            }
            provinceMapper.batchSave(provinceBeanList);
        } catch (IOException e) {
            e.printStackTrace();
        }
	    return true;
    }

    /**
     * 处理市数据入口
     * @param pcode 省编码
     * @return
     */
    public boolean parseCity(String pcode) throws IOException {
        String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/"+ pcode +".html";
        Document html = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
                .timeout(60000)
                .get();
        Elements trs = html.select(".citytr");
        List<CityBean> cityBeanArrayList = new ArrayList<>();
        for(int i = 0; i < trs.size(); i++){
            Element tr = trs.get(i);
            Element tdCode = tr.select("td").get(0);
            Element tdName = tr.select("td").get(1);
            CityBean city = new CityBean();
            city.setCreateUserId(1);
            city.setCreateTime(new Date());
            city.setStatus(ConstantsUtil.STATUS_NORMAL);
            city.setPcode(pcode);
            city.setCode(tdCode.select("a").get(0).text());
            city.setName(tdName.select("a").get(0).text());
            cityBeanArrayList.add(city);
        }
        cityMapper.batchSave(cityBeanArrayList);
        return true;
    }

    /**
     * 处理县数据入口
     * @param ccode 市编码
     * @return
     */
    public boolean parseCounty(String ccode) throws IOException {
        String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/"+ ccode.substring(0, 2) +"/"+ ccode.substring(0, 4) +".html";
        Document html = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
                .timeout(60000)
                .get();
        Elements trs = html.select(".countytr");
        List<CountyBean> countyBeanList = new ArrayList<>();
        for(int i = 0; i < trs.size(); i++){
            Element tr = trs.get(i);
            Element tdCode = tr.select("td").get(0);
            Element tdName = tr.select("td").get(1);
            CountyBean county = new CountyBean();
            county.setCreateUserId(1);
            county.setCreateTime(new Date());
            county.setCcode(ccode);
            county.setCode(tdCode.select("a").size()> 0 ? tdCode.select("a").get(0).text(): tdCode.text());
            county.setName(tdName.select("a").size()> 0 ? tdName.select("a").get(0).text(): tdName.text());
            countyBeanList.add(county);
        }
        countyMapper.batchSave(countyBeanList);
        return true;
    }

	/*public static void main(String[] args) {
        String ccode = "440800000000";
        String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/"+ ccode.substring(0, 2) +"/"+ ccode.substring(0, 4) +".html";
        try {
            Document html = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
                    .timeout(10000)
                    .get();
            Elements trs = html.select(".countytr");
            List<CountyBean> countyBeanList = new ArrayList<>();
            for(int i = 0; i < trs.size(); i++){
                Element tr = trs.get(i);
                Element tdCode = tr.select("td").get(0);
                Element tdName = tr.select("td").get(1);
                CountyBean county = new CountyBean();
                county.setCreateUserId(1);
                county.setCreateTime(new Date());
                county.setCcode(ccode);
                county.setCode(tdCode.select("a").size()> 0 ? tdCode.select("a").get(0).text(): tdCode.text());
                county.setName(tdName.select("a").size()> 0 ? tdName.select("a").get(0).text(): tdName.text());
                countyBeanList.add(county);
            }
            int i = 0;
//            provinceMapper.batchSave(provinceBeanList);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}*/
}
