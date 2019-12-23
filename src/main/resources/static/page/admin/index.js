BUI.use('common/main',function(){
var config = [{
        id:'welcome',
        homePage : 'welcome',
        menu:[{
            text:'基本操作',
            items:[
                {id:'welcome',text:'欢迎页面',href:'/ad/wc/welcome'},
                {id:'history',text:'登录历史',href:'/ad/wc/loginHistory'},
                {id:'about',text:'关于我们',href:'/ad/wc/about'},
                {id:'contact',text:'联系我们',href:'/ad/wc/contact'},
                {id:'appdownload',text:'APP下载',href:'/ad/wc/download'}
            ]
        }]
    },{
        id: 'user',
        homePage: 'search',
        menu:[{
            text: '用户管理',
            items:[
                {id: 'search', text: '查询用户', href: '/ad/us/search', closeable: false},
                {id:'new',text:'新增用户',href:'/ad/us/new'},
                {id:'active',text:'在线用户',href:'/ad/us/active'},
                {id:'black',text:'黑名单用户',href:'/ad/us/black'}
            ]
        },{
            text:'用户统计',
            items:[
                {id:'chart',text:'图表展示',href:'/ad/us/chart'},
            ]
        }]
    },{
        id: 'blog',
        homePage: 'publish',
        menu:[{
            text:'博客管理',
            items:[
                {id:'publish',text:'写一博',href:'/pb?noHeader=true',closeable : false},
                {id:'search',text:'查询博客',href: '/ad/bg/search'},
                {id:'manager',text:'管理博客',href: '/index?noHeader=true'},
                {id:'draft',text:'博客草稿',href:'/ad/bg/draft.jsp'}
            ]
        },{
            text:'博客统计',
            items:[
                {id:'chart',text:'图表展示',href:'blog/chart.jsp'}
            ]
        },{
            text:'博客审核',
            items:[
                {id:'check',text:'博客审核',href:'/ad/bg/check'}
            ]
        }]
    },{
        id: 'circle',
        homePage: 'task',
        menu:[{
            text:'任务配置',
            items:[
                {id:'task',text:'任务管理',href:'/ad/cc/task',closeable : false}
            ]
        }]
    },{
        id: 'permission',
        homePage: 'permission',
        menu:[{
            text:'权限管理',
            items:[
                {id:'permission',text:'权限管理',href:'/ad/pm/permission'}
            ]
        },{
            text:'角色管理',
            items:[
                {id:'role',text:'角色管理',href:'/ad/pm/role'}
            ]
        },{
            text:'链接管理',
            items:[
                {id:'link',text:'链接管理',href:'/ad/pm/link'}
            ]
        }]
    },{
        id: 'mall',
        homePage: 'home',
        menu:[{
            text:'首页管理',
            items:[
                {id:'home',text:'首页管理',href:'/ad/mall/home'}
            ]
        },{
            text:'商品管理',
            items:[
                {id:'product',text:'商品管理',href:'/ad/mall/product'},
                {id:'product',text:'添加商品',href:'/ad/mall/product-add'}
            ]
        },{
            text:'其他',
            items:[
                {id:'other',text:'其他',href:'/ad/mall/other'}
            ]
        }]
    },{
        id: 'setting',
        homePage: 'job',
        menu:[{
            text:'任务调度',
            items:[
                {id:'job',text:'任务管理',href:'/ad/st/job'}
            ]
        },{
            text:'选项配置',
            items:[
                {id:'option',text:'系统选项',href:'/ad/st/option'}
            ]
        },{
            text:'缓存管理',
            items:[
                {id:'clear',text:'清理缓存',href:'/ad/st/clearCache'}
            ]
        }]
    }];
  new PageUtil.MainPage({
    modulesConfig : config
  });
});

/**
 * 打开当前模块的菜单配置页面
 *
 */
 function openPreModuleTab(id, searchParams){ 
	 openTab(null, id, searchParams);
}

/**
* 打开菜单配置页面
*
*/
function openTab(moduleId, id, searchParams){ 
	if(isEmpty(id)){
		layer.msg("菜单配置页面ID为空");
		return;
	}
	var topManager = top.topManager;    	
	if(topManager){
		var index = topManager.__attrVals.currentModelIndex;
		var config = topManager.__attrVals.modulesConfig;
		var hasId = false;
		var menus = config[index].menu;
		var preModuleId = config[index].id;
		
		
		if(isNotEmpty(moduleId) && preModuleId != moduleId){
			//不同模块的切换，当前的模块将清空
			layer.confirm('检测到您将切换到其他模块，当前的模块将临时被替换，是否继续操作？', {
				  btn: ['确定','点错了'] //按钮
			}, function(){
				layer.closeAll('dialog');
				//获取新的菜单列表
				for(var i = 0; i < config.length; i++){
					if(config[i].id == moduleId){
						menus = config[i].menu;
						break;
					}
				}
				openOneTab(moduleId, id, searchParams, menus, hasId);
			}, function(){
				
			});
		}else{
			openOneTab(moduleId, id, searchParams, menus, hasId);
		}
	}
}

/**
*	执行打开页面
*/
function openOneTab(moduleId, id, searchParams, menus, hasId){
	if(typeof(menus) != 'undefined' && menus.length > 0){
		var menu;
		for(var i = 0; i < menus.length; i++){
			menu = menus[i];
			for(var j = 0; j < menu.items.length; j++){
				if(menu.items[j].id == id){
    				hasId = true;
    				break;
    			}
			}
		}
		if(!hasId){
			layer.msg("当前的Tab在配置中找不到，请核实！");
			return;
		}
		
		//打开左侧菜单中配置过的页面
	  	top.topManager.openPage({
	  		moduleId: moduleId,
	    	id : id,
	    	//search : 'a=123&b=456'
	    	search : searchParams
	  	});
	}else{
		layer.msg("菜单配置有错误，请认真核查！");
		return;
	}
}