//表单的容器
var promotionFormContainer;
layui.use(['table', 'form', 'layedit'], function(){
    table = layui.table;
    form = layui.form;;
    //第一个实例
      table.render({
        elem: '#data-table'
        ,height: 'full-240'
        ,url: '/mall/promotion/seat/paging' //数据接口
        ,page: true //开启分页
        ,sort:true
        ,where: {'field': 'id', "order": "desc"}
//        ,initSort: {field:'seat_id', type:'desc'} //这个是生成数据后在页面端的排序字段
        ,cols: [[ //表头
          {field: 'id', title: 'ID', width:80, sort: true, fixed: 'left'}
          ,{field: 'createUser', title: '创建人', width:90}
          ,{field: 'create_time', title: '创建时间', width:110, sort: true}
          ,{field: 'platform', title: '平台', width:80,sort: true}
          ,{field: 'seat_id', title: '推广位ID', width: 165, sort: true}
          ,{field: 'seat_name', title: '推广位名称'}
          ,{field: 'allotUser', title: '分配对象', width: 120}
          ,{field: 'allot_time', title: '分配时间', width:165, sort: true}
          ,{fixed: 'right', title:'操作', toolbar: '#operateBar', width:150}
        ]]

      });
    table.on('sort(seat-table-filter)', function(obj){ //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            console.log(obj.field); //当前排序的字段名
            console.log(obj.type); //当前排序类型：desc（降序）、asc（升序）、null（空对象，默认排序）
            if(isEmpty(obj.type))
                obj.type = "asc";
            console.log(this); //当前排序的 th 对象

//            if(obj.field == "platform"){
                var index = layer.load(2, {
                    shade: [0.1,'#fff'] //0.1透明度的白色背景
                });
                //尽管我们的 table 自带排序功能，但并没有请求服务端。
                //有些时候，你可能需要根据当前排序的字段，重新向服务端发送请求，从而实现服务端排序，如：
                table.reload('data-table', { //testTable是表格容器id
                    initSort: obj //记录初始排序，如果不设的话，将无法标记表头的排序状态。 layui 2.1.1 新增参数
                    ,where: { //请求参数（注意：这里面的参数可任意定义，并非下面固定的格式）
                      field: obj.field //排序字段
                      ,order: obj.type //排序方式
                    }, done: function (res, curr, count) {
                        console.log(res);
                        layer.close(index);
                    }
                });
//            }

        });
    //监听工具条
    table.on('tool(seat-table-filter)', function(obj){
        var data = obj.data;
        if(obj.event === 'detail'){
            //layer.msg('ID：'+ data.id + ' 的查看操作');
            linkToProduct(data.product_id);
        } else if(obj.event === 'del'){
            layer.confirm('确定要删除该推广位吗？此是不可逆行为，请慎重！', function(index){
//                var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
                var loadi = layer.load(2, {
                    shade: [0.1,'#fff'] //0.1透明度的白色背景
                });
                $.ajax({
                    type : "DELETE",
                    url : "/mall/promotion/seat/"+ data.id,
                    dataType: 'json',
                    success : function(dataAfter) {
                        layer.close(loadi);
                        if(dataAfter.isSuccess){
                            obj.del();
                            layer.msg("推广位删除成功，2秒后将自动刷新");
                            reloadPage(2000);
                        }else{
                            ajaxError(dataAfter);
                        }
                    },
                    error : function(dataAfter) {
                        ajaxError(dataAfter);
                        layer.close(loadi);
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
        }else if(obj.event === 'allot'){
            //获取未分配的对象列表
            var loadi = layer.load(2, {
                shade: [0.1,'#fff'] //0.1透明度的白色背景
            });
            $.ajax({
                type : "GET",
                url : "/mall/promotion/seat/"+ data.id +"/noallot/list/",
                data: data.field,
                dataType: 'json',
                success : function(dataAfter) {
                    layer.close(loadi);
                    if(dataAfter.isSuccess){
                        if(dataAfter.data.length > 0){
                            var contentHtml = '<dl id="allot-dl">';
                            for(var i = 0; i < dataAfter.data.length; i++){
                                var user = dataAfter.data[i];
                                contentHtml += '<dd><span>'+ user.account +'</span><a onclick="allotObject('+ data.id +', '+ user.id +');" class="layui-btn layui-btn-normal layui-btn-xs" lay-event="del" style="height: 25px !important; line-height: 25px !important;">分配</a></dd>';
                            }
                            contentHtml += '</dl>';
                            layer.open({
                              title: '给'+ data.platform + '的推广位《'+ data.seat_name +'》分配对象', //页面标题
                              type: 1,
                              skin: 'layui-layer-rim', //加上边框
                              area: ['420px', '340px'], //宽高
                              content: contentHtml //'<dl id="allot-dl"><dd>列表一<a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="del" style="height: 25px !important; line-height: 25px !important;">删除</a></dd></dl>'
                            });
                        }
                    }else{
                        ajaxError(dataAfter);
                    }
                },
                error : function(dataAfter) {
                    ajaxError(dataAfter);
                    layer.close(loadi);
                }
            });

        }else if(obj.event === 'delallot'){//解除绑定分配
            layer.confirm('确定要解除该推广位的用户绑定吗？', function(index){
                //获取未分配的对象列表
                var loadi = layer.load(2, {
                    shade: [0.1,'#fff'] //0.1透明度的白色背景
                });
                $.ajax({
                    type : "DELETE",
                    url : "/mall/promotion/seat/"+ data.id +"/noallot",
                    data: data.field,
                    dataType: 'json',
                    success : function(dataAfter) {
                        layer.close(loadi);
                        if(dataAfter.isSuccess){
                            layer.msg("解除绑定对象成功，2秒后将自动刷新");
                            reloadPage(2000);
                        }else{
                            ajaxError(dataAfter);
                        }
                    },
                    error : function(dataAfter) {
                        ajaxError(dataAfter);
                        layer.close(loadi);
                    }
                });
            });
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

     $('#auto-create-pdd-seats ').on('click', function(){
        //弹出输入链接地址的菜单
        layer.prompt({title: '请输入本次生成的推广位数量', formType: 2, content: '<input class="layui-layer-input"></input>'}, function(pass, index){
            layer.close(index);
            var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
            $.ajax({
                type : "POST",
                url : "/mall/promotion/seat/auto/create/pdd",
                data: {number: parseInt(pass)},
                dataType: 'json',
                success : function(data) {
                    layer.close(loadi);
                    if(data.isSuccess){
                        layer.msg(data.message + ", 2秒后自动刷新");
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
    });
  
    //监听提交
    form.on('submit(new-promotion-seat-form-submit)', function(data){
        //layer.msg(JSON.stringify(data.field));
        var edit = $(this).closest("form").find("input[name='id']").length > 0;
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
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
                layer.close(loadi);
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
/**
**给推广位分配对象
**/
function allotObject(seatId, userId){
//    var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
    var loadi = layer.load(2, {
        shade: [0.1,'#fff'] //0.1透明度的白色背景
    });
    $.ajax({
        type : "POST",
        url : "/mall/promotion/seat/"+ seatId +"/allot/"+ userId,
        dataType: 'json',
        success : function(data) {
            layer.close(loadi);
            if(data.isSuccess){
                layer.msg("分配对象成功，2秒后将自动刷新");
                reloadPage(2000);
            }else{
                ajaxError(data);
            }
        },
        error : function(data) {
            ajaxError(data);
            layer.close(loadi);
        }
    });
}

$(function(){
    promotionFormContainer = $("#promotion-seat-container");
    $("#my-layui-btn-group button").on("click", function(e){
        var index = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        var $this = $(this);
        //有些时候，你可能需要根据当前排序的字段，重新向服务端发送请求，从而实现服务端排序，如：
        table.reload('data-table', { //testTable是表格容器id
//                initSort: obj //记录初始排序，如果不设的话，将无法标记表头的排序状态。 layui 2.1.1 新增参数
            page: {
                curr: 1   //重新总第一页开始
            },
            where: { //请求参数（注意：这里面的参数可任意定义，并非下面固定的格式）
              platform: $this.attr("value")
            }, done: function (res, curr, count) {
                console.log(res);
                layer.close(index);
                $("#my-layui-btn-group button").each(function(){
                    $(this).addClass("layui-btn-primary");
                });
                $this.removeClass("layui-btn-primary");
            }
        });


    });
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
