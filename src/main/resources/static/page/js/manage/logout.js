var $buttonGroup;
var $reasonGroup;
var $noteGroup;
layui.use(['layedit', 'form'], function(){
    form = layui.form;
    $buttonGroup = $("#button-group");
    $reasonGroup = $("#reason-group");
    $noteGroup = $("#note-group");
    if(time){
        $buttonGroup.append('<button id="left-button" type="button" class="layui-btn layui-btn-primary" disabled>距离注销还有6天10小时10分10秒</button>');
        $buttonGroup.append('<button type="submit" class="layui-btn layui-btn-warm" lay-submit="" lay-filter="cancel">取消申请</button>');
        var seconds = parseInt((new Date(time).getTime() - new Date().getTime()) / 1000);
        leftSeconds(seconds,
            function(sc){
                $("#left-button").text('还剩余'+ formatSeconds(sc));
            }, function(){
                $("#left-button").remove();
                $buttonGroup.append('<button type="submit" class="layui-btn layui-btn-danger" lay-submit="" lay-filter="logout-now">立即注销</button>');
            }
        );
    }else{
        $reasonGroup.show();
        $noteGroup.show();
        $buttonGroup.html('<button type="submit" class="layui-btn" lay-submit="" lay-filter="apply">申请注销</button>');
    }

    //监听提交
    form.on('submit(cancel)', function(dataField){
        layer.confirm('确定要取消申请注销账号吗？', function(index){

            var loadi = layer.load(2, {
                shade: [0.1,'#fff'] //0.1透明度的白色背景
            });
            $.ajax({
                type : "DELETE",
                url : "/my/manage/all/logout",
                dataType: 'json',
                success : function(data) {
                    layer.close(loadi);
                    if(data.success){
                        layer.msg(data.message);
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
        return false;
    });

    //监听提交
    form.on('submit(apply)', function(dataField){
        var reason = dataField.field.reason;
        var note = dataField.field.note;
        layer.confirm('确定要申请注销账号吗', function(index){
            var loadi = layer.load(2, {
                shade: [0.1,'#fff'] //0.1透明度的白色背景
            });
            $.ajax({
                type : "POST",
                url : "/my/manage/all/logout",
                data: {reason: reason, note: note},
                dataType: 'json',
                success : function(data) {
                    layer.close(loadi);
                    if(data.success){
                        layer.msg(data.message);
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
        return false;
    });

    //监听提交
    form.on('submit(logout-now)', function(dataField){
        layer.confirm('确定要注销账号吗？ 所有的账号相关的数据会被永久移除除数据库！', function(index){

            var loadi = layer.load(2, {
                shade: [0.1,'#fff'] //0.1透明度的白色背景
            });
            $.ajax({
                type : "DELETE",
                url : "/my/manage/all/logout/destroy",
                dataType: 'json',
                success : function(data) {
                    layer.close(loadi);
                    if(data.success){
                        layer.msg(data.message);
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
        return false;
    });
});

/**
 * 格式化秒
 * @param int  value 总秒数
 * @return string result 格式化后的字符串
 */
function formatSeconds(value) {
    var theTime = parseInt(value);// 需要转换的时间秒
    var theTime1 = 0;// 分
    var theTime2 = 0;// 小时
    var theTime3 = 0;// 天
    if(theTime > 60) {
        theTime1 = parseInt(theTime/60);
        theTime = parseInt(theTime%60);
        if(theTime1 > 60) {
            theTime2 = parseInt(theTime1/60);
            theTime1 = parseInt(theTime1%60);
            if(theTime2 > 24){
                //大于24小时
                theTime3 = parseInt(theTime2/24);
                theTime2 = parseInt(theTime2%24);
            }
      }
    }
    var result = '';
    if(theTime > 0){
        result = ""+parseInt(theTime)+"秒";
    }
    if(theTime1 > 0) {
        result = ""+parseInt(theTime1)+"分"+result;
    }
    if(theTime2 > 0) {
        result = ""+parseInt(theTime2)+"小时"+result;
    }
    if(theTime3 > 0) {
        result = ""+parseInt(theTime3)+"天"+result;
    }
    return result;
}

/*
剩余秒数计算
*/
function leftSeconds(seconds, proceed, callback) {
    proceed(seconds);//倒计时没有结束执行的函数
    if(seconds && seconds < 0){
        callback(); //结束执行回调函数
        return;
    }
    //延迟一秒执行一次
    setTimeout(function () {
        seconds = seconds - 1;
        leftSeconds(seconds, proceed, callback);
    }, 1000)
}