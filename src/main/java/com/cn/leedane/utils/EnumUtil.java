package com.cn.leedane.utils;

import com.cn.leedane.model.AttentionBean;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.ChatBean;
import com.cn.leedane.model.ChatBgBean;
import com.cn.leedane.model.ChatBgUserBean;
import com.cn.leedane.model.CollectionBean;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.model.FanBean;
import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.FriendBean;
import com.cn.leedane.model.GalleryBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.PrivateChatBean;
import com.cn.leedane.model.ReportBean;
import com.cn.leedane.model.ScoreBean;
import com.cn.leedane.model.SignInBean;
import com.cn.leedane.model.TransmitBean;
import com.cn.leedane.model.UploadBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.ZanBean;
import com.cn.leedane.model.circle.CircleBean;

/**
 * 枚举工具类
 * @author LeeDane
 * 2016年7月12日 上午10:27:42
 * Version 1.0
 */
public class EnumUtil {
	
	/**
	 * 网络抓取类型
	 * @author LeeDane
	 * 2016年3月22日 上午10:16:46
	 * Version 1.0
	 */
	public enum WebCrawlType {
		全部(""),网易新闻("网易新闻"), 散文网("散文网"), 散文网短篇小说("散文网短篇小说");
	
		private WebCrawlType(String value) {
			this.value = value;
		}
	
		public final String value;
	
	}
	
	/**
	 * 消息通知类型
	 * @author LeeDane
	 * 2016年3月22日 上午10:16:30
	 * Version 1.0
	 */
	public enum NotificationType {
		全部("全部"),通知("通知"),艾特我("@我"), 评论("评论"),转发("转发"),赞过我("赞过我"),私信("私信"),留言("留言");
	
		private NotificationType(String value) {
			this.value = value;
		}
	
		public final String value;
	}
	/**
	 * 获取NotificationType对象
	 * @param type
	 * @return
	 */
	public static NotificationType getNotificationType(String type){
		for(NotificationType nt: NotificationType.values()){
			if(nt.value.equals(type)){
				return nt;
			}
		}
		return null;
	}
	
	/**
	 * 数据库表类型
	 * @author LeeDane
	 * 2016年3月22日 上午10:16:30
	 * Version 1.0
	 */
	public enum DataTableType {
		博客("t_blog"),心情("t_mood"), 评论("t_comment"),转发("t_transmit"), 阅读("t_read"),赞("t_zan"),用户("t_user"),通知("t_notification"),
		聊天("t_chat"), 聊天背景("t_chat_bg"), 聊天背景与用户("t_chat_bg_user"), 私信("t_private_chat"),文件("t_file_path"), 
		粉丝("t_fan"), 收藏("t_collection"),关注("t_attention"), 好友("t_friend"), 图库("t_gallery"), 操作日志("t_operate_log"),
		举报("t_report"), 积分("t_score"), 签到("t_sign_in"), 上传("t_upload"), 权限("t_permission"), 角色权限("t_role_permission"),
		角色("t_role"), 用户角色("t_user_role"), 链接管理("t_link_manage"), 留言("t_message_board"), 素材("t_material"), 
		任务("t_job_manage"), 贡献值("t_circle_contribution"),圈子("t_circle"), 圈子成员("t_circle_member"), 帖子("t_circle_post"), 
		不存在的表("t_inexistence"), 分类("t_category"), 商店("t_mall_shop"), 商店商品("t_mall_product"), 商品订单("t_mall_order"),
		商品心愿单("t_mall_wish"), 大事件("t_event"), 选项配置("t_option"), 推广位管理("t_mall_promotion_seat"), 黑名单("t_manage_black"),
		事件提醒("t_manage_remind"), 未来信件("t_future_letter");
	
		private DataTableType(String value) {
			this.value = value;
		}
		public final String value;
	
	}
	
	/**
	 * 获取表的中文名
	 * @param tableName 表的英文名
	 * @return
	 */
	public static String getTableCNName(String tableName){
		for(DataTableType ts: DataTableType.values()){
			if(ts.value.equals(tableName)){
				return ts.name();
			}
		}
		return null;
	}
	
	/**
	 * 数据库表类型
	 * @author LeeDane
	 * 2016年3月22日 上午10:16:30
	 * Version 1.0
	 */
	public enum BeanClassType {
		博客(BlogBean.class),心情(MoodBean.class), 评论(CommentBean.class),转发(TransmitBean.class),赞(ZanBean.class),
		用户(UserBean.class),通知(NotificationBean.class), 聊天(ChatBean.class), 聊天背景(ChatBgBean.class), 聊天背景与用户(ChatBgUserBean.class), 
		私信(PrivateChatBean.class),文件(FilePathBean.class), 粉丝(FanBean.class), 收藏(CollectionBean.class),关注(AttentionBean.class),
		好友(FriendBean.class), 图库(GalleryBean.class), 操作日志(OperateLogBean.class),举报(ReportBean.class), 积分(ScoreBean.class), 
		签到(SignInBean.class), 上传(UploadBean.class), 圈子(CircleBean.class);
	
		private BeanClassType(Class<?> value) {
			this.value = value;
		}
		public final Class<?> value;
	
	}
	
	/**
	 * 获取BeanClass对象
	 * @param type
	 * @return
	 */
	public static String getDataTable(Class<?> type){
		for(BeanClassType ts: BeanClassType.values()){
			if(ts.value == type){
				return ts.name();
			}
		}
		return null;
	}
	
	/**
	 * 获取BeanClass对象
	 * @param tableCNName 表的中文名
	 * @return
	 */
	public static Class<?> getBeanClass(String tableCNName){
		for(BeanClassType ts: BeanClassType.values()){
			if(ts.name().equals(tableCNName)){
				return ts.value;
			}
		}
		return null;
	}
	
	/**
	 * 举报类型
	 * @author LeeDane
	 * 2016年3月25日 上午10:18:18
	 * Version 1.0
	 */
	public enum ReportType {
		未知(0),色情低俗(1), 广告骚扰(2),政治敏感(3),违法(4),倾诉投诉(5);
	
		private ReportType(int value) {
			this.value = value;
		}
	
		public final int value;
	
	}
	
	/**
	 * 时间类型
	 * @author LeeDane
	 * 2016年3月22日 上午10:17:01
	 * Version 1.0
	 */
	public enum TimeScope {
		昨日("1"), 当日("2"), 本周("3"), 本月("4"), 本年("5");

		private TimeScope(String value) {
			this.value = value;
		}
		public final String value;
	}
	
	/**
	 * 文章分类
	 * @author LeeDane
	 * 2016年12月5日 下午1:41:58
	 * Version 1.0
	 */
	public enum BlogCategory {
		我的日志("note"), 学习笔记("study"), 我的日常("我的日常");

		private BlogCategory(String value) {
			this.value = value;
		}
		public final String value;

	}
	
	/**
	 * 获取BlogCategory对象
	 * @param type
	 * @return
	 */
	public static BlogCategory getBlogCategory(String type){
		for(BlogCategory ts: BlogCategory.values()){
			if(ts.value.equals(type)){
				return ts;
			}
		}
		return null;
	}
	

	/**
	 * 获取TimeScope对象
	 * @param type
	 * @return
	 */
	public static TimeScope getTimeScope(String type){
		for(TimeScope ts: TimeScope.values()){
			if(ts.value.equals(type)){
				return ts;
			}
		}
		return null;
	}
	
	/**
	 * 邮件类型
	 * @author LeeDane
	 * 2016年9月6日 上午9:43:40
	 * Version 1.0
	 */
	public enum EmailType {
		新邮件(0), 回复(1), 转发(2);

		private EmailType(int value) {
			this.value = value;
		}

		public final int value;

	}
	
	/**
	 * 服务器返回码
	 * @author LeeDane
	 * 2016年1月20日 上午10:17:54
	 * Version 1.0
	 */
	public enum ResponseCode{
		请先登录(1001),
		邮件已发送成功(1002),
		不是未注册状态邮箱不能发注册码(1003),
		邮件发送失败(1004),
		暂时不支持手机找回密码功能(1005),
		暂时不支持邮箱找回密码功能(1006),
		未知的找回密码类型(1007),
		注销成功(1008),
		账号已被禁用(1009),
		账号未被激活(1010),
		请先完善账号信息(1011),
		账号已被禁言(1012),
		账号已被注销(1013),
		服务器处理异常(500),
		链接不存在(400),
		资源不存在(404),
		请求返回成功码(200),
		文件不存在(2001),
		操作文件失败(2002),
		某些参数为空(2003),
		缺少参数(2004),
		JSON数据解析失败(2005),
		禁止访问(2006),
		系统维护时间(2007),
		文件上传失败(2008),
		没有操作实例(2009),
		没有操作权限(2010),
		没有下载码(2011),
		下载码失效(2012),
		数据库保存失败(2013),
		添加的记录已经存在(2014),
		数据库删除数据失败(2015),
		操作对象不存在(2016),
		参数信息不符合规范(2017),
		缺少请求参数(2018),
		该博客不存在(2019),
		标签添加成功(2020),
		标签长度不能超过5位(2021),
		数据更新失败(2022),
		数据删除失败(2023),
		账号或密码为空(2024),
		您的账号登录失败太多次(2025),
		该文章不需要审核(2026),
		用户已被禁言(2027),
		用户已被禁止使用(2028),
		注册未激活账户(2029),
		未完善信息(2030),
		转发成功(2031),
		转发失败(2032),
		删除转发成功(2033),
		删除转发失败(2034),
		评论成功(2035),
		评论失败(2036),
		删除评论成功(2037),
		删除评论失败(2038),
		点赞成功(2039),
		点赞失败(2040),
		取消点赞成功(2041),
		取消点赞失败(2042),
		某些字段超过其存储所需的长度(2043),
		验证码验证失败(2044),
		用户不存在或请求参数不对(3001),
		用户已经注销(3002),
		请先验证邮箱(3003),
		恭喜您登录成功(3004),
		账号或密码不匹配(3005),
		注册失败(3006),
		该邮箱已被占用(3007),
		该用户已被占用(3008),
		该手机号已被注册(3009),
		操作过于频繁(3010),
		手机号为空或者不是11位数(3011),
		操作成功(3012),
		操作失败(3013),
		该通知类型不存在(3014),
		用户名不能为空(3015),
		密码不能为空(3016),
		两次密码不匹配(3017),
		检索关键字不能为空(3018),
		原密码错误(3019),
		要修改的密码跟原密码相同(3020),
		新密码修改成功(3021),
		系统检测到有敏感词(3022),
		请填写每次用户每次下载扣取的积分(3023),
		自己上传的聊天背景资源(3023),
		聊天内容不能为空(3024),
		您还没有绑定电子邮箱(3025),
		对方还没有绑定电子邮箱(3026),
		该用户不存在(3027),
		邮件已经发送(3028),
		参数不存在或为空(3029),
		该资源现在不支持评论(3030),
		该资源现在不支持转发(3031),
		更新评论状态成功(3032),
		更新转发状态成功(3033),
		更新评论状态失败(3034),
		更新转发状态失败(3035),
		删除通知成功(3036),
		删除通知失败(3037),
		私信内容不能为空(3038),
		好友关系不存在(3039),
		好友关系不是待确认状态(3040),
		关注成功(3041),
		解除好友关系成功(3042),
		添加好友失败(3043),
		不能添加自己为好友(3044),
		删除的通知不存在(3045),
		删除的聊天记录不存在(3046),
		删除聊天记录成功(3047),
		删除聊天记录失败(3048),
		心情图片链接处理失败(3049),
		发表心情成功(3050),
		没有要同步的数据(3051),
		数据同步成功(3052),
		系统不支持查找该年份的数据(3053),
		话题不能为空(3054),
		图片大于1M无法上传(3055),
		图片大于5M无法上传(3116),
		收藏成功(3056),
		数据库对象数量不符合要求(3057),
		记账位置信息为空(3058),
		添加成功(3059),
		添加失败(3060),
		修改成功(3061),
		修改失败(3062),
		删除成功(3063),
		删除失败(3064),
		免登录码校验失败(3065),
		免登录码为空(3066),
		登录页面已经过期(3067),
		获取不到一级分类列表(3068),
		获取不到二级分类列表(3069),
		举报成功(3070),
		不能举报自己发布的资源(3071),
		请使用有管理员权限的账号登录(3072),
		数据库修改失败(3073),
		用户不存在(3074),
		密码重置成功(3075),
		用户注销成功(3076),
		不能给自己发信息(3077),
		暂时不支持发送短信(3078),
		未知的发送消息类型(3079), 
		通知已经发送(3080),
		私信已经发送(3081),
		头像上传成功(3082),
		上传的文件过大(3083),
		非合法的链接(3084),
		链接操作失败(3085),
		没有更多数据(3086),
		目前暂不支持的操作方法(3087),
		非正常登录状态(3088),
		不能访问没有授权的链接(2010),
		token获取异常(3089),
		用户名或者密码不匹配(3090),
		账户已锁定(3091),
		用户名或密码错误次数过多(3092),
		密码不正确(3093),
		恭喜您成功绑定当前微信(3094),
		文件上传成功(3095), 
		空指针异常(3096),
		圈子名称不能为空(3097),
		根据请求参数构建实体对象失败(3098),
		已超出您能创建的圈子数量(3099),
		该圈子名称已经被占用啦(3100),
		该圈子不存在(3101),
		您已经在圈子中(3102),
		回答的答案不正确(3103),
		用户已经不在该圈子中(3104),
		今天已经打卡(3105),
		贡献打卡成功(3106),
		今天还未打卡(3107),
		请先加入该圈子(3108),
		该帖子不存在(3109),
		该帖子无法校验(3110),
		圈主不能被删除(3111),
		该成员已被移除出圈子(3112),
		自己不能将自己移除出该圈子(3113),
		该圈子已被设置为不添加成员(3114),
		该圈子成员已超额(3115),
		token过期或无效(3117),
		无法从详情中解析到主图(4001),
		该商品不存在或已被删除(4002),
		该商店不存在或已被删除(4003),
		淘宝api请求失败(4004),
		参数为空或者不符合规范异常(4005),
		该订单已不存在(4006),
		完成状态的订单无法删除的异常(4007),
		请使用有商城管理员权限的账号登录(4008),
		RSA加密解密异常(4009),
		测试角色权限受限异常(4010),
		该宝宝不存在(4011),
		该宝宝生活方式记录不存在(4012),
		该股票不存在(4013),
		该股票购买记录不存在(4014),
		该股票卖出记录不存在(4015),
		请先删除该股票的全部购买记录(4016),
		请先删除该股票购买记录所对应的全部卖出记录(4017),
		请核实是否超额卖出(4018),
		该提醒任务不存在或者不支持共享(4019),
		请先上传图片才能继续打卡(4020),
		请先定位才能继续打卡(4021),
		超出打卡范围(4022),
		该任务目前不支持共享(4023),
		已超出任务的参与日期(4024),
		已超出任务的参与人数(4025),
		该人员还没有申请加入任务记录(4026),
		该任务申请记录不是等待创建者审批状态(4027),
		请先计步才能继续打卡(4028),
		计步数不足以打卡(4029),
		该任务您还没有邀请记录(4030),
		非法操作异常(4031),
		该任务不支持共享(4032),
		该心情不存在(4033),
		ES服务器连接异常(4034),
		ES索引不存在(4035),
		头像上传失败(4036),
		该事件不存在(4037),
		该图库不存在(4038),
		请输入正确验证码(4039),
		淘宝api异常(4040),
		京东api异常(4041),
		系统无法解析出地址栏中的id(4042),
		拼多多api异常(4043),
		页面授权已经过期(4044),
		该推广位不存在(4045),
		苏宁api异常(4046),
		GitHub异常(4047),
		您已经有推荐码(4048),
		已经绑定过推荐人(4049),
		;
		
		private ResponseCode(int value) {
			this.value = value;
		}
		public final int value;
	}
	
	/**
	 * 根据值获取ResponseCode的key值
	 * @param code
	 * @return
	 */
	public static String getResponseValue(int code){
		for(ResponseCode rc: ResponseCode.values()){
			if(rc.value == code){
				return rc.name();
			}
		}
		return "";
	}

	
	/**
	 * 获取ReportType对象的key值
	 * @param type
	 * @return
	 */
	public static String getReportType(int type){
		for(ReportType nt: ReportType.values()){
			if(nt.value == type){
				return nt.name();
			}
		}
		return null;
	}
	
	/*public static void main(String[] args) {
		logger.info(getResponseValue(1001));
	}*/
	
	/**
     * 记账图标
     * value为展示的文字
     */
    public enum FinancialIcons{
        银行卡("银行卡"),
        功课("功课"),
        支付("支付"),
        交通("交通"),
        教育("教育"),
        旅店("旅店"),
        蛋糕("蛋糕"),
        电影("电影"),
        现金("现金"),
        购物("购物"),
        住房("住房"),
        购物车("购物车"),
        包包("包包"),
        礼物("礼物"),
        手机("手机"),
        话费("话费"),
        笔记本("笔记本"),
        商店("商店"),
        出租车("出租车"),
        收入("收入"),
        支出("支出"),
        饮料("饮料"),
        买菜做饭("买菜做饭"),
        地铁("地铁"),
        话费充值("话费充值"),
        上网费("上网费"),
        快递("快递"),
        运动("运动");
        FinancialIcons(String value) {
            this.value = value;
        }
        public final String value;
    }
    
    /**
     * 平台类型
     * value为展示的文字
     */
    public enum PlatformType{
        网页版("web"),
        安卓版("android"),
        微信公众平台版("wechat");
        PlatformType(String value) {
            this.value = value;
        }
        public final String value;
    }
    
    /**
     * 商品平台类型
     * @author LeeDane
     * 2017年10月25日 下午3:13:31
     * version 1.0
     */
	public enum ProductPlatformType {
		淘宝("淘宝网"),京东("京东"), 苏宁("苏宁"), 拼多多("拼多多"), 系统自营("系统自营");
	
		private ProductPlatformType(String value) {
			this.value = value;
		}
	
		public final String value;
	}

	/**
	 * 第三方授权平台类型
	 * @author LeeDane
	 * 2017年10月25日 下午3:13:31
	 * version 1.0
	 */
	public enum Oauth2PlatformType {
		淘宝("淘宝网"), 京东("京东"), 苏宁("苏宁"), 拼多多("拼多多"), github("github");

		private Oauth2PlatformType(String value) {
			this.value = value;
		}

		public final String value;
	}
	
	/**
     * 商品统计类型类型
     * @author LeeDane
     * 2017年10月25日 下午3:13:31
     * version 1.0
     */
	public enum MallProductStatisticsType {
		心愿单(1),评论数(2), 访问数(3), 购买数(4);
	
		private MallProductStatisticsType(int value) {
			this.value = value;
		}
	
		public final int value;
	}
	
	/**
	 * 获取MallProductStatisticsType对象
	 * @param value
	 * @return
	 */
	public static String getMallProductStatisticsType(int value){
		for(MallProductStatisticsType nt: MallProductStatisticsType.values()){
			if(nt.value == value){
				return nt.name();
			}
		}
		return "";
	}
	
	/**
	 * 商品订单状态
	 * @author LeeDane
	 * 2017年12月8日 下午3:06:51
	 * version 1.0
	 */
	public enum MallOrderType {
		禁用(0),
		已提交(1),  //表示用户提交了订单申请
		已删除(2),
		待结算佣金(20),  //系统从第三方平台获取订单后进行匹配，匹配成功后的状态
		待支付佣金(21),  //系统定时对待结算佣金的状态做结算佣金的处理、做统计后的状态
		已支付佣金 (22), //系统对待支付佣金的订单做支付后订单的状态
		有争议 (23), //对同时出现一个订单号对应多个人的情况，系统将订单修改为有争议状态，方便审核后再进入待结算佣金
		已完成(24); //对该订单的结算用户或者非法交易的订单做完成处理，将不支持对此订单做任何操作
	
		private MallOrderType(int value) {
			this.value = value;
		}
		public final int value;
	}
	/**
	 * 获取MallOrderType对象
	 * @param value
	 * @return
	 */
	public static String getMallOrderType(int value){
		for(MallOrderType nt: MallOrderType.values()){
			if(nt.value == value){
				return nt.name();
			}
		}
		return "";
	}
	
	
	/**
	 * 宝宝的生活方式
	 * @author LeeDane
	 * 2018年6月12日 下午5:40:49
	 * version 1.0
	 */
	public enum BabyLifeType {
		吃喝(1),睡觉(2), 洗刷(3), 臭臭(4);
	
		private BabyLifeType(int value) {
			this.value = value;
		}
		public final int value;
	}
	/**
	 * 获取宝宝的生活方式
	 * @param value
	 * @return
	 */

	public static String getBabyLifeType(int value){
		for(BabyLifeType nt: BabyLifeType.values()){
			if(nt.value == value){
				return nt.name();
			}
		}
		return "";
	}
	
	/**
	 * 宝宝的喂养方式
	 * @author LeeDane
	 * 2018年6月12日 下午4:37:55
	 * version 1.0
	 */
	public enum BabyEatType {
		亲喂养(1),瓶喂养(2), 碗喂养(3);
	
		private BabyEatType(int value) {
			this.value = value;
		}
		public final int value;
	}
	/**
	 * 获取宝宝的喂养方式
	 * @param value
	 * @return
	 */
	public static String getBabyEatType(int value){
		for(BabyEatType nt: BabyEatType.values()){
			if(nt.value == value){
				return nt.name();
			}
		}
		return "";
	}
	
	/**
	 * 宝宝的反应情况
	 * @author LeeDane
	 * 2018年6月12日 下午4:31:50
	 * version 1.0
	 */
	public enum BabyReaction {
		积极配合(1),一般配合(2), 不怎么配合(3), 不配合(4);
	
		private BabyReaction(int value) {
			this.value = value;
		}
		public final int value;
	}
	/**
	 * 获取宝宝的反应情况
	 * @param value
	 * @return
	 */
	public static String getBabyReaction(int value){
		for(BabyReaction nt: BabyReaction.values()){
			if(nt.value == value){
				return nt.name();
			}
		}
		return "";
	}
	
	/**
	 * 打卡类型
	 * @author LeeDane
	 * 2018年10月8日 上午10:14:39
	 * version 1.0
	 */
	public enum ClockInType {
		普通打卡(1),图片打卡(2), 位置打卡(3), 计步打卡(4);
	
		private ClockInType(int value) {
			this.value = value;
		}
		public final int value;
	}
	/**
	 * 获取ClockInType对象
	 * @param value
	 * @return
	 */
	public static String getClockInType(int value){
		for(ClockInType nt: ClockInType.values()){
			if(nt.value == value){
				return nt.name();
			}
		}
		return "";
	}
	
	/**
	 * 自定义消息的业务类型
	 * @author LeeDane
	 * 2018年10月15日 下午3:36:40
	 * version 1.0
	 */
	public enum CustomMessageExtraType {
		其他未知类型(-1), 聊天(1), 任务打卡(2), 请求加好友(3), 同意加好友(4)
		, 请求加入任务(5), 同意加入任务(6), 刷新任务(7), 任务修改(8), 邀请加入任务(9),
		任务打卡送积分(10), 动态失败超过三次(11),打卡等待审核(12);
	
		private CustomMessageExtraType(int value) {
			this.value = value;
		}
		public final int value;
	}
	
	/**
	 * 任务积分的操作类型
	 * @author LeeDane
	 * 2018年11月5日 下午5:20:08
	 * version 1.0
	 */
	public enum ClockScoreOperateType {
		新增(1),修改(2), 删除(3);
	
		private ClockScoreOperateType(int value) {
			this.value = value;
		}
	
		public final int value;
	
	}
	
	/**
	 * 任务积分的业务类型
	 * @author LeeDane
	 * 2018年11月13日 下午12:37:35
	 * version 1.0
	 */
	public enum ClockScoreBusinessType {
		每日打卡(1),结束统计(2), 额外奖励(3), 成员加入(4);
	
		private ClockScoreBusinessType(int value) {
			this.value = value;
		}
	
		public final int value;
	
	}

	/**
	 * 日志操作类型
	 * @author LeeDane
	 * 2019年8月13日 上午9:01:01
	 * Version 1.0
	 */
	public enum LogOperateType {
		内部接口(0), 网页端(1), 对外接口(2);

		private LogOperateType(int value) {
			this.value = value;
		}
		public final int value;
	}


	/**
	 * oauth2授权类型
	 */
	public enum Oauth2Type {
		登录(1),绑定(2), 注销(3);

		private Oauth2Type(int value) {
			this.value = value;
		}
		public final int value;
	}

	/**
	 * 聚合apid类型
	 * @author LeeDane
	 * 2016年3月22日 上午10:16:46
	 * Version 1.0
	 */
	public enum JuheApiType {
		笑话精选(1),头条新闻(2), 历史今天(3);

		private JuheApiType(int value) {
			this.value = value;
		}

		public final int value;

	}

	/**
	 * 通知消息类型
	 * @author LeeDane
	 * 2019年12月22日 上午10:16:46
	 * Version 1.0
	 */
	public enum NoticeType {
		邮件(1),站内信(2), 短信(3), websocket(4);

		private NoticeType(int value) {
			this.value = value;
		}

		public final int value;
	}

	/**
	 * 短信业务类型
	 * @author LeeDane
	 * 2016年7月12日 上午10:23:48
	 * Version 1.0
	 */
	public enum NoticeSMSType {
		ADD_FRIEND("addFriend"),  //增加好友
		AGREE_FRIEND("agreeFriend"),  //同意好友
		PUBLISHED_MOOD("publishedMood"),  //发表说说
		ADD_COMMENT("addComment"),  //添加评论
		REGISTER_VALIDATION("register"), //用户注册验证码
		LOGIN_VALIDATION("login"), //用户登录验证码
		UPDATE_PSW_VALIDATION("updatePSW"), //修改密码验证码
		UPDATE_INFO_VALIDATION("updateInfo"), //信息变更验证码
		LOGIN_ERROR_VALIDATION("loginError"), //登录异常验证码
		IDENTITY_VALIDATION("identity"), //身份验证验证码
		ACTIVITY_VALIDATION("activity"), //活动确认验证码
		BIND_PHONE_VALIDATION("bindPhone"), //手机号码绑定验证码
		REMIND_TAKE_MEDICINE("takeMedicine"), //提醒吃药
		AGAIN_REMIND_TAKE_MEDICINE("againTakeMedicine"), //再次提醒吃药
		MY_ATTENTION("myAttention"), //我的关注
		FUTURE_LETTER("futureLetter"); //

		public String value;
		private NoticeSMSType(String value){
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	/**
	 * 获取NoticeSMSType对象
	 * @param type
	 * @return
	 */
	public static NoticeSMSType getNoticeSMSType(String type){
		for(NoticeSMSType ns: NoticeSMSType.values()){
			if(ns.value.equals(type)){
				return ns;
			}
		}
		return null;
	}

	/**
	 * 根据NoticeSMSType对象获取字符
	 * @param type
	 * @return
	 */
	public static String getNoticeSMSTypeValue(NoticeSMSType type){
		for(NoticeSMSType nt: NoticeSMSType.values()){
			if(nt.value.equalsIgnoreCase(type.value)){
				return nt.name();
			}
		}
		return "";
	}
}
