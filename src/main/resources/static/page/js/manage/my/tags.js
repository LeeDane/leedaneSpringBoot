layui.use(['layedit'], function(){
    var word_array = [
              {text: "旅游达人", weight: 15, html: {onclick: "cloudClick(this);"}},
              {text: "IT精英", weight: 9, html: {onclick: "cloudClick(this);"}},
              {text: "购物狂人", weight: 6, html: {onclick: "cloudClick(this);"}},
              {text: "孝敬父母", weight: 7, html: {onclick: "cloudClick(this);"}},
              {text: "相夫教子", weight: 5, html: {onclick: "cloudClick(this);"}},
              {text: "吃货", weight: 10, html: {onclick: "cloudClick(this);"}},
              {text: "工作狂", weight: 5, html: {onclick: "cloudClick(this);"}},
              {text: "可甜可咸", weight: 12, html: {onclick: "cloudClick(this);"}},
              {text: "健身达人", weight: 5, html: {onclick: "cloudClick(this);"}},
              {text: "修身养性", weight: 10, html: {onclick: "cloudClick(this);"}},
              {text: "学习", weight: 3, html: {onclick: "cloudClick(this);"}},
              {text: "运动", weight: 5, html: {onclick: "cloudClick(this);"}},
              {text: "读书", weight: 7, html: {onclick: "cloudClick(this);"}},
              {text: "交友", weight: 9, html: {onclick: "cloudClick(this);"}},
              {text: "霸道总裁", weight: 13, html: {onclick: "cloudClick(this);"}},
              {text: "模特", weight: 11, html: {onclick: "cloudClick(this);"}},
              {text: "医生", weight: 2, html: {onclick: "cloudClick(this);"}},
              {text: "教书育人", weight: 5, html: {onclick: "cloudClick(this);"}},
              {text: "夜猫子", weight: 8, html: {onclick: "cloudClick(this);"}},
              {text: "网络红人", weight: 10, html: {onclick: "cloudClick(this);"}}
              // ...as many words as you want
          ];
    $("#cloud").jQCloud(word_array);
    $("body").on("mouseenter", "#tags-contrainer button", function() {
        var val = this.innerText;
        $(this).html(val + '<span class="layui-badge">x</span>');
        $(this).attr("onclick", 'deleteTag(this);');
    });
    $("body").on("mouseleave", "#tags-contrainer button", function() {
        $(this).find("span").remove();
    });
});

/**
** 添加自定义的标签
**/
function addCustomTag(){
    //prompt层

    layer.prompt({title: '请填写自定义标签', formType: 2}, function(pass, index){
        layer.close(index);
        if(pass.length > 8){
            layer.msg('自定义标签不能超过8个字符');
            return;
        }
        addTag(pass);
    });
}

/**
** 云标签的点击事件
**/
function cloudClick(obj){
    var $a = $(obj);
    addTag($a.text());
}

/**
** 添加标签
**/
function addTag(text){
    if(isEmpty(text)){
        layer.msg('添加标签不能为空');
        return;
    }
    //判断是否已经存在
    var $buttons = $("#tags-contrainer button");
    if($buttons.length > 0){
        if($buttons.length == 10){
            layer.msg('目前一个人只能添加10个标签。');
            return;
        }

        for(var i = 0; i < $buttons.length; i++){
            if($($buttons[i]).text() == text){
                layer.msg('该标签已经存在，无需重复添加！');
                return;
            }
        }
    }
     $("#tags-contrainer").append('<button type="button" class="layui-btn layui-btn-xs layui-btn-normal">'+ text +'</button>');
    ifHasTag();
 }

/**
** 判断是否有标签存在
**/
function ifHasTag(){
    if($("#tags-contrainer button").length > 0){
        $("#no-tag").hide();
        $(".upload-tags").show();
    }else{
        $("#no-tag").show();
        $(".upload-tags").hide();
    }
}

/**
**  在页面端删除tag
**/
function deleteTag(obj){
    $(obj).closest("button").remove();
    ifHasTag();
}

/**
** 提交标签
**/
function uploadTags(){
    //判断是否已经存在
    var $buttons = $("#tags-contrainer button");
    if($buttons.length == 0){
        layer.msg('您还没有要提交的标签');
        return;
    }
    var tags = "";
    for(var i = 0; i < $buttons.length; i++){
        tags = tags + $($buttons[i]).text() +",";
    }

    tags = deleteLastStr(tags);
    var loadi = layer.load(2, {
        shade: [0.1,'#fff'] //0.1透明度的白色背景
    });
    $.ajax({
        type : "POST",
        url : "/my/manage/my/tags/save",
        data: {tags, tags},
        dataType: 'json',
        success : function(data) {
            layer.close(loadi);
            if(data.isSuccess){
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
}