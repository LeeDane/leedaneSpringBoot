layui.use(['layedit', 'form'], function(){
    form = layui.form;
    //监听提交
    form.on('submit(send-password)', function(data){
        if(data.field.newpassword != data.field.confirmpassword){
            layer.tips("确认密码不一致！", "#confirmpassword",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }
        if(data.field.newpassword == data.field.password){
            layer.tips("新密码跟旧密码相同！", "#newpassword",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        var crypt = new JSEncrypt();
        crypt.setPublicKey($("#publicKey").val());
        $.ajax({
            type : "PUT",
            url : "/us/user/pwd",
            data: {password: $.md5(data.field.password), new_password: $.md5(data.field.newpassword)},
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