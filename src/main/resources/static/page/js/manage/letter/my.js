layui.use(['element', 'util','table'], function(){
    elem = layui.element;
    util = layui.util;
    table = layui.table;
    table.render({
        elem: '#mytable'
        ,url:'/my/manage/future/letter/list'
        //    ,toolbar: '#toolbarDemo'
        ,title: '数据表'
        ,cols: [[
            {
            field:'id', title:'ID', width:80, unresize: true, sort: true}
            ,{field:'statusText', title:'状态', width:150}
            ,{field:'calla', title:'称呼', width:100}
            ,{field:'subject', title:'标题', width:150}
            ,{field:'sign', title:'签名', width:90}
            ,{field:'publica', title:'是否公开', width:100}
            ,{field:'phone', title:'手机号码', width:120}
            ,{field:'email', title:'电子邮箱', width:200}
            ,{field:'way', title:'发送方式', width:150}
            ,{field:'end', title:'到期时间', width:180}
            ,{field:'create_time', title:'创建时间', width:180}
            ,{fixed: 'right', title:'操作', toolbar: '#barDemo', width:100}
        ]]
        ,page: true
    });

    //监听行工具事件
    table.on('tool(mytable)', function(obj){
        var data = obj.data;
        //console.log(obj)
        if(obj.event === 'del'){
            layer.confirm('确定要删除该条信件记录吗？非可逆行为，请慎重操作！！', function(index){
                var loadi = layer.load('努力删除中…'); //需关闭加载层时，执行layer.close(loadi)即可
                $.ajax({
                    type : "delete",
                    url : "/my/manage//future/letter/"+ data.id,
                    dataType: 'json',
                    beforeSend:function(){
                    },
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
                        layer.close(loadi);
                        ajaxError(data);
                    }
                });
            });
        }
    });
});