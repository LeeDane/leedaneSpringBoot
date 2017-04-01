package com.cn.leedane.springboot;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.cn.leedane.shiro.MShiroFilterFactoryBean;
import com.cn.leedane.shiro.MyShiroRealm;

/**
 * Shiro 配置
 * @author LeeDane
 * 2017年3月23日 下午2:07:15
 * version 1.0
 */
@Configuration
public class ShiroConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(ShiroConfiguration.class);

	
	private MyShiroRealm myShiroRealm;
	@Bean
    public EhCacheManager getEhCacheManager() {  
        EhCacheManager em = new EhCacheManager();  
        em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");  
        return em;  
    }  
	
    @Bean(name = "myShiroRealm")
    public MyShiroRealm myShiroRealm(CacheManager cacheManager) {  
        MyShiroRealm realm = new MyShiroRealm(); 
        realm.setCacheManager(cacheManager);
        
        myShiroRealm = realm;
        return realm;
    } 

    /**
     * 注册DelegatingFilterProxy（Shiro）
     * 集成Shiro有2种方法：
     * 1. 按这个方法自己组装一个FilterRegistrationBean（这种方法更为灵活，可以自己定义UrlPattern，
     * 在项目使用中你可能会因为一些很但疼的问题最后采用它， 想使用它你可能需要看官网或者已经很了解Shiro的处理原理了）
     * 2. 直接使用ShiroFilterFactoryBean（这种方法比较简单，其内部对ShiroFilter做了组装工作，无法自己定义UrlPattern，
     * 默认拦截 /*）
     *
     * @param dispatcherServlet
     * @return
     * @author SHANHY
     * @create  2016年1月13日
     */
	  @Bean
	  public FilterRegistrationBean filterRegistrationBean() {
	      FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
	      filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
	      //  该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理  
	      filterRegistration.addInitParameter("targetFilterLifecycle", "true");
	      filterRegistration.setEnabled(true);
	      filterRegistration.addUrlPatterns("/*");// 可以自己灵活的定义很多，避免一些根本不需要被Shiro处理的请求被包含进来
	      return filterRegistration;
	  }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    /**
     * session相关管理参考：http://blog.csdn.net/mooyinn/article/details/45288997?ref=myread
     * @return
     */
    @Bean(name="sessionDAO")
    public MemorySessionDAO getSessionDao(){
    	return new MemorySessionDAO();
    }
    
    @Bean(name = "sessionManager")
    public DefaultWebSessionManager getSessionManager(){
    	DefaultWebSessionManager manager = new DefaultWebSessionManager();
    	manager.setSessionDAO(getSessionDao());
    	return manager;
    }
    

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(MyShiroRealm myShiroRealm) {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setRealm(myShiroRealm);
//      <!-- 用户授权/认证信息Cache, 采用EhCache 缓存 --> 
        dwsm.setCacheManager(getEhCacheManager());
        
        //设置session管理器
        dwsm.setSessionManager(getSessionManager());
        return dwsm;
    }

    
    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(getDefaultWebSecurityManager(myShiroRealm));
        return aasa;
    }
    
    

    /**
     * 加载shiroFilter权限控制规则（从数据库读取然后配置）
     * 注意：经过测试发现，定义不拦截anon应该放到拦截authc前面才有效，匹配规则是从上到下，发现符合条件的是就不再进行下一步的处理
     * @author SHANHY
     * @create  2016年1月14日
     */
    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean){
        /////////////////////// 下面这些规则配置最好配置到配置文件中 ///////////////////////
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/index", "anon");//anon 不拦截系统首页链接
        filterChainDefinitionMap.put("/", "anon");//anon 不拦截系统首页链接
        filterChainDefinitionMap.put("/dl", "anon");//anon 不拦截下载链接
        filterChainDefinitionMap.put("/dt", "anon");//anon 不拦截文章详情链接
        filterChainDefinitionMap.put("/lg", "anon");//anon 不拦截文登录链接
        filterChainDefinitionMap.put("/s", "anon");//anon 不拦截搜索详情链接
        filterChainDefinitionMap.put("/cs", "anon");//anon 不拦截聊天广场链接
        filterChainDefinitionMap.put("/403", "anon");//anon 不拦截403链接
        filterChainDefinitionMap.put("/websocket", "anon");//anon 不拦截聊天广场链接
        filterChainDefinitionMap.put("/scanLogin", "anon");//anon 不拦截扫码登陆链接
        filterChainDefinitionMap.put("/content", "anon");//anon 不拦截app博客详情链接
        // authc：该过滤器下的页面必须验证后才能访问，它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
        filterChainDefinitionMap.put("/*", "authc");// 拦截全部的链接
        // anon：它对应的过滤器里面是空的,什么都没做
        logger.info("##################从数据库读取权限规则，加载到shiroFilter中##################");
        //filterChainDefinitionMap.put("/user/edit/**", "authc,perms[user:edit]");// 这里为了测试，固定写死的值，也可以从数据库或其他配置中读取

        //filterChainDefinitionMap.put("/login", "anon");
        ///filterChainDefinitionMap.put("/**", "anon");//anon 可以理解为不拦截

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    }

    /**
     * ShiroFilter<br/>
     * 注意这里参数中的 StudentService 和 IScoreDao 只是一个例子，因为我们在这里可以用这样的方式获取到相关访问数据库的对象，
     * 然后读取数据库相关配置，配置到 shiroFilterFactoryBean 的访问规则中。实际项目中，请使用自己的Service来处理业务逻辑。
     *
     * @param myShiroRealm
     * @param stuService
     * @param scoreDao
     * @return
     * @author SHANHY
     * @create  2016年1月14日
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new MShiroFilterFactoryBean();
        // 必须设置 SecurityManager  
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/lg");
        // 登录成功后要跳转的连接
        shiroFilterFactoryBean.setSuccessUrl("/my");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        loadShiroFilterChain(shiroFilterFactoryBean);
        return shiroFilterFactoryBean;
    }
}
