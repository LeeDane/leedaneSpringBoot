package com.cn.leedane.springboot;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.handler.BlogHandler;
import com.cn.leedane.handler.ChatBgUserHandler;
import com.cn.leedane.handler.ChatSquareHandler;
import com.cn.leedane.handler.CircleOfFriendsHandler;
import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.handler.CommentHandler;
import com.cn.leedane.handler.CommonHandler;
import com.cn.leedane.handler.FanHandler;
import com.cn.leedane.handler.FriendHandler;
import com.cn.leedane.handler.InitCacheData;
import com.cn.leedane.handler.MoodHandler;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.SignInHandler;
import com.cn.leedane.handler.TransmitHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.WechatHandler;
import com.cn.leedane.handler.ZanHandler;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.handler.circle.CircleMemberHandler;

/**
 * 项目启动的入口
 * @author LeeDane
 * 2017年3月13日 上午11:35:00
 * Version 1.0
 */
@EnableAutoConfiguration(exclude={MultipartAutoConfiguration.class})
@SpringBootApplication  //全局controller控制
@MapperScan("com.cn.leedane.mapper")
@EnableTransactionManagement
@ComponentScan("com.cn.leedane")
//标注启动了缓存
@EnableCaching
public class StartUpApplication /*implements TransactionManagementConfigurer*/{
	private static Logger logger = Logger.getLogger(StartUpApplication.class);
	
	@Bean(name = "dataSource")
	@Qualifier(value = "dataSource")
	@Primary
	@ConfigurationProperties(prefix = "c3p0")
	public DataSource dataSource(){
		return DataSourceBuilder.create().type(com.mchange.v2.c3p0.ComboPooledDataSource.class).build();
	}
	
	@Bean  
    public DataSourceTransactionManager makeDataSourceTransactionManager() {  
        DataSourceTransactionManager manager = new DataSourceTransactionManager();  
        manager.setDataSource(dataSource());  
        return manager;  
    } 
	
	@Bean  
    public SqlSessionFactory makeSqlSessionFactoryBean() throws Exception {  
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean(); 
        /** 设置mybatis configuration 扫描路径 ,不设置将无法处理驼峰式命名类型和null处理*/
        sessionFactory.setConfigLocation(new ClassPathResource("config/mybatis-config.xml"));
        sessionFactory.setDataSource(dataSource());  
        sessionFactory.setTypeAliasesPackage("com.cn.leedane.model");  
        return sessionFactory.getObject();  
    } 
	
	
	
	@Bean(name= "blogHandler")
    public BlogHandler getBlogHandler() {
        return new BlogHandler();  
    }
	
	@Bean(name= "chatBgUserHandler")
    public ChatBgUserHandler getChatBgUserHandler() {
        return new ChatBgUserHandler();  
    }
	
	@Bean(name= "chatSquareHandler")
    public ChatSquareHandler getChatSquareHandler() {
        return new ChatSquareHandler();  
    }
	
	@Bean(name= "circleOfFriendsHandler")
    public CircleOfFriendsHandler getCircleOfFriendsHandler() {
        return new CircleOfFriendsHandler();  
    }
	
	@Bean(name= "cloudStoreHandler")
    public CloudStoreHandler getCloudStoreHandler() {
        return new CloudStoreHandler();  
    }
	
	@Bean(name= "commentHandler")
    public CommentHandler getCommentHandler() {
        return new CommentHandler();  
    }
	
	@Bean(name= "commonHandler")
    public CommonHandler getCommonHandler() {
        return new CommonHandler();  
    }
	
	@Bean(name= "fanHandler")
    public FanHandler getFanHandler() {
        return new FanHandler();  
    }
	
	@Bean(name= "friendHandler")
    public FriendHandler getFriendHandler() {
        return new FriendHandler();  
    }
	
	@Bean(name= "moodHandler")
    public MoodHandler getMoodHandler() {
        return new MoodHandler();  
    }
	
	@Bean(name= "notificationHandler")
    public NotificationHandler getNotificationHandler() {
        return new NotificationHandler();  
    }
	

	@Bean(name= "signInHandler")
    public SignInHandler getSignInHandler() {
        return new SignInHandler();  
    }
	

	@Bean(name= "transmitHandler")
    public TransmitHandler getTransmitHandler() {
        return new TransmitHandler();  
    }
	

	@Bean(name= "userHandler")
    public UserHandler getUserHandler() {
        return new UserHandler();  
    }
	

	@Bean(name= "wechatHandler")
    public WechatHandler getWechatHandler() {
        return new WechatHandler();  
    }
	

	@Bean(name= "zanHandler")
    public ZanHandler getZanHandler() {
        return new ZanHandler();  
    }
	
	@Bean(name= "circleHandler")
    public CircleHandler getCircleHandler() {
        return new CircleHandler();  
    }
	
	@Bean(name= "circleMemberHandler")
    public CircleMemberHandler getCircleMemberHandler() {
        return new CircleMemberHandler();  
    }
	
	/*
	 * ehcache 主要的管理器
	 */
	/*@Bean(name = "systemEhCacheManager")
	public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean){
		return new EhCacheCacheManager (bean.getObject ());
	}*/
	
	/*
     * ehcache 主要的管理器
     */
    @Bean(name = "ehCacheCacheManager")
    public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean){
        return new EhCacheCacheManager (bean.getObject ());
    }

    /*
     * 据shared与否的设置,Spring分别通过CacheManager.create()或new CacheManager()方式来创建一个ehcache基地.
     */
    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean(){
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean ();
        cacheManagerFactoryBean.setConfigLocation (new ClassPathResource ("ehcache.xml"));
        cacheManagerFactoryBean.setShared (true);
        return cacheManagerFactoryBean;
    }
    
    @Bean(name= "systemCache")
    public SystemCache getSystemCache() {
		//SystemCache.setSystemEhCache(ehCacheCacheManager(ehCacheManagerFactoryBean()).getCache("systemEhCache"));
        return new SystemCache();  
    }
	
	@Bean(name= "directory")
    public SimpleFSDirectory getSimpleFSDirectory(){
        try {
			return new SimpleFSDirectory(new File("C:\\index\\leedaneLuceneIndex"));
		} catch (IOException e) {
			e.printStackTrace();
		} 
        return null;
    }

	@Bean(name= "analyzer")
    public IKAnalyzer getIKAnalyzer() {
        return new IKAnalyzer(true);  
    }
	
	@Bean(name= "indexWriterConfig")
    public IndexWriterConfig getIndexWriterConfig() {
        return new IndexWriterConfig(Version.LUCENE_4_10_2, getIKAnalyzer());  
    }

	@Bean(name= "indexWriter")
	@Scope(value="singleton") //标记该bean为单例的
    public IndexWriter getIndexWriter() {
        try {
			return new IndexWriter(getSimpleFSDirectory(), getIndexWriterConfig());
		} catch (IOException e) {
			e.printStackTrace();
		}  
        return null;
    }
	
	@Bean(name= "exceptionHandler")
	public ExceptionHandler getExceptionHandler(){
		return new ExceptionHandler();
	}
	
	//显示声明CommonsMultipartResolver为mutipartResolver
    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver(ServletContext servletContext){
    	CustomMultipartResolver resolver = new CustomMultipartResolver(servletContext);
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(true);//resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
        resolver.setMaxInMemorySize(1*1024); //1M
        resolver.setMaxUploadSize(3000*1024*1024);//上传文件大小 20M 20*1024*1024
        return resolver;
    }
    
    @Bean
    @Order(0)
    public MultipartFilter multipartFilter() {
    	MultipartFilter multipartFilter = new MultipartFilter();
    	multipartFilter.setMultipartResolverBeanName("multipartResolver");
    	return multipartFilter;
    }
    
    @Bean  
    public Scheduler scheduler() throws IOException, SchedulerException {  
        SchedulerFactory schedulerFactory = new StdSchedulerFactory(quartzProperties());  
        Scheduler scheduler = schedulerFactory.getScheduler();  
        scheduler.start();  
        return scheduler;  
    } 
    /** 
     * 设置quartz属性 
     * @throws IOException 
     * 2016年10月8日下午2:39:05 
     */  
    public Properties quartzProperties() throws IOException {  
        Properties prop = new Properties();  
        prop.put("quartz.scheduler.instanceName", "ServerScheduler");  
        prop.put("org.quartz.scheduler.instanceId", "AUTO");  
        prop.put("org.quartz.scheduler.skipUpdateCheck", "true");  
        prop.put("org.quartz.scheduler.instanceId", "NON_CLUSTERED");  
        prop.put("org.quartz.scheduler.jobFactory.class", "org.quartz.simpl.SimpleJobFactory"); 
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");  
        prop.put("org.quartz.threadPool.threadCount", "5");  
        return prop;  
    }  
    
    @Bean(name= "initCacheData")
    public InitCacheData getInitCacheData() {
        return new InitCacheData();  
    }
    
	public static void main(String[] args) {
		logger.warn( "项目开始启动。。。" );
        //SpringApplication.run("classpath:spring-common.xml", args);
		ApplicationContext ctx = (ApplicationContext)SpringApplication.run(StartUpApplication.class, args);
		SpringUtil.setApplicationContext2(ctx);
		
		 /*ConfigurableApplicationContext context = (ConfigurableApplicationContext)ctx;  
	        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)context.getBeanFactory(); 
	        beanFactory.removeBeanDefinition("multipartResolver");*/
	
        //InitCacheData initCacheData = new InitCacheData();
        //initCacheData.init();
		InitCacheData initCacheData = (InitCacheData) ctx.getBean("initCacheData");
		initCacheData.init();
	}

	// 创建事务管理器1
   /* @Bean(name = "txManager")
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}*/
    
    @Bean
    public PropertySourcesPlaceholderConfigurer createPropertySourcesPlaceholderConfigurer() {
        ClassPathResource resource = new ClassPathResource("leedane.properties");
        PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertyPlaceholderConfigurer.setLocation(resource);
        return propertyPlaceholderConfigurer;
    }

}
