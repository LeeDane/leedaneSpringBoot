var $categoryListContainer; // 容器
var alreadyData, noData;
var currentItemIndex = 0;//全局标记当前的item项所在的索引
layui.use(['layer', 'form', 'element'], function(){
	layer = layui.layer;
	form = layui.form;
	var element1 = layui.element;
	$carouselContainer = $("#carousel-container");
	$shopContainer = $("#shop-container");
	$categoryListContainer = $("#categoryListContainer");
	
	form.on('select(categorys-filter)', function(data){ 
		console.log(data.elem); //得到select原始DOM对象 
		getMallHomeCategory(data.value);
	});
	
	
	//下面的tab删除事件无效，不知道是layui的bug还是自己没有使用好，通过解除绑定事件的方式处理
	/*element.on('tabDelete(test1)', function(data){
		  console.log(this); //当前Tab标题所在的原始DOM元素
		  console.log(data.index); //得到当前Tab的所在下标
		  console.log(data.elem); //得到当前的Tab大容器
		});*/
	
	//删除分类的操作
	$('.layui-tab-close').unbind("click"); 
	$(".layui-tab-close").on("click", function(e1){
		//阻止事件的冒泡
		event.preventDefault();
		event.stopPropagation();
		var thisObj = $(this);
		var itemId = thisObj.closest("li").attr("lay-id");
		layer.confirm('确定要在首页移除分类吗？将同时清空该分类下面关联的子分类以及商品列表，此是不可逆行为，请慎重！', function(index){
	    	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
			$.ajax({
				type : "DELETE",
				url : "/mall/home/item/"+ itemId,
				dataType: 'json',
				success : function(data) {
					layer.close(loadi);
					if(data.success){
						var layId = thisObj.closest("li").attr("lay-id");
						element.tabDelete("categoryListFilter", layId);
						layer.closeAll();
					}else{
						ajaxError(data);
					}
				},
				error : function(data) {
					ajaxError(data);
				}
			});
      });
		
	});
	getMallHomeCategory($categoryListContainer.find(".layui-this").eq(0).attr("lay-id"));
	getCarousels();
	getShops();
	
	element.on('tab(categoryListFilter)', function(data){
		var layId = $(data.elem.context).attr("lay-id");
		currentItemIndex = data.index;
		getMallHomeCategory(layId);
		return false;
	});
	
	$(document).on("click", '.already-allot-item', function(event){
		index = $(".already-allot-item").index($(this));
		var data = alreadyData[index];
		$("#no-allot").append('<span class="layui-badge no-allot-item hand" style="margin-top: 5px; margin-right: 5px;">'+ data.value +'</span>');
		noData.push(data);
		alreadyData.splice(index, 1);//开始位置,删除个数
		$(this).remove();
	});
	
	$(document).on("click", ".no-allot-item", function(event){
		index = $(".no-allot-item").index($(this));
		var data = noData[index];
		$("#already-allot").append('<span class="layui-badge layui-bg-orange already-allot-item hand" style="margin-top: 5px; margin-right: 5px;">'+ data.value +'</span>');
		alreadyData.push(data);
		noData.splice(index, 1);//开始位置,删除个数
		$(this).remove();
	});
});
/*********************************     管理轮播      *******************************************/
var $carouselContainer;
/**
 * 获取轮播商品列表
 */
function getCarousels(){
	//异步请求去保存轮播数据并获取全部的轮播数据
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，mall/home/item/执行layer.close(loadi)即可
	$.ajax({
		type : "get",
		url : "/mall/home/carousels",
		dataType: 'json',
		beforeSend:function(){
			$carouselContainer.empty();
		},
		success : function(data) {
			layer.close(loadi);
			
			if(data.success && data.message){
				if(data.message.homeCarouselBeans == null || data.message.homeCarouselBeans.length == 0){
					
				}else{
					for(var i = 0; i < data.message.homeCarouselBeans.length; i++){
						buildEachCarouselRow(i, data.message.homeCarouselBeans[i]);
					}
				}
				//alert(new Date(data.message.homeCarouselBeans[1].createTime).Format("yyyy-MM-dd HH:mm:ss"));
				//dealProductSearch(data.message[5], data.total);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 构建每一行轮播商品html
 * @param product
 * @param index
 */
function buildEachCarouselRow(index, carouselBean){
		var product = carouselBean.productBean;
		var html = '<div class="layui-col-md2 layui-col-xs12">'+
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ (isNotEmpty(carouselBean.img) ? carouselBean.img: product.mainImgLinks.split(";")[0]) +'" class="m-product-item-img"/>		'+						
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" href="/mall/product/'+ carouselBean.productId +'/detail" title="'+ changeNotNullString(product.title) +'">'+ changeNotNullString(product.title) +'</a>'+
								'<p style="text-align: left;"><span class="m-product-price">¥'+ product.price +'</span> <span style="float: right; margin-right: 8px; text-decoration: line-through; font-size: 0.6em;">原价: ¥'+product.oldPrice +'</span></p>'+
								'<p style="text-align: left;"><span class="m-product-price" style="font-size: 1.0em;">比率：'+ product.cashBackRatio +'%</span>&nbsp;&nbsp;<span style="float: right; margin-right: 8px; font-size: 1.0em;">佣金: ¥'+(product.price * product.cashBackRatio / 100).toFixed(2) +'</span></p>'+
								'<p><button class="layui-btn layui-btn-danger" style="height: 30px; line-height: 30px; margin-top: 5px;" onclick="deleteCarousel(this, '+ carouselBean.id +');">删除</button></p>'+
							'</div>'+
						'</div>'+
					'</div>';
	
		$carouselContainer.append(html);
}

/**
 * 删除轮播商品
 * @param obj
 * @param carouselId
 */
function deleteCarousel(obj, carouselId){
	
	layer.confirm('确定要移除轮播展示的商品吗？此是不可逆行为，请慎重！', function(index){
    	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
    	$.ajax({
    		type : "DELETE",
    		url : "/mall/home/carousel/"+ carouselId,
    		dataType: 'json',
    		beforeSend:function(){
    			
    		},
    		success : function(data) {
    			layer.close(loadi);
    			if(data.success){
    				layer.msg(data.message + "，1秒后将自动刷新");
    				reloadPage(1000);
    			}else{
    				ajaxError(data);
    			}
    		},
    		error : function(data) {
    			layer.close(loadi);
    			ajaxError(data);
    		}
    	});
      });
}
	
/**
 * 选择轮播商品后的回调函数
 * @param product
 */
function addCarousel(product){
	console.log(product);
	//异步请求去保存轮播数据并获取全部的轮播数据
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "POST",
		data: {"product_id": product.auctionId, order: 1},
		url : "/mall/home/carousel",
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				layer.msg(data.message + "，1秒后将自动刷新");
				reloadPage(1000);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}
/*********************************     商店       ******************************************/
var $shopContainer;
/**
 * 选择轮播商店后的回调函数
 * @param shop
 */
function addShop(shop){
	console.log(shop);
	//异步请求去保存轮播数据并获取全部的轮播数据
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "POST",
		data: {"shop_id": shop.id, shop_order: 1},
		url : "/mall/home/shop",
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				layer.msg(data.message + "，1秒后将自动刷新");
				reloadPage(1000);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 获取商店列表
 */
function getShops(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/mall/home/shops",
		dataType: 'json',
		beforeSend:function(){
			$shopContainer.empty();
		},
		success : function(data) {
			layer.close(loadi);
			
			if(data.success && data.message){
				if(data.message.homeShopBeans == null || data.message.homeShopBeans.length == 0){
					
				}else{
					for(var i = 0; i < data.message.homeShopBeans.length; i++){
						buildEachShopRow(i, data.message.homeShopBeans[i]);
					}
				}
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 构建每一行商店html
 * @param index
 * @param shopBean
 */
function buildEachShopRow(index, shopBean){
		var shop = shopBean.shopBean;
		var html = '<div class="layui-col-md2 layui-col-xs12">'+
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ changeNotNullString(shop.img) +'" class="m-product-item-img"/>		'+						
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" href="/mall/shop/'+ shop.id +'/detail" title="'+ changeNotNullString(shop.name) +'">'+ changeNotNullString(shop.name) +'</a>'+
								'<p><button class="layui-btn layui-btn-danger" style="height: 30px; line-height: 30px; margin-top: 5px;" onclick="deleteShop(this, '+ shopBean.id +');">删除</button></p>'+
							'</div>'+
						'</div>'+
					'</div>';
	
		$shopContainer.append(html);
}

/**
 * 删除商店
 * @param obj
 * @param shopId
 */
function deleteShop(obj, shopId){
	
	layer.confirm('确定要移除该商店吗？此是不可逆行为，请慎重！', function(index){
    	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
    	$.ajax({
    		type : "DELETE",
    		url : "/mall/home/shop/"+ shopId,
    		dataType: 'json',
    		beforeSend:function(){
    			
    		},
    		success : function(data) {
    			layer.close(loadi);
    			if(data.success){
    				layer.msg(data.message + "，1秒后将自动刷新");
    				reloadPage(1000);
    			}else{
    				ajaxError(data);
    			}
    		},
    		error : function(data) {
    			layer.close(loadi);
    			ajaxError(data);
    		}
    	});
      });
}

/*********************************     新增项     *******************************************/
function addItem(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "GET",
		url : "/mall/home/item/noList?t="+ Math.random(),
		dataType: 'json',
		beforeSend:function(){
			
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success && data.message.no){
				//页面层-自定义
				layer.open({
				  type: 1,
				  title: '新增首页展示的分类',
				  closeBtn: 0,
				  shadeClose: false,
				  skin: 'layui-layer-rim',
				  content: buildAddCategoryDiv(data.message.no),
				  scrollbar: false,
				  success: function(layero, index){
					  form.render();
					  //监听提交
					  form.on('submit(add-category-form-submit)', function(data){
					    //layer.msg(JSON.stringify(data.field));
					    var categoryId = $(this).closest("form").find('select[name="new-category"]').val();//分类id
					    var number = parseInt($(this).closest("form").find('input[name="show-number"]').val()); //数量
					    if(number < 1 || number > 10){
					    	layer.msg("数量不合法，请重新输入");
					    	return false;
					    }
					    
					    var order = parseInt($(this).closest("form").find('input[name="show-order"]').val()); //排序
					    if(order < 1){
					    	layer.msg("排序输入不合法，请重新输入");
					    	return false;
					    }
					    var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
						$.ajax({
							type : "POST",
							url : "/mall/home/item",
							data: {number: number, category_order: order, category_id: categoryId},
							dataType: 'json',
							success : function(data) {
								layer.close(loadi);
								if(data.success){
									layer.msg(data.message + "，1秒后将自动刷新");
									reloadPage(1000);
								}else{
									ajaxError(data);
								}
							},
							error : function(data) {
								ajaxError(data);
							}
						});
					    return false;
					  });
				  }
				});
				
				
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}
/**
 * 构建对添加分类的div
 * @param no
 */
function buildAddCategoryDiv(no){
	var html = '<div>'+
					'<form class="layui-form" style="padding: 10px;">'+ 
					    '<div class="layui-form-item">'+
					    	'<label class="layui-form-label">选择分类</label>'+ 
					    	'<div class="layui-input-inline">'+ 
								'<select lay-search="" name="new-category">';
					for(var i = 0; i < no.length; i++){
						   html += '<option value="'+ no[i].key +'" text="'+ no[i].value +'">'+ no[i].value +'</option>';
					}    	
					  html +=  '</select>'+
					  		'</div>'+
			  			'</div>'+
			  			'<div class="layui-form-item">'+
			  				'<label class="layui-form-label">展示数量</label>'+ 
			  				'<div class="layui-input-block">'+
				  		      '<input type="number" value="1"  name="show-number"  min="1" max="10" step="1" lay-verify="required|number" autocomplete="off" placeholder="请输入大于0小于11的整数" class="layui-input">'+
				  		    '</div>'+
				    	'</div>'+
				    	'<div class="layui-form-item">'+
			  				'<label class="layui-form-label">展位排序</label>'+ 
			  				'<div class="layui-input-block">'+
				  		      '<input type="number" value="1"  name="show-order"  min="1" step="1" lay-verify="required|number" autocomplete="off" placeholder="请输入大于0的整数" class="layui-input">'+
				  		    '</div>'+
				    	'</div>'+
				'<button style="float: right; margin-right: 5px; margin-bottom: 5px;" class="layui-btn layui-btn-small" lay-submit="" lay-filter="add-category-form-submit" title="提交">立即提交</button><button style="float: right; margin-right: 5px; margin-bottom: 5px;" class="layui-btn layui-btn-small layui-btn-primary" title="取消" onclick="cancelCategory();">取消</button></form></div>';
	
	return html;
}

/**
 * 提交添加分类
 * @param obj
 */
function addCategorySubmit(obj){
	
}

/*********************************     管理分类      *******************************************/
/**
 * 获取商城首页的分类
 * @param itemId
 */
function getMallHomeCategory(itemId){
	$categoryListContainer.find(".layui-show").find("#sunCategoryContainer").find(".layui-badge").remove();
	//异步请求去保存轮播数据并获取全部的轮播数据
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "GET",
		url : "/mall/home/item/"+ 165 +"?t="+ Math.random(),
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				if(data.message){
					var baseInfoHtml = '<div>展示的数量： <span>'+ data.message.number +'<i class="layui-icon hand" onclick="updateItemNumber(this, '+ data.message.number +', '+ data.message.itemId +');" style="font-size: 15px; color: #1E9FFF;">&#xe642;</i></span></div>'+
				    					'<div>排序号： <span>'+ data.message.order +'<i class="layui-icon hand" onclick="updateItemOrder(this, '+ data.message.order +', '+ data.message.itemId +');" style="font-size: 15px; color: #1E9FFF;">&#xe642;</i></span></div>';
					$categoryListContainer.find(".layui-show").find("#baseInfoCategory").html(baseInfoHtml);
					appendChildren(data.message.childrens);
					appendProducts(data.message.productBeans);
				}
			}else{
				ajaxError(data);
				$categoryListContainer.find(".layui-tab-item").eq(currentItemIndex).html("获取错误");
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 弹出修改项的展示数量页面
 * @param obj
 * @param number
 * @param itemId
 */
function updateItemNumber(obj, number, itemId){
	//页面层-自定义
	layer.open({
	  type: 1,
	  title: '修改显示数量',
	  closeBtn: 0,
	  shadeClose: false,
	  skin: 'yourclass',
	  content: '<input class="update-item-input-style" type="number" value="1" min="1" max="10" step="1" lay-verify="required|number" autocomplete="off" placeholder="请输入大于0小于11的整数"></input>'+
		  '<div><button style="float: right; margin-right: 5px; margin-bottom: 5px;" class="layui-btn layui-btn-small" title="提交" onclick="submitUpdateItemNumber(this, '+ itemId +');">立即提交</button><button style="float: right; margin-right: 5px; margin-bottom: 5px;" class="layui-btn layui-btn-small layui-btn-primary" title="取消" onclick="cancelCategory();">取消</button></div>'
	});
}

/**
 * 执行修改分类项的数量
 * @param obj
 * @param itemId
 */
function submitUpdateItemNumber(obj, itemId){
	var number = $(obj).closest(".layui-layer-content").find("input").val();
	if(number < 1 || number > 10){
    	layer.msg("数量不合法，请重新输入");
    	return false;
    }
	
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "PUT",
		data:{number: number},
		url : "/mall/home/item/"+ itemId,
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				if(data.message){
					layer.closeAll();
					layer.msg(data.message + "，1秒后将自动刷新");
					reloadPage(1000);
				}
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 弹出修改项的展示排序页面
 * @param obj
 * @param order
 * @param itemId
 */
function updateItemOrder(obj, order, itemId){
	//页面层-自定义
	layer.open({
	  type: 1,
	  title: '修改显示排序',
	  closeBtn: 0,
	  shadeClose: false,
	  skin: 'yourclass',
	  content: '<input class="update-item-input-style" type="number" value="1" min="1" max="10" step="1" lay-verify="required|number" placeholder="请输入大于0的整数"></input>'+
		  '<div><button style="float: right; margin-right: 5px; margin-bottom: 5px;" class="layui-btn layui-btn-small" title="提交" onclick="submitUpdateItemOrder(this, '+ itemId +');">立即提交</button><button style="float: right; margin-right: 5px; margin-bottom: 5px;" class="layui-btn layui-btn-small layui-btn-primary" title="取消" onclick="cancelCategory();">取消</button></div>'
	});
}

/**
 * 执行修改分类项的排序
 * @param obj
 * @param itemId
 */
function submitUpdateItemOrder(obj, itemId){
	var order = $(obj).closest(".layui-layer-content").find("input").val();
	if(order < 1){
    	layer.msg("排序输入不合法，请重新输入");
    	return false;
    }
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "PUT",
		data:{order: order},
		url : "/mall/home/item/"+ itemId,
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				if(data.message){
					layer.closeAll();
					layer.msg(data.message + "，1秒后将自动刷新");
					reloadPage(1000);
				}
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 插入子分类列表
 * @param childerns
 */
function appendChildren(childerns){
	var html = '';
	if(childerns){
		for(var i = 0; i < childerns.length; i++){
			html += '<span class="layui-badge layui-bg-orange" style="margin-top: 5px; margin-right: 5px;">'+ childerns[i].value +'</span>';
		}
	}
	$categoryListContainer.find(".layui-show").find("#sunCategoryContainer").append(html);
}

function appendProducts(products){
	var html = '';
	$categoryListContainer.find(".layui-show").find("#productListShow").empty();
	if(products){
		for(var i = 0; i < products.length; i++){
				var product = products[i].productBean;
			    html += '<div class="layui-col-md2 layui-col-xs12">'+
							'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
								'<img alt="" src="'+ product.mainImgLinks.split(";")[0] +'" class="m-product-item-img"/>		'+						
								'<div class="m-product-contrainer">'+
									'<a class="m-product-desc" href="/mall/product/'+ product.id +'/detail" title="'+ changeNotNullString(product.title) +'">'+ changeNotNullString(product.title) +'</a>'+
									'<p style="text-align: left;"><span class="m-product-price">¥'+ product.price +'</span> <span style="float: right; margin-right: 8px; text-decoration: line-through; font-size: 0.6em;">原价: ¥'+product.oldPrice +'</span></p>'+
									'<p style="text-align: left;"><span class="m-product-price" style="font-size: 1.0em;">比率：'+ product.cashBackRatio +'%</span>&nbsp;&nbsp;<span style="float: right; margin-right: 8px; font-size: 1.0em;">佣金: ¥'+(product.price * product.cashBackRatio / 100).toFixed(2) +'</span></p>'+
									'<p><button class="layui-btn layui-btn-danger" style="height: 30px; line-height: 30px; margin-top: 5px;" onclick="deleteItemProduct(this, '+ products[i].itemId +' , '+ products[i].id +');">删除</button></p>'+
								'</div>'+
							'</div>'+
						'</div>';
		}
	}
	$categoryListContainer.find(".layui-show").find("#productListShow").append(html);
}

/**
 * 管理分类
 */
function manageCategory(obj){
	var ddd = $(obj).closest(".layui-tab-item");
	var itemId = ddd.attr("data-id");
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "GET",
		url : "/mall/home/item/"+ itemId +"/matching?t="+ Math.random(),
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				//页面层-自定义
				layer.open({
				  type: 1,
				  title: '子分类的管理',
				  closeBtn: 0,
				  shadeClose: false,
				  skin: 'yourclass',
				  content: buildChilderCategoryManageDiv(data.message, itemId)
				});
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 构建对子分类管理的div
 * @param message
 * @param itemId
 */
function buildChilderCategoryManageDiv(message, itemId){
	alreadyData = message.already;
	noData = message.no;
	var html = '<div>'+
					'<fieldset class="layui-elem-field">'+
					  '<legend>已经分配的分类(点击删除)</legend>'+
					  '<div class="layui-field-box" id="already-allot">';
			for(var i = 0 ; i < message.already.length; i++){
				html += '<span class="layui-badge layui-bg-orange already-allot-item hand" style="margin-top: 5px; margin-right: 5px;">'+ message.already[i].value +'</span>';
			}
		 
			html += ' </div>'+
					'</fieldset>'+
					'<fieldset class="layui-elem-field">'+
					  '<legend>未分配的分类(点击添加)</legend>'+
					  '<div class="layui-field-box" id="no-allot">';
			for(var i = 0 ; i < message.no.length; i++){
				html += '<span class="layui-badge no-allot-item hand" style="margin-top: 5px; margin-right: 5px;">'+ message.no[i].value +'</span>';
			}
			 html += ' </div>'+
					'</fieldset>'+
				'</div>'+
				'<div><button style="float: right; margin-right: 5px; margin-bottom: 5px;" class="layui-btn layui-btn-small" title="提交" onclick="submitCategory('+ itemId +');">立即提交</button><button style="float: right; margin-right: 5px; margin-bottom: 5px;" class="layui-btn layui-btn-small layui-btn-primary" title="取消" onclick="cancelCategory();">取消</button></div>';
	
	return html;
}

/**
 * 提交分类
 * @param itemId
 */
function submitCategory(itemId){
	var updataId = "";
	for(var i = 0; i < alreadyData.length; i++){
		if(i == alreadyData.length - 1)
			updataId += alreadyData[i].key;
		else
			updataId += alreadyData[i].key + ",";
	}
	
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "PUT",
		url : "/mall/home/item/"+ itemId +"/category",
		data: {data: JSON.stringify(alreadyData)},
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				layer.closeAll();
				layer.msg(data.message + "，1秒后将自动刷新");
				reloadPage(1000);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 取消分类
 */
function cancelCategory(){
	layer.closeAll();
}
/*********************************     管理商品      *******************************************/
/**
 * 管理商品
 * @param obj
 */
function manageProduct(obj){
	var itemId = $(obj).closest(".layui-tab-item").attr("data-id");
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "GET",
		url : "/mall/home/item/"+ itemId +"/products",
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				//页面层-自定义
				layer.open({
				  type: 1,
				  title: false,
				  closeBtn: 0,
				  shadeClose: false,
				  skin: 'yourclass',
				  content: buildChilderCategoryManageDiv(data.message, itemId)
				});
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 选择item的商品的回调函数
 * @param product
 */
function getItemProduct(product){
	console.log(product);
	var itemId = $categoryListContainer.find(".layui-this").attr("lay-id");
	//异步请求去保存轮播数据并获取全部的轮播数据
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "POST",
		data: {"product_id": product.id},
		url : "/mall/home/item/"+ itemId +"/product",
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				layer.msg(data.message + "，1秒后将自动刷新");
				reloadPage(1000);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 移除分类项的商品
 * @Param obj
 * @Param itemId
 * @Param itemProductId
 */
function deleteItemProduct(obj, itemId, itemProductId){
	layer.confirm('确定要移除该项的商品吗？此是不可逆行为，请慎重！', function(index){
		//异步请求去保存轮播数据并获取全部的轮播数据
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : "DELETE",
			url : "/mall/home/item/"+ itemId +"/product/"+ itemProductId,
			dataType: 'json',
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.success){
					layer.msg(data.message + "，1秒后将自动刷新");
					reloadPage(1000);
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
	});
}