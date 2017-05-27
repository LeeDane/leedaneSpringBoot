var type;
var fileIndex = 1;
var $image;
$(function(){
	
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

/**
 * 选择本地图片
 */
function addImage(){
	$("#form-id-"+ fileIndex).remove();
	$("body").append('<form id="form-id-'+ fileIndex +'" style="display: none;"><input id="add-file-'+ fileIndex +'" type="file" name="file" onchange="haveImageInut(this);" accept="image/gif,image/png,image/jpg,image/bmp,image/psd,image/jpeg"/></form>');
	$('#add-file-'+ fileIndex +'').click();
}
/**
 * 对文件选择后的监听
 * @param obj
 */
function haveImageInut(obj){	
	this.url = window.URL.createObjectURL(document.getElementById("add-file-"+ fileIndex).files[0]);
	$image.attr("src", this.url);
	$image.cropper('replace', this.url);
}

/**
 * 显示tab
 */
function showTab(){
	if(type == "普通"){
		$image.cropper("destroy");
		var options = {
		          // strict: false,
		          // responsive: false,
		          // checkImageOrigin: false

		          // modal: false,
		          // guides: false,
		          // highlight: false,
		          // background: false,

		          // autoCrop: false,
		          // autoCropArea: 0.5,
		          // dragCrop: false,
		          // movable: false,
		          // resizable: false,
		          // rotatable: false,
		          // zoomable: false,
		          // touchDragZoom: false,
		          // mouseWheelZoom: false,

		          // minCanvasWidth: 320,
		          // minCanvasHeight: 180,
		          // minCropBoxWidth: 160,
		          // minCropBoxHeight: 90,
		          // minContainerWidth: 320,
		          // minContainerHeight: 180,

		          // build: null,
		          // built: null,
		          // dragstart: null,
		          // dragmove: null,
		          // dragend: null,
		          // zoomin: null,
		          // zoomout: null,

		          aspectRatio: 3 / 4,
		          preview: '.img-preview',
		          crop: function (data) {
		            $dataX.val(Math.round(data.x));
		            $dataY.val(Math.round(data.y));
		            $dataHeight.val(Math.round(data.height));
		            $dataWidth.val(Math.round(data.width));
		          }
		        };
		$image.cropper(options);
	}else if(type == "头像"){
		$image.cropper("destroy");
		var options = {
		          // strict: false,
		          // responsive: false,
		          // checkImageOrigin: false

		          // modal: false,
		          guides: false,
		          // highlight: false,
		          // background: false,

		          //autoCrop: false,
		          autoCropArea: 0.5,
		          dragCrop: false,
		          movable: false,
		          resizable: false,
		          cropBoxMovable: true,
	              cropBoxResizable: false,
	              toggleDragModeOnDblclick: false,
	              rotatable: false,
	              center: false,
		          rotatable: false,
		          zoomable: false,
		          touchDragZoom: false,
		          mouseWheelZoom: false,

		          // minCanvasWidth: 320,
		          // minCanvasHeight: 180,
		          minCropBoxWidth: 90,
		          minCropBoxHeight: 90,
		          // minContainerWidth: 320,
		          // minContainerHeight: 180,

		          // build: null,
		          // built: null,
		          // dragstart: null,
		          // dragmove: null,
		          // dragend: null,
		          // zoomin: null,
		          // zoomout: null,
		          aspectRatio: 1 / 1,
		          preview: '.img-preview',
		          crop: function (data) {
		            $dataX.val(Math.round(data.x));
		            $dataY.val(Math.round(data.y));
		            $dataHeight.val(Math.round(data.height));
		            $dataWidth.val(Math.round(data.width));
		          }
		        };
		$image.cropper(options);
	}
}

/**
 * 获取当前的数据
 * 原先是把数据放到formData中，再通过其get()方法获取，现在手机上测试发现用不了该方法：参考https://developer.mozilla.org/en-US/docs/Web/API/FormData/get
 */
function getData(){
	var data = {};
	var formData = new FormData(document.getElementById("form-id-"+ fileIndex));//表单id
	formData.append("img-data", JSON.stringify($image.cropper("getData")));
	
	var $input = $("#add-file-"+ fileIndex);
	var filePath = $input.val();
	var fileName=filePath.replace(/^.+?\\([^\\]+?)(\.[^\.\\]*?)?$/gi,"$1");  //正则表达式获取文件名，不带后缀
	var fileExt=filePath.replace(/.+\./,"");   //正则表达式获取后缀
	var fileFullName = fileName + "." + fileExt; //文件的全称，包括后缀
	
	formData.append("file-name", fileFullName);
	formData.append("file-size", $input[0].files[0].size);
	
	//重新填充一份数据
	data.formData = formData;
	data.fileName = fileFullName;
	data.fileSize = $input[0].files[0].size;
	data.imgData = JSON.stringify($image.cropper("getData"));
	return data;
}
