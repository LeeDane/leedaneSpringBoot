var pageSize = 8;
var currentIndex = 0;
var circles;
var totalPage = 0;
var $circleEditModal;
$(function(){
	$circleEditModal = $("#edit-circle-modal");
	$("[data-toggle='tooltip']").tooltip();
	
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-circle").addClass("active");
	
	$(".tooltip").css("display", "block");
	
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
			$(".table tbody tr").remove();
			$(".pagination").empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				circles = data.message;
				if(circles.length == 0){
					$(".table tbody").append('<tr>空空的，还没有数据</tr>');
					return;
				}
				for(var i = 0; i < circles.length; i++){
					$(".table tbody").append(buildEachCircleRow(i, circles[i]));
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
			html += '<a href="javascript:void(0);" onclick="rowEditJob(this);" style="margin-right: 10px;">分配</a><a href="javascript:void(0);" onclick="showEditCircleModal(this, '+ index +');" style="margin-right: 10px;">编辑</a><a href="javascript:void(0);" onclick="doDelete(this, '+ index +');" style="margin-right: 10px;">删除</a>';
		}
		html += '</td>'+
			'</tr>';
	return html;
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
	
	selectHtml += '</select></li>';
	
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
    getCircles();
}

/**
 * 点击向左的按钮
 */
function goIndex(index){
	currentIndex = index;
	getCircles();
}

/**
 * 点击向左的按钮
 */
function pre(){
	currentIndex --;
	if(currentIndex < 0)
		currentIndex = 0;
	getCircles();
}


/**
 * 点击向右的按钮
 */
function next(){
	currentIndex ++;
	if(currentIndex > totalPage)
		currentIndex = totalPage;
	getCircles();
}

/**
 * 选择图像后的回调
 * @param links
 */
function afterSelect(links){
	$('input[name="circle_path"]').val(links);
}

