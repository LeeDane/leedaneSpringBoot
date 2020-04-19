layui.use(['layedit', 'form', 'table'], function(){
    form = layui.form;
    table = layui.table;
      table.render({
        elem: '#mytable'
        ,url:'/my/manage/security/blacks'
    //    ,toolbar: '#toolbarDemo'
        ,title: '数据表'
        ,cols: [[
            {field:'id', title:'ID', width:80, unresize: true, sort: true}
            ,{field:'user_id', title:'用户ID', width:100}
            ,{field:'name', title:'用户名', width:100}
            ,{field:'authorization', title:'授权信息', width:450}
            ,{field:'link', title:'链接', width:120}
            ,{field:'create_time', title:'创建时间', width:180}
            ,{fixed: 'right', title:'操作', toolbar: '#barDemo', width:180}
        ]]
        ,page: true
      });
    //监听行工具事件
    table.on('tool(mytable)', function(obj){
        var data = obj.data;
        //console.log(obj)
        if(obj.event === 'del'){
            layer.confirm('确定要删除该条黑名单记录吗', function(index){
                var loadi = layer.load('努力删除中…'); //需关闭加载层时，执行layer.close(loadi)即可
                $.ajax({
                    type : "delete",
                    url : "/my/manage/security/black/"+ data.id,
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
    //监听提交
    form.on('submit(add-black)', function(dataField){
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        $.ajax({
            type : "POST",
            url : "/my/manage/security/black",
            data: {userId: dataField.field.blackUserId},
            dataType: 'json',
            success : function(data) {
                layer.close(loadi);
                if(data.success){
                    layer.msg(data.message +",1秒钟后自动刷新");
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