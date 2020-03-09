layui.use('table', function(){
  var table = layui.table;
  table.render({
    elem: '#mytable'
    ,url:'/my/manage/all/my/collection'
//    ,toolbar: '#toolbarDemo'
    ,title: '数据表'
    ,cols: [[
    	{field:'id', title:'ID', width:80, unresize: true, sort: true}
    	,{field:'type', title:'类型', width:100}
      ,{field:'source', title:'标题', width:600}
      ,{field:'link', title:'链接', width:180}
      ,{field:'create_time', title:'创建时间', width:180}
      ,{fixed: 'right', title:'操作', toolbar: '#barDemo', width:100}
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
      layer.confirm('确定要删除该条收藏记录吗', function(index){
        var loadi = layer.load('努力删除中…'); //需关闭加载层时，执行layer.close(loadi)即可
        $.ajax({
            type : "delete",
            url : "/my/manage/all/my/collection?id="+ data.id,
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
    }
  });
});