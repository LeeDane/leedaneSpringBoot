layui.use(['layedit', 'form'], function(){
    form = layui.form;
    //监听提交
    form.on('submit(send-email)', function(dataField){
        //校验是否发送给自己
        var oldEmail = $("#old-email").text();
        if(isNotEmpty(oldEmail) && oldEmail == dataField.field.email){
            layer.tips("需要绑定的邮箱不能跟原来的一致！", "#email",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }
        //校验邮箱的格式
        /*if(!/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/.test(dataField.field.email)){
            layer.tips("请先填写正确的电子邮箱！", "#email",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }*/
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        var crypt = new JSEncrypt();
        crypt.setPublicKey($("#publicKey").val());
        $.ajax({
            type : "POST",
            url : "/my/manage/my/email/bind",
            data: {email: dataField.field.email, pwd: crypt.encrypt($.md5(dataField.field.password))},
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