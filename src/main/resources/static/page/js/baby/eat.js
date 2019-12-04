layui.use(['layer', 'laydate'], function(){
	layer = layui.layer;
	laydate = layui.laydate;
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-baby").addClass("active");
	laydate.render({
		    elem: '#test-n2'
		    ,position: 'static'
		    ,lang: 'cn'
		    ,showBottom: false
		    ,theme: '#337ab7'
		    ,calendar: true
		    ,mark: {
		        '0-11-29': '生日'
		        ,'0-5-31': '跨年' //每年12月31日
		        ,'0-0-10': '工资' //每个月10号
		        ,'2017-8-15': '' //具体日期
		        ,'2017-8-20': '预发' //如果为空字符，则默认显示数字+徽章
		        ,'2017-8-21': '发布'
		      }
			,done: function(value, date, endDate){
			    console.log(value); //得到日期生成的值，如：2017-08-18
			    console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
			    console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
			  }
		  }, function(string){
			  alert(ddd);
		  });
	$('#leftFeed').bind('input propertychange', function() {  
		$('#personal-total').html((Number($('#leftFeed').val()) + Number($('#rightFeed').val())).toFixed(2));  
	}); 
	
	$('#rightFeed').bind('input propertychange', function() {  
	    $('#personal-total').html((Number($('#leftFeed').val()) + Number($('#rightFeed').val())).toFixed(2));  
	}); 
});

/**
 * 选择喂养类型
 * @param obj
 */
function selectOnchang(obj){
	if(obj.selectedIndex == 0){
		$("#personal-feed").addClass("show");
		$("#personal-feed").removeClass("hide");
		$("#bottle-bowl-feed").addClass("hide");
		$("#bottle-bowl-feed").removeClass("show");
	}else{
		$("#personal-feed").addClass("hide");
		$("#personal-feed").removeClass("show");
		$("#bottle-bowl-feed").addClass("show");
		$("#bottle-bowl-feed").removeClass("hide");
	}
}

/**
 * 提交表单
 */
function submitEat(){
	
	//检查空判断
	var formControl = $('#myform').find(".form-control");
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
		//var data=$('#myform').serializeArray();
		//data = formArrayToObject(data);
		//params["id"] = dataId;
		params['occur_time'] = params.occur_date + " " +params.occur_time +":00";
		params.life_type = 1;
		var loadi = layer.load('努力加载中…');
		$.ajax({
			type : "post",
			data: params,
			url : "/baby/"+ babyId +"/life",
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
					
					layer.close(loadi);
					if(data.isSuccess){
						layer.msg("添加成功");
						window.location.reload();
					}else
						layer.msg(data.message);
					
			},
			error : function(dd) {
				layer.close(loadi);
				layer.msg("网络请求失败");
			}
		});
	}
	
	
}

/**
 * 将form的serializeArray();处理后的数据转换成json对象的方法
 * @param data
 */
function formArrayToObject(data){
	var jsonObject = {};
	if(!data)
		return jsonObject;
	
	for(var i = 0; i < data.length; i++){
		var object = data[i];
		var key = object['name'];
		var value = object['value'];
		jsonObject[key] = value;
	}
	console.log(jsonObject);
	return jsonObject;
}