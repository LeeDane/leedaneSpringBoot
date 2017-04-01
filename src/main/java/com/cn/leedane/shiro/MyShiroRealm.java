package com.cn.leedane.shiro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cn.leedane.exception.user.BannedAccountException;
import com.cn.leedane.exception.user.CancelAccountException;
import com.cn.leedane.exception.user.NoActiveAccountException;
import com.cn.leedane.exception.user.NoCompleteAccountException;
import com.cn.leedane.exception.user.NoValidationEmailAccountException;
import com.cn.leedane.exception.user.StopUseAccountException;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.RoleBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.UserTokenBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.service.UserTokenService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.PlatformType;

/**
 * 
 * @author LeeDane
 * 2017年3月23日 下午2:10:22
 * version 1.0
 */
public class MyShiroRealm extends AuthorizingRealm{
	private static final Logger logger = LoggerFactory.getLogger(MyShiroRealm.class);

    @Autowired
    private UserMapper userMapper; 
    
    @Autowired
    private UserTokenService<UserTokenBean> userTokenService;
    
    @Autowired
    private UserHandler userHandle;
    
    @Autowired
    private UserService<UserBean> userService;

    /**
     * 权限认证，为当前登录的Subject授予角色和权限 
     * @see 经测试：本例中该方法的调用时机为需授权资源被访问时 
     * @see 经测试：并且每次访问需授权资源时都会执行该方法中的逻辑，这表明本例中默认并未启用AuthorizationCache 
     * @see 经测试：如果连续访问同一个URL（比如刷新），该方法不会被重复调用，Shiro有一个时间间隔（也就是cache时间，在ehcache-shiro.xml中配置），超过这个时间间隔再刷新页面，该方法会被执行
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("##################执行Shiro权限认证##################");
        System.out.println("##################执行Shiro权限认证##################");
        //获取当前登录输入的用户名，等价于(String) principalCollection.fromRealm(getName()).iterator().next();
        int userid = (int)super.getAvailablePrincipal(principalCollection); 
        //到数据库查是否有此对象
        UserBean user = userMapper.findById(UserBean.class, 1);//userMapper.findByName(loginName);// 实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        if(user!=null){
            //权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
            Set<String> roles = new HashSet<String>();
            roles.add("ADMIN");
            //用户的角色集合
            info.setRoles(/*user.getRolesName()*/roles);
            //用户的角色对应的所有权限，如果只使用角色定义访问权限，下面的四行可以不要
            List<RoleBean> roleList = /*user.getRoleList()*/ new ArrayList<RoleBean>();
            //for (RoleBean role : roleList) {
            	Set<String> permissions = new HashSet<String>();
            	permissions.add("ADMIN_MANAGER");
                info.addStringPermissions(/*role.getPermissionsName()*/ permissions);
            //}
            // 或者按下面这样添加
            //添加一个角色,不是配置意义上的添加,而是证明该用户拥有admin角色    
//            simpleAuthorInfo.addRole("admin");  
            //添加权限  
//            simpleAuthorInfo.addStringPermission("admin:manage");  
//            logger.info("已为用户[mike]赋予了[admin]角色和[admin:manage]权限");
            return info;
        }
        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        return null;
    }

    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authenticationToken) throws AuthenticationException {
    	 CustomAuthenticationToken customAuthenticationToken =(CustomAuthenticationToken) authenticationToken;
        //String dd = ReflectionToStringBuilder.toString(customAuthenticationToken, ToStringStyle.MULTI_LINE_STYLE);
    	 
    	/* Collection<Session> sessions = sessionDAO.getActiveSessions();
    	 for(Session session:sessions){

    		 if("leedane".equals(String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)))) {
    			 session.setTimeout(0);//设置session立即失效，即将其踢出系统
    			 break;
    		 }

    	 }*/
    	 
       //throw new LockedAccountException();
    	 UserBean user = customAuthenticationToken.getUser();
    	 if(user == null)
    		 throw new UnknownAccountException(); //抛出未知异常
    	 
    	 if(user.getStatus() != ConstantsUtil.STATUS_NORMAL){
     		if(user.getStatus() == ConstantsUtil.STATUS_NO_TALK){
					throw new BannedAccountException(); //抛出用户被禁言异常
				}else if(user.getStatus() == ConstantsUtil.STATUS_DELETE){
					throw new CancelAccountException(); //抛出用户已经注销异常
				}else if(user.getStatus() == ConstantsUtil.STATUS_DISABLE){
					throw new StopUseAccountException(); //抛出用户暂时被禁止使用异常
				}else if(user.getStatus() == ConstantsUtil.STATUS_NO_VALIDATION_EMAIL){
					throw new NoValidationEmailAccountException(); //抛出用户未校验邮箱异常
				}else if(user.getStatus() == ConstantsUtil.STATUS_NO_ACTIVATION){
					throw new NoActiveAccountException(); //抛出用户未激活异常
				}else if(user.getStatus() == ConstantsUtil.STATUS_INFORMATION){
					throw new NoCompleteAccountException(); //抛出用户未完善信息异常
				}else{
					throw new UnknownAccountException(); //抛出未知异常
				}
    	 }
    	 
    	 if(customAuthenticationToken.getPlatformType() == PlatformType.安卓版){
	    	 //在数据库校验token
	       	UserTokenBean userTokenBean = userTokenService.getUserToken(user, customAuthenticationToken.getToken(), null);
	       	if(userTokenBean != null){
	       		return new SimpleAuthenticationInfo(customAuthenticationToken.getUserId(), userTokenBean.getToken(), getName());
	       	}else
	       		throw new UnsupportedTokenException(); //抛出不支持的token异常	
        }else{
        	return new SimpleAuthenticationInfo(user.getId(), customAuthenticationToken.getPassword(), getName());
        }
    }
    
    @Override
    public void setCachingEnabled(boolean cachingEnabled) {
    	cachingEnabled = false;
    	super.setCachingEnabled(cachingEnabled);
    }
}
