var layerIndex; //获取窗口索引
var $SearchContainer;
layui.use(['layer', 'laypage'], function(){
	layer = layui.layer;
	laypage = layui.laypage;
	$SearchContainer = $("input[name='shop-search-value']")
	$productContainer = $("#product-row-container");
});

var product_pageSize = 4;
var product_start = 0;
var product_currentIndex = 0;
var product_totalPage = 0;
var products;
var product_sort = "createTime";
var product_sort_desc= true;

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
		url : "/mall/search/product?"+ jsonToGetRequestParams({keyword: searchText, sort: product_sort, desc: product_sort_desc,  start: (product_currentIndex * product_pageSize), current: product_currentIndex, type: 5, rows: product_pageSize, t: Math.random()}),
		dataType: 'json',
		beforeSend:function(){
		},
		beforeSend:function(){
			
		},
		success : function(data) {
			layer.close(loadi);
			$(".search-need-time").text(data.consumeTime);
			if(data.isSuccess && isNotEmpty(data.message)){
				dealProductSearch(data.message.list, data.total);
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
	if(!message || message.length == 0){
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
		    ,count: total //数据总数，从服务端得到
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

/**
 * 构建每一行商品html
 * @param product
 * @param index
 */
function buildEachSearchProductRow(index, product){
		var html = '<div class="layui-col-md6 layui-col-xs6" onclick="getSelectProductData('+ index +');">'+
						'<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
							'<img alt="" src="'+ product.img.split(";")[0] +'" class="m-product-item-img"/>		'+
							'<div class="m-product-contrainer">'+
								'<a class="m-product-desc" href="javascript:void(0);" title="'+ changeNotNullString(product.title) +'">'+ changeNotNullString(product.title) +'</a>'+
								'<p style="text-align: left;"><span class="m-product-price">¥'+ product.price +'</span> <span style="float: right; margin-right: 8px; text-decoration: line-through; font-size: 0.6em;">原价: ¥'+product.oldPrice +'</span></p>'+
								'<p style="text-align: left;"><span class="m-product-price" style="font-size: 1.0em;">比率：'+ product.cashBackRatio +'%</span>&nbsp;&nbsp;<span style="float: right; margin-right: 8px; font-size: 1.0em;">佣金: ¥'+(product.price * product.cashBackRatio / 100).toFixed(2) +'</span></p>'+
							'</div>'+
						'</div>'+
					'</div>';
	
	  $productContainer.append(html);
}

/**
 * 选中商品
 * @param index
 */
function getSelectProductData(index){
	layerIndex = parent.layer.getFrameIndex(window.name); //获取窗口索引
	parent.getSelectProductData(layerIndex, products[index]);
}