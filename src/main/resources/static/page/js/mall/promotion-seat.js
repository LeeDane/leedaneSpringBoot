//表单的容器
var promotionFormContainer;
layui.use(['table', 'form', 'layedit'], function(){
    var table = layui.table;
    form = layui.form;;
    //监听工具条
    table.on('tool(demo)', function(obj){
        var data = obj.data;
        if(obj.event === 'detail'){
            //layer.msg('ID：'+ data.id + ' 的查看操作');
            linkToProduct(data.product_id);
        } else if(obj.event === 'del'){
            layer.confirm('确定要删除该推广位吗？此是不可逆行为，请慎重！', function(index){
                var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
                $.ajax({
                    type : "DELETE",
                    url : "/mall/promotion/seat/"+ data.id,
                    dataType: 'json',
                    success : function(data) {
                        layer.close(loadi);
                        if(data.isSuccess){
                            obj.del();
                            layer.msg("推广位删除成功，2秒后将自动刷新");
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
            promotionFormContainer.show("fast");
            promotionFormContainer.find("form").find("input[name='id']").remove();
            promotionFormContainer.find("form").append('<input type="hidden" name="id" value="'+ data.id +'"/>');
            promotionFormContainer.find('input[name="seat_name"]').val(data.seat_name);
            //处理平台
            var options = promotionFormContainer.find('select[name="platform"]').find("option");
            var platform = data.platform;
            for(var i = 0; i < options.length; i++){
                if($(options[i]).attr("value") == platform){
                    $(options[i]).attr("selected", "selected");
                    form.render('select');
                    break; //退出循环
                }
            }
            promotionFormContainer.find('select[name="platform"]').val(data.platform);
            promotionFormContainer.find('input[name="seat_id"]').val(data.seat_id);
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
        },newPromotionSeat: function(){ //新推广位
            var width = $(window).width() > 600 ? "420px": $(window).width()+"px";
            //页面层
            layer.open({
                type: 1,
                title: '生成分享链接',
                skin: 'layui-layer-rim', //加上边框
                area: [width, '340px'], //宽高
                content: buildNewPromotionSeat()
            });
        }
    };
  
    $('.promotion-seat-group .new-promotion-seat-btn ').on('click', function(){
        //var type = $(this).data('type');
        //active[type] ? active[type].call(this) : '';
        promotionFormContainer.toggle("fast");
        promotionFormContainer.find("form")[0].reset();
        promotionFormContainer.find("form").find("input[name='id']").remove();
    });
  
    //监听提交
    form.on('submit(new-promotion-seat-form-submit)', function(data){
        //layer.msg(JSON.stringify(data.field));
        var edit = $(this).closest("form").find("input[name='id']").length > 0;
        var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
        $.ajax({
            type : edit ? "PUT": "POST",
            url : edit ? ("/mall/promotion/seat/"+ data.field.id): ("/mall/promotion/seat/"),
            data: data.field,
            dataType: 'json',
            success : function(data) {
                layer.close(loadi);
                if(data.isSuccess){
                    layer.msg(edit ? '推广位修改成功，2秒后将自动刷新' :"推广位添加成功，2秒后将自动刷新");
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
    promotionFormContainer = $("#promotion-seat-container");
});

/**
 * 构建弹出添加推广位的框
 * @returns {String}
 */
function buildNewPromotionSeat(){
	var html = '<div class="layui-tab layui-tab-brief" lay-filter="shareLinkBrief">123555'+
				'</div> ';
	return html;
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