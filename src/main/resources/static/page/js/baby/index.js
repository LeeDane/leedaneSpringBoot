layui.use(['layer', 'laydate'], function(){
	layer = layui.layer;
	laydate = layui.laydate;
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
  		        data:['吃喝','睡觉','穿戴']
  		    },
  		    grid: {
  		    	left: '5%',
		  		top: '40',
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
	  		        	color: '#4cae4c',
	  		        },
  		            data:[320, 332, 301, 334, 390, 330, 320, 320, 332, 301, 334, 390, 330, 0, 320, 3, 31, 4, 390, 330, 320, 320, 320, 332]
  		        },
  		        {
  		            name:'睡觉',
  		            type:'bar',
  		            stack: '吃喝',
  		            itemStyle:{
    		        	color: '#337ab7',
    		        },
  		            data:[0, 132, 101, 134, 90, 230, 0, 120, 132, 101, 134, 90, 0, 210, 120, 132, 101, 134, 90, 230, 210, 320, 320, 332]
  		        },
  		        {
  		            name:'穿戴',
  		            type:'bar',
  		            stack: '吃喝',
  		            itemStyle:{
    		        	color: '#f0ad4e',
    		        },
  		            data:[220, 182, 191, 234, 290, 330, 310, 220, 182, 0, 234, 290, 330, 310, 220, 182, 191, 234, 290, 330, 310, 320, 320, 332]
  		        }
  		    ]
  		};
  	 myChart.setOption(option);
  	 
  	//获取屏幕的宽度
 	//浏览器可视区域页面的宽度
 	var winW = $(window).width(); 
 	if(winW < 900){
 		$aa = $(".laydate-theme-molv .layui-laydate-main");
 		$aa.attr("max-width", (winW - 20) +"px!important")
 	}
	});