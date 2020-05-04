var path = "/";//记录当前的path
var $pathBreadcrumb; //存放路径的容器
var $cards; //卡片容器集合
layui.use(['element', 'util', 'form'], function(){
    elem = layui.element;
    form = layui.form;
    util = layui.util;

    //鼠标悬停提示特效
    $("#layui-badge-dot-text").hover(function() {
        openTipMsg("#layui-badge-dot-text", "1、请不要上传违法行为、色情、暴动、政治目的强等文件到云盘.<br/>2、该工具免费使用，最终解释权归抖音所属，请不要用于非法途径.<br/>3、服务器资源有限，目前限制抖音视频最大50M，超过部分可能无法播放，请酌情合理使用即可。<br/>4、如果使用过程中有什么问题(包括侵权、涉案、取证等)，请及时联系管理员处理。感谢你的合作。<br/>", 2);
    }, function() {
        layer.close(subtips);
    });
    $pathBreadcrumb = $("#path-breadcrumb");
    $cards = $("#cards");

    //监听提交
    form.on('submit(parse-link)', function(dataField){
        if(isEmpty(dataField.field.url)){
            layer.tips("请先输入链接！", "#parse-link-url",{tips:[3,'#FFB800'], time: 3000});
            return false;
        }

        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        $.ajax({
            type : "GET",
            url : "/my/manage/tool/douyin/remove/watermark",
            data: {text: dataField.field.url},
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