package com.cn.leedane.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.JsoupUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.bg)
public class BlogController extends BaseController{

	protected final Log log = LogFactory.getLog(BlogController.class);
	
	/**
	 * 系统级别的缓存对象
	 */
	@Autowired
	private SystemCache systemCache;
	
	
	private BlogBean blog;//博客实体
		
	@Autowired
	private BlogService<BlogBean> blogService;


	/**
	 * 发布博客
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/blog", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> releaseBlog(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		JSONObject json = getJsonFromMessage(message);
		
		//获取登录用户，获取不到用管理员账号
		UserBean user = getUserFromMessage(message);
		if(user == null)
			user = OptionUtil.adminUser;
		
		/**
		 * 是否有主图
		 */
		boolean hasImg = JsonUtil.getBooleanValue(json, "has_img");
		
		/**
		 * 是否要自动截取摘要摘要
		 */
		boolean hasDigest = JsonUtil.getBooleanValue(json, "has_digest");
		blog = new BlogBean();
		blog.setTitle(JsonUtil.getStringValue(json, "title"));
		String content = JsonUtil.getStringValue(json, "content");
		blog.setContent(content);
		blog.setTag(JsonUtil.getStringValue(json, "tag"));
		blog.setFroms(JsonUtil.getStringValue(json, "froms"));
		
		//非管理员发布的文章需要审核
		int status = JsonUtil.getIntValue(json, "status");

		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
		if(!currentUser.hasRole(RoleController.ADMIN_ROLE_CODE) && status == ConstantsUtil.STATUS_NORMAL){
			status = ConstantsUtil.STATUS_AUDIT;
		}
		blog.setStatus(status);
		
		blog.setCanComment(JsonUtil.getBooleanValue(json, "can_comment"));
		blog.setCanTransmit(JsonUtil.getBooleanValue(json, "can_transmit"));
		blog.setCategory(JsonUtil.getStringValue(json, "category"));
		
		String imgUrl = JsonUtil.getStringValue(json, "img_url");
		String digest = "";
		if(hasImg){
			//判断是否有指定的图片，没有的话会自动解析内容中的第一张图片的src的值作为地址
			if(StringUtil.isNull(imgUrl)){
				Document h = Jsoup.parse(content);
				Elements a = h.getElementsByTag("img");
				if(a != null && a.size() > 0)
					imgUrl= a.get(0).attr("src");
			}
			
			if(StringUtil.isNotNull(imgUrl))
				//获取主图信息
				blog.setHasImg(hasImg);
			blog.setImgUrl(imgUrl);
			
			/**
			 * 非链接的字符串
			 */
			if(!StringUtil.isLink(imgUrl)){
				JsoupUtil.getInstance().base64ToLink(imgUrl, user.getAccount());
				
			}
			//将base64位的图片保存在数据在本地
			/*String path = "F:/upload";
			File file = new File(path);
			System.out.println("==="+path);
			if(!file.exists()){
				file.mkdir();
			}
			String account = user != null ? user.getAccount() : "admin";
			String filePath = path + "/" + account +"_"+System.currentTimeMillis()+ (int)Math.random()*1000 +".png";
			System.out.println(ImageUtil.convertBase64ToImage(filePath, imgUrl));*/
			
		}
		
		blog.setCreateUserId(JsonUtil.getIntValue(json, "create_user_id", user.getId()));
		blog.setCreateTime(new Date());
		
		if(hasDigest){
			digest = JsoupUtil.getInstance().getDigest(content, 0, 100);
		}else{
			digest = JsonUtil.getStringValue(json, "digest");
		}
		
		blog.setRecommend(JsonUtil.getBooleanValue(json, "is_recommend"));
		
		blog.setDigest(digest);
		if(JsonUtil.getIntValue(json, "bid") > 0){
			blog.setId(JsonUtil.getIntValue(json, "bid"));
		}
		message.putAll(blogService.addBlog(blog, user));   
		return message.getMap();
	}
	
	/**
	 * 删除博客
	 * @return
	 */
	@RequestMapping(value = "/blog", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deleteBlog(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
				return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(blogService.deleteById(getJsonFromMessage(message), request, getUserFromMessage(message)));
		return message.getMap();
	}
	
	/**
	 * 获得分页的Blog
	 * @return
	 */
	@RequestMapping(value = "/blogs", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(@RequestParam(value="pageSize", required = false) Integer pageSize,
				@RequestParam(value="last_id", required = false) Integer lastId,
				@RequestParam(value="first_id", required = false) Integer firstId,
				@RequestParam(value="method", required = true) String method,
				HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		/*JSONObject json = getJsonFromMessage(message);
		int pageSize = JsonUtil.getIntValue(json, "pageSize"); //每页的大小
		int lastId = JsonUtil.getIntValue(json, "last_id");
		int firstId = JsonUtil.getIntValue(json, "first_id");
		
		//执行的方式，现在支持：uploading(向上刷新)，lowloading(向下刷新)，firstloading(第一次刷新)
		String method = JsonUtil.getStringValue(json, "method");*/
		List<Map<String,Object>> r = new ArrayList<Map<String,Object>>();
		StringBuffer sql = new StringBuffer();
		System.out.println("执行的方式是："+method +",pageSize:"+pageSize+",lastId:"+lastId+",firstId:"+firstId);
		//下刷新
		if(method.equalsIgnoreCase("lowloading")){
			sql.append("select b.id, b.img_url, b.title, b.has_img, b.tag, date_format(b.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" , b.digest, b.froms, b.create_user_id, b.category, u.account ");
			sql.append(" from "+DataTableType.博客.value+" b inner join "+DataTableType.用户.value+" u on b.create_user_id = u.id ");
			sql.append(" where b.status = ?  and b.id < ? order by b.id desc limit 0,?");
			r = blogService.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, lastId, pageSize);
			
		//上刷新
		}else if(method.equalsIgnoreCase("uploading")){
			sql.append("select b.id, b.img_url, b.title, b.has_img, b.tag, date_format(b.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" , b.digest, b.froms, b.create_user_id, b.category, u.account ");
			sql.append(" from "+DataTableType.博客.value+" b inner join "+DataTableType.用户.value+" u on b.create_user_id = u.id ");
			sql.append(" where b.status = ? and b.id > ?  limit 0,?");
			r = blogService.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, firstId, pageSize);
			
		//第一次刷新
		}else if(method.equalsIgnoreCase("firstloading")){
			sql.append("select b.id, b.img_url, b.title, b.has_img, b.tag, date_format(b.create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sql.append(" , b.digest, b.froms, b.create_user_id, b.category, u.account ");
			sql.append(" from "+DataTableType.博客.value+" b inner join "+DataTableType.用户.value+" u on b.create_user_id = u.id ");
			sql.append(" where b.status = ?  order by b.id desc limit 0,?");
			r = blogService.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, pageSize);
		}else{
			message.put("isSuccess", false);
			message.put("message", "目前暂不支持的操作方法");
			return message;
		}
		
		System.out.println("数量："+r.size());
		if(r.size() == 5){
			System.out.println("开始ID:"+r.get(0).get("id"));
			System.out.println("结束ID:"+r.get(4).get("id"));
		}
		message.put("isSuccess", true);
		message.put("message", r);
		return message.getMap();
	}
	
	
	
	/**
	 * 获取博客的的基本信息(不包括内容)
	 * @return
	 */
	@RequestMapping(value = "/info", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getInfo(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(blogService.getInfo(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 根据博客ID获取一条博客信息
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/blog/{blogId}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getOneBlog(
			@PathVariable("blogId") int blogId,
			HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(blogService.getOneBlog(blogId, getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获得最热门的n条记录
	 * @return
	 */
	@RequestMapping(value = "/hotestBlogs", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getHotestBlogs(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> ls = this.blogService.getHotestBlogs(5);
		message.put("isSuccess", true);
		message.put("message", ls);
		return message.getMap();
	}
	
	/**
	 * 获得最新的n条记录
	 * @return
	 */
	@RequestMapping(value="/newestBlogs", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getNewestBlogs(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> ls = this.blogService.getNewestBlogs(5);
		message.put("isSuccess", true);
		message.put("message", ls);
		return message.getMap();
	}
	
	/**
	 * 获得推荐的n条博客
	 * @return
	 */
	@RequestMapping(value="/recommendBlogs", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getRecommendBlogs(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> ls = this.blogService.getHotestBlogs(5);
		message.put("isSuccess", true);
		message.put("message", ls);
		return message.getMap();
	}
	
	/**
	 * 获取轮播图片信息
	 * @return
	 */
	@RequestMapping(value="/carouselImgs", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getCarouselImgs(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		JSONObject json = getJsonFromMessage(message);
		int num = JsonUtil.getIntValue(json, "num"); //获取图片的数量
		//执行的方式，现在支持：simple(取最新)，hostest(取最热门的)
		String method = JsonUtil.getStringValue(json, "method");
		
		List<Map<String,Object>> r = new ArrayList<Map<String,Object>>();
		String sql = "";
		System.out.println("执行的方式是："+method +",获取的数量:"+num);
		//普通获取，取最新的图片信息，按照create_time倒序排列
		if(method.equalsIgnoreCase("simple")){
			sql = "select id, img_url, title, digest from "+DataTableType.博客.value+" where status = " + ConstantsUtil.STATUS_NORMAL + " and img_url != '' order by create_time desc,id desc limit 0,?";
			r = blogService.executeSQL(sql, num);
			
		//按照热度，取最热门的图片信息，按照id倒序排序
		}else if(method.equalsIgnoreCase("hostest")){
			sql = "select id, img_url, title, digest from "+DataTableType.博客.value+" where status = " + ConstantsUtil.STATUS_NORMAL + "  and img_url != '' and NOW() < DATE_ADD(create_time,INTERVAL 7 DAY) order by (comment_number*0.45 + transmit_number*0.25 + share_number*0.2 + zan_number*0.1 + read_number*0.1) desc,id desc limit 0,?";
			r = blogService.executeSQL(sql, num);	
		}else if(method.equalsIgnoreCase("recommend")){
			sql = "select id, img_url, title, digest from "+DataTableType.博客.value+" where status=?  and img_url != '' and is_recommend=? order by id desc limit 0,?";
			r = blogService.executeSQL(sql, ConstantsUtil.STATUS_NORMAL, true, num);	
		}else{
			message.put("message", "目前暂不支持的操作方法");
			return message.getMap();
		}
		
		message.put("isSuccess", true);
		message.put("message", r);
		return message.getMap();
	}
	
	/**
	 * 添加标签
	 * @return
	 */
	@RequestMapping(value="/tag", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> addTag(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(blogService.addTag(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 查看草稿列表
	 * @return
	 */
	@RequestMapping(value="/drafts", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> draftList(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(blogService.draftList(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 编辑文章
	 * @return
	 */
	@RequestMapping(value="/blog/edit/{blog_id}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> edit(@PathVariable("blog_id") int blogId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(blogService.edit(blogId, getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 未审核文章列表
	 * @return
	 */
	@RequestMapping(value="/noChecks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> noCheckPaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(blogService.noCheckPaging(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 审核文章（管理员）
	 * @return
	 */
	@RequestMapping(value="/check", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> check(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(blogService.check(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
