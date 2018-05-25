/**
 * 初始化点击复制事件
 */
function initClipBoard(){
	var clipboard = new Clipboard('.layui-btn');

	  clipboard.on('success', function(e) {
	      //console.info('Action:', e.action);
	      //console.info('Text:', e.text);
	      //console.info('Trigger:', e.trigger);
	      //console.info('Trigger:', e.trigger.previousSibling.id);
	      //$("#"+ e.trigger.previousSibling.id).text(e.text);
	      layer.tips('此链接已复制成功', "#"+ e.trigger.previousSibling.id, {time: 1000});
	      //layer.msg("链接已复制成功");
	      //e.clearSelection();
	  });

	  clipboard.on('error', function(e) {
	      //console.error('Action:', e.action);
	      //console.error('Trigger:', e.trigger);
		  layer.tips('此链接已复制失败', "#"+ e.trigger.previousSibling.id, {time: 1000});
	  });
}


/**
 * 生成共享链接
 * @param taobaoId
 */
function buildShareLink(taobaoId){
	var noPhone = $(window).width() > 600; //通过简单判断当前的设备是否是移动端使用
	var width = noPhone ? "420px": $(window).width()+"px";
	if(!noPhone){
		//询问框
		layer.confirm('系统检测到您当前可能是使用移动设备，此处分享的链接<font color="red">可能无法在移动端</font>直接打开，请确认是否有必要继续操作？', {
		  btn: ['继续获取','取消'] //按钮
		}, function(){
			layer.closeAll('dialog');
			loadShareLink(taobaoId, width);
		}, function(){
		  layer.msg('您已取消，欢迎您换到电脑端再进行获取分享该商品链接操作！');
		});
	}else{
		loadShareLink(taobaoId, width);
	}
}

/**
 * 执行获取分享链接
 * @param taobaoId
 * @param width
 */
function loadShareLink(taobaoId, width){
	var loadi = layer.load('努力加载中…'); //需关闭加载层时，执行layer.close(loadi)即可
	$.ajax({
		type : "get",
		url : "/mall/taobao/build/share/link?taobaoId="+ taobaoId +"&t=" +Math.random(),
		dataType: 'json',
		success : function(data) {
			layer.close(loadi);
			if(data.isSuccess){
				//页面层
				layer.open({
				  type: 1,
				  title: '生成分享链接',
				  skin: 'layui-layer-rim', //加上边框
				  area: [width, '360px'], //宽高
				  content: buildTaobaoShareContent(data.message)
				});
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
 * 构建弹出提示
 * @param message
 * @returns {String}
 */
function buildTaobaoShareContent(message){
	var html = '<div class="layui-tab layui-tab-brief" lay-filter="shareLinkBrief">'+
				   '<ul class="layui-tab-title">'+
				    '<li class="layui-this" id="li-short">短链接</li>'+
				    '<li id="li-long">长链接</li>'+
				    '<li id="li-qr">二维码</li>'+
				    '<li id="li-token">淘口令</li>'+
				  '</ul>'+
				  '<div class="layui-tab-content tab-share-content">'+
				    '<div class="layui-tab-item layui-show" id="tab-content-share">'+
						'<ol>'+
							'<li>该链接可能无法在移动设备上打开</li>'+
							'<li>如您推广的是航旅的当面付、火车票或者理财保险类商品，将无法获得佣金。</li>'+
							'<li>短链接只有300天的有效期，过期失效需要重新获取。</li>'+
							'<li>请勿将此推广链接打开后再发送给用户，否则无法跟踪。</li>'+
							'<li>若订单使用红包或购物券后佣金有可能支付给红包推广者，如您是自推自买，请勿使用红包及购物券。</li>'+
						'<ol>';
				if(isNotEmpty(message.shortLinkUrl)){
					html += '<div>'+
							    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
							    '<input id="shortLinkUrl" type="text" class="tab-share-input tab-share-input-link" readonly="readonly" value="'+ message.shortLinkUrl +'">'+
							    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#shortLinkUrl">复制链接</button>'+
							    '<button class="layui-btn layui-btn-danger" onclick="openLink(\''+ message.shortLinkUrl +'\');">跳转</button>'+
							'</div>';
				}
				
				if(isNotEmpty(message.couponShortLinkUrl)){
					html += '<div style="margin-top: 5px;">'+
							    '<label style="font-weight: 400; line-height: 20px;">领券链接：</label>'+
							    '<input id="couponShortLinkUrl" type="text" class="tab-share-input tab-share-input-link" readonly="readonly" value="'+ message.couponShortLinkUrl +'">'+
							    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#couponShortLinkUrl">复制链接</button>'+
							    '<button class="layui-btn layui-btn-danger" onclick="openLink(\''+ message.couponShortLinkUrl +'\');">跳转</button>'+
							'</div>';
				}
						
			html += '</div>'+
				    '<div class="layui-tab-item">'+
					    '<ol>'+
					    	'<li>该链接可能无法在移动设备上打开</li>'+
							'<li>如您推广的是航旅的当面付、火车票或者理财保险类商品，将无法获得佣金。</li>'+
							'<li>请勿将此推广链接打开后再发送给用户，否则无法跟踪。</li>'+
							'<li>若订单使用红包或购物券后佣金有可能支付给红包推广者，如您是自推自买，请勿使用红包及购物券。</li>'+
						'<ol>';
						
			if(isNotEmpty(message.clickUrl)){
				html += '<div>'+
						    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
						    '<input id="clickUrl" type="text" class="tab-share-input tab-share-input-link" readonly="readonly" value="'+ message.clickUrl +'">'+
						    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#clickUrl">复制链接</button>'+
						    '<button class="layui-btn layui-btn-danger" onclick="openLink(\''+ message.clickUrl +'\');">跳转</button>'+
						'</div>';
			}
			
			if(isNotEmpty(message.couponLink)){
				html += '<div>'+
						    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
						    '<input id="couponLink" type="text" class="tab-share-input tab-share-input-link" readonly="readonly" value="'+ message.couponLink +'">'+
						    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#couponLink">复制链接</button>'+
						    '<button class="layui-btn layui-btn-danger" onclick="openLink(\''+ message.couponLink +'\');">跳转</button>'+
						'</div>';
			}
			html += '</div>'+
				    '<div class="layui-tab-item">'+
						 '<ol>'+
						 	'<li>该链接可能无法在移动设备上打开</li>'+
							'<li>如您推广的是航旅的当面付、火车票或者理财保险类商品，将无法获得佣金。</li>'+
							'<li>二维码是由短链接转换而来，只有300天的有效期，过期失效需要重新获取。</li>'+
							'<li>若订单使用红包或购物券后佣金有可能支付给红包推广者，如您是自推自买，请勿使用红包及购物券。</li>'+
						'<ol>';
			
			if(isNotEmpty(message.qrCodeUrl)){
				html += '<div style="text-align: center;">'+
						    '<img src="'+ message.qrCodeUrl +'" style="width: 100px; height: 100px;"/>'+
						'</div>';
			}
			html += '</div>' +
				    '<div class="layui-tab-item">'+
					    '<ol>'+
					    	'<li>该链接可能无法在移动设备上打开</li>'+
							'<li>如您推广的是航旅的当面付、火车票或者理财保险类商品，将无法获得佣金。</li>'+
							'<li>淘口令只有30天的有效期，过期失效需要重新获取。</li>'+
							'<li>若订单使用红包或购物券后佣金有可能支付给红包推广者，如您是自推自买，请勿使用红包及购物券。</li>'+
						'<ol>';
						
			if(isNotEmpty(message.taoToken)){
				html += '<div>'+
						    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
						    '<input id="taoToken" type="text" class="tab-share-input" readonly="readonly" value="'+ message.taoToken +'">'+
						    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#taoToken">复制链接</button>'+
						'</div>';
			}
			
			if(isNotEmpty(message.couponLinkTaoToken)){
				html += '<div>'+
						    '<label style="font-weight: 400; line-height: 20px;">商品链接：</label>'+
						    '<input id="couponLinkTaoToken" type="text" class="tab-share-input" value="'+ message.couponLinkTaoToken +'">'+
						    '<button class="layui-btn layui-btn-danger" data-clipboard-action="copy" data-clipboard-target="#couponLinkTaoToken">复制链接</button>'+
						'</div>';
			}
			
			html += '</div>'+
				  '</div>'+
				'</div> ';
	
	return html;
}
