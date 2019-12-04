//浏览器可视区域页面的高度
var winH = $(window).height(); 
layui.use(['layer', 'laypage'], function(){
	layer = layui.layer;
	laypage = layui.laypage;
	
	//示范一个公告层
    layer.open({
      type: 1
      ,title: false //不显示标题栏
      ,closeBtn: true
      ,area: '300px;'
      ,shade: 0.8
      ,id: 'LAY_layuipro' //设定一个id，防止重复弹出
      ,btn: ['火速围观', '残忍拒绝']
      ,btnAlign: 'c'
      ,moveType: 1 //拖拽模式，0或者1
      ,content: '<div id="jdjjdd" style="padding: 50px; line-height: 22px; background-color: #393D49; color: #fff; font-weight: 300;">你知道吗？亲！<br>layer ≠ layui<br><br>layer只是作为Layui的一个弹层模块，由于其用户基数较大，所以常常会有人以为layui是layerui<br><br>layer虽然已被 Layui 收编为内置的弹层模块，但仍然会作为一个独立组件全力维护、升级。<br><br>我们此后的征途是星辰大海 ^_^</div>'
      ,yes: function(layero){
    	
        /*var btn = layero.find('.layui-layer-btn');
        btn.find('.layui-layer-btn0').attr({
          href: 'http://www.layui.com/'
          ,target: '_blank'
        });*/
        alert($("#jdjjdd").text());
      },
      end: function(){
    	  window.opener=null;
    	  window.open('','_self');
    	  window.top.close();
      }
    });
});

$(function(){
	$(".navbar-nav .nav-main-li").each(function(){
		$(this).removeClass("active");
	});
	$(".nav-mall").addClass("active");
});

