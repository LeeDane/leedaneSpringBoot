$(function(){
	var link = document.createElement('link');
    link.type = 'text/css';
    link.rel = 'stylesheet';
    link.href = '/page/other/bootstrap-3.3.0/css/bootstrap.min.css';
    document.getElementsByTagName("head")[0].appendChild(link);
    
    var link1 = document.createElement('link');
    link1.type = 'text/css';
    link1.rel = 'stylesheet';
    link1.href = '/page/other/layui/css/layui.css';
    document.getElementsByTagName("head")[0].appendChild(link1);
    
	$.getScript("/page/other/bootstrap-3.3.0/js/bootstrap.min.js");
	$.getScript("/page/other/layui/layui.js");
	$.getScript("/page/other/layui/lay/dest/layui.all.js");
	$.getScript("/page/js/base.js");
});