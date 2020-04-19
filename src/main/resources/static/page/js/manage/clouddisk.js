var path = "/";//记录当前的path
var $pathBreadcrumb; //存放路径的容器
var $cards; //卡片容器集合
layui.use(['element', 'util', 'table', 'upload', 'form'], function(){
    elem = layui.element;
    form = layui.form;
    util = layui.util;
    var table = layui.table;
    upload = layui.upload;
    table.render({
        elem: '#mytable'
        ,url:'/my/manage/all/my/attention'
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
            layer.confirm('确定要删除该条关注记录吗', function(index){
                var loadi = layer.load('努力删除中…'); //需关闭加载层时，执行layer.close(loadi)即可
                $.ajax({
                    type : "delete",
                    url : "/my/manage/all/my/attention?id="+ data.id,
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

    //鼠标悬停提示特效
    $("#layui-badge-dot-text").hover(function() {
        openTipMsg("#layui-badge-dot-text", "1、请不要上传违法行为、色情、暴动、政治目的强等文件到云盘.<br/>2、云盘完全免费使用，但是服务器资源有限，请酌情合理使用即可。<br/>3、如果使用过程中有什么问题，请及时联系管理员处理。感谢你的合作。<br/>", 2);
    }, function() {
        layer.close(subtips);
    });

    form.on('checkbox(select-all)', function(data){
        console.log(data.elem); //得到checkbox原始DOM对象
        console.log(data.elem.checked); //是否被选中，true或者false
        console.log(data.value); //复选框value值，也可以通过data.elem.value得到
        console.log(data.othis); //得到美化后的DOM对象
        var $checkboxs = $(".layui-card-body input[type=checkbox]");
        $checkboxs.prop('checked', data.elem.checked);
        form.render('checkbox');
    });

    form.on('checkbox(select-single)', function(data){
        console.log(data.elem); //得到checkbox原始DOM对象
        console.log(data.elem.checked); //是否被选中，true或者false
        console.log(data.value); //复选框value值，也可以通过data.elem.value得到
        console.log(data.othis); //得到美化后的DOM对象
        if(!data.elem.checked){
            var $checkboxs = $(".layui-card-header input[lay-filter=select-all]");
            $checkboxs.eq(0).prop('checked', false);
            form.render('checkbox');
        }else{
            form.render('checkbox');
            //判断是否是全选拉
            var all = true;
            var $checkboxs = $(".layui-card-body input[type=checkbox]");
            for(var i = 0; i < $checkboxs.length; i++){
                if(!$checkboxs.eq(i).prop('checked')){
                    all = false;
                    break;
                }
            }

            if(all){
                var $checkboxAlls = $(".layui-card-header input[lay-filter=select-all]");
                $checkboxAlls.eq(0).prop('checked', true);
                form.render('checkbox');
            }
        }

    });
    $pathBreadcrumb = $("#path-breadcrumb");
    $cards = $("#cards");
    list();
});

//获取列表
function list(){
    //取消全选
    $(".layui-card-header input[lay-filter=select-all]").eq(0).prop('checked', false);
    form.render('checkbox');
    var loadi = layer.load(2, {
        shade: [0.1,'#fff'] //0.1透明度的白色背景
    });
    $.ajax({
        type : "get",
        url : "/my/manage/clouddisk/read/catalog?path="+ getPath(),
        dataType: 'json',
        beforeSend:function(){
            //$pathBreadcrumb.empty();
            $cards.find(".layui-card-body").remove();
        },
        success : function(data) {
            layer.close(loadi);
            if(data.success){
                buildBreadcrumb();
                $cards.find(".layui-card-body").remove();
                path = data.extra.path;
                if(data.message.length == 0){
                    $cards.append('<div class="layui-card-body">该目录下暂时没有文件，请先上传</div>');
                }else{
                    for(var i = 0; i < data.message.length; i++){
                        var file = data.message[i];
                        var html = '<div class="layui-card-body">'+
                                        '<div class="layui-container">'+
                                            '<div class="layui-row">'+
                                                '<div class="layui-col-md1 layui-col-xs12" style="text-align:center;">'+
                                                    '<div class="layui-form">'+
                                                        '<div class="layui-form-item">'+
                                                            '<input type="checkbox" name="" lay-skin="primary" lay-filter="select-single">'+
                                                        '</div>'+
                                                    '</div>'+
                                                '</div>'+
                                                '<div class="layui-col-md5 layui-col-xs12">';
                                                if(!file.isdir){
                                                html += '<i class="layui-icon layui-icon-file-b" style="font-size: 13px; margin-right: 5px;"></i><a class="myfilename">'+ changeNotNullString(file.name) +"</a>" ;
                                                }else{
                                                html += '<i class="layui-icon layui-icon-file" style="font-size: 15px; color: #FFB800; margin-right: 5px;"></i><a class="myfilename" onclick="ckickFolder(this);">'+ changeNotNullString(file.name) +"</a>";
                                                }
                                      html +=  '</div>'+
                                                '<div class="layui-col-md1 layui-col-xs12">'+ (file.isdir ? "文件夹" : formatFileSize(file.size)) +'</div>'+
                                                '<div class="layui-col-md2 layui-col-xs12">'+ setTimeAgo(file.time) +'</div>'+
                                                '<div class="layui-col-md3 layui-col-xs12">'+
                                                    '<div class="layui-btn-group">'+
                                                        '<button type="button" class="layui-btn layui-btn-xs layui-btn-warm" onclick="rename(this);">重命名</button>'+
                                                       /* '<button type="button" class="layui-btn layui-btn-xs">分享</button>'+*/
                                                        '<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="deleteFile(this);">删除</button>';
                                                        if(!file.isdir){
                                                           html += '<button type="button" class="layui-btn layui-btn-xs" onclick="downloadFile(this);">下载</button>';
                                                        }
                                            html += '</div>'+
                                                '</div>'+
                                            '</div>'+
                                        '</div>'+
                                    '</div>';
                        $cards.append(html);
                    }
                    form.render(); //更新全部
                }
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

/**
** 构建buildBreadcrumb
**/
function buildBreadcrumb(){
    $pathBreadcrumb.empty();
    if(!isEmpty(path)){
        var paths = path.split("/");
        for(var i = 0; i < paths.length; i++){
             if(!isEmpty(paths[i]))
                $pathBreadcrumb.append('<span lay-separator>/</span><a onclick="ckickBreadcrumb(this);" data-index="'+ i +'">'+ paths[i] +'</a>');
        }
    }
    elem.render('breadcrumb','bread');
}

var tmpUploadIndex = -1; //保存临时上传文件的索引数

function openUpload(){
    //页面层
    layer.open({
        type: 1,
        skin: 'layui-layer-rim', //加上边框
        area: ['650px', '340px'], //宽高
        content: '<div class="layui-upload" style="padding: 8px;">'+
                  '<button type="button" class="layui-btn layui-btn-warm" id="testList">选择多文件</button> '+
                  '<div class="layui-upload-list">'+
                    '<table class="layui-table">'+
                      '<thead>'+
                        '<tr><th>文件名</th>'+
                        '<th>大小</th>'+
                        '<th>状态</th>'+
                        '<th>操作</th>'+
                      '</tr></thead>'+
                      '<tbody id="demoList"></tbody>'+
                    '</table>'+
                  '</div>'+
                  '<button type="button" class="layui-btn" id="testListAction">开始上传</button>'+
                '</div> '
    });


    //多文件列表示例
    var demoListView = $('#demoList')
    ,uploadListIns = upload.render({
        elem: '#testList'
        ,url: '/my/manage/clouddisk/create/file' //改成您自己的上传接口
        ,data: {path: getPath()}
        ,accept: 'file'
        ,type: 'POST'
        ,multiple: true
        ,auto: false
        ,bindAction: '#testListAction'
        ,choose: function(obj){
            var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function(index, file, result){
                var tr = $(['<tr id="upload-'+ index +'">'
                    ,'<td>'+ file.name +'</td>'
                    ,'<td>'+ (file.size/1024).toFixed(1) +'kb</td>'
                    ,'<td>等待上传</td>'
                    ,'<td>'
                      ,'<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                      ,'<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>'
                    ,'</td>'
                    ,'</tr>'].join(''));

                //单个重传
                tr.find('.demo-reload').on('click', function(){
                    obj.upload(index, file);
                });

                //删除
                tr.find('.demo-delete').on('click', function(){
                    delete files[index]; //删除对应的文件
                    tr.remove();
                    uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });
                demoListView.append(tr);
            });
        },before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
           layer.load(2, {shade: [0.1,'#fff']}); //上传loading
           tmpUploadIndex = 0;
         }
        ,done: function(res, index, upload){
            if(res.success){ //上传成功
                tmpUploadIndex = tmpUploadIndex + 1;//自增一次
                var tr = demoListView.find('tr#upload-'+ index)
                ,tds = tr.children();
                tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                tds.eq(3).html(''); //清空操作
                if(demoListView.find('tr').length == tmpUploadIndex){
                    list();
                    layer.closeAll();
                }
                return delete this.files[index]; //删除文件队列已经上传成功的文件
            }
            this.error(index, upload);
        }
        ,error: function(index, upload){
            var tr = demoListView.find('tr#upload-'+ index)
            ,tds = tr.children();
            tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
            tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
        }
    });
}

/***
** 添加文件夹
**/
function addFolder(){
    //prompt层
    layer.prompt({title: '请添加文件夹到目录：'+ getPath(), formType: 3}, function(text, index){
        layer.close(index);
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        $.ajax({
            type : "post",
            url : "/my/manage/clouddisk/create/folder",
            data: {path: getPath() + "/" + text},
            dataType: 'json',
            beforeSend:function(){
            },
            success : function(data) {
                layer.close(loadi);
                if(data.success){
                    //path = path + "/" + text;
                    list();
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

/**
** 点击文件夹
**/
function ckickFolder(obj){
    path = path + "/" + $(obj).text();
    list();
}

/**
** 点击导航
**/
function ckickBreadcrumb(obj){
    var index = $(obj).data("index");
    if(!isEmpty(path)){
        var paths = path.split("/");
        var tmpPath = "";
        for(var i = 0; i < paths.length; i++){
            if(!isEmpty(paths[i])){
                //重新拼接字符串
                tmpPath += "/" + paths[i];
                if(i == index){
                    path = tmpPath;
                    list();
                    break;
                }
             }
        }
    }
}

/**
** 获取文件路径
**/
function getPath(){
    return isEmpty(path) ? "/": path;
}

/**
** 重命名文件
**/
function rename(obj){
    var oldName = $(obj).closest(".layui-row").find(".myfilename").html();
    //prompt层
    layer.prompt({title: '将名称《'+ oldName +"》进行替换成", formType: 3}, function(text, index){
        layer.close(index);
        var loadi = layer.load(2, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
        $.ajax({
            type : "post",
            url : "/my/manage/clouddisk/rename",
            data: {oldName: getPath() + "/" +oldName, newName: getPath() + "/" +text},
            dataType: 'json',
            beforeSend:function(){},
            success: function(data) {
                layer.close(loadi);
                if(data.success){
                    list();
                }else{
                    ajaxError(data);
                }
            },
            error: function(data) {
                layer.close(loadi);
                ajaxError(data);
            }
        });
    });
}
/**
** 返回根目录
**/
function toRoot(){
    path = "/";
    list();
}

/**
** 删除文件
**/
function deleteFile(obj){
    var filename = $(obj).closest(".layui-row").find(".myfilename").html();
    //询问框
    layer.confirm('您确定要删除文件《'+ filename +'》, 如果是文件夹，其子目录下的所有文件都将被删除？', {
        icon: 2,
        skin: "delete-confirm-btn",
        btn: ['确定','取消'] //按钮
    }, function(){
         var loadi = layer.load(2, {
             shade: [0.1,'#fff'] //0.1透明度的白色背景
         });
         $.ajax({
             type : "delete",
             url : "/my/manage/clouddisk/delete?path="+ getPath() + "/" +filename ,
             dataType: 'json',
             beforeSend:function(){},
             success : function(data) {
                 layer.close(loadi);
                 layer.closeAll();
                 if(data.success){
                     list();
                 }else{
                     ajaxError(data);
                 }
             },
             error : function(data) {
                 layer.close(loadi);
                 ajaxError(data);
             }
         });
    }, function(){
    });
}

/**
** 删除批量文件
**/
function deleteBatch(){
    //检查是否有选中
    if($(".layui-card-body input[type=checkbox]:checked").length < 1){
        layer.msg("请至少选择一个文件");
        return;
    }
    var $checkboxs = $(".layui-card-body input[type=checkbox]:checked");
    var filenames = ""; //记录文件名称列表
    for(var i = 0; i < $checkboxs.length; i++){
        filenames += $checkboxs.eq(i).closest(".layui-row").find(".myfilename").html() + ",";
    }
    var confirmContent =  $(".layui-card-header input[lay-filter=select-all]:checked").length > 0 ? "您确定要清空该文件夹下所有文件？": "您确定要删除这"+ $checkboxs.length +"个文件？";
    //询问框
    layer.confirm(confirmContent + ', 如果是文件夹，其子目录下的所有文件都将被删除。', {
        icon: 2,
        skin: "delete-confirm-btn",
        btn: ['确定','取消'] //按钮
    }, function(){
         var loadi = layer.load(2, {
             shade: [0.1,'#fff'] //0.1透明度的白色背景
         });
         $.ajax({
             type : "delete",
             url : "/my/manage/clouddisk/delete/batch?path=" +deleteLastStr(filenames)+"&&root="+ getPath() ,
             dataType: 'json',
             beforeSend:function(){},
             success : function(data) {
                 layer.close(loadi);
                 layer.closeAll();
                 if(data.success){
                     list();
                 }else{
                     ajaxError(data);
                 }
             },
             error : function(data) {
                 layer.close(loadi);
                 ajaxError(data);
             }
         });
    }, function(){
    });
}

/**
** 下载文件
**/
function downloadFile(obj){
    var filename = $(obj).closest(".layui-row").find(".myfilename").html();
    var aEle = document.createElement("a");// 创建a标签
    // blob = new Blob([content]);
    aEle.download = filename;//设置下载的名称
    //aEle.href = URL.createObjectUrl(blob);
    aEle.href = "/my/manage/clouddisk/download/file?path="+ getPath() + "/" +filename +"&name="+ filename;// content为后台返回的下载地址
    aEle.click();// 设置点击事件
}
