/**
**	分页的工具类
**/
/**
 * 分页的容器
 */
var $pageContainer; 
/**
 * 分页执行的函数名称
 */
var pageFunc;

/**
 * 当前页面的索引
 */
var currentIndex = 0;

/**
 * 每一页最多的数
 */
var pageSize = 8;

/**
 * 总的页数
 */
var totalPage = 0;

/**
 * 初始化分页工具
 * @param container
 * @param func
 */
function initPage(container, func){
	//默认是8条
	initPage(container, func, 8);
}

/**
 * 初始化分页工具
 * @param container
 * @param func
 * @param pageSize
 */
function initPage(container, func, pageSize){
	$pageContainer = $(container);
	pageFunc = func;
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
	//$(".pagination").html(html);
	$pageContainer.html(html);
}

/**
 * 选择改变的监听
 */
function optionChange(){
	var objS = document.getElementsByTagName("select")[0];
    var index = objS.options[objS.selectedIndex].value;
    currentIndex = index;
    doCallback(eval(pageFunc), []);
}

/**
 * 点击向左的按钮
 */
function goIndex(index){
	currentIndex = index;
	doCallback(eval(pageFunc), []);
}

/**
 * 点击向左的按钮
 */
function pre(){
	currentIndex --;
	if(currentIndex < 0)
		currentIndex = 0;
	doCallback(eval(pageFunc), []);
}


/**
 * 点击向右的按钮
 */
function next(){
	currentIndex ++;
	if(currentIndex > totalPage)
		currentIndex = totalPage;
	doCallback(eval(pageFunc), []);
}