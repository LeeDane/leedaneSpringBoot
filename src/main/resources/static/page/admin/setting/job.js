var jobs;
var currentIndex = 0;
var pageSize = 8;
var totalPage = 0;
var $addOrEditModal;
//浏览器可视区域页面的高度
$(function(){
	$addOrEditModal = $("#add-or-edit-job");
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
	
	//默认查询操作
	getJobs();
});

/**
* 查询
*/
function getJobs(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		url : "/jm/jobs?"+ jsonToGetRequestParams(getQueryPagingParams()),
		dataType: 'json', 
		beforeSend:function(){
		
		},
		success : function(data) {
			layer.close(loadi);
			//清空原来的数据
			$(".table").find(".each-row").remove();
			if(data.isSuccess){
				if(data.message.length == 0){
					$(".table").append('<tr class="each-row"><td colspan="7">空空的，还没有数据</td></tr>');
					return;
				}
				
				jobs = data.message;
				for(var i = 0; i < jobs.length; i++){
					$(".table").append(buildRow(jobs[i], i));
					if(jobs[i].status != 1)
						$(".table").find(".each-row").eq(i).addClass("status-disabled-row");
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
 * 添加任务
 */
function addJob(){
	$addOrEditModal.attr("data-id", "");
	$addOrEditModal.find('input[name="job_group"]').val("");
	$addOrEditModal.find('input[name="expression"]').val("");
	$addOrEditModal.find('input[name="class_name"]').val("");
	$addOrEditModal.find('input[name="job_name"]').val("");
	$addOrEditModal.find('input[name="job_order"]').val("");
	$addOrEditModal.find('textarea[name="job_desc"]').val("");
	$addOrEditModal.find('input[name1="status_normal"]').prop("checked", true);
	$addOrEditModal.find('input[name1="status_disabled"]').removeAttr("checked");
	$addOrEditModal.modal("show");
}

/**
 * 修改任务
 */
function editJob(){
	var checkboxs = $("tr.each-row").find('input[type="checkbox"]:checked');
	
	if(!checkboxs || checkboxs.length == 0){
		layer.msg("请选择您要编辑的任务");
		return;
	}
	if(checkboxs && checkboxs.length > 1 ){
		layer.msg("一次只能编辑一个任务");
		return;
	}
	
	var job = jobs[$(checkboxs[0]).closest("tr").attr("index")];
	$addOrEditModal.attr("data-id", job.id);
	$addOrEditModal.find('input[name="job_group"]').val(job.job_group);
	$addOrEditModal.find('input[name="expression"]').val(job.expression);
	$addOrEditModal.find('input[name="class_name"]').val(job.class_name);
	$addOrEditModal.find('input[name="job_name"]').val(job.job_name);
	$addOrEditModal.find('input[name="job_order"]').val(job.job_order);
	$addOrEditModal.find('textarea[name="job_desc"]').val(job.job_desc);
	if(job.status == 1){
		$addOrEditModal.find('input[name1="status_normal"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_disabled"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="status_disabled"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_normal"]').removeAttr("checked");
	}
	$addOrEditModal.modal("show");
}

/**
 * 修改任务
 */
function rowEditJob(obj){
	var tr = $(obj).closest("tr.each-row");
	var index = tr.attr("index");
	
	var job = jobs[index];
	$addOrEditModal.attr("data-id", job.id);
	$addOrEditModal.find('input[name="job_group"]').val(job.job_group);
	$addOrEditModal.find('input[name="expression"]').val(job.expression);
	$addOrEditModal.find('input[name="class_name"]').val(job.class_name);
	$addOrEditModal.find('input[name="job_name"]').val(job.job_name);
	$addOrEditModal.find('input[name="job_order"]').val(job.job_order);
	$addOrEditModal.find('textarea[name="job_desc"]').val(job.job_desc);
	if(job.status == 1){
		$addOrEditModal.find('input[name1="status_normal"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_disabled"]').removeAttr("checked");
	}else{
		$addOrEditModal.find('input[name1="status_disabled"]').prop("checked", true);
		$addOrEditModal.find('input[name1="status_normal"]').removeAttr("checked");
	}
	$addOrEditModal.modal("show");
}

/**
 * 删除单个任务
 */
function rowDeleteJob(obj){
	var tr = $(obj).closest("tr.each-row");
	var dataId = tr.attr("data-id");
	
	layer.confirm('您要删除这1条任务记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/jm/job/" + dataId,
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
 * 删除多个任务
 */
function deletesJob(){
	var checkboxs = $("tr.each-row").find('input[type="checkbox"]:checked');
	
	if(!checkboxs || checkboxs.length == 0){
		layer.msg("请选择您要删除的任务");
		return;
	}
	
	layer.confirm('您要删除这'+ checkboxs.length +'条任务记录吗？', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var pmids = "";
		for(var i = 0; i < checkboxs.length; i++){
			pmids += $(checkboxs[i]).closest("tr.each-row").attr("data-id") +",";
		}
		
		pmids = deleteLastStr(pmids);
		
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "delete",
			url : "/jm/jobs?pmids=" + pmids,
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
			return;
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
			url : "/jm/job",
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
 * 获取请求列表
 */
function getQueryPagingParams(){
	return {page_size: pageSize, current: currentIndex, total: totalPage, t: Math.random()};
}

/**
 * 构建每一行的html
 * @param job
 * @param index
 * @returns {String}
 */
function buildRow(job, index){
	var html = '<tr class="each-row" data-id="'+ job.id+'" index="'+ index +'">'+
					'<td><input type="checkbox" /></td>'+
					'<td>'+ changeNotNullString(job.job_name) +'</td>'+
					'<td>'+ job.job_group+'</td>'+
					'<td>'+ job.expression+'</td>'+
					'<td>'+ job.class_name+'</td>'+
					'<td>'+ changeNotNullString(job.job_order) +'</td>'+
					'<td>'+ changeNotNullString(job.job_desc) +'</td>'+
					'<td>'+ (job.status == 1? '正常': '禁用')+'</td>'+
					'<td>'+ job.create_time+'</td>'+
					'<td><a href="javascript:void(0);" onclick="rowEditJob(this);" style="margin-right: 10px;">编辑</a><a href="javascript:void(0);" onclick="rowDeleteJob(this);" style="margin-right: 10px;">删除</a></td>'+
				'</tr>';
	return html;
}

/**
 * 生成分页div
 * @param total
 */
function pageDivUtil(total){
	var html = '<li>'+
					'<a href="javascript:void(0);" onclick="pre();" aria-label="Previous">'+
						'<span aria-hidden="true">&laquo;</span>'+
					'</a>'+
				'</li>';
	totalPage = parseInt(Math.ceil(total / pageSize));
	var start = 0;
	var end = totalPage > start + 10 ? start + 10: totalPage;
	
	var selectHtml = '<li><select class="form-control" onchange="optionChange()">';
	for(var i = 0; i < totalPage; i++){
		if(currentIndex == i)
			selectHtml += '<option name="pageIndex" selected="selected" value="'+ i +'">'+ (i + 1) +'</option>';
		else
			selectHtml += '<option name="pageIndex" value="'+ i +'">'+ (i + 1) +'</option>';
	}
	selectHtml += '</select></li>';
	for(var i = start; i < end; i++){
		if(currentIndex == i)
			html += '<li class="active"><a href="javascript:void(0);" onclick="goIndex('+ i +');">'+ (i+1) +'</a></li>';
		else
			html += '<li><a href="javascript:void(0);" onclick="goIndex('+ i +');">'+ (i+1) +'</a></li>';
	}
	html += '<li>'+
				'<a href="javascript:void(0);" onclick="next();" aria-label="Next">'+
					'<span aria-hidden="true">&raquo;</span>'+
				'</a>'+
			'</li>';
	
	selectHtml += '<li><a href="javascript:void(0);">共计：' +total +'条记录</a></li>';
	html += selectHtml;
	$(".pagination").html(html);
}

/**
 * 选择改变的监听
 */
function optionChange(){
	var objS = document.getElementsByTagName("select")[0];
    var index = objS.options[objS.selectedIndex].value;
    currentIndex = index;
    getJobs();
}

/**
 * 点击向左的按钮
 */
function goIndex(index){
	currentIndex = index;
	getJobs();
}

/**
 * 点击向左的按钮
 */
function pre(){
	currentIndex --;
	if(currentIndex < 0)
		currentIndex = 0;
	getJobs();
}


/**
 * 点击向右的按钮
 */
function next(){
	currentIndex ++;
	if(currentIndex > totalPage)
		currentIndex = totalPage;
	getJobs();
}