package com.cn.leedane.shiro;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by sun on 2017-3-21.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    /**
     * 此方法把该拦截器实例化成一个bean,否则在拦截器里无法注入其它bean
     * @return
     */
    /*@Bean
    SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor();
    }*/
    /**
     * 配置拦截器
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(sessionInterceptor())
               /* .addPathPatterns("/**")
                .excludePathPatterns("/login","/permission/userInsert",
                        "/error","/tUser/insert","/gif/getGifCode")*/;
    }

}