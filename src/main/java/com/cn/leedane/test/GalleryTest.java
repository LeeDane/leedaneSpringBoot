package com.cn.leedane.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.model.GalleryBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.GalleryService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserService;

/**
 * 图库相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:11:02
 * Version 1.0
 */
public class GalleryTest extends BaseTest {
	@Resource
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Resource
	private UserService<UserBean> userService;
	@Resource
	private GalleryService<GalleryBean> galleryService;
	

	@Test
	public void addGallery() throws Exception{		
		List<String> urls = new ArrayList<String>();
		urls.add("http://img5.duitang.com/uploads/item/201410/17/20141017203130_Uv4nA.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201206/16/20120616215822_xwM5i.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201407/01/20140701205708_SCKut.jpeg");
		urls.add("http://cdn.duitang.com/uploads/item/201411/12/20141112141422_KJUAB.jpeg");
		urls.add("http://cdn.duitang.com/uploads/item/201410/31/20141031185512_2ZhGX.thumb.700_0.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201407/13/20140713145251_hzjWT.thumb.700_0.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201410/26/20141026192857_CciiS.thumb.700_0.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201407/14/20140714173101_vzhf5.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201407/07/20140707080421_Y3WvE.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201307/26/20130726150918_8R58U.thumb.700_0.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201409/16/20140916144310_UKdHd.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201404/23/20140423150515_UGz2W.thumb.700_0.jpeg");
		urls.add("http://cdn.duitang.com/uploads/item/201411/15/20141115185032_eSuF4.thumb.700_0.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201408/13/20140813220859_kyskE.jpeg");
		urls.add("http://cdn.duitang.com/uploads/item/201412/13/20141213222229_aeeGs.thumb.700_0.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201110/22/20111022085558_i4sny.thumb.600_0.jpg");
		urls.add("http://cdn.duitang.com/uploads/item/201402/08/20140208215400_H2mhL.jpeg");
		urls.add("http://imgsrc.baidu.com/forum/pic/item/e2b36d65b3af1d987c1e714f.jpg");
		urls.add("http://img4q.duitang.com/uploads/item/201204/30/20120430021331_eUtLV.jpeg");
		urls.add("http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1206/25/c1/12116132_1340604742047.jpg");
		urls.add("http://img4.duitang.com/uploads/item/201412/04/20141204145746_VUREi.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201112/30/20111230191207_jzGvh.thumb.600_0.jpg");
		urls.add("http://img4.duitang.com/uploads/blog/201308/27/20130827000406_edvRA.thumb.600_0.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201411/21/20141121155247_iMFaT.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201304/10/20130410171759_M3ejc.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201407/30/20140730175904_s4Zm5.thumb.700_0.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201405/30/20140530003817_jZSnJ.jpeg");
		urls.add("http://imgcache.mysodao.com/img1/M06/F5/58/CgAPDE5DzYfHvDwoAAF2CKrQJ00346-30f9d06a.JPG");
		urls.add("http://img4.duitang.com/uploads/item/201212/24/20121224204622_fuLih.thumb.700_0.jpeg");
		urls.add("http://img4q.duitang.com/uploads/item/201403/22/20140322145431_8HP2N.thumb.700_0.jpeg");
		urls.add("http://imgsrc.baidu.com/forum/pic/item/c9cd0d46f21fbe099aae6a986b600c338644ad55.jpg");
		urls.add("http://picm.photophoto.cn/010/068/001/0680010230.jpg");
		urls.add("http://imgsrc.baidu.com/forum/pic/item/65fbfbedab64034f652e5cf9afc379310b551d69.jpg");
		urls.add("http://img5.duitang.com/uploads/item/201212/17/20121217120248_CsFym.thumb.700_0.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201202/23/20120223174650_YieV4.jpg");
		urls.add("http://img4.duitang.com/uploads/item/201206/09/20120609215955_CvTh4.thumb.600_0.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201407/18/20140718120930_FtxMf.jpeg");
		urls.add("http://img4.duitang.com/uploads/item/201407/13/20140713210546_2kUyc.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201411/16/20141116143348_fAC8L.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201410/18/20141018162812_uiwce.thumb.700_0.jpeg");
		urls.add("http://img5.duitang.com/uploads/item/201409/20/20140920173718_fZQYd.thumb.700_0.jpeg");
		urls.add("http://a2.att.hudong.com/38/59/300001054794129041591416974.jpg");
		urls.add("http://img.taopic.com/uploads/allimg/120403/57997-12040319145195.jpg");
		urls.add("http://www.pptbz.com/Soft/UploadSoft/200911/2009110522430362.jpg");
		urls.add("http://pic22.nipic.com/20120624/3934205_154516272343_2.jpg");
		urls.add("http://img.taopic.com/uploads/allimg/121009/235036-12100Z1522750.jpg");
		urls.add("http://img3.imgtn.bdimg.com/it/u=1758213975,2070880152&fm=21&gp=0.jpg");
		urls.add("http://img5.imgtn.bdimg.com/it/u=514535754,1264113209&fm=21&gp=0.jpg");
		urls.add("http://imgsrc.baidu.com/forum/pic/item/9740728b4710b912b25f360ac3fdfc039345227d.jpg");
		urls.add("http://a.hiphotos.baidu.com/zhidao/pic/item/5882b2b7d0a20cf479d7ed6275094b36adaf99c3.jpg");
		urls.add("http://img3.imgtn.bdimg.com/it/u=3553795728,1993953697&fm=21&gp=0.jpg");
		urls.add("http://a.hiphotos.baidu.com/zhidao/pic/item/472309f7905298223a7af857d5ca7bcb0b46d4dd.jpg");
		urls.add("http://img4.imgtn.bdimg.com/it/u=3437382537,2226233399&fm=21&gp=0.jpg");
		urls.add("http://img1.imgtn.bdimg.com/it/u=733284646,2340674283&fm=21&gp=0.jpg");
		urls.add("http://img5.imgtn.bdimg.com/it/u=685223551,2786407012&fm=21&gp=0.jpg");
		urls.add("http://b.hiphotos.baidu.com/zhidao/pic/item/f636afc379310a5512fbb3bdb64543a98326109f.jpg");
		urls.add("http://img0.imgtn.bdimg.com/it/u=1992613834,3471668034&fm=21&gp=0.jpg");
		urls.add("http://b.hiphotos.baidu.com/zhidao/pic/item/359b033b5bb5c9eab43182aad739b6003bf3b3bd.jpg");
		urls.add("http://img5.duitang.com/uploads/item/201404/18/20140418175840_T5MaX.thumb.700_0.jpeg");
		urls.add("http://b.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=21c67817b8a1cd1105e37a268922e4c4/37d12f2eb9389b5096d87c758735e5dde7116ef6.jpg");
		urls.add("http://wenwen.soso.com/p/20110507/20110507215055-983374822.jpg");
		urls.add("http://img5.duitang.com/uploads/item/201410/24/20141024162647_mhhS8.jpeg");
		urls.add("http://d.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=a36c5153eb24b899de69713e5e3631ad/50da81cb39dbb6fdd9329e750b24ab18972b3773.jpg");
		urls.add("http://img0.imgtn.bdimg.com/it/u=2612574618,251711442&fm=21&gp=0.jpg");
		urls.add("http://img2.ph.126.net/Ao9yL5uilA393nZ7rwWIAQ==/6597858116494767725.jpg");
		urls.add("http://img4.imgtn.bdimg.com/it/u=3720505377,1021126019&fm=21&gp=0.jpg");
		urls.add("http://img2.imgtn.bdimg.com/it/u=3088093270,989458624&fm=21&gp=0.jpg");
		urls.add("http://www.talkimages.cn/images/medium/20133227/tkm002_111950.jpg");
		urls.add("http://img0.ph.126.net/5PZ7K4zvX8YaMBU6vLPYQA==/6598128596354978781.jpg");
		urls.add("http://www.talkimages.cn/images/medium/20133063/tkf003_285434.jpg");
		urls.add("http://d.hiphotos.baidu.com/zhidao/pic/item/c2cec3fdfc039245fd7074678594a4c27c1e2594.jpg");
		urls.add("http://f.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=2d1025ad9e3df8dca6688795f8215ebd/7dd98d1001e939017ce0a6647aec54e737d1968d.jpg");
		
		/************************ 郑秀妍/郑秀晶/允儿 ***************************************/
		urls.add("http://img0.imgtn.bdimg.com/it/u=1591944052,1790391968&fm=21&gp=0.jpg");
		urls.add("http://img3.imgtn.bdimg.com/it/u=4181970511,1599816522&fm=11&gp=0.jpg");
		urls.add("http://img0.imgtn.bdimg.com/it/u=3107481650,829433050&fm=21&gp=0.jpg");
		urls.add("http://a.hiphotos.baidu.com/zhidao/pic/item/a8773912b31bb05108d69ed0347adab44bede05c.jpg");
		urls.add("http://upload.ct.youth.cn/2015/1020/1445319194725.jpg");
		urls.add("http://img3.douban.com/img/celebrity/large/48607.jpg");
		urls.add("http://cdn.duitang.com/uploads/item/201507/28/20150728132546_R4MAK.thumb.700_0.jpeg");
		urls.add("http://img2.duitang.com/uploads/item/201201/14/20120114021914_sQkUh.thumb.600_0.jpg");
		urls.add("http://www.fjsen.com/images/attachement/jpg/site1/2011-11-30/3133318085797759324.jpg");
		urls.add("http://ww.chunhui12.com/imgup8/1268481/98567481%20%281105%29.jpg");
		urls.add("http://img3.imgtn.bdimg.com/it/u=1364803186,2571957335&fm=11&gp=0.jpg");
		urls.add("http://img4.duitang.com/uploads/item/201412/24/20141224194045_tTZik.jpeg");
		urls.add("http://img3.doubanio.com/view/note/large/public/p31300877.jpg");
		urls.add("http://cdnweb.b5m.com/web/discuz/attachment/portal/201402/18/98b659e86e079548a194b30eb0d4511c.jpg");
		urls.add("http://img2.imgtn.bdimg.com/it/u=578955132,2274756128&fm=21&gp=0.jpg");
		urls.add("http://a.hiphotos.baidu.com/zhidao/pic/item/00e93901213fb80e6b3e39ba37d12f2eb83894eb.jpg");
		urls.add("http://img1.imgtn.bdimg.com/it/u=2116620289,2627111674&fm=21&gp=0.jpg");
		urls.add("http://g.hiphotos.baidu.com/zhidao/pic/item/c83d70cf3bc79f3dd60d428ebaa1cd11738b299b.jpg");
		urls.add("http://imgsrc.baidu.com/baike/pic/item/d089b986f69b185566096e5c.jpg");
		urls.add("http://h.hiphotos.baidu.com/zhidao/pic/item/58ee3d6d55fbb2fb2cdcac49494a20a44723dcf2.jpg");
		urls.add("http://photo.l99.com/bigger/21/1359916408697_8g50kc.jpg");
		urls.add("http://a3.att.hudong.com/85/27/01300000167299128005277094944.jpg");
		urls.add("http://imgsrc.baidu.com/forum/pic/item/10dfa9ec8a1363273ba925b2918fa0ec09fac7ef.jpg");
		urls.add("http://kpopn.com/wp-content/uploads/2010/10/YoonEunhye.jpg");
		urls.add("http://img4.duitang.com/uploads/item/201211/08/20121108114707_PhWd3.jpeg");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		urls.add("");
		
		UserBean user = userService.findById(1);
		String str;
		Map<String, Object> message;
		for(String url: urls){
			if(!StringUtil.isNull(url)){
				try {
					str = "{'path':'"+url+"', 'desc':'测试添加的网络图片'}";
					JSONObject jo = JSONObject.fromObject(str);
					message = galleryService.addLink(jo, user, null);
					if((boolean) message.get("isSuccess")){
						//System.out.println("加入图库成功："+url);
					}else{
						System.out.println("加入图库失败，失败原因："+message.get("message")+"------>"+url);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("加入图库失败，失败原因报错------>"+url);
					continue;
				}
				Thread.sleep(500);
			}
		}
		
		/*for(String url: urls){
			if(!StringUtil.isNull(url)){
				bean = new GalleryBean();
				bean.setCreateTime(new Date());
				bean.setCreateUser(userService.findById(1));
				bean.setDesc("测试方式上传的数据");
				bean.setPath(url);
				bean.setStatus(ConstantsUtil.STATUS_NORMAL);
				galleryService.save(bean);
			}
		}*/
		
	}
	
	@Test
	public void addGalleryTest() throws Exception{		
		List<String> urls = new ArrayList<String>();
		urls.add("http://192.168.42.21:8080/leedane/test/image/01.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/02.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/03.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/04(1).jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/2.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/3.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/5.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/6.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/7.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/8.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/9.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/9fcdce0dgw1eem095w7gkj20c80c8wfb.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/ac80f176jw1eeoh16taxfj20c8095dgr.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/ac80f176jw1eeoh17n0b6j20c807o0t6.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/eight.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/five.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/four.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/homepage_bg.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/nine.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/one.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/seven.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/six.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/three.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/two.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/b7d7fd67-fb3b-454d-aa6e-acf8ea7bf1dd_20141017203130_Uv4nA.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/6d6137cd-52cf-4c48-9533-c06bfd1d27e8_20120616215822_xwM5i.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/5d2ef069-27d7-4ed4-b795-57565dca1dc2_20140701205708_SCKut.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/4cbd3970-97fc-411f-9e6b-dcde8c873040_20140713145251_hzjWT.thumb.700_0.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/6bcf0775-1fd6-49e4-b23e-93256dd1d322_20141026192857_CciiS.thumb.700_0.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/4db3f629-483b-4afc-9918-3b58eae15c90_20140714173101_vzhf5.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/721b3a2b-2c30-41f9-be45-5796eaef214d_20140707080421_Y3WvE.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/07f3e503-5545-4b32-81e9-8da36abd79c5_20140916144310_UKdHd.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/b51aab92-7218-4231-8e2a-f9dec4b121f8_20130726150918_8R58U.thumb.700_0.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/1d313058-915c-4c49-bef4-da5b915681dc_20140813220859_kyskE.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/a37f0f59-7ec9-4eeb-98cb-5fbf4a1ca38d_20140423150515_UGz2W.thumb.700_0.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/faca16ab-2ccb-4867-854a-9ff5c143d1fb_20111022085558_i4sny.thumb.600_0.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/8dd62847-892f-4f71-88e8-d3a6c51d2fc9_20140208215400_H2mhL.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/92167e97-3827-4313-b31c-5e0668bb61d1_12116132_1340604742047.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/854d57f8-e7f9-430b-9edd-2ac395ea5414_20111230191207_jzGvh.thumb.600_0.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/63038630-0911-4398-9972-06142ac4e68c_20141204145746_VUREi.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/642b5b57-4969-4892-9401-730f46ef2178_20130827000406_edvRA.thumb.600_0.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/b5dbb32e-4c4f-4652-970f-2235eef5ccab_20141121155247_iMFaT.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/4461387e-39b5-4692-99c9-49ebc30af131_20140730175904_s4Zm5.thumb.700_0.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/852c7795-fb56-43ca-8302-1353ab389081_20140530003817_jZSnJ.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/ebfab2b1-b0fa-4f8a-966d-6b8eddca9592_20121224204622_fuLih.thumb.700_0.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/7db1c69b-3ea6-489e-8115-92f60a7d2f5d_c9cd0d46f21fbe099aae6a986b600c338644ad55.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/927a0593-9e99-4779-9eba-47d75c60d36b_0680010230.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/a9e50a05-f216-49e8-a9d8-7840feb05035_65fbfbedab64034f652e5cf9afc379310b551d69.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/5549a756-25bb-4302-9b57-54fcb651388d_20141116143348_fAC8L.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/65632530-4875-49f6-a502-d994b6207e94_20140718120930_FtxMf.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/05750002-9cf7-4f7d-9731-52809c3d736f_20140920173718_fZQYd.thumb.700_0.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/6008280e-1710-4741-89f2-7020b0a2f44d_20141018162812_uiwce.thumb.700_0.jpeg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/25e576d8-b03e-4215-819c-bdbe1fdd36f1_300001054794129041591416974.jpg");
		urls.add("http://192.168.42.21:8080/leedane/test/image/ae1fd5c4-5910-48ed-b57f-1a3623be5191_e2b36d65b3af1d987c1e714f.jpg");
		/*urls.add("http://192.168.42.21:8080/leedane/test/image/");
		urls.add("http://192.168.42.21:8080/leedane/test/image/");
		urls.add("http://192.168.42.21:8080/leedane/test/image/");
		urls.add("http://192.168.42.21:8080/leedane/test/image/");
		urls.add("http://192.168.42.21:8080/leedane/test/image/");
		urls.add("http://192.168.42.21:8080/leedane/test/image/");*/
		UserBean user = userService.findById(1);
		String str;
		Map<String, Object> message;
		for(String url: urls){
			try {
				str = "{'path':'"+url+"', 'desc':'测试添加的网络图片'}";
				JSONObject jo = JSONObject.fromObject(str);
				message = galleryService.addLink(jo, user, null);
				if((boolean) message.get("isSuccess")){
					//System.out.println("加入图库成功："+url);
				}else{
					System.out.println("加入图库失败，失败原因："+message.get("message")+"------>"+url);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("加入图库失败，失败原因报错------>"+url);
				continue;
			}
			Thread.sleep(500);
		}
		
	}
	
	@Test
	public void getGallery(){
		UserBean user = userService.findById(1);
		//String str = "{\"uid\": 1, \"pageSize\": 10, \"method\":\"lowloading\", \"last_id\":19, \"pic_size\":\"30x30\"}";
		String str = "{'uid':1,'method':'firstloading','pageSize':10}";
		JSONObject jo = JSONObject.fromObject(str);
		try {
			List<Map<String, Object>> ls = galleryService.getGalleryByLimit(jo, user, null);
			System.out.println("总数:" +ls.size());
			for(Map<String, Object> m: ls){
				System.out.println("ID:" +m.get("id"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
