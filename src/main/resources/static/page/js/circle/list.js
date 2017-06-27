var circles;
var $circleEditModal;
var $tableContainer;
$(function(){
	initPage(".pagination", "getCircles");
	$circleEditModal = $("#edit-circle-modal");
	$("[data-toggle='tooltip']").tooltip();
	
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-circle").addClass("active");
	
	$(".tooltip").css("display", "block");
	
	$tableContainer = $(".table tbody");
	
	//创建圈子
	$(".create-circle").click(function(){
		$.ajax({
			url : "/cc/check",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				if(data.isSuccess){
					showCreateModal(data.message);
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				ajaxError(data);
			}
		});
	});
	getCircles();
});

/**
 * 获取我的圈子列表
 * @param bid
 */
function getCircles(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var params = {page_size: pageSize, current: currentIndex, total: totalPage, t: Math.random()};
	$.ajax({
		url : "/cc/circles?"+ jsonToGetRequestParams(params),
		dataType: 'json',
		beforeSend:function(){
			$tableContainer.find("tr").remove();
			$(".pagination").empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				circles = data.message;
				if(circles.length == 0){
					if(currentIndex == 0){
						$tableContainer.append('<tr><td colspan="7">您还未加入任何的圈子！</td></tr>');
					}else{
						$tableContainer.append('<tr><td colspan="7">已经没有更多的圈子啦，请重新选择！</td></tr>');
						pageDivUtil(data.total);
					}
					return;
				}
				for(var i = 0; i < circles.length; i++){
					$tableContainer.append(buildEachCircleRow(i, circles[i]));
					$("#row-index-"+i).data("circle", circles[i]);
				}
				pageDivUtil(data.total);
			}else{
				ajaxError(data);
			}
			isLoad = false;
		},
		error : function(data) {
			isLoad = false;
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

/**
 * 构建每一行圈子html
 * @param index
 * @param circle
 * @returns {String}
 */
function buildEachCircleRow(index, circle){
	var html = '<tr id="row-index-'+ index +'">'+
					'<td width="30"><img alt="" src="'+ (isEmpty(circle.circle_path) ? '' : circle.circle_path) +'" width="25" height="25"/></td>'+
					'<td width="150" class="cut-text"><a href="javascript:void(0);" onclick="linkToCircle('+ circle.id +');">'+ circle.name +'</a></td>'+
					'<td>'+ circle.create_user_name +'</td>'+
					'<td>'
				if(isNotEmpty(circle.admins)){
					html += changeNotNullString(getAdminMapValue(circle.adminMap));
				}
			html += '</td>'+
					'<td>'+ circle.circle_desc +'</td>'+
					'<td width="60">'+ (circle.status == 1? '正常': (circle.status == 2 ? '已删除': '禁用'))+'</td>'+
					'<td width="100">'+ circle.create_time +'</td>'+
					'<td width="140">';
		if(circle.create_user_id == loginUserId){
			html += '<a href="javascript:void(0);" onclick="showAllotAdminList(this, '+ circle.id+');" style="margin-right: 10px;">分配</a><a href="javascript:void(0);" onclick="showEditCircleModal(this, '+ index +');" style="margin-right: 10px;">编辑</a><a href="javascript:void(0);" onclick="doDelete(this, '+ index +');" style="margin-right: 10px;">删除</a>';
		}
		html += '</td>'+
			'</tr>';
	return html;
}

/**
 * 展示分配的管理员列表
 * @param obj
 * @param circleId
 */
function showAllotAdminList(obj, circleId){
	//获取所有的管理员列表
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/cc/circle/"+ circleId +"/admins",
		dataType: 'json', 
		beforeSend:function(){
			$("#alreadyAdmins").html("");
			$("#notAdmins").html("");
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				for(var i = 0 ; i < data.message.length; i++){
					if(data.message[i].role_type == 0){
						buildNotAdminsHtml(data.message[i]);
					}else if(data.message[i].role_type == 2){
						buildAlreadyAdminsHtml(data.message[i]);
					}
				}
				$("#admin-list").modal("show");
				$("#admin-list").attr("data-id", circleId);
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
 * 添加没有分配管理员的角色的html
 * @param member
 */
function buildNotAdminsHtml(circleMember){
	var html = '<span class="label label-info" data-id="'+ circleMember.member_id +'" onclick="notClick(this);">'+ circleMember.account +'</span>';
	$("#notAdmins").append(html);
}

/**
 * 添加已经分配管理员的角色的html
 * @param member
 */
function buildAlreadyAdminsHtml(circleMember){
	var html = '<span class="label label-primary" data-id="'+ circleMember.member_id +'" onclick="alreadyClick(this);">'+ circleMember.account +'</span>';
	$("#alreadyAdmins").append(html);
}

/**
 * 将已经分配的取消，加入待分配列表
 * @param obj
 */
function notClick(obj){
	var circleMember = {};
	circleMember.member_id = $(obj).attr("data-id");
	circleMember.account = $(obj).text();
	$(obj).remove();
	buildAlreadyAdminsHtml(circleMember);
}

/**
 * 将已经分配的取消，加入待分配列表
 * @param obj
 */
function alreadyClick(obj){
	var circleMember = {};
	circleMember.member_id = $(obj).attr("data-id");
	circleMember.account = $(obj).text();
	$(obj).remove();
	buildNotAdminsHtml(circleMember);
}

/**
 * 分配权限操作
 * @param obj
 */
function admin(obj){
	var modal = $(obj).closest(".modal");
	var circleId = modal.attr("data-id");
	var alreadyAdmins = modal.find("#alreadyAdmins span");
	var adminIds = "";
	alreadyAdmins.each(function(){
		adminIds += $(this).attr("data-id") +",";
	});
	
	adminIds = deleteLastStr(adminIds);
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/cc/circle/"+ circleId +"/admins/allot?admins="+ adminIds,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				layer.msg(data.message + ",1秒后自动刷新");
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


/**
 * 获取管理员的列表
 */
function getAdminMapValue(adminMap){
	if(adminMap){
		var html = '';
		for(var m in adminMap){
			html += '<a href="javascript: void(0);" onclick="linkToMy('+ m +');">' + adminMap[m] + '</a>,';
		}
		
		return deleteLastStr(html);
	}
	return '';
}

function getCircleData(index){
	return $("#row-index-"+ index).data("circle");
}

/**
 * 展示编辑圈子的modal
 * @param index
 */
function showEditCircleModal(obj, index){
	$circleEditModal.modal("show");
	$circleEditModal.attr("data-index", index);
	var circle = getCircleData(index);
	$circleEditModal.find('input[name="name"]').val(circle.name);
	$circleEditModal.find('textarea[name="circle_desc"]').val(changeNotNullString(circle.circle_desc));
	$circleEditModal.find('input[name="circle_path"]').val(changeNotNullString(circle.circle_path));
}

/**
 * 执行编辑操作
 */
function doEdit(obj){
	var modal = $(obj).closest(".modal");
	var dataId = $("#row-index-"+modal.attr("data-index")).data("circle").id;
	
	var formControl = modal.find(".form-control");
	var params = {};
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
			
		params[name] = $(this).val();
	});
	
	if(flag){
		params.cid = dataId;
		console.log(params);
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : isEmpty(dataId) ? "post" : "put",
			data : params,
			url : "/cc/circle",
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
}

/**
 * 删除单个
 */
function doDelete(obj, index){
	var tr = $(obj).closest("tr.each-row");
	var dataId = getCircleData(index).id;
	
	layer.confirm('您要删除这1条圈子吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/cc/circle/" + dataId,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.isSuccess){
					layer.msg(data.message + ",1秒后自动刷新");
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
	}, function(){
	});
}

/**
 * 展示创建圈子的modal
 * @param number
 */
function showCreateModal(number){
	//prompt层
	layer.prompt({title: '输入圈子名称并确认(还能创建'+ number +'个)', formType: 0}, function(pass, promptIndex){
	  //layer.close(promptIndex);
	  //loading层
	  var index = layer.load(1, {
	    shade: [0.1,'#fff'] //0.1透明度的白色背景
	  });
	  
	  $.ajax({
		  	type: 'POST',
			url : "/cc/circle",
			data: {name: pass},
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				//关闭弹出loading
				layer.close(index);
				if(data.isSuccess){
					layer.msg(data.message);
					//添加圈子成功，关闭窗口
					layer.close(promptIndex);
				}else{
					ajaxError(data);
				}
			},
			error : function(data) {
				layer.close(index);
				ajaxError(data);
			}
		});
	});
}
/**
 * 选择图像后的回调
 * @param links
 */
function afterSelect(links){
	$('input[name="circle_path"]').val(links);
}

