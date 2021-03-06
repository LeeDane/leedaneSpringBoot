layui.use(['layer'], function(){
	layer = layui.layer;
	initPage(".pagination", "getLinks");
	$addOrEditModal = $("#add-or-edit-role-or-permission");
	$("#totalCheckbox").change(function(){
		if(!$(this).hasClass("checked")){
			$("tr.each-row").find("input[type='checkbox']").each(function(){
				$(this).prop("checked", true);
			});
			$(this).addClass("checked");
		}else{
			$("tr.each-row").find("input[type='checkbox']").each(function(){
				$(this).removeAttr("checked");
			});
			$(this).removeClass("checked");
		}
			
	});
	$("tr.each-row").find("input[type='checkbox']").on("click", function(){
		alert("ddd");
	});
	$tableContainer = $(".table");
	//默认查询操作
	getLinks();  
});
var links;
var $addOrEditModal;
var $tableContainer;
/**
* 查询
*/
function getLinks(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/ln/links?"+ jsonToGetRequestParams(getQueryPagingParams()),
		dataType: 'json', 
		beforeSend:function(){
		
		},
		success : function(data) {
			layer.close(loadi);
			//清空原来的数据
			$tableContainer.find(".each-row").remove();
			if(data.success){
				if(data.message.length == 0){
					if(currentIndex == 0){
						$tableContainer.append('<tr class="each-row"><td colspan="10">暂时还没有任何链接！</td></tr>');
					}else{
						$tableContainer.append('<tr class="each-row"><td colspan="10">已经没有更多的链接啦，请重新选择！</td></tr>');
						pageDivUtil(data.total);
					}
					return;
				}
				
				links = data.message;
				for(var i = 0; i < links.length; i++){
					$tableContainer.append(buildRow(links[i], i));
					if(links[i].status != 1)
						$tableContainer.find(".each-row").eq(i).addClass("status-disabled-row");
				}
				pageDivUtil(data.total);
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			layer.close(loadi);
			isLoad = false;
			ajaxError(data);
		}
	});
}

/**
 * 添加链接
 */
function addRole(){
	$addOrEditModal.attr("data-id", "");
	$addOrEditModal.find('input[name="link"]').val("");
	$addOrEditModal.find('input[name="alias"]').val("");
	$addOrEditModal.find('input[name="order"]').val("");
	$addOrEditModal.find('input[name1="status_normal"]').prop("checked", true);
	$addOrEditModal.find('input[name1="status_disabled"]').removeAttr("checked");
	
	$addOrEditModal.find('input[name1="all_true"]').prop("checked", true);
	$addOrEditModal.find('input[name1="all_false"]').removeAttr("checked");
	
	$addOrEditModal.find('input[name1="role_true"]').prop("checked", true);
	$addOrEditModal.find('input[name1="role_false"]').removeAttr("checked");
	
	$addOrEditModal.modal("show");
}

/**
 * 修改链接
 */
function editRole(){
	var checkboxs = $("tr.each-row").find('input[type="checkbox"]:checked');
	
	if(!checkboxs || checkboxs.length == 0){
		layer.msg("请选择您要编辑的链接");
		return;
	}
	if(checkboxs && checkboxs.length > 1 ){
		layer.msg("一次只能编辑一个链接");
		return;
	}
	
	var link = links[$(checkboxs[0]).closest("tr").attr("index")];
	$addOrEditModal.attr("data-id", link.id);
	$addOrEditModal.find('input[name="link"]').val(link.link);
	$addOrEditModal.find('input[name="alias"]').val(link.alias);
	$addOrEditModal.find('input[name="order"]').val(link.order_);
	if(link.status == 1){
		$addOrEditModal.find('input[name1="status_normal"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_disabled"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="status_disabled"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_normal"]').removeAttr("checked");
	}
	
	if(link.all_){
		$addOrEditModal.find('input[name1="all_true"]').prop("checked", true);
		$addOrEditModal.find('input[name1="all_false"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="all_false"]').prop("checked", true);
		$addOrEditModal.find('input[name1="all_true"]').removeAttr("checked");
	}
	
	if(link.role){
		$addOrEditModal.find('input[name1="role_true"]').prop("checked", true);
		$addOrEditModal.find('input[name1="role_false"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="role_false"]').prop("checked", true);
		$addOrEditModal.find('input[name1="role_true"]').removeAttr("checked");
	}
	$addOrEditModal.modal("show");
}

/**
 * 修改链接
 */
function rowEditRole(obj){
	var tr = $(obj).closest("tr.each-row");
	var index = tr.attr("index");
	
	var link = links[index];
	$addOrEditModal.attr("data-id", link.id);
	$addOrEditModal.find('input[name="link"]').val(link.link);
	$addOrEditModal.find('input[name="alias"]').val(link.alias);
	$addOrEditModal.find('input[name="order"]').val(link.order_);
	if(link.status == 1){
		$addOrEditModal.find('input[name1="status_normal"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_disabled"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="status_disabled"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_normal"]').removeAttr("checked");
	}
	
	if(link.all_){
		$addOrEditModal.find('input[name1="all_true"]').prop("checked", true);
		$addOrEditModal.find('input[name1="all_false"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="all_false"]').prop("checked", true);
		$addOrEditModal.find('input[name1="all_true"]').removeAttr("checked");
	}
	
	if(link.role){
		$addOrEditModal.find('input[name1="role_true"]').prop("checked", true);
		$addOrEditModal.find('input[name1="role_false"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="role_false"]').prop("checked", true);
		$addOrEditModal.find('input[name1="role_true"]').removeAttr("checked");
	}
	$addOrEditModal.modal("show");
}

/**
 * 删除单个链接
 */
function rowDeleteRole(obj){
	var tr = $(obj).closest("tr.each-row");
	var dataId = tr.attr("data-id");
	
	layer.confirm('您要删除这1条链接记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/ln/link/" + dataId,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.success){
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
 * 删除多个链接
 */
function deletesRole(){
	var checkboxs = $("tr.each-row").find('input[type="checkbox"]:checked');
	
	if(!checkboxs || checkboxs.length == 0){
		layer.msg("请选择您要删除的链接");
		return;
	}
	
	layer.confirm('您要删除这'+ checkboxs.length +'条链接记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var lnids = "";
		for(var i = 0; i < checkboxs.length; i++){
			lnids += $(checkboxs[i]).closest("tr.each-row").attr("data-id") +",";
		}
		
		lnids = deleteLastStr(lnids);
		
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/ln/links?lnids=" + lnids,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.success){
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
 * 展示角色或者权限的列表
 * @param obj
 * @param role
 * @param lnid
 */
function showRoleOrPermissionList(obj, role, lnid){
	//获取所有的角色列表
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/ln/link/"+ lnid +"/roleOrPermissions?role="+role,
		dataType: 'json', 
		beforeSend:function(){
			$("#alreadyCodes").html("");
			$("#notCodes").html("");
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				for(var i = 0 ; i < data.message.length; i++){
					if(isEmpty(data.message[i].has)){
						buildNotCodesHtml(data.message[i]);
					}else{
						buildAlreadyCodesHtml(data.message[i]);
					}
				}
				$("#role-list").modal("show");
				$("#role-list").attr("data-id", lnid);
				$("#role-list").attr("isRole", role);
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
 * 添加没有分配角色的html
 * @param user
 */
function buildNotCodesHtml(roleOrPermission){
	var html = '<span class="label label-info" data-id="'+ roleOrPermission.id +'" onclick="notClick(this);">'+ roleOrPermission.code +'</span>';
	$("#notCodes").append(html);
}

/**
 * 添加已经分配角色的html
 * @param user
 */
function buildAlreadyCodesHtml(roleOrPermission){
	var html = '<span class="label label-primary" data-id="'+ roleOrPermission.id +'" onclick="alreadyClick(this);">'+ roleOrPermission.code +'</span>';
	$("#alreadyCodes").append(html);
}

/**
 * 将已经分配的取消，加入待分配列表
 * @param obj
 */
function notClick(obj){
	var roleOrPermission = {};
	roleOrPermission.id = $(obj).attr("data-id");
	roleOrPermission.code = $(obj).text();
	$(obj).remove();
	buildAlreadyCodesHtml(roleOrPermission);
}

/**
 * 将已经分配的取消，加入待分配列表
 * @param obj
 */
function alreadyClick(obj){
	var roleOrPermission = {};
	roleOrPermission.id = $(obj).attr("data-id");
	roleOrPermission.code = $(obj).text();
	$(obj).remove();
	buildNotCodesHtml(roleOrPermission);
}

/**
 * 给链接分配角色/权限操作
 * @param obj
 */
function roleOrPermission(obj){
	var modal = $(obj).closest(".modal");
	var lnid = modal.attr("data-id");
	var alreadyCodes = modal.find("#alreadyCodes span");
	var roleOrPermissionIds = "";
	alreadyCodes.each(function(){
		roleOrPermissionIds += $(this).attr("data-id") +",";
	});
	
	var role = modal.attr("isRole");
	roleOrPermissionIds = deleteLastStr(roleOrPermissionIds);
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/ln/link/"+ lnid +"/roleOrPermissions/allot?role="+ role +"&roleOrPermissionIds="+ roleOrPermissionIds,
		dataType: 'json', 
		beforeSend:function(){
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
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
 * 添加获取编辑的提交操作
 * @param obj
 */
function addOrEditCommit(obj){
	var modal = $(obj).closest(".modal");
	var dataId = modal.attr("data-id");
	
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
			return flag;
		}
			
		params[name] = $(this).val();
	});
	
	if(flag){
		params.id = dataId;
		params.status = modal.find('input[name="status"]:checked').val();
		params.role = modal.find('input[name="role"]:checked').val() == "true" ? true: false;
		params.all = modal.find('input[name="all"]:checked').val() == "true" ? true: false;
		
		console.log(params);
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : isEmpty(dataId) ? "post" : "put",
			data : params,
			url : "/ln/link",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data.success){
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
 * 获取请求列表
 */
function getQueryPagingParams(){
	return {page_size: pageSize, current: currentIndex, total: totalPage, t: Math.random()};
}

function buildRow(link, index){
	var html = '<tr class="each-row" data-id="'+ link.id+'" index="'+ index +'">'+
					'<td><input type="checkbox" /></td>'+
					'<td>'+ changeNotNullString(link.link) +'</td>'+
					'<td>'+ changeNotNullString(link.alias) +'</td>'+
					'<td>'+ changeNotNullString(link.order_) +'</td>';
		if(isNotEmpty(link.roleOrPermissionCodes)){
			var roleOrPermissionCodes = link.roleOrPermissionCodes.split(",");
			html += '<td>';
			for(var ln = 0; ln < roleOrPermissionCodes.length; ln++)
				html += '<span class="label label-primary">'+ roleOrPermissionCodes[ln] +'</span>';
			
			html += '</td>';
		}else{
			html += '<td></td>';
		}
		html += '<td>'+ (link.status == 1? '正常': '禁用')+'</td>'+
				'<td>'+ (link.all_? '全部匹配': '任意匹配')+'</td>'+
				'<td>'+ (link.role? '角色控制': '权限控制')+'</td>'+
				'<td>'+ link.create_time+'</td>'+
				'<td><a href="javascript:void(0);" onclick="showRoleOrPermissionList(this, '+ link.role +','+ link.id +');" style="margin-right: 10px;">分配</a><a href="javascript:void(0);" onclick="rowEditRole(this);" style="margin-right: 10px;">编辑</a><a href="javascript:void(0);" onclick="rowDeleteRole(this);" style="margin-right: 10px;">删除</a></td>'+
				'</tr>';
	return html;
}

