layui.use(['layedit', 'form'], function(){
    form = layui.form;
    $('#get-check-code').on('click', function(){
        var $phone = $("#phone");
        var phone = $phone.val();
        if(isEmpty(phone) || phone.length != 11){
            layer.tips("请先填写完整11位手机号码！", "#phone",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }
        if(!/^1\d{10}$/.test(phone)){
            layer.tips("请先填写正确的手机号码！", "#phone",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }

        //判断是否是发送自己
        var oldPhone = $("#old-phone").text();
        if(isNotEmpty(oldPhone) && oldPhone == phone){
            layer.tips("需要绑定的手机号码不能跟原来的一致！", "#phone",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }
        var $clickThis = $(this);
        $clickThis.find("button").addClass("layui-btn-disabled");
        $clickThis.find("button").attr("disabled", "disabled");
        $phone.attr("disabled", "disabled");

        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        $.ajax({
            type : "POST",
            url : "/tl/sms/sendCode",
            data: {phone: phone, type: "bindPhone"},
            dataType: 'json',
            success : function(data) {
                layer.close(loadi);
                if(data.isSuccess){
                    //发送成功，页面开始倒计时
                    layer.msg(data.message);
                    leftSeconds(120,
                        function(sc){
                            $clickThis.html('<button type="button" class="layui-btn layui-btn-sm layui-btn-disabled" disabled="disabled">还剩余'+ sc +'秒</button>');
                        }, function(){
                            $phone.removeAttr("disabled");
                            $clickThis.html('<button type="button" class="layui-btn layui-btn-sm" style=""><i class="layui-icon layui-icon-release" style="font-size: 12px!important;"></i>获取验证码</button>');
                        }
                    );
                }else{
                    $clickThis.find("button").removeClass("layui-btn-disabled");
                    $clickThis.find("button").removeAttr("disabled");
                    $phone.removeAttr("disabled");
                    ajaxError(data);
                }
            },
            error : function(data) {
                layer.close(loadi);
                $clickThis.find("button").removeClass("layui-btn-disabled");
                $clickThis.find("button").removeAttr("disabled");
                $phone.removeAttr("disabled");
                ajaxError(data);
            }
        });
        return false;
    });
    //监听提交
    form.on('submit(send-phone)', function(dataField){
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        var crypt = new JSEncrypt();
        crypt.setPublicKey($("#publicKey").val());
        $.ajax({
            type : "POST",
            url : "/my/manage/my/phone/bind",
            data: {phone: dataField.field.phone,code: dataField.field.code,type: "bindPhone",pwd: crypt.encrypt($.md5(dataField.field.password))},
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