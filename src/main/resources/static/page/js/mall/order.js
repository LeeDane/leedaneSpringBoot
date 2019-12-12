//表单的容器
var orderFormContainer; 
layui.use(['table', 'laydate', 'form', 'layedit'], function(){
    var table = layui.table;
    form = layui.form;
    //监听表格复选框选择
    table.on('checkbox(demo)', function(obj){
        console.log(obj)
    });

    var laydate = layui.laydate;
    //日期
    laydate.render({
        elem: '#order-time',
        type: 'datetime'
    });

    laydate.render({
        elem: '#pay-time',
        type: 'datetime'
    });

    var mallOrderRemindNotice = localStorage.getItem("mall-order-remind-notice");
    if(isEmpty(mallOrderRemindNotice) || mallOrderRemindNotice == "false"){
        //示范一个公告层
        var noticeOpen = layer.open({
            type: 1
            ,title: false //不显示标题栏
            ,closeBtn: false
            ,area: '450px;'
            ,shade: 0.8
            ,id: 'LAY_layuipro' //设定一个id，防止重复弹出
            ,btn: ['已知悉', '下次提醒']
            ,btnAlign: 'c'
            ,moveType: 1 //拖拽模式，0或者1
            ,content: '<div style="padding: 50px; line-height: 22px; background-color: #393D49; color: #fff; font-weight: 300;">来自开发者的道歉信<br>由于目前为止网站引流量太少，<br>暂时无法开通更多高级权限接口。<br>无法直接对接平台关联到您的订单信息。<br>很抱歉给您在使用过程中带来这一步繁杂的操作。<br>待本系统达到各个购物平台的接口权限标准,<br>必将及时对接各个平台的接口,<br>减少手动绑定订单这一步操作，<br>再次跟您说一声：抱歉。。。 ^_^</div>'
            ,success: function(layero){
            },
            yes: function(index, layero){
                localStorage.setItem("mall-order-remind-notice",true); //存入 参数： 1.调用的值 2.所要存入的数据
                layer.close(noticeOpen);
            }
        });
    }
  
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
                    url : "/mall/order/"+ data.id,
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
        }else if(obj.event === 'edit'){
            /* layer.alert('编辑行：<br />'+ JSON.stringify(data)) */
            orderFormContainer.show("fast");
            orderFormContainer.find("form").find("input[name='id']").remove();
            orderFormContainer.find("form").append('<input type="hidden" name="id" value="'+ data.id +'"/>');
            orderFormContainer.find('input[name="order_code"]').val(data.order_code);
            orderFormContainer.find('input[name="product_code"]').val(data.product_code);
            orderFormContainer.find('input[name="order_time"]').val(data.order_time);
            orderFormContainer.find('input[name="pay_time"]').val(data.pay_time);
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
            url : edit ? ("/mall/order/"+ data.field.id): ("/mall/order"),
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

    //监听平台的选择
    form.on('select(platform)', function (data) {
        platform = data.value;
        platform = data.elem[data.elem.selectedIndex].text;
        form.render('select');
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

/**
**  查看商品
**/
function parseUrl(obj, event){
    //弹出输入链接地址的菜单
    layer.prompt({title: '请输入所在平台的商品地址（长链接）/淘口令等', formType: 2, content: '<textarea class="layui-layer-input" placeholder="目前支持淘宝/天猫、京东、拼多多等平台的商品长链接地址以及淘宝/天猫商品的淘口令中解析出商品的ID字段自动填充到商品编号里面。"></textarea>', maxlength: 1000}, function(pass, index){
        layer.close(index);
        var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
        $.ajax({
            type : "POST",
            url : "/mall/tool/parse/url",
            data: {url: pass},
            dataType: 'json',
            success : function(data) {
                layer.close(loadi);
                if(data.isSuccess){
                    $("input[name='product_code']").val(data.message);
                }else{
                    ajaxError(data);
                }
            },
            error : function(data) {
                ajaxError(data);
            }
        });
    });

    event.stopPropagation()// 阻止冒泡
    if(event.preventDefault){
        event.preventDefault();
    }else{
        window.event.returnValue == false;
    }
}

/**
**  查看商品
**/
function lookProduct(obj, event){
    //判断平台
    var platform = orderFormContainer.find('select[name="platform"]').val();
    if(isEmpty(platform)){
        layer.msg("请先选择购物平台");
        prevent(event);
        return;
    }

    var productCode = orderFormContainer.find('input[name="product_code"]').val();
    if(isEmpty(productCode)){
        layer.msg("请先填写商品编号");
        prevent(event);
        return;
    }

    var id = productCode;
    if(platform == "淘宝网"){
        id = "tb_"+ productCode;
    }else if(platform == "京东"){
        id = "jd_"+ productCode;
    }else if(platform == "拼多多"){
        id = "pdd_"+ productCode;
    }if(platform == ""){
        id = "zy_"+ productCode;
    }
    toProductDetail(id);
    prevent(event);
}

/**
**阻止冒泡事件传播
**/
function prevent(event){
    event.stopPropagation()// 阻止冒泡
    if(event.preventDefault){
        event.preventDefault();
    }else{
        window.event.returnValue == false;
    }
}