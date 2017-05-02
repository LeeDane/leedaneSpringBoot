//实例化编辑器
//建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
//var ue = UE.getEditor('editor');
var ue = UE.getEditor('editor', {
    "initialFrameHeight": "200",
    
    toolbars: [[
              'fullscreen', 'source', '|', 'undo', 'redo', '|',
              'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'superscript', 'subscript', 'removeformat', 'formatmatch', 'autotypeset', 'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'selectall', 'cleardoc', '|',
              'rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
              'customstyle', 'paragraph', 'fontfamily', 'fontsize', '|',
              'directionalityltr', 'directionalityrtl', 'indent', '|',
              'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'touppercase', 'tolowercase', '|',
              'link', 'unlink', 'anchor', '|', 'imagenone', 'imageleft', 'imageright', 'imagecenter', '|', 
              'simpleupload', 'insertimage','emotion', 'scrawl'/*, 'insertvideo' , 'music' MP3*/, 'attachment', 'map', /* 'gmap', 谷歌地图 */ 'insertframe', 'insertcode'/* , 'webapp' 百度应用 */, 'pagebreak', 'template', 'background', '|',
              'horizontal', 'date', 'time', 'spechars', /*'snapscreen', */'wordimage', '|',
              'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', 'charts', '|',
              'print', 'preview', 'searchreplace', 'drafts'/* , 'help' 帮助 */
          ]],
    enterTag: "&nbsp;"
});

$(function(){
	if(bid > 0){
		ue.addListener("ready", function () {
			// editor准备好之后才可以使用,不然要是ue还没有初始化完成就调用就会报错
			getEditBlog(bid);
		});
		
	}
	
	//是否有主图
	$('[name="has_img"]').click(function(){ 
		if(this.checked){
			$(".img-url-row").removeClass("hidden");
		}else{
			$(".img-url-row").addClass("hidden");
		}
	}); 
	
	//是否原创
	$('[name="is_original"]').click(function(){ 
		if(!this.checked){
			$(".is-original-row").removeClass("hidden");
		}else{
			$(".is-original-row").addClass("hidden");
		}
	}); 
	
});

/**
 * 获取编辑的博客
 * @param bid
 */
function getEditBlog(bid){
	$.ajax({
		url : "/bg/blog/edit/"+ bid,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			if(data != null && data.isSuccess && data.message.length == 1){
				addToEdit(data.message[0]);
			}else{
				ajaxError(data);
				javascript:window.history.forward(1);
				setTimeout('window.open("/pb", "_self")', 1500);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 添加标签
 * @param obj
 */
function addTag(obj){
	var obj = $(obj);
	var text = obj.val();
	if(isEmpty(text)){
		layer.msg("标签不能为空");
		obj.focus();
		return;
	}
	
	if(text.length > 10){
		layer.msg("标签长度不能超过10位");
		return;
	}
	addTagItem(obj, text);
}

function addTagItem(obj, text){
	var tagList = obj.closest(".row").find(".tag-list");
	var items = tagList.find(".tag-item");
	var length = items.length;
	if(items && length > 2){
		layer.msg("标签超过3个，请先删除后添加");
    		return;
    	}
					    
  
		var tagItemHtml = '<div class="btn-group dropup tag-item tag-item-'+ length +'">'+
				        '<button type="button" class="btn btn-primary tag-value">'+ text +'</button>'+
				        '<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-expanded="false">'+
				          '<span class="caret"></span>'+
				          '<span class="sr-only">Toggle Dropdown</span>'+
				        '</button>'+
				        '<ul class="dropdown-menu dropdown-menu-right" role="menu">'+
				          '<li><a href="javascript:void(0);" onclick="deleteTag(this, '+length+');">删除</a></li>'+    
				          '<li><a href="javascript:void(0);" onclick="moveLeftTag(this, '+length+');">左移</a></li>'+
				          '<li><a href="javascript:void(0);" onclick="moveRightTag(this, '+length+');">右移</a></li>'+
				          '<li class="divider"></li>'+
				          '<li><a href="javascript:void(0);" onclick="clearTag(this);">清空</a></li>'+
				        '</ul>'+
				      '</div>';
	tagList.append(tagItemHtml);
	obj.val("");
}
/**
 * 删除标签
 * @param obj
 */
function deleteTag(obj){
	var o = $(obj);
	o.closest(".row").find(".tag-input").focus();
	o.closest(".tag-item").remove();
}

/**
 * 左移动标签
 * @param obj
 * @param index
 */
function moveLeftTag(obj, index){
	var current = $(obj);
	
	if(index == 0)
		return;
	
	var currentText = current.closest(".tag-item").find(".tag-value").text();
	
	var leftIndex = index - 1;
	var left = current.closest(".tag-list").find(".tag-item-"+ leftIndex);
	var leftText = left.find(".tag-value").text();
	
	current.closest(".tag-item").find(".tag-value").text(leftText);
	left.find(".tag-value").text(currentText);
}

/**
 * 右移动标签
 * @param obj
 * @param index
 */
function moveRightTag(obj, index){
	var current = $(obj);
	
	if(index == 2)
		return;
	
	var currentText = current.closest(".tag-item").find(".tag-value").text();
	
	var rightIndex = index + 1;
	var right = current.closest(".tag-list").find(".tag-item-"+ rightIndex);
	var rightText = right.find(".tag-value").text();
	
	current.closest(".tag-item").find(".tag-value").text(rightText);
	right.find(".tag-value").text(currentText);
}

/**
 * 清空标签
 * @param obj
 */
function clearTag(obj){
	var o = $(obj);
	o.closest(".row").find(".tag-input").focus();
	o.closest(".tag-list").empty();
	o.remove();
}

 /**
  * 发布文章
  */
 function release(){
	var jsonParams = {};
   	jsonParams.status = 1;
	if(buildParams(jsonParams)){
		doRelease(jsonParams);
	}
 }
 /**
  * 发布草稿
  */
 function draft(){
	var jsonParams = {};
    jsonParams.status = -1;
 	if(buildParams(jsonParams)){
 		doRelease(jsonParams);
 	}
 }
 
 /**
  * 执行发布(草稿)文章
  * @param jsonParams
  */
 function doRelease(jsonParams){
	 var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : "post",
			data : jsonParams,
			url : "/bg/blog",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data != null && data.isSuccess){
					layer.msg("发布文章成功");
					window.location.reload();
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
  * 获取请求的参数
  * @param jsonParams
  * @returns {Boolean}
  */
 function buildParams(jsonParams){
  	//校验标题
  	var titleObj = $('[name="title"]');
  	var title = titleObj.val();
  	if(isEmpty(title)){
  		layer.msg(titleObj.attr("placeholder"));
  		titleObj.focus();
  		return false;
  	}
  	jsonParams.title = title;
  	
  	//校验内容
  	var content = ue.getContent();
  	if(isEmpty(content)){
  		layer.msg("内容不能为空");
  		ue.focus();
  		return false;
  	}
  	
  	console.log(ue.getAllHtml());
  	jsonParams.content = content;
  	
  	//是否有图
  	var hasImg = $('[name="has_img"]').is(':checked');
  	if(hasImg){
  		var img_url = $('[name="img_url"]').val();
  		if(isNotEmpty(img_url) && !isLink(img_url)){
			layer.msg("该图片的链接不合法");
			$('[name="img_url"]').focus();
			return false;
		}
  		jsonParams.img_url = img_url;
  	}
  	jsonParams.has_img = hasImg;
  
  	//是否原创
  	var original = $('[name="is_original"]').is(':checked');
  	if(!original){
  		var originLinkObj = $('[name="origin_link"]');
  		var originLink = originLinkObj.val();
  		if(isEmpty(originLink)){
  			layer.msg(originLinkObj.attr("placeholder"));
  			originLinkObj.focus();
      		return false;
  		}
  		
  		if(!isLink(originLink)){
			layer.msg("该图片的链接不合法");
			originLinkObj.focus();
			return false;
		}
  		jsonParams.origin_link = originLink;
  		
  		var sourceObj = $('[name="source"]');
  		var source = sourceObj.val();
  		if(isEmpty(source)){
  			layer.msg(sourceObj.attr("placeholder"));
  			sourceObj.focus();
      		return false;
  		}
  		jsonParams.source = source;
  	}
  	//是否推荐
  	var recommend = $('[name="is_recommend"]').is(':checked');
  	jsonParams.is_recommend = recommend;
  	//获取摘要
  	var digest = $('[name="digest"]').val();
  	jsonParams.has_digest = isEmpty(digest);
  	jsonParams.digest = digest;
  	
  	//获取标签
  	var tagItems = $(".tag-item");
  	var tagText = '';
  	if(tagItems && tagItems.length > 0){
  		for(var i = 0; i < tagItems.length; i++){
  			if(i == tagItems.length -1)
  				tagText = tagText + $(tagItems[i]).find(".tag-value").text();
  			else
  				tagText = tagText + $(tagItems[i]).find(".tag-value").text() + ",";
  		}
  		jsonParams.tag = tagText;
  	}
  	
  	//是否能评论
  	var canComment = $('[name="can_comment"]').is(':checked');
  	jsonParams.can_comment = canComment;
  	
  	//是否能转发
  	var canTransmit = $('[name="can_transmit"]').is(':checked');
  	jsonParams.can_transmit = canTransmit;
  	
  	//是否公开
  	var public_ = $('[name="public"]').is(':checked');
  	jsonParams.public_ = public_;
  	
  	jsonParams.froms = "web网页端";
  	
  	//分类
  	jsonParams.category = $('[name="category"]').val();
  	
  	if(bid && bid > 0)
  		jsonParams.bid = bid;
  	
  	var createUserId = $('[name="create_user_id"]').val();
  	if(isNotEmpty(createUserId))
  		jsonParams.create_user_id = createUserId;

  	return true;
 }
 
 var draftList;
 /**
  * 加载草稿列表
  */
 function draftlist(){
	 var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			url : "/bg/drafts",
			dataType: 'json', 
			beforeSend:function(){
				$("#draft-group-list").empty();
				draftList = [];
			},
			success : function(data) {
				layer.close(loadi);
				if(data != null && data.isSuccess){
					$("#load-draft").modal("show");
					buildDraftList(data.message);
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
  * 构造草稿列表
  * @param blogs
  */
 function buildDraftList(blogs){
	 draftList = blogs;
	 $("#draft-group-list").empty();
	 for(var i = 0 ; i < blogs.length; i++){
		 var itemsHtml = '<div class="list-group-item draft-item" style="cursor:pointer;">'+
									'<div class="row">'+
								'<div class="col-lg-1 col-sm-1" onclick="addDraftToEdit('+i+');">'+
									'<span class="badge">' + (i + 1) + '</span>'+
								'</div>'+
								'<div class="col-lg-6 col-sm-6" onclick="addDraftToEdit('+i+');">'+ blogs[i].title +
								'</div>'+
								'<div class="col-lg-4 col-sm-4" onclick="addDraftToEdit('+i+');">'+ blogs[i].create_time +
								'</div>'+
								'<div class="col-lg-1 col-sm-1"  onclick="deleteDraft(this, '+i+');"><button type="button" class="close" aria-hidden="true">×</button></div>'+
							'</div>'+
						'</div>';
		$("#draft-group-list").append(itemsHtml);
	 }
	 
 }
 
 /**
  * 将草稿加载到编辑列表
  * @param index
  */
 function addDraftToEdit(index){
	 if(!draftList || draftList.length < 1){
		 layer.msg("草稿列表为空");
		 return;
	 }
	 
	 if(draftList.length <= index){
		 layer.msg("编辑的草稿索引越界，请刷新");
		 return;
	 }
	 addToEdit(draftList[index]);
	 $("#load-draft").modal("hide");
 }
 
 /**
  * 删除草稿
  * @param index
  */
 function deleteDraft(obj, index){
	 if(!draftList || draftList.length < 1){
		 layer.msg("草稿列表为空");
		 return;
	 }
	 
	 if(draftList.length <= index){
		 layer.msg("编辑的草稿索引越界，请刷新");
		 return;
	 }
	 
	 layer.confirm('您要删除该条草稿记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/bg/blog?b_id=" + draftList[index].id,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg(data.message);
					draftList.splice(index,1);
					buildDraftList(draftList);
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
	}, function(data){
		ajaxError(data);
	});
 }
 
 /**
  * 将文章列表添加到编辑页面
  * @param blog
  */
 function addToEdit(blog){
	//标题
  	var titleObj = $('[name="title"]');
  	titleObj.val(blog.title);
  	
  	//内容
  	var ct = blog.content;
  	ue.setContent(ct);
  	
  	//是否有图
  	$('[name="has_img"]').attr('checked', blog.has_img);
  	if(blog.has_img){
  		$(".img-url-row").removeClass("hidden");
  		$('[name="img_url"]').val(blog.img_url);
  	}
  
  	//是否原创
  	var isOriginal = isEmpty(blog.origin_link) && isEmpty(blog.source);
  	$('[name="is_original"]').prop('checked', isOriginal);
  	if(!isOriginal){
  		$(".is-original-row").removeClass("hidden");
  		$('[name="origin_link"]').val(changeNotNullString(blog.origin_link));
  		$('[name="source"]').val(changeNotNullString(blog.source));
  	}
  	//是否推荐
  	$('[name="is_recommend"]').prop('checked', blog.is_recommend);
  	
  	//摘要
  	$('[name="digest"]').val(blog.digest);
  	
  	//获取标签
  	var tagText = blog.tag;
  	if(isNotEmpty(tagText)){
  		var tagObj = $(".tag-input");
  		var tagArray = tagText.split(",");
  		for(var i = 0; i < tagArray.length; i++){
  			addTagItem(tagObj, tagArray[i]);
  		}
  	}
  	
  	//评论
  	$('[name="can_comment"]').attr('checked', blog.can_comment);
  	
  	if(isNotEmpty(blog.category)){
  		$('[name="category"]').find('option[text="'+ blog.category +'"]').attr("selected",true); 
  	}
  	
  	//转发
  	$('[name="can_transmit"]').attr('checked', blog.can_transmit);
  	
  	$('[name="create_user_id"]').val(""+blog.create_user_id);
  		
 }