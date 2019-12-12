

/**
** 刷新验证码
*/
function refresh(){
    $('#captcha_img').attr("src", "/kaptcha?"+Math.random());
}
var bindType = 1;
/**
** 选择绑定类型，1是注册， 2是登录
**/
function bindTypeCheck(obj){
    var $obj = $(obj);
    if($obj.hasClass("type-register")){
        bindType = 0;
        $obj.html("未有账号");
        //登录
        $("#confirm-password").hide();
        $("#rg-phone").hide();
        $obj.attr("class", "type-login");
    }else{
        bindType = 1;
        $obj.html("已有账号");
        //注册
        $("#confirm-password").show();
        $("#rg-phone").show();
        $obj.attr("class", "type-register");
    }
}

/**
 ** 刷新验证码
 */
function rgRefresh(){
    $('#rg_captcha_img').attr("src", "/kaptcha?"+Math.random());
}

function doBind(){
    var account = $("#account").val();
    if(isEmpty(account)){
        layer.msg("请输入账号");
        $("#account").focus();
        return;
    }

    var password = $("#password").val();
    if(isEmpty(password)){
        layer.msg("请输入密码");
        $("#password").focus();
        return;
    }


    var confirmPassword = $("#confirm-password").val();
    if(bindType == 1){
        if(isEmpty(confirmPassword)){
            layer.msg("请输入确认密码");
            $("#confirm-password").focus();
            return;
        }

        if(confirmPassword != password){
            layer.msg("两次密码输入不匹配");
            $("#conform-password").focus();
            return;
        }
    }


    var mobilePhone = $("#rg-phone").val();
    if(bindType == 1){
        if(isEmpty(mobilePhone) || mobilePhone.length != 11){
            layer.msg("请输入正确的手机号码");
            $("#rg-phone").focus();
            return;
        }
    }

    var code = $("#img-code").val();
    if(isEmpty(code)){
        layer.msg("请输入验证码");
        $("#img-code").focus();
        return;
    }

    var crypt = new JSEncrypt();
    crypt.setPublicKey($("#publicKey").val());
    var params;
    if(bindType == 1){
        params = {iparams: $("#params").val(), mobilePhone: crypt.encrypt(mobilePhone), account: account, password: crypt.encrypt($.md5(password)), confirmPassword: crypt.encrypt($.md5(confirmPassword)), code: code, t: Math.random()};
    }else{
        params = {iparams: $("#params").val(), account: account, password: crypt.encrypt($.md5(password)), code: code, t: Math.random()};
    }
    var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
    $.ajax({
        type : "post",
        data : params,
        url : "/oauth2/bind/first",
        dataType: 'json',
        withCredentials:true,
        beforeSend:function(request){
        },
        success : function(data) {
            layer.close(loadi);
            if(data.isSuccess){
                layer.msg(data.message);
                window.open("/lg", '_self');
            }else{
                layer.close(loadi);
                ajaxError(data);
            }
        },
        error : function(data) {
            layer.close(loadi);
            ajaxError(data);
        }
    });
}
//回车执行登录
document.onkeydown=function(event){
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if(e && e.keyCode==13){ // enter 键
        doBind();
    }
}