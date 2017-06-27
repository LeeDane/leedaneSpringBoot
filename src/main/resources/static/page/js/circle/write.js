var $contentContainer;
$(function(){
	$contentContainer = $("textarea[name='content']");
	$contentContainer.markdown();
	
	$("[data-toggle='tooltip']").tooltip();
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
	var len = $(obj).closest(".tag-list").find(".tag-item").length;
	if(len == 1)
		return;
	
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
  * 写/编辑帖子
  * @param obj
  * @param isEdit
  */
 function release(obj, isEdit){
	var jsonParams = {};
   	jsonParams.status = 1;
   	if(buildParams(jsonParams)){
 		doRelease(jsonParams, isEdit);
 	}
 }
 
 /**
  * 执行发布/编辑帖子
  * @param jsonParams
  * @param isEdit
  */
 function doRelease(jsonParams, isEdit){
	var formControl = $(".container").find(".form-control");
	var flag = true;
	formControl.each(function(index){
		var name = $(this).attr("name");
		var empty = $(this).attr("empty");
		if(empty && empty == "false" && isEmpty($(this).val())){
			$(this).focus();
			layer.msg($(this).attr("placeholder"));
			flag = false;
			return;
		}
		if(name == "content"){
			var markdown = $contentContainer.data('markdown');
			var text = markdown.parseContent();
			jsonParams[name] = text;
		}else{
			jsonParams[name] = $(this).val();
		}
	});
	if(flag){
		if(isEdit)
			jsonParams.post_id = postId;
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : !isEdit ? "post": "PUT",
			data : jsonParams,
			url : "/cc/"+ circleId +"/post",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data != null && data.isSuccess){
					layer.msg(data.message);
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
	}
 }
 
 /**
  * 获取请求的参数
  * @param jsonParams
  * @returns {Boolean}
  */
 function buildParams(jsonParams){
  	
  	//是否有图
  	var $img = $('.img-container').find('img');
  	if($img && $img.length > 0){
  		var imgs = "";
  		$img.each(function(index){
  			imgs = imgs + $(this)[0].src +";";
  		});
  		jsonParams.has_img = true;
  		jsonParams.imgs = deleteLastStr(imgs);
  	}else{
  		jsonParams.has_img = false;
  	}
  	//是否推荐
  	var recommend = $('[name="is_recommend"]').is(':checked');
  	jsonParams.post_recommend = recommend;
  	
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

  	jsonParams.froms = "web网页端";
  
  	if(circleId && circleId > 0)
  		jsonParams.circle_id = circleId;
  	
  	var createUserId = $('[name="create_user_id"]').val();
  	if(isNotEmpty(createUserId))
  		jsonParams.create_user_id = createUserId;

  	return true;
 }
 
 /**
  * 选择素材后的回调函数
  */
 function afterSelect(links){
	 $('.img-container').empty();
	 var array = links.split(";");
	 var html = '';
	 for(var i = 0; i < array.length; i++){
		html += '<div class="col-lg-4">'+
						'<img src="'+ changeNotNullString(array[i])+'" style="width: 100%; height: 180px;" class="img-responsive" onClick="" />'+
					'</div>';
		//$contentContainer.val($contentContainer.val() + '![]('+ array[i] +')\r\n');
	 }
	 $('.img-container').append(html);
	 $('body').removeClass("modal-open");
 }