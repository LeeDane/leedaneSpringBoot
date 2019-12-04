var testEditor;
testEditor = editormd("test-editormd", {
     placeholder:'本编辑器支持Markdown编辑，左边编写，右边预览',  //默认显示的文字，这里就不解释了
     width: "100%",
     height: 450,
     syncScrolling: "single",
     path: "/page/other/editor-md-master/lib/",   //你的path路径（原资源文件中lib包在我们项目中所放的位置）
     //theme: "dark",//工具栏主题
     //previewTheme: "dark",//预览主题
     //editorTheme: "pastel-on-dark",//编辑主题
     saveHTMLToTextarea: true,
     emoji: false,
     taskList: true,
     tocm: true,         // Using [TOCM]
     tex: true,                   // 开启科学公式TeX语言支持，默认关闭
     flowChart: true,             // 开启流程图支持，默认关闭
     sequenceDiagram: true,       // 开启时序/序列图支持，默认关闭,
     toolbarIcons : function() {  //自定义工具栏，后面有详细介绍
         return editormd.toolbarModes['full']; // full, simple, mini
      },
});

layui.use('table', function(){
  var table = layui.table;
  table.render({
    elem: '#test'
    ,url:'/ev/events'
//    ,toolbar: '#toolbarDemo'
    ,title: '数据表'
    ,cols: [[
    	{field:'id', title:'ID', width:80, unresize: true, sort: true}
      ,{field:'account', title:'账号', width:120}
      ,{field:'source', title:'源内容', width:180}
      ,{field:'content', title:'内容', width:450}
      ,{field:'modify_time', title:'最后修改时间', width:120}
      ,{fixed: 'right', title:'操作', toolbar: '#barDemo', width:120}
    ]]
    ,page: true
  });

  //头工具栏事件
  table.on('toolbar(test)', function(obj){
    var checkStatus = table.checkStatus(obj.config.id);
    switch(obj.event){
      case 'getCheckData':
        var data = checkStatus.data;
        layer.alert(JSON.stringify(data));
      break;
      case 'getCheckLength':
        var data = checkStatus.data;
        layer.msg('选中了：'+ data.length + ' 个');
      break;
      case 'isAll':
        layer.msg(checkStatus.isAll ? '全选': '未全选');
      break;
    };
  });

  //监听行工具事件
  table.on('tool(test)', function(obj){
    var data = obj.data;
    //console.log(obj)
    if(obj.event === 'del'){
      layer.confirm('真的删除行么', function(index){
        var loadi = layer.load('努力删除中…'); //需关闭加载层时，执行layer.close(loadi)即可
        $.ajax({
            type : "delete",
            url : "/ev/manage?eid="+ data.id,
            dataType: 'json',
            beforeSend:function(){
            },
            success : function(data) {
                layer.close(loadi);
                if(data.isSuccess){
                    layer.msg(data.message +",1秒钟后自动刷新");
                    setTimeout("window.location.reload();", 1000);
                }else{
                    ajaxError(data);
                }
            },
            error : function(data) {
                layer.close(loadi);
                ajaxError(data);
            }
        });
      });
    } else if(obj.event === 'edit'){
        testEditor.setMarkdown(data.source);
        $("#publish").text("编辑");
        eventId = data.id;
        scrollTop();
      /*layer.prompt({
        formType: 2
        ,value: data.source
      }, function(value, index){
        obj.update({
          email: value
        });
        layer.close(index);
      });*/
    }
  });
});

var eventId = 0;
/**
* 发布事件
*/
function addEvent(){
    if(!isEmpty(testEditor.getMarkdown())){
        layer.confirm('MD编辑器还有内容，是否清空后再编辑？', {
          btn: ['取消','确定'] //按钮
        }, function(){
            layer.close();
        }, function(){
          testEditor.setMarkdown("");
          $("#publish").text("发布");
          eventId = 0;
          scrollTop();
        });
    }else{
        testEditor.setMarkdown("");
        $("#publish").text("发布");
        eventId = 0;
        scrollTop();
    }
}

/**
* 滚动到顶部
*/
function scrollTop(){
    $("html,body").animate({
        scrollTop: 0
    }, 500);
}

/**
* 发布操作
*/
function publish(){
    if(isEmpty(testEditor.getMarkdown())){
        layer.msg("请先输入内容再提交");
        testEditor.autoFocus ();
        return;
    }

    var params = {};
    if(eventId > 0){
        params.eid = eventId;
    }

    params.content = testEditor.getHTML();
    params.source = testEditor.getMarkdown();
    var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
    $.ajax({
        type : eventId < 1 ? "post" : "put",
        data : params,
        url : "/ev/manage",
        dataType: 'json',
        beforeSend:function(){
        },
        success : function(data) {
            layer.close(loadi);
            if(data.isSuccess){
                layer.msg(data.message +",1秒钟后自动刷新");
                setTimeout("window.location.reload();", 1000);
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