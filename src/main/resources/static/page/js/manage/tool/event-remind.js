var path = "/";//记录当前的path
var $pathBreadcrumb; //存放路径的容器
var $cards; //卡片容器集合
layui.use(['element', 'util', 'form', 'laydate','table'], function(){
    elem = layui.element;
    form = layui.form;
    util = layui.util;
    table = layui.table;
    laydate = layui.laydate;
    //时间选择器
    laydate.render({
        elem: '#task-time'
        ,type: 'time'
    });

    //限制结束时间加1天
    var dateTime = new Date();
    dateTime = dateTime.setDate(dateTime.getDate()+ 1);
    dateTime = new Date(dateTime);
    var minDate = dateTime.Format("yyyy-MM-dd");
    laydate.render({
        elem: '#end-date'
        ,type: 'date'
        ,min: minDate
    });

    table.render({
        elem: '#mytable'
        ,url:'/my/manage/tool/event/reminds'
        //    ,toolbar: '#toolbarDemo'
        ,title: '数据表'
        ,cols: [[
            {
            field:'id', title:'ID', width:80, unresize: true, sort: true}
            ,{field:'name', title:'名称', width:150}
            ,{field:'type', title:'类型', width:150}
            ,{field:'content', title:'内容', width:150}
            ,{field:'time', title:'执行时间', width:90}
            ,{field:'cycle', title:'执行周期', width:90}
            ,{field:'cron', title:'表达式', width:120}
            ,{field:'way', title:'方式', width:150}
            ,{field:'create_time', title:'创建时间', width:180}
            ,{fixed: 'right', title:'操作', toolbar: '#barDemo', width:100}
        ]]
        ,page: true
    });

    //鼠标悬停提示特效
    $("#layui-badge-dot-text").hover(function() {
        openTipMsg("#layui-badge-dot-text", "1、每发送一条短信，平台将自费支付0.045元，请合理使用该功能.<br/>2、请合法使用，不要发送非法信息.<br/>3、如果使用过程中有什么问题(包括侵权、涉案、取证等)，请及时联系管理员处理。感谢您的合作。<br/>", 2);
    }, function() {
        layer.close(subtips);
    });

    $("#layui-badge-task-way").hover(function() {
        openTipMsg("#layui-badge-task-way", "1、如果短信无法选择，请核查是否绑定手机号码.<br/>2、如果邮件无法选择，请核查是否绑定电子邮箱.<br/>3、其他问题请联系管理员反馈。<br/>", 2);
    }, function() {
        layer.close(subtips);
    });
    $pathBreadcrumb = $("#path-breadcrumb");
    $cards = $("#cards");

    //监听提交
    form.on('submit(add-task)', function(dataField){
        if(isEmpty(dataField.field.name)){
            layer.tips("请先输入消息名称！", "#task-name",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }

        if(isEmpty(dataField.field.time)){
            layer.tips("请先选择时间！", "#task-time",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }

        var cron;
        if(dataField.field.cycle == "day"){
            var time = dataField.field.time;
            var h = time.substring(0, 2);
            var m = time.substring(3, 5);
            var s = time.substring(6, 8);
            cron = parseInt(s) + " " + parseInt(m) + " " + parseInt(h) + " * * ?";
        }
        var way = "";
        if(!isEmpty(dataField.field.waySms)){
            way += " 短信";
        }

        if(!isEmpty(dataField.field.wayNotify)){
            way += " 站内信";
        }

        if(!isEmpty(dataField.field.wayEmail)){
            way += " 邮件";
        }

        if(isEmpty(way)){
            layer.tips("通知方式至少选择一种！", "#task-way",{tips:[2,'#FFB800'], time: 3000});
            return false;
        }
        var params = {name: dataField.field.name,
                        cron: cron,
                        time: dataField.field.time,
                        cycle: dataField.field.cycle,
                        type: dataField.field.type,
                        end: dataField.field.end,
                        way: way,
                        content: isEmpty(dataField.field.content) ? dataField.field.name: dataField.field.content};

        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });

        $.ajax({
            type : "POST",
            url : "/my/manage/tool/event/remind",
            data: params,
            dataType: 'json',
            success : function(data) {
                layer.close(loadi);
                if(data.success){
                    layer.msg(data.message+", 1秒后自动跳转");
                    reloadPage(1000);
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

    //监听行工具事件
    table.on('tool(mytable)', function(obj){
        var data = obj.data;
        //console.log(obj)
        if(obj.event === 'del'){
            layer.confirm('确定要删除该条提醒记录吗', function(index){
                var loadi = layer.load('努力删除中…'); //需关闭加载层时，执行layer.close(loadi)即可
                $.ajax({
                    type : "delete",
                    url : "/my/manage/tool/event/remind/"+ data.id,
                    dataType: 'json',
                    beforeSend:function(){
                    },
                    success : function(data) {
                        layer.close(loadi);
                        if(data.success){
                            layer.msg(data.message +",1秒钟后自动刷新");
                            reloadPage(1000);
                        }else{
                            ajaxError(data);
                        }
                    },
                    error : function(data) {
                        layer.close(loadi);
                        ajaxError(data);
                    }
                });
            });
        }
    });
});