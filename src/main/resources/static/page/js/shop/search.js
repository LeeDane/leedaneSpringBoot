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
	  
	  var clipboard = new Clipboard('.layui-btn');

	  clipboard.on('success', function(e) {
	      //console.info('Action:', e.action);
	      //console.info('Text:', e.text);
	      //console.info('Trigger:', e.trigger);
	      //console.info('Trigger:', e.trigger.previousSibling.id);
	      //$("#"+ e.trigger.previousSibling.id).text(e.text);
	      layer.tips('此链接已复制成功', "#"+ e.trigger.previousSibling.id, {time: 1000});
	      //layer.msg("链接已复制成功");
	      //e.clearSelection();
	  });

	  clipboard.on('error', function(e) {
	      //console.error('Action:', e.action);
	      //console.error('Trigger:', e.trigger);
		  layer.tips('此链接已复制失败', "#"+ e.trigger.previousSibling.id, {time: 1000});
	  });
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
		url = "/taobao/search?"+ jsonToGetRequestParams(getTaobaosRequestParams());
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
		var html = '<div class="layui-col-md4 layui-col-xs12">'+
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ product.mainImgLinks.split(";")[0] +'" class="m-product-item-img"/>		'+						
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" href="/shop/product/'+ product.id +'/detail" title="'+ changeNotNullString(product.title) +'">'+ changeNotNullString(product.title) +'</a>'+
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
	if(message.length == 0){
		if(taobao_currentIndex == 0){
			$taobaoContainer.append("搜索不到符合条件的商品，请换条件试试！");
		}else{
			$taobaoContainer.append("已经没有更多的商品啦，请重新选择！");
		}
	}else{
		taobaos = message;
		for(var i = 0; i < taobaos.length; i++){
			buildEachSearchTaobaoRow(i, taobaos[i]);
		}
	}
	
	if(taobao_currentIndex == 0 && (!taobaos || taobaos.length ==0)){
		$("#taobao-pager").empty();
	}else{
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
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ taobao.img +'" class="m-product-item-img"/>		'+						
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" title="'+ changeNotNullString(taobao.title) +'">'+ changeNotNullString(taobao.title) +'</a>'+
								'<p style="margin-left: 5px; text-align: left;"><span style="font-size: 0.8em;">'+ taobao.shopTitle +'</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="m-product-price" style="float: right; margin-right: 8px;">¥'+ taobao.price +'</span></p>'+
								'<p style="text-align: left;"><span class="m-product-price" style="font-size: 1.0em;">比率：'+ taobao.cashBackRatio +'%</span>&nbsp;&nbsp;<span style="float: right; margin-right: 8px; font-size: 1.0em;">佣金: ¥'+(taobao.price * taobao.cashBackRatio / 100).toFixed(2) +'</span></p>'+
							'</div>'+
						'</div>'+
					'</div>';			
		$taobaoContainer.append(html);
}

/**
 * 生成共享链接
 * @param taobaoId
 */
function buildShareLink(taobaoId){
	var width = $(window).width() > 600 ? "420px": $(window).width()+"px";
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "get",
		url : "/taobao/build/share/link?taobaoId="+ taobaoId +"&t=" +Math.random(),
		dataType: 'json',
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				//页面层
				layer.open({
				  type: 1,
				  title: '生成分享链接',
				  skin: 'layui-layer-rim', //加上边框
				  area: [width, '340px'], //宽高
				  content: buildTaobaoShareContent(data.message)
				});
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
 * 构建弹出提示
 * @param message
 * @returns {String}
 */
function buildTaobaoShareContent(message){
	var html = '<div class="layui-tab layui-tab-brief" lay-filter="shareLinkBrief">'+
				   '<ul class="layui-tab-title">'+
				    '<li class="layui-this" id="li-short">短链接</li>'+
				    '<li id="li-long">长链接</li>'+
				    '<li id="li-qr">二维码</li>'+
				    '<li id="li-token">淘口令</li>'+
				  '</ul>'+
				  '<div class="layui-tab-content tab-share-content">'+
				    '<div class="layui-tab-item layui-show" id="tab-content-share">'+
						'<ol>'+
							'<li>如您推广的是航旅的当面付、火车票或者理财保险类商品，将无法获得佣金。</li>'+
							'<li>短链接只有300天的有效期，过期失效需要重新获取。</li>'+
							'<li>请勿将此推广链接打开后再发送给用户，否则无法跟踪。</li>'+
							'<li>若订单使用红包或购物券后佣金有可能支付给红包推广者，如您是自推自买，请勿使用红包及购物券。</li>'+
						'<ol>';
				if(isNotEmpty(message.shortLinkUrl)){
					html += '<div>'+
							    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
							    '<input id="shortLinkUrl" type="text" class="tab-share-input tab-share-input-link" readonly="readonly" value="'+ message.shortLinkUrl +'">'+
							    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#shortLinkUrl">复制链接</button>'+
							    '<button class="layui-btn layui-btn-danger" onclick="openLink(\''+ message.shortLinkUrl +'\');">跳转</button>'+
							'</div>';
				}
				
				if(isNotEmpty(message.couponShortLinkUrl)){
					html += '<div style="margin-top: 5px;">'+
							    '<label style="font-weight: 400; line-height: 20px;">领券链接：</label>'+
							    '<input id="couponShortLinkUrl" type="text" class="tab-share-input tab-share-input-link" readonly="readonly" value="'+ message.couponShortLinkUrl +'">'+
							    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#couponShortLinkUrl">复制链接</button>'+
							    '<button class="layui-btn layui-btn-danger" onclick="openLink(\''+ message.couponShortLinkUrl +'\');">跳转</button>'+
							'</div>';
				}
						
			html += '</div>'+
				    '<div class="layui-tab-item"  id="tab-content-shop">'+
					    '<ol>'+
							'<li>如您推广的是航旅的当面付、火车票或者理财保险类商品，将无法获得佣金。</li>'+
							'<li>请勿将此推广链接打开后再发送给用户，否则无法跟踪。</li>'+
							'<li>若订单使用红包或购物券后佣金有可能支付给红包推广者，如您是自推自买，请勿使用红包及购物券。</li>'+
						'<ol>';
						
			if(isNotEmpty(message.clickUrl)){
				html += '<div>'+
						    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
						    '<input id="clickUrl" type="text" class="tab-share-input tab-share-input-link" readonly="readonly" value="'+ message.clickUrl +'">'+
						    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#clickUrl">复制链接</button>'+
						    '<button class="layui-btn layui-btn-danger" onclick="openLink(\''+ message.clickUrl +'\');">跳转</button>'+
						'</div>';
			}
			
			if(isNotEmpty(message.couponLink)){
				html += '<div>'+
						    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
						    '<input id="couponLink" type="text" class="tab-share-input tab-share-input-link" readonly="readonly" value="'+ message.couponLink +'">'+
						    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#couponLink">复制链接</button>'+
						    '<button class="layui-btn layui-btn-danger" onclick="openLink(\''+ message.couponLink +'\');">跳转</button>'+
						'</div>';
			}
			html += '</div>'+
				    '<div class="layui-tab-item"  id="tab-content-shop">'+
						 '<ol>'+
							'<li>如您推广的是航旅的当面付、火车票或者理财保险类商品，将无法获得佣金。</li>'+
							'<li>二维码是由短链接转换而来，只有300天的有效期，过期失效需要重新获取。</li>'+
							'<li>若订单使用红包或购物券后佣金有可能支付给红包推广者，如您是自推自买，请勿使用红包及购物券。</li>'+
						'<ol>';
			
			if(isNotEmpty(message.qrCodeUrl)){
				html += '<div style="text-align: center;">'+
						    '<img src="'+ message.qrCodeUrl +'" style="width: 100px; height: 100px;"/>'+
						'</div>';
			}
			html += '</div>' +
				    '<div class="layui-tab-item"  id="tab-content-shop">'+
					    '<ol>'+
							'<li>如您推广的是航旅的当面付、火车票或者理财保险类商品，将无法获得佣金。</li>'+
							'<li>淘口令只有30天的有效期，过期失效需要重新获取。</li>'+
							'<li>若订单使用红包或购物券后佣金有可能支付给红包推广者，如您是自推自买，请勿使用红包及购物券。</li>'+
						'<ol>';
						
			if(isNotEmpty(message.taoToken)){
				html += '<div>'+
						    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
						    '<input id="taoToken" type="text" class="tab-share-input" readonly="readonly" value="'+ message.taoToken +'">'+
						    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#taoToken">复制链接</button>'+
						'</div>';
			}
			
			if(isNotEmpty(message.couponLinkTaoToken)){
				html += '<div>'+
						    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
						    '<input id="couponLinkTaoToken" type="text" class="tab-share-input" value="'+ message.couponLinkTaoToken +'">'+
						    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#couponLinkTaoToken">复制链接</button>'+
						'</div>';
			}
			
			html += '</div>'+
				  '</div>'+
				'</div> ';
	
	return html;
}

/**
 * 跳转到淘宝
 * @param link
 */
function linkToTaobao(link){
	
}