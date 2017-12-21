//表单的容器
var orderFormContainer; 
layui.use(['table', 'laydate', 'form', 'layedit'], function(){
  var table = layui.table;
  var form = layui.form;
  //监听表格复选框选择
  table.on('checkbox(demo)', function(obj){
    console.log(obj)
  });
  
  var laydate = layui.laydate;
  //日期
  laydate.render({
    elem: '#date'
  });
  
  //监听工具条
  table.on('tool(demo)', function(obj){
    var data = obj.data;
    if(obj.event === 'detail'){
      //layer.msg('ID：'+ data.id + ' 的查看操作');
      linkToProduct(data.product_id);
    } else if(obj.event === 'del'){
      layer.confirm('确定要删除该订单吗？此是不可逆行为，请慎重！', function(index){
    	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : "DELETE",
			url : "/shop/order",
			data: data.field,
			dataType: 'json',
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					obj.del();
					layer.msg("订单删除成功，2秒后将自动刷新");
					reloadPage(2000);
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				ajaxError(data);
			}
		});
      });
    } else if(obj.event === 'edit'){
      /* layer.alert('编辑行：<br />'+ JSON.stringify(data)) */
    	orderFormContainer.show("fast");
    	orderFormContainer.find("form").find("input[name='id']").remove();
    	orderFormContainer.find("form").append('<input type="hidden" name="id" value="'+ data.id +'"/>');
    	orderFormContainer.find('input[name="order_code"]').val(data.order_code);
    	orderFormContainer.find('input[name="product_code"]').val(data.product_code);
    	orderFormContainer.find('input[name="order_date"]').val(data.order_date);
    	orderFormContainer.find('input[name="title"]').val(data.title);
    	//处理平台
    	var options = orderFormContainer.find('select[name="platform"]').find("option");
    	var platform = data.platform;
    	for(var i = 0; i < options.length; i++){
    		if($(options[i]).attr("value") == platform){
    			$(options[i]).attr("selected", "selected");
    			form.render('select');
    			break; //退出循环
    		}
    	}
    	orderFormContainer.find('select[name="platform"]').val(data.platform);
    	orderFormContainer.find('input[name="referrer"]').val(data.referrer);
    }
  });
  
  var $ = layui.$, active = {
    getCheckData: function(){ //获取选中数据
      var checkStatus = table.checkStatus('idTest')
      ,data = checkStatus.data;
      layer.alert(JSON.stringify(data));
    }
    ,getCheckLength: function(){ //获取选中数目
      var checkStatus = table.checkStatus('idTest')
      ,data = checkStatus.data;
      layer.msg('选中了：'+ data.length + ' 个');
    }
    ,isAll: function(){ //验证是否全选
      var checkStatus = table.checkStatus('idTest');
      layer.msg(checkStatus.isAll ? '全选': '未全选')
    },newOrder: function(){ //新订单
    	var width = $(window).width() > 600 ? "420px": $(window).width()+"px";
    	//页面层
		layer.open({
		  type: 1,
		  title: '生成分享链接',
		  skin: 'layui-layer-rim', //加上边框
		  area: [width, '340px'], //宽高
		  content: buildNewOrder()
		});
    }
  };
  
  $('.order-group .new-order-btn ').on('click', function(){
    //var type = $(this).data('type');
    //active[type] ? active[type].call(this) : '';
	  orderFormContainer.toggle("fast");
	  orderFormContainer.find("form")[0].reset();
	  orderFormContainer.find("form").find("input[name='id']").remove();
  });
  
  //监听提交
  form.on('submit(new-order-form-submit)', function(data){
    //layer.msg(JSON.stringify(data.field));
    var edit = $(this).closest("form").find("input[name='id']").length > 0;
    var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : edit ? "PUT": "POST",
		url : edit ? "/shop/order/"+ data.field.id: "/shop/order",
		data: data.field,
		dataType: 'json',
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg(edit ? '订单修改成功，2秒后将自动刷新' :"新的订单添加成功，2秒后将自动刷新");
				reloadPage(2000);
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
});

$(function(){
	orderFormContainer = $("#new-order-container");
});

/**
 * 构建弹出添加订单的框
 * @returns {String}
 */
function buildNewOrder(){
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
						'<ol>'+
					'</div>'+
				  '</div>'+
				'</div> ';
	
	return html;
}