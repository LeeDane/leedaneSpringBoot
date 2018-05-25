var layerIndex; //获取窗口索引
var $SearchContainer;
layui.use(['layer', 'laypage'], function(){
	layer = layui.layer;
	laypage = layui.laypage;
	$SearchContainer = $("input[name='shop-search-value']")
	$shopContainer = $("#shop-row-container");
});

var shop_pageSize = 4;
var shop_start = 0;
var shop_currentIndex = 0;
var shop_totalPage = 0;
var shops;
var shop_sort = "createTime";
var shop_sort_desc= true;

/**
 * 执行搜索
 */
function doSearch(){
	var searchText = $SearchContainer.val();
	if(isEmpty(searchText)){
		layer.msg("请输入您要搜索的内容！");
		$SearchContainer.focus();
		return;
	}
	
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "get",
		url : "/s/s?"+ jsonToGetRequestParams({keyword: searchText, sort: shop_sort, desc: shop_sort_desc,  start: (shop_currentIndex * shop_pageSize), current: shop_currentIndex, type: 4, rows: shop_pageSize, t: Math.random()}),
		dataType: 'json',
		beforeSend:function(){
		},
		beforeSend:function(){
			
		},
		success : function(data) {
			layer.close(loadi);
			$(".search-need-time").text(data.consumeTime);
			if(data.isSuccess && isNotEmpty(data.message)){
				dealShopSearch(data.message[4], data.total);
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
 * 处理是商店的搜索
 * @param message
 */
function dealShopSearch(message, total){
	$shopContainer.empty();
	if(message.length == 0){
		if(shop_currentIndex == 0){
			$shopContainer.append("搜索不到符合条件的商店，请换条件试试！");
		}else{
			$shopContainer.append("已经没有更多的商店啦，请重新选择！");
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
		    ,count: total[5] //数据总数，从服务端得到
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

/**
 * 构建每一行商店html
 * @param shop
 * @param index
 */
function buildEachSearchShopRow(index, shop){
		var html = '<div class="layui-col-md6 layui-col-xs6" onclick="getSelectShopData('+ index +');">'+
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ shop.img +'" class="m-product-item-img"/>		'+						
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" href="javascript:void(0);" title="'+ changeNotNullString(shop.name) +'">'+ changeNotNullString(shop.name) +'</a>'+
							'</div>'+
						'</div>'+
					'</div>';
	
		$shopContainer.append(html);
}

/**
 * 选中商店
 * @param index
 */
function getSelectShopData(index){
	layerIndex = parent.layer.getFrameIndex(window.name); //获取窗口索引
	parent.getSelectShopData(layerIndex, shops[index]);
}