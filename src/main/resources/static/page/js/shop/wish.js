var SEARCH_TYPE_PRODUCT = 5; //商品
var SEARCH_TYPE_SHOP = 4; //商店
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
		  }
		});
	  
	  laypage = layui.laypage;
	  
	  $(".common-search").remove();
	  searchKey = getURLParam(decodeURI(window.location.href), "q");
	  preType = getURLParam(decodeURI(window.location.href), "tp");
	  if(isEmpty(searchKey)){
		layer.msg("获取不到您要检索的关键字！");
		return;
	  }
	
	  $SearchContainer = $("input[name='shop-search-value']");
	  $SearchContainer.val(searchKey);
	  //搜索商品
	  if(isEmpty(preType) || preType == SEARCH_TYPE_PRODUCT){
		  preType = SEARCH_TYPE_PRODUCT;
		  $("#li-product").addClass("layui-this");
		  $("#li-shop").removeClass("layui-this");
		  $("#tab-content-product").addClass("layui-show");
		  $("#tab-content-shop").removeClass("layui-show");
	  }else{
		  preType = SEARCH_TYPE_SHOP;
		  $("#li-shop").addClass("layui-this");
		  $("#li-product").removeClass("layui-this");
		  $("#tab-content-shop").addClass("layui-show");
		  $("#tab-content-product").removeClass("layui-show");
	  }
	  doSearch();
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
	if(preType == SEARCH_TYPE_PRODUCT){
		$("#li-product").addClass("layui-this");
		$("#li-shop").removeClass("layui-this");
		$("#tab-content-product").addClass("layui-show");
		$("#tab-content-shop").removeClass("layui-show");
	}else{
		$("#li-shop").addClass("layui-this");
		$("#li-product").removeClass("layui-this");
		$("#tab-content-shop").addClass("layui-show");
		$("#tab-content-product").removeClass("layui-show");
  }
	doSearch();
}

/**
 * 执行搜索
 */
function doSearch(){
	if(SEARCH_TYPE_PRODUCT == preType){
		$productContainer = $("#product-row-container");
		$productContainer.empty();
	}else if(SEARCH_TYPE_SHOP == preType){
		$shopContainer = $("#shop-row-container");
		$shopContainer.empty();	
	}
	
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "get",
		url : "/s/s?"+ jsonToGetRequestParams(SEARCH_TYPE_PRODUCT == preType ? getProductsRequestParams(): getShopsRequestParams()),
		dataType: 'json',
		beforeSend:function(){
			if(SEARCH_TYPE_PRODUCT == preType)
				$productContainer.empty();
		},
		beforeSend:function(){
			
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess && isNotEmpty(data.message)){
				$("#search-need-time").text(data.consumeTime);
				if(data.message[preType]){
					if(SEARCH_TYPE_PRODUCT == preType)
						dealProductSearch(data.message[preType], data.total);
					else
						dealShopSearch(data.message[preType], data.total);
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
 * 处理是商品的搜索
 * @param message
 */
function dealProductSearch(message, total){
	if(message.length == 0){
		if(product_currentIndex == 0){
			$productContainer.append("搜索不到符合条件的商品，请换条件试试！");
		}else{
			$productContainer.append("已经没有更多的商品啦，请重新选择！");
		}
	}else{
		products = message;
		for(var i = 0; i < products.length; i++){
			buildEachSearchProductRow(i, products[i]);
		}
	}
	
	if(product_currentIndex == 0 && (!products || products.length ==0)){
		$("#product-pager").empty();
	}else{
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
		var html = '<div class="layui-col-md3 layui-col-xs12">'+
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ product.mainImgLinks.split(";")[0] +'" style="width: calc(100% - 80px); height: calc(160px - 60px); margin-top: 10px;"/>		'+						
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" href="/shop/product/'+ product.id +'/detail" title="'+ changeNotNullString(product.title) +'">'+ changeNotNullString(product.title) +'</a>'+
								'<p><span class="m-product-price">¥'+ product.price +'</span> <span style="text-decoration: line-through; font-size: 0.6em;">原价: ¥'+product.oldPrice +'</span></p>'+
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
	if(message.length == 0){
		if(shop_currentIndex == 0){
			$shopContainer.append("搜索不到符合条件的商品，请换条件试试！");
		}else{
			$shopContainer.append("已经没有更多的商品啦，请重新选择！");
		}
	}else{
		shops = message;
		for(var i = 0; i < shops.length; i++){
			buildEachSearchShopRow(i, shops[i]);
		}
	}
	
	if(shop_currentIndex == 0 && (!shops || shops.length ==0)){
		$("#shop-pager").empty();
	}else{
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
			    	doShopSearch();
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
		var html = '<div class="layui-col-md3 layui-col-xs12">'+
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ shop.img +'" style="width: calc(100% - 80px); height: calc(160px - 50px); margin-top: 10px;"/>	'+							
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" href="/shop/'+ shop.id +'" title="'+ shop.name +'" style="margin-left: 0px!important; margin-top: 10px;">'+ shop.name +'</a>'+				    			
							'</div>'+
						'</div>'+
					'</div>';
					
		$shopContainer.append(html);
}
