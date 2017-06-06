//退出登录
function logout(){
	$.ajax({
		dataType: 'json',
		url : "/us/logout?t="+Math.random(),
		beforeSend:function(){
		},
		success : function(data) {
			layer.msg(data.message);
			if(data.isSuccess)
				//刷新当前页面
				window.location.reload();
			else
				ajaxError(data);
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

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
             id: 'setting', 
             homePage: 'job',
             menu:[{
                 text:'任务调度',
                 items:[
                        {id:'job',text:'任务管理',href:'/ad/st/job'}
                 ]
             }]
         },{
	        id:'form',
	        menu:[{
	            text:'表单页面',
	            items:[
	              {id:'code',text:'表单代码',href:'form/code.html'},
	              {id:'example',text:'表单示例',href:'form/example.html'},
	              {id:'introduce',text:'表单简介',href:'form/introduce.html'},
	              {id:'valid',text:'表单基本验证',href:'form/basicValid.html'},
	              {id:'advalid',text:'表单复杂验证',href:'form/advalid.html'},
	              {id:'remote',text:'远程调用',href:'form/remote.html'},
	              {id:'group',text:'表单分组',href:'form/group.html'},
	              {id:'depends',text:'表单联动',href:'form/depends.html'}
	            ]
	          },{
	            text:'成功失败页面',
	            items:[
	              {id:'success',text:'成功页面',href:'form/success.html'},
	              {id:'fail',text:'失败页面',href:'form/fail.html'}
	            
	            ]
	          },{
	            text:'可编辑表格',
	            items:[
	              {id:'grid',text:'可编辑表格',href:'form/grid.html'},
	              {id:'form-grid',text:'表单中的可编辑表格',href:'form/form-grid.html'},
	              {id:'dialog-grid',text:'使用弹出框',href:'form/dialog-grid.html'},
	              {id:'form-dialog-grid',text:'表单中使用弹出框',href:'form/form-dialog-grid.html'}
	            ]
	          }]
         },{
	        id:'search',
	        menu:[{
	            text:'搜索页面',
	            items:[
	              {id:'code',text:'搜索页面代码',href:'search/code.html'},
	              {id:'example',text:'搜索页面示例',href:'search/example.html'},
	              {id:'example-dialog',text:'搜索页面编辑示例',href:'search/example-dialog.html'},
	              {id:'introduce',text:'搜索页面简介',href:'search/introduce.html'}, 
	              {id:'config',text:'搜索配置',href:'search/config.html'}
	            ]
	          },{
	            text : '更多示例',
	            items : [
	              {id : 'tab',text : '使用tab过滤',href : 'search/tab.html'}
	            ]
	          }]
	      },{
	        id:'detail',
	        menu:[{
	            text:'详情页面',
	            items:[
	              {id:'code',text:'详情页面代码',href:'detail/code.html'},
	              {id:'example',text:'详情页面示例',href:'detail/example.html'},
	              {id:'introduce',text:'详情页面简介',href:'detail/introduce.html'}
	            ]
	          }]
	      },{
	        id : 'chart',
	        menu : [{
	          text : '图表',
	          items:[
	              {id:'code',text:'引入代码',href:'chart/code.html'},
	              {id:'line',text:'折线图',href:'chart/line.html'},
	              {id:'area',text:'区域图',href:'chart/area.html'},
	              {id:'column',text:'柱状图',href:'chart/column.html'},
	              {id:'pie',text:'饼图',href:'chart/pie.html'}, 
	              {id:'radar',text:'雷达图',href:'chart/radar.html'}
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