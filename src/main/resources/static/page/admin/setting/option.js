var pageSize = 10;
var currentIndex = 0;
var totalPage  = 0;
layui.use(['layer', 'laypage'], function(){
	layer = layui.layer;
	laypage = layui.laypage;
	$addOrEditModal = $("#add-or-edit-option");
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
	$tableContainer = $(".table");
	//默认查询操作
	getOptions();
});
var options;
var $addOrEditModal;
var $tableContainer;

/**
* 查询
*/
function getOptions(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/option/options?"+ jsonToGetRequestParams(getQueryPagingParams()),
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
						$tableContainer.append('<tr class="each-row"><td colspan="11">暂时还没有任何option！</td></tr>');
					}else{
						$tableContainer.append('<tr class="each-row"><td colspan="11">已经没有更多的option啦，请重新选择！</td></tr>');
						pageDivUtil(data.total);
					}
					return;
				}

				options = data.message;
				for(var i = 0; i < options.length; i++){
					$tableContainer.append(buildRow(options[i], i));
					if(options[i].status != 1)
						$tableContainer.find(".each-row").eq(i).addClass("status-disabled-row");
				}

				/*pageDivUtil(data.total);*/
				//执行一个laypage实例
                 laypage.render({
                    elem: 'option-pager' //注意，这里的 test1 是 ID，不用加 # 号
                    ,layout: ['prev', 'page', 'next', 'count', 'skip']
                    ,count: data.total //数据总数，从服务端得到
                    ,limit: pageSize
                    ,curr: currentIndex + 1
                    ,jump: function(obj, first){
                        //obj包含了当前分页的所有参数，比如：
                        console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                        console.log(obj.limit); //得到每页显示的条数
                        if(!first){
                            currentIndex = obj.curr -1;
                            getOptions();
                        }
                      }
                 });
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
 * 添加option
 */
function addOption(){
	$addOrEditModal.attr("data-id", "");
	$addOrEditModal.find('input[name="option_key"]').val("");
	$addOrEditModal.find('input[name="option_value"]').val("");
	$addOrEditModal.find('textarea[name="option_desc"]').val("");
	$addOrEditModal.find('input[name="version"]').val("");
	$addOrEditModal.find('input[name1="status_normal"]').prop("checked", true);
	$addOrEditModal.find('input[name1="status_disabled"]').removeAttr("checked");
	$addOrEditModal.modal("show");
}

/**
 * 修改option
 */
function editOption(){
	var checkboxs = $("tr.each-row").find('input[type="checkbox"]:checked');
	
	if(!checkboxs || checkboxs.length == 0){
		layer.msg("请选择您要编辑的option");
		return;
	}
	if(checkboxs && checkboxs.length > 1 ){
		layer.msg("一次只能编辑一个option");
		return;
	}
	
	var option = options[$(checkboxs[0]).closest("tr").attr("index")];
	$addOrEditModal.attr("data-id", option.id);
	$addOrEditModal.find('input[name="option_key"]').val(option.option_key);
	$addOrEditModal.find('input[name="option_value"]').val(option.option_value);
	$addOrEditModal.find('textarea[name="option_desc"]').val(option.option_desc);
	$addOrEditModal.find('input[name="version"]').val(option.version);
	if(option.status == 1){
		$addOrEditModal.find('input[name1="status_normal"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_disabled"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="status_disabled"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_normal"]').removeAttr("checked");
	}
	$addOrEditModal.modal("show");
}

/**
 * 修改option
 */
function rowEditOption(obj){
	var tr = $(obj).closest("tr.each-row");
	var index = tr.attr("index");
	
	var option = options[index];
	$addOrEditModal.attr("data-id", option.id);
	$addOrEditModal.find('input[name="option_key"]').val(option.option_key);
	$addOrEditModal.find('input[name="option_value"]').val(option.option_value);
	$addOrEditModal.find('textarea[name="option_desc"]').val(option.option_desc);
	$addOrEditModal.find('input[name="version"]').val(option.version);
	if(option.status == 1){
		$addOrEditModal.find('input[name1="status_normal"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_disabled"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="status_disabled"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_normal"]').removeAttr("checked");
	}
	$addOrEditModal.modal("show");
}

/**
 * 删除单个option
 */
function rowDeleteOption(obj){
	var tr = $(obj).closest("tr.each-row");
	var dataId = tr.attr("data-id");
	
	layer.confirm('您要删除这1条option记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/option/" + dataId,
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
 * 删除多个option
 */
function deletesOption(){
	var checkboxs = $("tr.each-row").find('input[type="checkbox"]:checked');
	
	if(!checkboxs || checkboxs.length == 0){
		layer.msg("请选择您要删除的option");
		return;
	}
	
	layer.confirm('您要删除这'+ checkboxs.length +'条option记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var optionIds = "";
		for(var i = 0; i < checkboxs.length; i++){
			optionIds += $(checkboxs[i]).closest("tr.each-row").attr("data-id") +",";
		}
		
		optionIds = deleteLastStr(optionIds);
		
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/option/options?optionIds=" + optionIds,
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
		
		console.log(params);
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type : isEmpty(dataId) ? "post" : "put",
			data : params,
			url : "/option",
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

/**
 * 构建每一行的html
 * @param option
 * @param index
 * @returns {String}
 */
function buildRow(option, index){
	var html = '<tr class="each-row" data-id="'+ option.id+'" index="'+ index +'">'+
					'<td><input type="checkbox" /></td>'+
					'<td>'+ option.id +'</td>'+
					'<td>'+ changeNotNullString(option.option_key) +'</td>'+
					'<td>'+ option.option_value+'</td>'+
					'<td>'+ changeNotNullString(option.option_desc)+'</td>'+
					'<td>'+ changeNotNullString(option.version) +'</td>'+
					'<td>'+ (option.status == 1? '正常': '禁用')+'</td>'+
					'<td>'+ option.create_time+'</td>'+
					'<td><a href="javascript:void(0);" onclick="rowEditOption(this);" style="margin-right: 10px;">编辑</a><a href="javascript:void(0);" onclick="rowDeleteOption(this);" style="margin-right: 10px;">删除</a></td>'+
				'</tr>';
	return html;
}