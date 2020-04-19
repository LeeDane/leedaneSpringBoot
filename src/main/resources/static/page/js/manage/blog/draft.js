layui.use('table', function(){
  var table = layui.table;
  table.render({
    elem: '#mytable'
    ,url:'/my/manage/blog/draft'
//    ,toolbar: '#toolbarDemo'
    ,title: '数据表'
    ,cols: [[
    	{field:'id', title:'ID', width:80, unresize: true, sort: true}
    	,{field:'title', title:'标题', width:300}
      ,{field:'froms', title:'来源', width:100}
      ,{field:'can_comment', title:'评论', width:60}
      ,{field:'can_transmit', title:'转发', width:60}
      ,{field:'create_time', title:'创建时间', width:180}
      ,{field:'category', title:'分类', width:100}
      ,{field:'tag', title:'标签', width:150}
      ,{field:'content', title:'内容', width:600}
      ,{fixed: 'right', title:'操作', toolbar: '#barDemo', width:120}
    ]]
    ,page: true
  });

  //头工具栏事件
  table.on('toolbar(mytable)', function(obj){
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
  table.on('tool(mytable)', function(obj){
    var data = obj.data;
    //console.log(obj)
    if(obj.event === 'del'){
      layer.confirm('确定要删除该条博客记录吗', function(index){
        var loadi = layer.load('努力删除中…'); //需关闭加载层时，执行layer.close(loadi)即可
        $.ajax({
            type : "delete",
            url : "/my/manage/blog?id="+ data.id,
            dataType: 'json',
            beforeSend:function(){
            },
            success : function(data) {
                layer.close(loadi);
                if(data.success){
                    layer.msg(data.message +",2秒钟后自动刷新");
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
      });
    }else if(obj.event === 'publish'){
        openLink("/pb?bid="+ data.id);
    }
  });
});