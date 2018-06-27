layui.use(['layer', 'laydate', 'util'], function(){
	layer = layui.layer;
	laydate = layui.laydate;
	util = layui.util;
	
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
		    ,mark: mark
			,done: function(value, date, endDate){
			    console.log(value); //得到日期生成的值，如：2017-08-18
			    console.log(date); //得到日期时间对象：{year: 2017, month: 8, date: 18, hours: 0, minutes: 0, seconds: 0}
			    console.log(endDate); //得结束的日期时间对象，开启范围选择（range: true）才会返回。对象成员同上。
			    getLifes(value, value, 0, null);
			  }
		  }, function(string){
			  alert(ddd);
		  });
	
  	 
  	//获取屏幕的宽度
 	//浏览器可视区域页面的宽度
 	var winW = $(window).width(); 
 	if(winW < 900){
 		$aa = $(".laydate-theme-molv .layui-laydate-main");
 		$aa.attr("max-width", (winW - 20) +"px!important")
 	}
 	
 	$lifesContrainer = $("#lifes-contrainer");
 	if(babyId && babyId > 0){
 		getLifes((new Date()).Format("yyyy-MM-dd"), (new Date()).Format("yyyy-MM-dd"), 0, null);
 	}
});

var $lifesContrainer;
/**
 * 展示柱状图
 * @param life
 */
function showBar(result){
	// 基于准备好的dom，初始化echarts实例
  	myChart = echarts.init(document.getElementById('echarts'));
  	var option = {
  		    tooltip : {
  		        trigger: 'axis',
  		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
  		            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
  		        }
  		    },
  		    legend: {
  		        data:['吃喝','睡觉','洗澡']
  		    },
  		    grid: {
  		    	left: '5%',
		  		top: '60',
		  		right: '2%',
		  		bottom: '20'
  		    },
  		    calculable : true,
  		    xAxis : [
  		        {
  		            type : 'category',
  		            data : ['1','2','3','4','5','6','7','8','9','10','11','12','13','14', '15','16','17','18','19','20','21' ,'22','23','24']
  		        }
  		    ],
  		    yAxis : [
  		        {
  		            type : 'value'
  		        }
  		    ],
  		    series : [
  		        {
  		            name:'吃喝',
  		            type:'bar',
  		            stack: '吃喝',
	  		        itemStyle:{
	  		        	color: '#337ab7',
	  		        },
  		            data:[320, 332, 301, 334, 390, 330, 320, 320, 332, 301, 334, 390, 330, 0, 320, 3, 31, 4, 390, 330, 320, 320, 320, 332]
  		        },
  		        {
  		            name:'睡觉',
  		            type:'bar',
  		            stack: '吃喝',
  		            itemStyle:{
    		        	color: '#5cb85c',
    		        },
  		            data:[0, 132, 101, 134, 90, 230, 0, 120, 132, 101, 134, 90, 0, 210, 120, 132, 101, 134, 90, 230, 210, 320, 320, 332]
  		        },
  		        {
  		            name:'洗澡',
  		            type:'bar',
  		            stack: '吃喝',
  		            itemStyle:{
    		        	color: '#f0ad4e',
    		        },
  		            data:[220, 182, 191, 234, 290, 330, 310, 220, 182, 0, 234, 290, 330, 310, 220, 182, 191, 234, 290, 330, 310, 320, 320, 332]
  		        }
  		    ]
  		};
  	
 
   /* var data = new Array();
    var dataAxis = new Array();
    var max = 0;
	for (var key in column3DJson) {
		data.push(column3DJson[key]);
		dataAxis.push(key);
		max = Math.max(max,column3DJson[key]);
	}*/
	
    //var dataAxis = ['点', '击', '柱', '子', '或', '者', '两', '指', '在', '触', '屏', '上', '滑', '动', '能', '够', '自', '动', '缩', '放'];
    //var data = [220, 182, 191, 234, 290, 330, 310, 123, 442, 321, 90, 149, 210, 122, 133, 334, 198, 123, 125, 220];
    //var data = [220, 182, 191, 234, 290, 330, 310, 220, 182, 0, 234, 290, 330, 310, 220, 182, 191, 234, 290, 330, 310, 320, 320, 332];
    //var dataAxis = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23']
   // var yMax = 400;
   /* var dataShadow = [];

    for (var i = 0; i < data.length; i++) {
        dataShadow.push(yMax);
    }*/

    option = {
    		legend: {
  		        data:['吃喝','睡觉','洗刷','臭臭']
  		    },
        xAxis: {
            data: result.xAxis,
            axisLabel: {
                inside: false,
                textStyle: {
                    color: '#000'
                }
            },
            axisTick: {
                show: false
            },
            axisLine: {
                show: false
            },
            z: 10
        },
        yAxis: {
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                textStyle: {
                    color: '#999'
                }
            },
            max: function(value) {
                return Math.ceil(value.max * 1.5);
            }
        },
        grid: {
		    	left: '5%',
	  		top: '60',
	  		right: '2%',
	  		bottom: '20'
		},
        dataZoom: [
            {
                type: 'inside'
            }
        ],
        tooltip : {//鼠标悬浮弹窗提示  
        	show: true,
            orient: 'vertical',
            left: 'right',
            top: 'center',
            feature: {
                mark: {show: true},
                dataView: {show: true, readOnly: false},
                magicType: {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                restore: {show: true},
                saveAsImage: {show: true}
            }
         },  
        series:result.series
    };

    // Enable data zoom when user click bar.
    var zoomSize = 6;
    myChart.on('click', function (params) {
        console.log(result.xAxis[Math.max(params.dataIndex - zoomSize / 2, 0)]);
        myChart.dispatchAction({
            type: 'dataZoom',
            startValue: result.xAxis[Math.max(params.dataIndex - zoomSize / 2, 0)],
            endValue: result.xAxis[Math.min(params.dataIndex + zoomSize / 2, result.series[0].data.length - 1)]
        });
    });
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
}

/**
 * 获取宝宝生活方式列表
 * @param start
 * @param end
 * @param type
 * @param keyword
 */
function getLifes(start, end, type, keyword){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	var params = '';
	if(isNotEmpty(start)){
		params += '&start=' + start;
	}
	
	if(isNotEmpty(end)){
		params += '&end=' + end;
	}
	
	if(isNotEmpty(type)){
		params += '&type=' + type;
	}
	
	if(isNotEmpty(keyword)){
		params += '&keyword=' + keyword;
	}
	
	$.ajax({
		url : "/baby/"+ babyId +"/lifes?"+ params +"&t="+Math.random(),
		dataType: 'json', 
		beforeSend:function(){
			//$lifesContrainer.empty();
			$("#eat-number").empty();
			$("#sleep-number").empty();
			$("#wash-number").empty();
			$("#sick-number").empty();
			$("#show-occur-date").empty();
			$lifesContrainer.empty();
			$("#echarts").hide();
		},
		success : function(data) {
			layer.close(loadi);
			if(data != null && data.isSuccess){
				$("#eat-number").text(data.message.eatNumber);
				$("#sleep-number").text(data.message.sleepNumber);
				$("#wash-number").text(data.message.washNumber);
				$("#sick-number").text(data.message.sickNumber);
				$("#show-occur-date").text(data.message.occurDate);
				
				if(data.message.displays.length == 0){
					$lifesContrainer.append("您的宝宝还没有添加生活方式记录！");
					return;
				}
				
				for(var i = 0; i < data.message.displays.length; i++){
					$lifesContrainer.append(buildEachRow(data.message.displays[i]));
				}
				$("#echarts").show();
				showBar(data.message);
			}else{
				ajaxError(data);
			}
			console.log(data);
		},
		error : function(data) {
			layer.close(loadi);
			ajaxError(data);
		}
	});
}

function buildEachRow(life){
	var colorStyle;
	var boxColor;
	var titleText;
	var titleType;
	if(life.type == 1){
		boxColor = 'eat-box';
		colorStyle = 'panel-info';
		titleText = '吃';
		titleType = "eat";
	}else if(life.type == 2){
		boxColor = 'sleep-box';
		colorStyle = 'panel-success';
		titleText = '睡';
		titleType = "sleep";
	}else if(life.type == 3){
		boxColor = 'swap-box';
		colorStyle = 'panel-warning';
		titleText = '洗';
		titleType = "swap";
	}else if(life.type == 4){
		boxColor = 'sick-box';
		colorStyle = 'panel-danger';
		titleText = '臭';
		titleType = "sick";
	}
	var html = '<div class="panel '+ colorStyle +'">'+
					'<div class="panel-heading">'+
						'<div class="'+ boxColor +'"></div>'+
						'<h3 class="panel-title">'+
						titleText +
						'</h3>'+
					'</div>'+
					'<div class="panel-body">'+
						'<div style="word-break:break-all; word-wrap:break-all;">'+
							'<table class="table" >'+
							  '<tbody>';
					for(var i = 0; i < life.displays.length; i++){
						html +=  '<tr>'+
								      '<td width="120" style="border: 0px solid transparent !important; padding: 4px!important;">'+life.displays[i].key+'</td>'+
								      '<td style="border: 0px solid transparent !important; padding: 4px!important;">'+life.displays[i].value+'</td>'+
								 '</tr>';
					}
							   
					 html += '</tbody>'+
							'</table>'+
						'</div>'+
					'</div>'+
					'<div class="panel-footer">'+
						'<button type="button" class="btn btn-default btn-xs" onclick="deleteLife('+ life.id +');">'+
						  '<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> 删除'+
						'</button>'+
						
						'<button type="button" class="btn btn-default btn-xs" style="margin-left: 10px;" onclick="linkToEdit(babyId, \''+ titleType +'\');">'+
						  '<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> 编辑'+
						'</button>'+
					'</div>'+
				'</div>';
	return html;
}

function linkToEdit(id, type){
	window.open("/baby/"+ babyId +"/"+ type +"/manage");
}

/**
 * 高级搜索
 */
function advancedSearch(obj){
	var $start = $("#advanced-start-time");
	var $end = $("#advanced-end-time");
	
	var start = $start.val();
	var end = $end.val();
	
	/*if(isNotEmpty(start) && isEmpty(end)){
		end = start;
	}
	
	if(isNotEmpty(end) && isEmpty(start)){
		start = end;
	}*/
	//判断时间差
	if(isNotEmpty(start) && isNotEmpty(end)){
		var y1 = start.substring(0, 4);
		var m1 = start.substring(5, 7);
		var y2 = end.substring(0, 4);
		var m2 = end.substring(5, 7);
		if(y1 != y2 || m1 != m2){
			layer.msg("目前只支持获取单个月份的数据！");
			return;
		}
	}
	var $keyword = $("#advanced-keyword");
	var $lifeType = $("#advanced-life-type");
	getLifes(start, end, parseInt($lifeType.val()), $keyword.val());
}

/**
 * 删除生活方式
 * @param lifeId
 */
function deleteLife(lifeId){
	
	layer.confirm('您确定要删除该生活方式记录吗？此是不可逆行为，请慎重!', {
		  btn: ['确定','点错了'] //按钮
	}, function(){
		var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
		$.ajax({
			type: "DELETE",
			url : "/baby/"+ babyId +"/life/"+ lifeId,
			dataType: 'json', 
			beforeSend:function(){
			},
			success : function(data) {
				layer.close(loadi);
				if(data != null && data.isSuccess){
					layer.msg(data.message);
					window.location.reload();
				}else{
					ajaxError(data);
				}
				console.log(data);
			},
			error : function(data) {
				layer.close(loadi);
				ajaxError(data);
			}
		});
	}, function(){
	});
}