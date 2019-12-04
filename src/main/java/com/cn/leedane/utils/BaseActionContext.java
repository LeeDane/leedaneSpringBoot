package com.cn.leedane.utils;


/**
 * 我的ActionContext
 * @author LeeDane
 * 2016年7月12日 下午2:39:29
 * Version 1.0
 */
public class BaseActionContext /*extends ActionSupport implements ServletRequestAware,
ServletResponseAware, SessionAware*/ {
	/*public HttpServletResponse response;
	public HttpServletRequest request;
	public Map<String,Object> session;
	

	*//**
	 * 当前登录用户信息(通过拦截器注入)
	 *//*
	public UserBean user;
	
	*//**
	 * 请求的信息
	 *//*
	public String params;
	
	*//**
	 * 响应结果信息
	 *//*
	public Map<String, Object> message = new HashMap<String, Object>();
	
	// 操作日志
	@Autowired
	protected OperateLogService<OperateLogBean> operateLogService;
	
	public void setOperateLogService(
			OperateLogService<OperateLogBean> operateLogService) {
		this.operateLogService = operateLogService;
	}
	
	public Map<String, Object> getMessage() {
		return message;
	}

	public void setMessage(Map<String, Object> message) {
		this.message = message;
	}
	
	//返回结果中包含的是否成功
	protected boolean resIsSuccess = false;
	//返回结果中包含的提示信息
	protected String resmessage;
	//返回的编码
	private int responseCode;

	*//**
	 * create time 2015年5月30日 下午11:43:34
	 *//*
	private static final long serialVersionUID = 1L;
	
	
	static{
		session = ActionContext.getContext().getSession();
	}

	public MyActionContext(Map<String, Object> context) {
		super(context);
	}
	
	
	*//**
	 * 通过原先servlet方式输出json对象。
	 * 目的：解决复杂的文本中含有特殊的字符导致struts2的json
	 * 		解析失败，给客户端返回500的bug
	 *//*
	protected void printWriter(){
		JSONObject jsonObject = JSONObject.fromObject(message);
		response.setCharacterEncoding("utf-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.append(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer != null)
				writer.close();
		}
		
	}
	private ActionContext getActionContext() {
		return ActionContext.getContext();
	}
	
	public Map<String, Object> getSession() {
		return getActionContext().getSession();
	}
	
	*//**
	 * 封装原始的ActionContext的放入session
	 * @param key 键的名称
	 * @param value   键的值
	 *//*
	public void putInSession(String key, Object value){
		getSession().put(key, value);
	}
	
	*//**
	 * 封装原始的ActionContext的从session取数据
	 * @param key  键的名称
	 *//*
	public Object getFromSession(Object key){
		return getSession().get(key);
	}
	
	*//**
	 * 从session取多个数据
	 * @param keys  多个键的名称
	 * @return
	 *//*
	public List<Object> getMultFromSession(Object ... keys){
		if(keys.length < 1) return null;
		
		List<Object> values = new ArrayList<Object>();
		for(Object key : keys){
			values.add(getSession().get(key));
		}		
		return values;
	}
	
	*//**
	 * 封装原始的ActionContext的从session移除数据
	 * @param key 键的名称
	 *//*
	public void removeSession(Object key){
		if(getSession().containsKey(key)){
			getSession().remove(key);
		}
	}
	
	*//**
	 * 从session移除多个数据
	 * @param key  多个键的名称
	 *//*
	public void removeMultSession(Object ...keys){
		for(Object key : keys)
			if(getSession().containsKey(key)){
				getSession().remove(key);
			}
	}
	
	*//**
	 * 据指定语言获取多国语言配置中的值
	 * @param language  语言的编码
	 * @param aTextName  key值
	 * @param defaultValue  没有值得时候的默认值
	 * @return
	 *//*
	public String getTextBySpecifiedLanguage(String language, String aTextName, String defaultValue){
		if(StringUtil.isNull(aTextName)) return null;
		
		if(language != null && !language.equalsIgnoreCase(ConstantsUtil.SYSTEM_LANGUAGE_CN)){
			aTextName = aTextName +"_" +language;
		}
	
		String text;
		if(defaultValue == null){
			text = getText(aTextName);
		}else{
			text = getText(aTextName, defaultValue);
		}		
		return text;
	}
	
	*//**
	 * 据指定语言获取多国语言配置中的值
	 * @param language 语言的编码
	 * @param aTextName  key值
	 * @return
	 *//*
	public String getTextBySpecifiedLanguage(String language, String aTextName){
		return getTextBySpecifiedLanguage(language,aTextName);
	}
	
	*//**
	 * 根据指定语言获取多国语言配置中的值
	 * @param aTextName  key值
	 * @return
	 *//*
	public String getTextBySpecifiedLanguage(String aTextName){
		return getTextBySpecifiedLanguage(ConstantsUtil.SYSTEM_LANGUAGE_CN,aTextName);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}*/
}
