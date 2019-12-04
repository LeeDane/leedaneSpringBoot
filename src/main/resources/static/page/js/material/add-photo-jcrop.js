layui.use(['layer'], function(){
	layer = layui.layer;
	if(isNotEmpty(tabName)){
		$("#material-tabs").find("li").each(function(index){
			if($(this).attr("data-value") == tabName){
				type = tabName;
				$(this).addClass("active");
				showTab();
				return true;
			}else{
				$(this).removeClass("active");
			}
		});
	}else{
		 $("#material-tabs").find("li").eq(0).addClass("active");
		 type = $("#material-tabs").find("li").eq(0).attr("data-value")
		 showTab();
	}
	
	//获取通知类型
	$("#material-tabs").find("li").on("click", function(index){
		$("#material-tabs").find("li").removeClass("active");
		$(this).addClass("active");
		type = $(this).attr("data-value");
		showTab();
	});  
});
var type;

var jcrop_api;
function showTab(){
	if(jcrop_api)
		jcrop_api.destroy();
	
	if(type == "普通"){
	    $('#target').Jcrop({
	      onChange:   showCoords,
	      onSelect:   showCoords,
	      onRelease:  clearCoords
//	      onDblClick:
	    },function(){
	      jcrop_api = this;
	      jcrop_api.setOptions({ allowResize: true});
	    });
	}else if(type == "头像"){
		 $('#target').Jcrop({
			  onChange:   showCoords,
		      onSelect:   showCoords,
		      onRelease:  clearCoords
	      },function(){

	        jcrop_api = this;
	        jcrop_api.animateTo([10,10,110,110]);
	        jcrop_api.setOptions({allowResize: false});
	        //jcrop_api.disable();
	        // Setup and dipslay the interface for "enabled"
	        //$('#can_click,#can_move,#can_size').attr('checked','checked');
	        //$('#ar_lock,#size_lock,#bg_swap').attr('checked',false);
	        //$('.requiresjcrop').show();

	      });
	}
}

// Simple event handler, called from onChange and onSelect
// event handlers, as per the Jcrop invocation above
function showCoords(c){
  $('#x1').val(c.x);
  $('#y1').val(c.y);
  $('#x2').val(c.x2);
  $('#y2').val(c.y2);
  $('#w').val(c.w);
  $('#h').val(c.h);
};

function clearCoords(){
  $('#coords input').val('');
};
