layui.use(['layedit'], function(){
    //第三方绑定
    $('.third-bind').on('click', function(){
        //通过切字符串的方式获取请求的原始链接
        var href = decodeURI(window.location.href);
        var link = $(this).data("link");
        if(isEmpty(link)){
            layer.msg("要跳转的链接不能为空");
            return;
        }
        window.open(link + "?url=" + window.btoa(encodeURI(href)) +"&type=2", '_self');// 编码
    });
    //第三方解绑定
    $('.third-unbind').on('click', function(){
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        var id = $(this).data("id");
        $.ajax({
            type : "GET",
            url : "/my/manage/my/third/unbind?id="+ id,
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