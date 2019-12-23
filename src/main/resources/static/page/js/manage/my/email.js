layui.use(['form', 'layedit'], function(){
    form = layui.form;
    //监听提交
    form.on('submit(send-email)', function(data){
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        var crypt = new JSEncrypt();
        crypt.setPublicKey($("#publicKey").val());
        $.ajax({
            type : "POST",
            url : "/my/manage/my/email/bind",
            data: {email: data.field.email, pwd: crypt.encrypt($.md5(data.field.password))},
            dataType: 'json',
            success : function(data) {
                layer.close(loadi);
                if(data.isSuccess){
                    layer.msg(data.message);
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

});