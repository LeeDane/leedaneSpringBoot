var SEARCH_PRODUCT_PLATFORM; //搜索商品的平台
var currentSort;
layui.use(['form', 'laypage', "layer"], function(){
    layer = layui.layer;
    var element = layui.element;
    laypage = layui.laypage;
    form = layui.form;
     //监听提交
    form.on('submit(do-search-filter)', function(data){
        searchCurrentIndex = 0;
        currentSort = "";
        SEARCH_PRODUCT_PLATFORM = data.field.platform;
        if(isEmpty(SEARCH_PRODUCT_PLATFORM))
            SEARCH_PRODUCT_PLATFORM = "淘宝网";
        doSearch();
        return false;
    });
    //监听Tab切换，以改变地址hash值
    element.on('tab(resultTabBrief)', function(data){
        searchCurrentIndex = 0;
        currentSort = "";
        switch(data.index){
            case 0: //淘宝商品搜索
                SEARCH_PRODUCT_PLATFORM = "淘宝网";
			  	//商品
		  		break;
		  	case 1:  //京东商品搜索
		  		SEARCH_PRODUCT_PLATFORM = "京东";
		  		console.log(data.elem); //得到当前的Tab大容器
                break;
			  
		  	case 2:  //拼多多商品搜索
		  		SEARCH_PRODUCT_PLATFORM = "拼多多";
		  		console.log(data.elem); //得到当前的Tab大容器
                break;
            case 3:  //苏宁商品搜索
                SEARCH_PRODUCT_PLATFORM = "苏宁";
                console.log(data.elem); //得到当前的Tab大容器
                break;
        }
        $('select').val(SEARCH_PRODUCT_PLATFORM);
        form.render("select");
        doSearch();
    });
    $(".common-search").remove();
    searchKey = getURLParam(decodeURI(window.location.href), "q");
    SEARCH_PRODUCT_PLATFORM = getURLParam(decodeURI(window.location.href), "platform");
    //默认是淘宝搜索
    if(isEmpty(SEARCH_PRODUCT_PLATFORM)){
        SEARCH_PRODUCT_PLATFORM = "淘宝网";
    }else{
        $('select').val(SEARCH_PRODUCT_PLATFORM);
        form.render("select");
    }
    $SearchContainer = $("#search-value");
    $SearchContainer.bind("input propertychange", function() {
        console.log($(this).val());
    });
    if(isNotEmpty(condition)){
        condition = JSON.parse(condition); //可以将json字符串转换成json对象
    }
    if(isEmpty(searchKey)){
        console.log("获取不到您要检索的关键字！");
        buildSortConditionBtn();
        return;
    }
    $SearchContainer.val(searchKey);
    doSearch();
    initClipBoard();
});
var $SearchContainer;
var searchKey;
/**
** 按钮点击搜索商品
**/
function searchProductBtn(obj, platform){
    SEARCH_PRODUCT_PLATFORM = platform;
    currentSort = "";
    searchCurrentIndex = 0;
    doSearch();
}
/**
 * 执行搜索
 */
function doSearch(){
    $productContainer = $("#product-row-container");
    $productContainer.empty();
    searchKey = $SearchContainer.val();
    if(condition){
        buildSortConditionBtn();
    }
    if(isEmpty(searchKey)){
        console.log("获取不到您要检索的关键字！");
//        layer.msg("请先输入检索字段");
        $("#search-value").focus();
        layer.tips("请先输入检索字段", "#search-value",{tips:[3,'#FFB800'], time: 3000});
        return;
    }
    searchPageSize = 12;
    //搜索商品
    if(SEARCH_PRODUCT_PLATFORM == "淘宝网"){
        $("#li-tb-product").addClass("layui-this");
        $("#li-jd-product").removeClass("layui-this");
        $("#li-pdd-product").removeClass("layui-this");
        $("#li-sn-product").removeClass("layui-this");

    }else if(SEARCH_PRODUCT_PLATFORM == "京东"){
        $("#li-jd-product").addClass("layui-this");
        $("#li-tb-product").removeClass("layui-this");
        $("#li-pdd-product").removeClass("layui-this");
        $("#li-sn-product").removeClass("layui-this");
    }else if(SEARCH_PRODUCT_PLATFORM == "拼多多"){
        $("#li-pdd-product").addClass("layui-this");
        $("#li-tb-product").removeClass("layui-this");
        $("#li-jd-product").removeClass("layui-this");
        $("#li-sn-product").removeClass("layui-this");
    }else if(SEARCH_PRODUCT_PLATFORM == "苏宁"){
        searchPageSize = 8;
        $("#li-sn-product").addClass("layui-this");
        $("#li-tb-product").removeClass("layui-this");
        $("#li-jd-product").removeClass("layui-this");
        $("#li-pdd-product").removeClass("layui-this");
     }
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var	url = "/mall/search/product?"+ jsonToGetRequestParams(getRequestParams());
	$.ajax({
		type : "get",
		url : url,
		dataType: 'json',
		beforeSend:function(){
			$productContainer.empty();
		},
		success : function(data) {
			layer.close(loadi);
			$(".search-need-time").text(data.consumeTime);
			if(data.success && isNotEmpty(data.message)){
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

/***********************************  商品搜索   *************************************************/
/**
 * 处理是商品的搜索
 * @param message
 */
function dealProductSearch(message, total){
	$productContainer.empty();
	products = message;
	if(message.length == 0){
		if(searchCurrentIndex == 0){
			$productContainer.append("搜索不到符合条件的商品，请换条件试试！");
		}else{
			$productContainer.append("已经没有更多的商品啦，请重新选择！");
		}
	}else{
		for(var i = 0; i < products.length; i++){
			buildEachSearchProductRow(i, products[i]);
		}
	}
	
	if(SEARCH_PRODUCT_PLATFORM == "京东" || SEARCH_PRODUCT_PLATFORM == "苏宁"){
		$("#product-pager").empty();
		$("#product-pager").html('<button type="button" class="layui-btn layui-btn-sm" onclick="goPre();"><i class="layui-icon"></i>上一页('+ searchCurrentIndex +')</button><button type="button" class="layui-btn layui-btn-sm" onclick="goAfter();" style="float: right;">下一页('+ (searchCurrentIndex + 2) +')<i class="layui-icon"></i></button>');
		$("#product-pager").css("padding", "10px");
	}else{
		$("#product-pager").css("padding", "5px");
		//执行一个laypage实例
		 laypage.render({
		    elem: 'product-pager' //注意，这里的 test1 是 ID，不用加 # 号
		    ,layout: ['prev', 'page', 'next', 'count', 'skip']
		    ,count: total //数据总数，从服务端得到
		    ,limit: searchPageSize
		    , curr: searchCurrentIndex + 1
		    ,jump: function(obj, first){
			    //obj包含了当前分页的所有参数，比如：
			    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
			    console.log(obj.limit); //得到每页显示的条数
			    if(!first){
			    	searchCurrentIndex = obj.curr -1;
			    	doSearch();
			    }
			  }
		 });
	}
}

var searchPageSize = 12;
var searchCurrentIndex = 0;
var products;
var $productContainer;
/**
 * 获取商品请求列表参数
 */
function getRequestParams(){
	return {sort: currentSort,platform: SEARCH_PRODUCT_PLATFORM, keyword: searchKey, current: searchCurrentIndex, rows: searchPageSize, t: Math.random()};
}
/**
 * 构建每一行商品html
 * @param product
 * @param index
 * @param platform
 */
function buildEachSearchProductRow(index, product){
    var html = '<div class="layui-col-md3 layui-col-xs12 button_link" style="cursor: pointer;" onclick="toProductDetail(\''+ product.clickId +'\');">'+
                    '<div class="layui-col-md12 layui-col-xs12 m-product-item">'+
                        '<img alt="" src="'+ product.img +'" class="m-product-item-img"/>		'+
                        '<div class="m-product-contrainer">'+
                            '<p style=" margin-top: 6px;"><a class="m-product-desc" title="'+ changeNotNullString(product.title) +'">'+ changeNotNullString(product.title) +'</a></p>';
                  html += '<p style="margin-left: 5px; text-align: left; height: 18px;">';
                if(product.couponAmount > 0){
//						html +=	'<p style="margin-left: 5px; text-align: left;">券：<span style="color: red;">'+ product.couponAmount +'</span>元&nbsp;&nbsp;&nbsp;&nbsp;<span style="float: right; margin-right: 8px;">剩：'+ product.couponLeftCount +'</span></p>';
                   html += '<span class="coupon-discount">'+ product.couponAmount +'元券</span><span class="coupon-left">剩'+ product.couponLeftCount +'张</span>';
                }
                    if(product.sales){
                        html += '<span class="" style="float: right;margin-right: 8px;font-size: 12px;color: #999999;">'+ product.sales +'</span>';
                    }
                    html += '</p>';
                    html += '<p style="margin-left: 5px; text-align: left; margin-top: 6px;"><span style="font-size: 0.9em;">'+ product.shopTitle +'</span>&nbsp;&nbsp;&nbsp;&nbsp;<span class="m-product-price" style="float: right; margin-right: 8px;">¥'+ product.price +'</span></p>'+
                            '<p style="text-align: left; margin-top: 4px;"><span class="m-product-price" style="font-size: 1.0em;">比率：'+ product.cashBackRatio +'%</span>&nbsp;&nbsp;<span style="float: right; margin-right: 8px; font-size: 1.0em;">预估佣金: ¥'+product.cashBack +'</span></p>'+
                        '</div>'+
                    '</div>'+
                    '<span class="line line_top"></span>'+
                    '<span class="line line_right"></span>'+
                    '<span class="line line_bottom"></span>'+
                    '<span class="line line_left"></span>'+
                '</div>';
    $productContainer.append(html);
}

/**
**动态构建排序按钮列表
** @param currentSort 当前的排序
*/
function buildSortConditionBtn(){
    if(!condition.condition[SEARCH_PRODUCT_PLATFORM])
        return;
    var sortlist = condition.condition[SEARCH_PRODUCT_PLATFORM].sort;//获取相应平台的排序列表
    //判断当前的饿排序，默认是空的
    if(!currentSort || isEmpty(currentSort))
        currentSort = condition.condition[SEARCH_PRODUCT_PLATFORM].default;
    if(sortlist && sortlist.length > 0){
        var html = "";
        for(var i = 0; i < sortlist.length; i++){
            var currentClass = currentSort && (currentSort == sortlist[i].desc || currentSort == sortlist[i].asc) ? "": "layui-btn-warm";
            var iconHtml = "";
            var hasSortIcon = isNotEmpty(sortlist[i].desc) && isNotEmpty(sortlist[i].asc); //满足有升序和减序的值才会出现升降序的按钮

            if(currentSort && hasSortIcon){
                iconHtml = "&#xe61a;"; //默认是降序
                if(currentSort == sortlist[i].asc){
                    iconHtml = "&#xe619;";
                }
            }
            html += '<button class="layui-btn layui-btn-radius layui-btn-xs '+ currentClass +'" desc="true" index="'+ i +'" onclick="sortClick(this);" style="height: 30px !important; line-height: 30px!important;">'+ sortlist[i].value + (hasSortIcon ? ('<i class="layui-icon" style="margin-left: 5px;">'+ iconHtml +'</i>') : '') +'</button>';
        }
        $("#sort-btn-group").html(html);
    }
    return html;
}

/**
**点击排序字段
*/
function sortClick(obj){
    var $obj = $(obj);
    //当前还没有被点击并且没有升降序，则直接忽略这次点击事件
    if(!$obj.hasClass("layui-btn-warm") && $obj.find("i").length < 1){
        return;
    }
    //判断当前点击是哪个
    var index = parseInt($obj.attr("index"));
    var sort = condition.condition[SEARCH_PRODUCT_PLATFORM].sort[index];
    //没有点击过
    if($obj.hasClass("layui-btn-warm")){
        //先取desc,再取asc的值作为默认的
        currentSort = sort.desc;
        if(isEmpty(currentSort))
            currentSort = sort.asc;
    }else{
        //判断当前点击的是哪个
        currentSort = currentSort == sort.desc ? sort.asc : sort.desc;
    }
    //刷新一下列表
    searchCurrentIndex = 0;
    doSearch();
}

/**
** 点击上一页
*/
function goPre(){
    if(searchCurrentIndex == 0){
        layer.msg("没有上一页了！");
        return;
    }
    searchCurrentIndex--;
    doSearch();
}

/**
** 点击下一页
*/
function goAfter(){
    searchCurrentIndex++;
    doSearch();
}