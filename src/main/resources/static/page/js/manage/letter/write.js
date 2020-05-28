var layEditIndex = 0;
layui.use(['element', 'util', 'form', 'laydate', 'layedit'], function(){
    elem = layui.element;
    form = layui.form;
    util = layui.util;
    laydate = layui.laydate;

    layedit = layui.layedit;
    layEditIndex = layedit.build('content', {
        tool: [
          'strong' //加粗
          ,'italic' //斜体
          ,'underline' //下划线
          ,'del' //删除线

          ,'|' //分割线
          ,'left' //左对齐
          ,'center' //居中对齐
          ,'right' //右对齐
//          ,'link' //超链接
//          ,'unlink' //清除链接
//          ,'face' //表情
//          ,'image' //插入图片
//          ,'help' //帮助
        ]
    }); //建立编辑器

    //限制结束时间加1天
    var dateTime = new Date();
//    dateTime = dateTime.setDate(dateTime.getDate()+ 3);
//    dateTime = new Date(dateTime);
    var minDate = dateTime.Format("yyyy-MM-dd HH:mm:ss");
    laydate.render({
        elem: '#end-date'
        ,type: 'datetime'
        ,btns: ['clear', 'confirm']
        ,min: minDate
    });

    //鼠标悬停提示特效
    $("#layui-badge-dot-text").hover(function() {
        openTipMsg("#layui-badge-dot-text", "1、每发送一条短信，平台将自费支付0.045元，请合理使用该功能.<br/>2、请合法使用，不要发送非法信息.<br/>3、如果使用过程中有什么问题(包括侵权、涉案、取证等)，请及时联系管理员处理。感谢您的合作。<br/>", 2);
    }, function() {
        layer.close(subtips);
    });

    form.on('checkbox(way)', function(data){
        switch(data.elem.name){
            case 'waySms':
                if(data.elem.checked){
                    $("#phoneContainer").show(500);
                }else{
                    $("#phoneContainer").hide(500);
                }
            break;
            case 'wayEmail':
                if(data.elem.checked){
                    $("#emailContainer").show(500);
                }else{
                    $("#emailContainer").hide(500);
                }
            break;
        }
      console.log(data.elem); //得到checkbox原始DOM对象
      console.log(data.elem.checked); //是否被选中，true或者false
      console.log(data.value); //复选框value值，也可以通过data.elem.value得到
      console.log(data.othis); //得到美化后的DOM对象
    });

    //监听提交
    form.on('submit(add-letter)', function(dataField){
        var content = layedit.getContent(layEditIndex);
        if(isEmpty(content)){
            layer.tips("请先输入信件的内容！", "#contentContainer",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }

        var phone = dataField.field.phone;
        //发短信
        if(!isEmpty(dataField.field.waySms)){
            //校验手机号码合法
            if(!/^1\d{10}$/.test(phone)){
                layer.tips("请先输入合法手机号码！", "#phone",{tips:[3,'#FFB800'], time: 3000});
                return false;
            }
        }else{
            phone = "";
        }

        //发送邮件
        var email = dataField.field.email;
        if(!isEmpty(dataField.field.wayEmail)){
            //校验电子邮件是否合法
            if(!/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/.test(email)){
                layer.tips("请先输入合法电子邮箱！", "#email",{tips:[3,'#FFB800'], time: 3000});
                return false;
            }
        }else{
            email = "";
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
            layer.tips("通知方式至少选择一种！", "#way",{tips:[2,'#FFB800'], time: 3000});
            return false;
        }
        var params = {calla: dataField.field.calla,
                        end: dataField.field.end,
                        content: content,
                        publica: isEmpty(dataField.field.publica)? false : dataField.field.publica,
                        sign: dataField.field.sign,
                        subject: dataField.field.subject,
                        phone: phone,
                        email: email,
                        way: way};

        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });

        $.ajax({
            type : "POST",
            url : "/my/manage/future/letter",
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
});