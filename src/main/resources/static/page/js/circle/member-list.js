layui.use(['layer', 'form', 'laypage'], function(){
	  layer = layui.layer;
	  form = layui.form;
	  laypage = layui.laypage;
	  form.on('switch(has_question)', function(data){
	  var oo = $(data.elem);
	  var dd = oo.closest(".layui-input-block");
		  if(data.elem.checked){
			  $(data.elem).closest(".layui-input-block").append('<input lay-verify="required" type="text" name="question_title" placeholder="请输入用户加入时提示的标题" class="layui-input" style="margin-top: 5px;"/>');
             $(data.elem).closest(".layui-input-block").append('<input lay-verify="required" type="text" name="question_answer" placeholder="请输入用户加入时标题的密码" class="layui-input" style="margin-top: 5px;"/>');
		  }else{
			  $(data.elem).closest(".layui-input-block").find('input[name="question_title"]').remove();
             $(data.elem).closest(".layui-input-block").find('input[name="question_answer"]').remove();
		  }
		  console.log(data.elem); //得到checkbox原始DOM对象
		  console.log(data.elem.checked); //是否被选中，true或者false
		  console.log(data.value); //复选框value值，也可以通过data.elem.value得到
		  console.log(data.this); //得到美化后的DOM对象
		});   
	  
	  form.on('submit(*)', function(data){
		  console.log(data.elem); //被执行事件的元素DOM对象，一般为button对象
		  console.log(data.form); //被执行提交的form对象，一般在存在form标签时才会返回
		  var field = data.field; //当前容器的全部表单字段，名值对形式：{name: value}
		  for(var f in field){
			  if(field[f] == "on")
				  field[f] = true;
		  }
		  
		  //由于checkbox没有选中就不处理，这里
		  if(!inJson(field, "check_post"))
			  field.check_post = false;
		  
		  if(!inJson(field, "add_member"))
			  field.add_member = false;
		  
		  if(!inJson(field, "has_question"))
			  field.has_question = false;
		  
		//由于checkbox没有选中就不处理，这里
		  if(!inJson(field, "status"))
			  field.status = false;
		  field.status = field.status? 5: 1;
		  saveSetting(field);
		  return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
		});
	  //各种基于事件的操作，下面会有进一步介绍
	  
	/*initPage(".pagination", "getCircleMembers");*/
	$circleEditModal = $("#edit-circle-modal");
	$("[data-toggle='tooltip']").tooltip();
	
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-circle").addClass("active");
	
	$(".tooltip").css("display", "block");
	
	$tableContainer = $(".table tbody");
	//推荐成员的点击事件
	$(document).on("click", ".member-recommend", function(event){
		event.stopPropagation();//阻止冒泡
		recommendMember($(this));
	});
	
	//移除成员的点击事件
	$(document).on("click", ".member-delete", function(event){
		event.stopPropagation();//阻止冒泡
		deleteMember($(this));
	});
	
	//帖子审核的点击事件
	$(document).on("click", ".post-check", function(event){
		event.stopPropagation();//阻止冒泡
		window.open("/cc/"+ circleId + "/post/check", "_self");
	});
	//保存设置的点击事件
	/*$(document).on("click", ".save-setting", function(event){
		event.stopPropagation();//阻止冒泡
		saveSetting($(this));
	});*/
	
	getCircleMembers();
	
	if(canAdmin)
		//获取等待审核的帖子总数
		getNoCheckTotal();  
});
var circles;
var $circleEditModal;
var $tableContainer;

/**
 * 获取等待审核的帖子总数
 */
function getNoCheckTotal(){
	$.ajax({
		url : "/cc/"+ circleId +"/post/nochecktotal?t="+ Math.random(),
		dataType: 'json',
		beforeSend:function(){
		},
		success : function(data) {
			if(data.success){
				if(data.message > 0)
					$("button.post-check").text("审核帖子("+ data.message +")");
			}else{
				ajaxError(data);
			}
		},
		error : function(data) {
			ajaxError(data);
		}
	});
}

/**
 * 获取我的圈子成员列表
 * @param bid
 */
function getCircleMembers(){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var params = {page_size: pageSize, current: currentIndex, total: totalPage, t: Math.random()};
	$.ajax({
		url : "/cc/"+ circleId +"/members?"+ jsonToGetRequestParams(params),
		dataType: 'json',
		beforeSend:function(){
			$tableContainer.find("tr").remove();
			$(".pagination").empty();
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				circles = data.message;
				if(circles.length == 0){
					if(currentIndex == 0){
						$tableContainer.append('<tr><td colspan="7">该圈子还没有任何的成员！</td></tr>');
					}else{
						$tableContainer.append('<tr><td colspan="7">已经没有更多的成员啦，请重新选择！</td></tr>');
						/*pageDivUtil(data.total);*/
					}
					return;
				}
				for(var i = 0; i < circles.length; i++){
					$tableContainer.append(buildEachCircleMemberRow(i, circles[i]));
					$("#row-index-"+i).data("member", circles[i]);
				}
				/*pageDivUtil(data.total);*/
				
				//执行一个laypage实例
				 laypage.render({
				    elem: 'item-pager' //注意，这里的 test1 是 ID，不用加 # 号
				    ,layout: ['prev', 'page', 'next', 'count', 'skip']
				    ,count: data.total //数据总数，从服务端得到
				    ,limit: pageSize
				    ,theme: '#337ab7'
				    , curr: currentIndex + 1
				    ,jump: function(obj, first){
					    //obj包含了当前分页的所有参数，比如：
					    console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
					    console.log(obj.limit); //得到每页显示的条数
					    if(!first){
					    	currentIndex = obj.curr -1;
					    	getCircleMembers();
					    }
					  }
				 });
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
 * 是否推荐
 * @param obj
 */
function recommendMember(obj){
	var member = $(obj).closest("tr").data("member");
	//询问框
	layer.confirm('您确定要'+ (member.member_recommend? '取消推荐': '推荐') +'该成员吗，请谨慎！', {
	  btn: ['确定','放弃'] //按钮
	}, function(){
		doRecommend(member.member_id, member.member_recommend);
	}, function(){
	  
	});
}

/**
 * 执行移除用户
 * @param obj
 */
function deleteMember(obj){
	var member = $(obj).closest("tr").data("member");
	layer.confirm('您要将《'+ member.account +'》移除出圈子吗？删除掉将无法恢复，请慎重！', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		layer.prompt({title: '请输入您要移除该用户的原因(必填)', formType: 0}, function(pass, promptIndex){
		    var index = layer.load(1, {
		    	shade: [0.1,'#fff'] //0.1透明度的白色背景
		    });
		  	var loadi = layer.load('努力加载中…');
			$.ajax({
				type : "DELETE",
				url : '/cc/'+ circleId +'/member/'+ member.member_id +"?reason="+ pass,
				dataType: 'json', 
				beforeSend:function(){
				},
				success : function(data) {
						layer.close(loadi);
						if(data.success){
							layer.msg(data.message+ "，1秒后自动刷新");
							reloadPage(1000);
						}else{
							layer.msg(data.message);
							layer.close(loadi);
						}
				},
				error : function() {
					layer.close(loadi);
					layer.msg("网络请求失败");
				}
			});
		});
	}, function(){
	});
	
}

/**
 * 构建每一行圈子成员html
 * @param index
 * @param member
 * @returns {String}
 */
function buildEachCircleMemberRow(index, member){
	var html = '<tr id="row-index-'+ index +'">'+ 
					'<td width="30"><img alt="" src="'+ (isEmpty(member.user_pic_path) ? '' : member.user_pic_path) +'" width="25" height="25"/></td>'+ 
					'<td width="150" class="cut-text"><a href="javascript:void(0);" onclick="linkToMy('+ member.member_id +');">'+ member.account +'</a></td>'+ 
					'<td>'+ (member.contribute ? member.contribute : 0) +'</td>'+ 
					'<td>'+ (member.role_type == 0 ? '普通' : (member.role_type == 1 ? '圈主': '管理员')) +'</td>'+ 
					'<td>'+ (member.member_recommend? '推荐': '不推荐')+'</td>'+ 
					'<td width="100">'+ member.create_time +'</td>'+ 
					'<td width="140">';
				if(canAdmin){
					
					html += '<a href="javascript:void(0);" class="member-recommend" style="margin-right: 10px;">'+(member.member_recommend? '取消推荐': '推荐')+'</a>';
					if(member.role_type != 1 && member.member_id != loginUserId)
						html += '<a href="javascript:void(0);" class="member-delete" style="margin-right: 10px;">删除</a>';
				}
			html +='</td>'+ 
				'</tr>';
	return html;
}

function doRecommend(memberId, recommend){
	//获取成员对象
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type: 'POST',
		url : "/cc/"+ circleId +"/member/"+ memberId+"/recommend",
		data: {recommend: !recommend},
		dataType: 'json', 
		beforeSend:function(){
			
		},
		success : function(data) {
			layer.close(loadi);
			if(data.success){
				layer.msg("推荐成功，1秒后自动刷新");
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
			return flag;
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
				if(data.success){
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

/**
 * 保存设置
 * @param field
 */
function saveSetting(field){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type: "PUT",
		data: field,
		url : "/cc/" + circleId+"/setting/"+ settingId,
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
