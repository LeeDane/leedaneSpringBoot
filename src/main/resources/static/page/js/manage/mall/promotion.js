layui.use(['layedit'], function(){
    //第三方解绑定
    $('.apply-btn').on('click', function(){
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        var platform = $(this).data("platform");
        $.ajax({
            type : "POST",
            url : "/my/manage/mall/promotion/apply",
            data: {platform: platform},
            dataType: 'json',
            success : function(data) {
                layer.close(loadi);
                if(data.success){
                    //发送成功，页面开始倒计时
                    layer.msg(data.message +" 2秒后自动刷新");
                    reloadPage(2000);
                }else{
                    ajaxError(data);
                }
            },
            error : function(data) {
                layer.close(loadi);
                ajaxError(data);
            }
        });
        return false;
    });
});