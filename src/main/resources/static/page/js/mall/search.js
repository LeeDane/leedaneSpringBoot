var SEARCH_TYPE_PRODUCT = 5; //商品
var SEARCH_TYPE_SHOP = 4; //商店
var SEARCH_TYPE_TAOBAO = 6; //淘宝商品
layui.use(['form', 'laypage'], function(){
	  var element = layui.element;
	  //监听Tab切换，以改变地址hash值
	  element.on('tab(resultTabBrief)', function(data){
		  console.log(this); //当前Tab标题所在的原始DOM元素
		  console.log(data.index); //得到当前Tab的所在下标
		  switch(data.index){
		  case 0:
			   preType = SEARCH_TYPE_PRODUCT;
			   product_start = 0;
			   product_currentIndex = 0;
			  	//商品
			  	doSearch();
		  		break;
		  	case 1:  //商店 
		  		preType = SEARCH_TYPE_SHOP;
		  		shop_start = 0;
				shop_currentIndex = 0;
		  		console.log(data.elem); //得到当前的Tab大容器
		  		doSearch();
			  break;
			  
		  	case 2:  //查询淘宝的商品
		  		preType = SEARCH_TYPE_TAOBAO;
		  		taobao_start = 0;
				taobao_currentIndex = 0;
		  		console.log(data.elem); //得到当前的Tab大容器
		  		doSearch();
			  break;
		  }
		});
	  
	  laypage = layui.laypage;
	  
	  $(".common-search").remove();
	  searchKey = getURLParam(decodeURI(window.location.href), "q");
	  preType = getURLParam(decodeURI(window.location.href), "tp");
	  
	  $SearchContainer = $("input[name='shop-search-value']");
	  $("input[name='shop-search-value']").bind("input propertychange", function() {
			console.log($(this).val());
		});
	  
	  if(isEmpty(searchKey)){
		console.log("获取不到您要检索的关键字！");
		return;
	  }
	
	  $SearchContainer.val(searchKey);
	  //搜索商品
	  if(isEmpty(preType) || preType == SEARCH_TYPE_PRODUCT){
		  preType = SEARCH_TYPE_PRODUCT;
		  $("#li-product").addClass("layui-this");
		  $("#li-shop").removeClass("layui-this");
		  $("#li-taobao").removeClass("layui-this");
		  $("#tab-content-product").addClass("layui-show");
		  $("#tab-content-shop").removeClass("layui-show");
		  $("#tab-content-taobao").removeClass("layui-show");
	  }else if(preType == SEARCH_TYPE_SHOP){
		  preType = SEARCH_TYPE_SHOP;
		  $("#li-shop").addClass("layui-this");
		  $("#li-product").removeClass("layui-this");
		  $("#li-taobao").removeClass("layui-this");
		  $("#tab-content-shop").addClass("layui-show");
		  $("#tab-content-product").removeClass("layui-show");
		  $("#tab-content-taobao").removeClass("layui-show");
	  }else if(preType == SEARCH_TYPE_TAOBAO){
		  preType = SEARCH_TYPE_TAOBAO;
		  $("#li-taobao").addClass("layui-this");
		  $("#li-product").removeClass("layui-this");
		  $("#li-shop").removeClass("layui-this");
		  $("#tab-content-taobao").addClass("layui-show");
		  $("#tab-content-product").removeClass("layui-show");
		  $("#tab-content-shop").removeClass("layui-show");
	  }
	  doSearch();
	  
	  initClipBoard();
});
var $SearchContainer;
var searchKey;
var preType = 4;
$(function(){
	
});

/**
 * 搜索
 * @param obj
 */
function searchShop(obj, type){
	var searchText = $SearchContainer.val();
	if(isEmpty(searchText)){
		layer.msg("请输入您要搜索的内容！");
		$SearchContainer.focus();
		return;
	}
	searchKey = searchText;
	preType = type;
	
	 //搜索商品
	  if(isEmpty(preType) || preType == SEARCH_TYPE_PRODUCT){
		  preType = SEARCH_TYPE_PRODUCT;
		  $("#li-product").addClass("layui-this");
		  $("#li-shop").removeClass("layui-this");
		  $("#li-taobao").removeClass("layui-this");
		  $("#tab-content-product").addClass("layui-show");
		  $("#tab-content-shop").removeClass("layui-show");
		  $("#tab-content-taobao").removeClass("layui-show");
	  }else if(preType == SEARCH_TYPE_SHOP){
		  preType = SEARCH_TYPE_SHOP;
		  $("#li-shop").addClass("layui-this");
		  $("#li-product").removeClass("layui-this");
		  $("#li-taobao").removeClass("layui-this");
		  $("#tab-content-shop").addClass("layui-show");
		  $("#tab-content-product").removeClass("layui-show");
		  $("#tab-content-taobao").removeClass("layui-show");
	  }else if(preType == SEARCH_TYPE_TAOBAO){
		  preType = SEARCH_TYPE_TAOBAO;
		  $("#li-taobao").addClass("layui-this");
		  $("#li-product").removeClass("layui-this");
		  $("#li-shop").removeClass("layui-this");
		  $("#tab-content-taobao").addClass("layui-show");
		  $("#tab-content-product").removeClass("layui-show");
		  $("#tab-content-shop").removeClass("layui-show");
	  }
	doSearch();
}

/**
 * 执行搜索
 */
function doSearch(){
	var url = "";
	if(SEARCH_TYPE_PRODUCT == preType){
		$productContainer = $("#product-row-container");
		$productContainer.empty();
		url = "/s/s?"+ jsonToGetRequestParams(getProductsRequestParams());
	}else if(SEARCH_TYPE_SHOP == preType){
		$shopContainer = $("#shop-row-container");
		$shopContainer.empty();	
		url = "/s/s?"+ jsonToGetRequestParams(getShopsRequestParams());
	}else if(SEARCH_TYPE_TAOBAO == preType){
		$taobaoContainer = $("#taobao-row-container");
		$taobaoContainer.empty();
		url = "/mall/taobao/search?"+ jsonToGetRequestParams(getTaobaosRequestParams());
	}
	
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "get",
		url : url,
		dataType: 'json',
		beforeSend:function(){
			if(SEARCH_TYPE_PRODUCT == preType)
				$productContainer.empty();
		},
		beforeSend:function(){
			
		},
		success : function(data) {
			layer.close(loadi);
			$(".search-need-time").text(data.consumeTime);
			if(data.isSuccess && isNotEmpty(data.message)){
				if(data.message[preType]){
					if(SEARCH_TYPE_PRODUCT == preType)
						dealProductSearch(data.message[preType], data.total);
					else if(SEARCH_TYPE_SHOP == preType){
						dealShopSearch(data.message[preType], data.total);
					}	
				}else {
					if(SEARCH_TYPE_TAOBAO == preType)
						dealTaobaoSearch(data.message.list, data.total);
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
 * 处理是商品的搜索
 * @param message
 */
function dealProductSearch(message, total){
	$productContainer.empty();
	products = message;
	if(message.length == 0){
		if(product_currentIndex == 0){
			$productContainer.append("搜索不到符合条件的商品，请换条件试试！");
		}else{
			$productContainer.append("已经没有更多的商品啦，请重新选择！");
		}
	}else{
		for(var i = 0; i < products.length; i++){
			buildEachSearchProductRow(i, products[i]);
		}
	}
	
	if(product_currentIndex == 0 && (!products || products.length ==0)){
		$("#product-pager").empty();
		$("#product-pager").css("padding", "0");
	}else{
		$("#product-pager").css("padding", "5px");
		//执行一个laypage实例
		 laypage.render({
		    elem: 'product-pager' //注意，这里的 test1 是 ID，不用加 # 号
		    ,layout: ['prev', 'page', 'next', 'count', 'skip']
		    ,count: total[preType] //数据总数，从服务端得到
		    ,limit: product_pageSize
		    , curr: product_currentIndex + 1
		    ,jump: function(obj, first){
			    //obj包含了当前分页的所有参数，比如：
			    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
			    console.log(obj.limit); //得到每页显示的条数
			    if(!first){
			    	product_currentIndex = obj.curr -1;
			    	doSearch();
			    }
			  }
		 });
	}
}

var product_pageSize = 3;
var product_start = 0;
var product_currentIndex = 0;
var product_totalPage = 0;
var products;
var $productContainer;
var product_sort = "createTime";
var product_sort_desc= true;
/**
 * 获取商品请求列表参数
 */
function getProductsRequestParams(){
	return {keyword: searchKey, sort: product_sort, desc: product_sort_desc,  start: (product_currentIndex * product_pageSize), current: product_currentIndex, type: preType, rows: product_pageSize, t: Math.random()};
}

/**
 * 构建每一行商品html
 * @param product
 * @param index
 */
function buildEachSearchProductRow(index, product){
		var html = '<div class="layui-col-md4 layui-col-xs12">'+
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ product.mainImgLinks.split(";")[0] +'" class="m-product-item-img"/>		'+						
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" href="/mall/product/'+ product.id +'/detail" title="'+ changeNotNullString(product.title) +'">'+ changeNotNullString(product.title) +'</a>'+
								'<p style="text-align: left;"><span class="m-product-price">¥'+ product.price +'</span> <span style="float: right; margin-right: 8px; text-decoration: line-through; font-size: 0.6em;">原价: ¥'+product.oldPrice +'</span></p>'+
								'<p style="text-align: left;"><span class="m-product-price" style="font-size: 1.0em;">比率：'+ product.cashBackRatio +'%</span>&nbsp;&nbsp;<span style="float: right; margin-right: 8px; font-size: 1.0em;">佣金: ¥'+(product.price * product.cashBackRatio / 100).toFixed(2) +'</span></p>'+
							'</div>'+
						'</div>'+
					'</div>';
	
	  $productContainer.append(html);
}

/**
 * 商品时间排序
 * @param obj
 */
function productTimeSort(obj){
	/*product_sort = "createTime";
	var desc = $(this).attr("desc");
	if(desc == "true"){
		product_sort_desc = false;
		$(this).find("i").eq(0).text("&#xe61a;");
	}else{
		product_sort_desc = true;
		$(this).find("i").eq(0).text("&#xe619;");
	}
	product_currentIndex = 0;
	doSearch();*/
}

/**
 * 商品时间排序
 * @param obj
 */
function productPriceSort(obj){
	/*product_sort = "price";
	var desc = $(obj).attr("desc");
	if(desc == "true"){
		product_sort_desc = false;
		var i = $(obj).find("i");
		var i0 = i.eq(0);
		i0.text("&#xe61a;");
	}else{
		product_sort_desc = true;
		var i = $(obj).find("i");
		var i0 = i.eq(0);
		i0.text("&#xe619;");
	}
	product_currentIndex = 0;
	doSearch();*/
}

/***********************************  商店   *************************************************/
/**
 * 处理是商品的搜索
 * @param message
 */
function dealShopSearch(message, total){
	$shopContainer.empty();
	shops = message;
	if(message.length == 0){
		if(shop_currentIndex == 0){
			$shopContainer.append("搜索不到符合条件的商品，请换条件试试！");
		}else{
			$shopContainer.append("已经没有更多的商品啦，请重新选择！");
		}
	}else{
		for(var i = 0; i < shops.length; i++){
			buildEachSearchShopRow(i, shops[i]);
		}
	}
	
	if(shop_currentIndex == 0 && (!shops || shops.length ==0)){
		$("#shop-pager").empty();
		$("#shop-pager").css("padding", "0");
	}else{
		$("#shop-pager").css("padding", "5px");
		//执行一个laypage实例
		 laypage.render({
		    elem: 'shop-pager' //注意，这里的 test1 是 ID，不用加 # 号
		    ,layout: ['prev', 'page', 'next', 'count', 'skip']
		    ,count: total[preType] //数据总数，从服务端得到
		    ,limit: shop_pageSize
		    , curr: shop_currentIndex + 1
		    ,jump: function(obj, first){
			    //obj包含了当前分页的所有参数，比如：
			    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
			    console.log(obj.limit); //得到每页显示的条数
			    if(!first){
			    	shop_currentIndex = obj.curr -1;
			    	doSearch();
			    }
			  }
		 });
	}
}

var shop_pageSize = 3;
var shop_start = 0;
var shop_currentIndex = 0;
var shop_totalPage = 0;
var shops;
var $shopContainer;
/**
 * 获取商品请求列表参数
 */
function getShopsRequestParams(){
	return {keyword: searchKey,  start: (shop_currentIndex * shop_pageSize), current: shop_currentIndex, type: preType, rows: shop_pageSize, t: Math.random()};
}

/**
 * 构建每一行商品html
 * @param shop
 * @param index
 */
function buildEachSearchShopRow(index, shop){
		var html = '<div class="layui-col-md4 layui-col-xs12">'+
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ shop.img +'" class="m-product-item-img" style=""/>	'+							
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" href="/shop/'+ shop.id +'" title="'+ shop.name +'" style="margin-left: 0px!important; margin-top: 10px;">'+ shop.name +'</a>'+				    			
							'</div>'+
						'</div>'+
					'</div>';
					
		$shopContainer.append(html);
}

/***********************************  淘宝商品搜索   *************************************************/
/**
 * 处理是商品的搜索
 * @param message
 */
function dealTaobaoSearch(message, total){
	$taobaoContainer.empty();
	taobaos = message;
	if(message.length == 0){
		if(taobao_currentIndex == 0){
			$taobaoContainer.append("搜索不到符合条件的商品，请换条件试试！");
		}else{
			$taobaoContainer.append("已经没有更多的商品啦，请重新选择！");
		}
	}else{
		for(var i = 0; i < taobaos.length; i++){
			buildEachSearchTaobaoRow(i, taobaos[i]);
		}
	}
	
	if(taobao_currentIndex == 0 && (!taobaos || taobaos.length ==0)){
		$("#taobao-pager").empty();
		$("#taobao-pager").css("padding", "0");
	}else{
		$("#taobao-pager").css("padding", "5px");
		//执行一个laypage实例
		 laypage.render({
		    elem: 'taobao-pager' //注意，这里的 test1 是 ID，不用加 # 号
		    ,layout: ['prev', 'page', 'next', 'count', 'skip']
		    ,count: total //数据总数，从服务端得到
		    ,limit: taobao_pageSize
		    , curr: taobao_currentIndex + 1
		    ,jump: function(obj, first){
			    //obj包含了当前分页的所有参数，比如：
			    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
			    console.log(obj.limit); //得到每页显示的条数
			    if(!first){
			    	taobao_currentIndex = obj.curr -1;
			    	doSearch();
			    }
			  }
		 });
	}
}

var taobao_pageSize = 12;
var taobao_start = 0;
var taobao_currentIndex = 0;
var taobao_totalPage = 0;
var taobaos;
var $taobaoContainer;
/**
 * 获取商品请求列表参数
 */
function getTaobaosRequestParams(){
	return {keyword: searchKey, current: taobao_currentIndex, type: preType, rows: taobao_pageSize, t: Math.random()};
}

/**
 * 构建每一行商品html
 * @param taobao
 * @param index
 */
function buildEachSearchTaobaoRow(index, taobao){
		var html = '<div class="layui-col-md4 layui-col-xs12" style="cursor: pointer;" onclick="buildShareLink('+ taobao.auctionId +');">'+
						'<div class="layui-col-md12 layui-col-xs12 m-taobao-item">'+
							'<img alt="" src="'+ taobao.img +'" class="m-product-item-img"/>		'+						
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" title="'+ changeNotNullString(taobao.title) +'">'+ changeNotNullString(taobao.title) +'</a>';
					if(taobao.couponAmount > 0){
						html +=	'<p style="margin-left: 5px; text-align: left;">优惠：<span style="color: red;">'+ taobao.couponAmount +'</span>元&nbsp;&nbsp;&nbsp;&nbsp;<span style="float: right; margin-right: 8px;">剩余：'+ taobao.couponLeftCount +'</span></p>';
					}
						html += '<p style="margin-left: 5px; text-align: left;"><span style="font-size: 0.8em;">'+ taobao.shopTitle +'</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="m-product-price" style="float: right; margin-right: 8px;">¥'+ taobao.price +'</span></p>'+
								'<p style="text-align: left;"><span class="m-product-price" style="font-size: 1.0em;">比率：'+ taobao.cashBackRatio +'%</span>&nbsp;&nbsp;<span style="float: right; margin-right: 8px; font-size: 1.0em;">佣金: ¥'+(taobao.price * taobao.cashBackRatio / 100).toFixed(2) +'</span></p>'+
							'</div>'+
						'</div>'+
					'</div>';			
		$taobaoContainer.append(html);
}