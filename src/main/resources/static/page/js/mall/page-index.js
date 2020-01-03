var carousel;
var $itemsContainer;
var $brandsContainer;
layui.use(['layer', 'carousel', 'element'], function(){
	layer = layui.layer;
	carousel = layui.carousel;
	element = layui.element;
	$carouselContainer = $("#carousel-container");
	$itemsContainer = $("#items-container");
	$brandsContainer = $("#brands-container");
	getCarouselImgs();
	getShops();
	getItems();
});

var $carouselContainer;

/**
 * 获取轮播图数据
 */
function getCarouselImgs(){
	$.ajax({
		url : "/mall/home/carousels?t="+Math.random(),
		beforeSend:function(){
		},
		success : function(data) {
			$carouselContainer.empty();
			if(data != null && data.success){
				var homeCarouselBeans = data.message.homeCarouselBeans;
				if(homeCarouselBeans.length >0){
					var product;
					var carouselBean;
					$carouselContainer.append('<div carousel-item="" id="carousel-item"></div>');
					for(var i = 0; i < homeCarouselBeans.length; i++){
						product = homeCarouselBeans[i].productBean;
						carouselBean = homeCarouselBeans[i];
						var inner = '<div>'+
								    	'<img style="width: 100%; cursor: pointer;" src="'+ (isNotEmpty(carouselBean.img) ? carouselBean.img: product.mainImgLinks.split(";")[0]) +'" onclick="linkToProduct('+ carouselBean.productId +')" />'+
								    '</div>';
						$("#carousel-item").append(inner);
					}
					//图片轮播
					carousel.render({
					    elem: '#carousel-container'
					    ,width: '100%'
					    ,height: '340px'
					    ,interval: 3000
					});
				}else{
					$carouselContainer.remove();
				}
			}else{
				$carouselContainer.remove();
				ajaxError(data);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 获取分类项数据
 */
function getItems(){
	$.ajax({
		url : "/mall/home/items?t="+Math.random(),
		beforeSend:function(){
		},
		success : function(data) {
			if(data != null && data.success && data.message){
				if(data.message.length > 0){
					for(var i = 0; i < data.message.length; i++){
						$itemsContainer.append(itemsEachRow(i, data.message[i]));
					}
					layui.element.render("breadcrumb");
					//element.render('layui-row', '.layui-breadcrumb');
				}
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 
 * 每一行的
 * @param index
 * @param item
 */
function itemsEachRow(index, item){
	var productLength = item.productBeans && item.productBeans.length > 0 ? item.productBeans.length: 0;
	var html = '<div class="layui-row" desc="服装衣帽" style="margin-top: 10px;">'+
					'<div class="layui-col-md12 layui-col-xs12" >'+
						'<div class="m-title">'+ item.categoryText +
						'</div>'+
						'<div class="m-title-right">'+
						'</div>'+
						/*'<span class="layui-breadcrumb m-title-nav" lay-separator="|">'+
						  '<a href="">男装</a>'+
						  '<a href="">女装</a>'+
						  '<a href="">男鞋</a>'+
						  '<a href="">女鞋</a>'+
						  '<a href="">户外</a>'+
						  '<a href="">运动</a>'+
						  '<a href="">童装</a>'+
						'</span>'+*/
						
						'<span class="layui-breadcrumb m-title-nav" lay-separator="|">';
				//展示子分类
				if(item.childrens && item.childrens.length > 0){
					for(var i = 0; i < item.childrens.length; i++){
						html += '<a href="/mall/category/'+ item.childrens[i].key +'">'+ item.childrens[i].value + '</a>';
					}
				}
				html +=	'</span>'+
					'</div>';
				
		if(productLength > 0){
			html += '<div class="layui-col-md3 layui-col-xs12" style="text-align:center; height:320px; cursor: pointer;  background: url(\''+ item.productBeans[0].productBean.mainImgLinks.split(";")[0] +'\') no-repeat; background-size:100% 100%;" onclick="linkToProduct('+ item.productBeans[0].productBean.id +');">'+
						'<p style="position: absolute; top: 100px; background-color: #000;  filter:Alpha(opacity=0.1); color: #fff;">'+ item.productBeans[0].productBean.title +'</p>'+
						'<img alt="" src="/page/images/icon_right.png" width="30" height="30" style="position: absolute; bottom: 10px; right: 10px;"/>'+
					'</div>';
		}
		if(productLength > 1){			
			html += '<div class="layui-col-md9 layui-col-xs12 m-item-contrainer" style="text-align:right; vertical-align:right; ">'+
						'<div class="layui-col-md12 layui-col-xs12" style="background-color: #fff;">'+
							'<div class="layui-col-md4 layui-col-xs12 m-product-item m-border-top m-border-left" onclick="linkToProduct('+ item.productBeans[1].productBean.id +');">'+
								'<img alt="" src="'+ item.productBeans[1].productBean.mainImgLinks.split(";")[0] +'" class="m-product-item-img"/>'+
								'<div class="m-product-contrainer">'+
									'<a class="m-product-desc" title="'+ item.productBeans[1].productBean.title +'">'+ item.productBeans[1].productBean.title +'</a>'+
									'<p><span class="m-product-price">¥'+ item.productBeans[1].productBean.price +'</span> <span style="text-decoration: line-through; font-size: 0.6em;">原价: ¥'+ item.productBeans[1].productBean.oldPrice +'</span></p>'+
								'</div>'+
							'</div>';
			if(productLength > 2){
					html +=	'<div class="layui-col-md4 layui-col-xs12 m-product-item m-border-top" onclick="linkToProduct('+ item.productBeans[2].productBean.id +');">'+
								'<img alt="" src="'+ item.productBeans[2].productBean.mainImgLinks.split(";")[0] +'" class="m-product-item-img"/>'+
								'<div class="m-product-contrainer">'+
									'<a class="m-product-desc" title="'+ item.productBeans[2].productBean.title +'">'+ item.productBeans[2].productBean.title +'</a>'+
									'<p><span class="m-product-price">¥'+ item.productBeans[2].productBean.price +'</span> <span style="text-decoration: line-through; font-size: 0.6em;">原价: ¥'+ item.productBeans[2].productBean.oldPrice +'</span></p>'+
								'</div>'+
							'</div>';
			}
			if(productLength > 3){
					html +=	'<div class="layui-col-md4 layui-col-xs12 m-product-item m-border-top" onclick="linkToProduct('+ item.productBeans[3].productBean.id +');">'+
								'<img alt="" src="'+ item.productBeans[3].productBean.mainImgLinks.split(";")[0] +'" class="m-product-item-img"/>'+
								'<div class="m-product-contrainer">'+
									'<a class="m-product-desc" title="'+ item.productBeans[3].productBean.title +'">'+ item.productBeans[3].productBean.title +'</a>'+
									'<p><span class="m-product-price">¥'+ item.productBeans[3].productBean.price +'</span> <span style="text-decoration: line-through; font-size: 0.6em;">原价: ¥'+ item.productBeans[3].productBean.oldPrice +'</span></p>'+
								'</div>'+
							'</div>';
			}
			if(productLength > 4){
					html +='<div class="layui-col-md4 layui-col-xs12 m-product-item m-border-left m-border-bottom" onclick="linkToProduct('+ item.productBeans[4].productBean.id +');">'+
								'<img alt="" src="'+ item.productBeans[4].productBean.mainImgLinks.split(";")[0] +'" class="m-product-item-img"/>'+
								'<div class="m-product-contrainer">'+
									'<a class="m-product-desc" title="'+ item.productBeans[4].productBean.title +'">'+ item.productBeans[4].productBean.title +'</a>'+
									'<p><span class="m-product-price">¥'+ item.productBeans[4].productBean.price +'</span> <span style="text-decoration: line-through; font-size: 0.6em;">原价: ¥'+ item.productBeans[4].productBean.oldPrice +'</span></p>'+
								'</div>'+
							'</div>';
			}
			if(productLength > 5){
					html +=	'<div class="layui-col-md4 layui-col-xs12 m-product-item m-border-bottom" onclick="linkToProduct('+ item.productBeans[5].productBean.id +');">'+
								'<img alt="" src="'+ item.productBeans[5].productBean.mainImgLinks.split(";")[0] +'" class="m-product-item-img"/>'+
								'<div class="m-product-contrainer">'+
									'<a class="m-product-desc" title="'+ item.productBeans[5].productBean.title +'">'+ item.productBeans[5].productBean.title +'</a>'+
									'<p><span class="m-product-price">¥'+ item.productBeans[5].productBean.price +'</span> <span style="text-decoration: line-through; font-size: 0.6em;">原价: ¥'+ item.productBeans[5].productBean.oldPrice +'</span></p>'+
								'</div>'+
							'</div>';
			}
			if(productLength > 6){
					html +=	'<div class="layui-col-md4 layui-col-xs12 m-product-item m-border-bottom" onclick="linkToProduct('+ item.productBeans[6].productBean.id +');">'+
								'<img alt="" src="'+ item.productBeans[6].productBean.mainImgLinks.split(";")[0] +'" class="m-product-item-img"/>'+
								'<div class="m-product-contrainer">'+
									'<a class="m-product-desc" title="'+ item.productBeans[6].productBean.title +'">'+ item.productBeans[6].productBean.title +'</a>'+
									'<p><span class="m-product-price">¥'+ item.productBeans[6].productBean.price +'</span> <span style="text-decoration: line-through; font-size: 0.6em;">原价: ¥'+ item.productBeans[6].productBean.oldPrice +'</span></p>'+
								'</div>'+
							'</div>';
			}
				html +=	'</div>'+
					'</div>';
		}
		html += '</div>';
	
	return html;
}

/**
 * 获取商店列表
 */
function getShops(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/mall/home/shops?limit=6",
		dataType: 'json',
		beforeSend:function(){
			$brandsContainer.empty();
		},
		success : function(data) {
			layer.close(loadi);
			
			if(data.success && data.message){
				if(data.message.homeShopBeans == null || data.message.homeShopBeans.length == 0){
					
				}else{
					var html = '';
					//var length = 
					for(var i = 0; i < data.message.homeShopBeans.length; i++){
						html += '<div class="layui-col-md2 layui-col-xs4" style="text-align:center;">'+
							    	'<img src="'+ data.message.homeShopBeans[i].shopBean.img +'" onclick="linkToShop('+ data.message.homeShopBeans[i].shopId +');" style="width: 100%; height: 80px;"/>'+
							    '</div>';
					}
					$brandsContainer.append(html);
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
 * 跳转到店铺页
 * @param shopId
 */
function linkToShop(shopId){
	window.open('/mall/shop/'+ shopId, '_blank');
}