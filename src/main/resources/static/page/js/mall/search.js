var SEARCH_PRODUCT_PLATFORM; //搜索商品的平台
layui.use(['form', 'laypage', "layer"], function(){
    layer = layui.layer;
    var element = layui.element;
    laypage = layui.laypage;
    //监听Tab切换，以改变地址hash值
    element.on('tab(resultTabBrief)', function(data){
        console.log(this); //当前Tab标题所在的原始DOM元素
        console.log(data.index); //得到当前Tab的所在下标
        tb_product_start = 0;
        tb_product_currentIndex = 0;
        switch(data.index){
            case 0: //淘宝商品搜索
                SEARCH_PRODUCT_PLATFORM = "淘宝网";
			  	//商品
			  	doSearch();
		  		break;
		  	case 1:  //京东商品搜索
		  		SEARCH_PRODUCT_PLATFORM = "京东";
		  		console.log(data.elem); //得到当前的Tab大容器
		  		doSearch();
                break;
			  
		  	case 2:  //拼多多商品搜索
		  		SEARCH_PRODUCT_PLATFORM = "拼多多";
		  		console.log(data.elem); //得到当前的Tab大容器
		  		doSearch();
                break;
        }
    });

    $(".common-search").remove();
    searchKey = getURLParam(decodeURI(window.location.href), "q");
    SEARCH_PRODUCT_PLATFORM = getURLParam(decodeURI(window.location.href), "platform");

    $SearchContainer = $("#search-value");
    $SearchContainer.bind("input propertychange", function() {
        console.log($(this).val());
    });

    if(isEmpty(searchKey)){
        console.log("获取不到您要检索的关键字！");
        return;
    }

    $SearchContainer.val(searchKey);
    //默认是淘宝搜索
    if(isEmpty(SEARCH_PRODUCT_PLATFORM)){
        SEARCH_PRODUCT_PLATFORM = "淘宝网";
    }
    doSearch();
    initClipBoard();
});
var $SearchContainer;
var searchKey;
$(function(){
	
});


/**
** 按钮点击搜索商品
**/
function searchProductBtn(obj, platform){
    searchKey = $SearchContainer.val();
    SEARCH_PRODUCT_PLATFORM = platform;
    doSearch();
}

/**
 * 执行搜索
 */
function doSearch(){
    $taobaoProductContainer = $("#tb-product-row-container");
    $taobaoProductContainer.empty();

    if(isEmpty(searchKey)){
        console.log("获取不到您要检索的关键字！");
        layer.msg("请先输入检索字段");
        return;
    }
    var	url = "/mall/search/product?"+ jsonToGetRequestParams(getTaobaosRequestParams());

    //搜索商品
    if(SEARCH_PRODUCT_PLATFORM == "淘宝网"){
        $("#li-tb-product").addClass("layui-this");
        $("#li-jd-product").removeClass("layui-this");
        $("#li-pdd-product").removeClass("layui-this");
    }else if(SEARCH_PRODUCT_PLATFORM == "京东"){
        $("#li-jd-product").addClass("layui-this");
        $("#li-tb-product").removeClass("layui-this");
        $("#li-pdd-product").removeClass("layui-this");
    }else if(SEARCH_PRODUCT_PLATFORM == "拼多多"){
        $("#li-pdd-product").addClass("layui-this");
        $("#li-tb-product").removeClass("layui-this");
        $("#li-jd-product").removeClass("layui-this");
    }
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "get",
		url : url,
		dataType: 'json',
		beforeSend:function(){
			$taobaoProductContainer.empty();
		},
		success : function(data) {
			layer.close(loadi);
			$(".search-need-time").text(data.consumeTime);
			if(data.isSuccess && isNotEmpty(data.message)){
                dealTaobaoProductSearch(data.message.list, data.total, data.platform);
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

/***********************************  淘宝商品搜索   *************************************************/
/**
 * 处理是商品的搜索
 * @param message
 */
function dealTaobaoProductSearch(message, total, platform){
	$taobaoProductContainer.empty();
	taobaos = message;
	if(message.length == 0){
		if(tb_product_currentIndex == 0){
			$taobaoProductContainer.append("搜索不到符合条件的商品，请换条件试试！");
		}else{
			$taobaoProductContainer.append("已经没有更多的商品啦，请重新选择！");
		}
	}else{
		for(var i = 0; i < taobaos.length; i++){
			buildEachSearchTaobaoProductRow(i, taobaos[i], platform);
		}
	}
	
	if(tb_product_currentIndex == 0 && (!taobaos || taobaos.length ==0)){
		$("#tb-product-pager").empty();
		$("#tb-product-pager").css("padding", "0");
	}else{
		$("#tb-product-pager").css("padding", "5px");
		//执行一个laypage实例
		 laypage.render({
		    elem: 'tb-product-pager' //注意，这里的 test1 是 ID，不用加 # 号
		    ,layout: ['prev', 'page', 'next', 'count', 'skip']
		    ,count: total //数据总数，从服务端得到
		    ,limit: taobao_pageSize
		    , curr: tb_product_currentIndex + 1
		    ,jump: function(obj, first){
			    //obj包含了当前分页的所有参数，比如：
			    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
			    console.log(obj.limit); //得到每页显示的条数
			    if(!first){
			    	tb_product_currentIndex = obj.curr -1;
			    	doSearch();
			    }
			  }
		 });
	}
}

var taobao_pageSize = 12;
var taobao_start = 0;
var tb_product_currentIndex = 0;
var taobao_totalPage = 0;
var taobaos;
var $taobaoProductContainer;
/**
 * 获取商品请求列表参数
 */
function getTaobaosRequestParams(){
	return {platform: SEARCH_PRODUCT_PLATFORM, keyword: searchKey, current: tb_product_currentIndex, rows: taobao_pageSize, t: Math.random()};
}

/**
 * 构建每一行商品html
 * @param taobao
 * @param index
 */
function buildEachSearchTaobaoProductRow(index, taobao, platform){
    var auctionId = "";
    if(platform == "淘宝网" || platform == "天猫"){
        auctionId = "tb_"+ taobao.auctionId;
    }else if(platform == "京东"){
        auctionId = "jd_"+ taobao.auctionId;
    }else if(platform == "拼多多"){
        auctionId = "pdd_"+ taobao.auctionId;
    }
    var html = '<div class="layui-col-md3 layui-col-xs12 button_link" style="cursor: pointer;" onclick="toProductDetail(\''+ auctionId +'\');">'+
                    '<div class="layui-col-md12 layui-col-xs12 m-taobao-item">'+
                        '<img alt="" src="'+ taobao.img +'" class="m-product-item-img"/>		'+
                        '<div class="m-product-contrainer">'+
                            '<p style=" margin-top: 6px;"><a class="m-product-desc" title="'+ changeNotNullString(taobao.title) +'">'+ changeNotNullString(taobao.title) +'</a></p>';
                if(taobao.couponAmount > 0){
//						html +=	'<p style="margin-left: 5px; text-align: left;">券：<span style="color: red;">'+ taobao.couponAmount +'</span>元&nbsp;&nbsp;&nbsp;&nbsp;<span style="float: right; margin-right: 8px;">剩：'+ taobao.couponLeftCount +'</span></p>';
                   html += '<p style="margin-left: 5px; text-align: left;"><span class="coupon-discount">'+ taobao.couponAmount +'元券</span><span class="coupon-left">剩'+ taobao.couponLeftCount +'张</span></p>';
                }
                    html += '<p style="margin-left: 5px; text-align: left; margin-top: 6px;"><span style="font-size: 0.9em;">'+ taobao.shopTitle +'</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="m-product-price" style="float: right; margin-right: 8px;">¥'+ taobao.price +'</span></p>'+
                            '<p style="text-align: left; margin-top: 4px;"><span class="m-product-price" style="font-size: 1.0em;">比率：'+ taobao.cashBackRatio +'%</span>&nbsp;&nbsp;<span style="float: right; margin-right: 8px; font-size: 1.0em;">预估佣金: ¥'+taobao.cashBack +'</span></p>'+
                        '</div>'+
                    '</div>'+
                    '<span class="line line_top"></span>'+
                    '<span class="line line_right"></span>'+
                    '<span class="line line_bottom"></span>'+
                    '<span class="line line_left"></span>'+
                '</div>';
    $taobaoProductContainer.append(html);
}