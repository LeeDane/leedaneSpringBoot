var $model;
$(function(){
    $(".navbar-nav .nav-main-li").each(function(){
        $(this).removeClass("active");
    });
    $(".nav-photo").addClass("active");
    $(".manage-to-gallery").on("click", function(){
        var link = $(".gallery-link").val();
        if(isEmpty(link)){
            layer.msg("请输入图片的链接");
            $(".gallery-link").focus();
            return;
        }

        if(!isLink(link)){
            layer.msg("该图片的链接不合法");
            $(".gallery-link").focus();
            return;
        }
        var params = {id: photoId, category: categoryId, path: $(".gallery-link").val(), desc: $(".gallery-desc").val(), width: $(".gallery-width").val(), height: $(".gallery-height").val(), length: $(".gallery-length").val(), t: Math.random()};
        addLink(params);
    });

    //初始化model
    $model = $('#manage-grallery');

    var x = 10;
    var y = 20;
    //鼠标滑入
    $("body").on("mouseover", ".layui-table-main .laytable-cell-1-0-1", function(e){
        var path = $(this).html();
        var imgTooltip = "<div id='imgTooltip'><img src='"+ path +"' alt='"+ path +"' width='180' height='180' /></div>";
        $("body").append(imgTooltip);
        $("#imgTooltip")
            .css({
            "top": (e.pageY + y) + "px",
            "left": (e.pageX + x) + "px"
            }).show("fast");
    });
    //鼠标滑出
    $("body").on("mouseout", ".layui-table-main .laytable-cell-1-0-1", function(e){
        $("#imgTooltip").remove();
    });
    //鼠标移动
    $("body").on("mousemove", ".layui-table-main .laytable-cell-1-0-1", function(e){
        $("#imgTooltip").css({
        "top": (e.pageY + y) + "px",
        "left": (e.pageX + x) + "px"
        });
    });
});
layui.use('table', function(){
  var table = layui.table;
  table.render({
    elem: '#test'
    ,url:'/gl/photos'
    ,title: '数据表'
    ,cols: [[
    	{field:'id', title:'ID', width:80, unresize: true, sort: true}
      ,{field:'path', title:'图片路径', width:600}
      ,{field:'format-length', title:'大小', width:80}
      ,{field:'height', title:'高度', width:80}
      ,{field:'width', title:'宽度', width:80}
      ,{field:'gallery_desc', title:'描述信息', width:120}
      ,{field:'category', title:'分类', width:120}
      ,{field:'modify_time', title:'最后修改时间', width:120}
      ,{fixed: 'right', title:'操作', toolbar: '#barDemo', width:120}
    ]]
    ,page: true
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
            url : "/gl/photo/"+ data.id,
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
        modelShow(data);
    }
  });
});

/**
** 显示model
**/
function modelShow(photo){
    $model.modal("show");
    if(photo){
        categoryId = photo.category_id;
        photoId = photo.id;
        $model.find("#myModalLabel").html("编辑图库");
        $model.find(".manage-to-gallery").text("编辑");
        $model.find(".gallery-link").val(photo.path);
        $model.find(".gallery-desc").val(changeNotNullString(photo.gallery_desc));
        $model.find(".gallery-width").val(changeNotNullString(photo.width));
        $model.find(".gallery-height").val(changeNotNullString(photo.height));
        $model.find(".gallery-length").val(changeNotNullString(photo.length));
        $model.find(".gallery-category").val(changeNotNullString(photo.category));
    }else{
        categoryId = 0;
        photoId = 0;
        $model.find("#myModalLabel").html("添加图库");
        $model.find(".manage-to-gallery").text("添加");
        $model.find(".gallery-link").val("");
        $model.find(".gallery-desc").val("");
        $model.find(".gallery-width").val("");
        $model.find(".gallery-height").val("");
        $model.find(".gallery-length").val("");
        $model.find(".gallery-category").val("");
    }

}

var photoId = 0;
/**
 * 添加图片链接
 * @param params
 */
function addLink(params){
	var loadi = layer.load('努力加载中…');
	$.ajax({
		type : "post",
		data: params,
		url : "/gl/photo",
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
				layer.close(loadi);
                if(data.isSuccess){
                    layer.msg(data.message);
                    reloadPage(1000);
                }else{
                    layer.msg(data.message);
                    layer.close(loadi);
                }
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});

}

/**
 * 选择链接后之后的回调函数
 */
function afterSelectLink(links){
	if(isNotEmpty(links)){
        $model.find(".gallery-width").val("");
        $model.find(".gallery-height").val("");
        $model.find(".gallery-length").val("");
		$model.find(".gallery-link").val(links);
	}
}

var categoryId = 0;
/**
  * 选择分类后的回调函数
  * @param id
  * @param text
  */
 function afterSelectCategory(id, text){
	 console.log(id + "" + text);
	 categoryId = id;
	 $('.gallery-category').val(text);
	 $(".gallery-desc").val("从素材选择");
	 $('body').removeClass("modal-open");
 }