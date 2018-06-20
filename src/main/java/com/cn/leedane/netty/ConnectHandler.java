package com.cn.leedane.netty;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

public class ConnectHandler extends  SimpleChannelInboundHandler<Message>{
	private static final Logger logger = Logger
			.getLogger(ConnectHandler.class.getName());
	
	private WebSocketServerHandshaker handshaker;
	
	 //增加黑名单功能
    private String[] blackIps = {"192.168.199.201"};

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("ConnectHandler -->加入在线列表");
        ChanneList.channels.add(ctx.channel());
    	
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	System.out.println("connet-->channelRead");
    	if(msg instanceof DefaultFullHttpRequest){
    		handleHttpRequest(ctx, ((FullHttpRequest) msg));
    	}else if(msg instanceof Message){
    		Message message = (Message) msg;
            //如果是连接信息，判断是否是黑名单ip
            if(message != null && message.getType() == MessageType.CONNECT_REQ.getValue()){
                Message response = null;
                boolean ok = true;
                for (String ip : blackIps) {
                    InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                    if(address.getHostName().equals(ip)){
                        ok = false;
                    }
                }
                response = ok ? buildMessage((byte)MessageType.CONNECT_SUCCESS.getValue()):
                        buildMessage((byte) MessageType.CONNECT_FAIL.getValue());
                
                //response = new Message();
                //PushServer server = new PushServer();
                //server.push();
                ctx.writeAndFlush(response);
            }else{
                ctx.fireChannelRead(message);
            }
    	}// 判断是否关闭链路的指令
    	else if (msg instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), ((CloseWebSocketFrame) msg)
					.retain());
		}// 判断是否ping消息
    	else if (msg instanceof PingWebSocketFrame) {
			ctx.channel().write(
					new PongWebSocketFrame(((PingWebSocketFrame)msg).content().retain()));
			return;
		}
		// 本例程仅支持文本消息，不支持二进制消息
    	else if (msg instanceof TextWebSocketFrame) {
    		// 返回应答消息
    		String request = ((TextWebSocketFrame) msg).text();
    		System.out.println("服务端收到：" + request);
    		if (logger.isLoggable(Level.FINE)) {
    			logger
    					.fine(String.format("%s received %s", ctx.channel(),
    							request));
    		}
    		/*TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
    				+ "hdhhdd：" + request);
    		*/
    		Message message = new Message();
            message.setType(MessageType.MSG_PUSH.getValue());
            PushMsg pushMsg = new PushMsg();
            pushMsg.setAuthor_name("中新社");
            pushMsg.setDate("2017-04-12 13:51");
            pushMsg.setThumbnail_pic_s("http:\\/\\/05.imgmini.eastday.com\\/mobile\\/20170412\\/20170412135121_ff0cae3d2601191a77afa948a8424142_1_mwpm_03200403.jpeg");
            pushMsg.setTitle("法国安娜思托保健品进军亚洲市场");
            pushMsg.setUrl("http:\\/\\/mini.eastday.com\\/mobile\\/170412135121788.html");
            message.setMsg(pushMsg);
            TextWebSocketFrame tws = new TextWebSocketFrame(JSONObject.fromObject(message).toString());
    		// 群发
    		for(Channel channel: ChanneList.channels)
    			channel.writeAndFlush(tws);
		}

        
    }
    
    private void handleHttpRequest(ChannelHandlerContext ctx,
			FullHttpRequest req) {
		if (!req.getDecoderResult().isSuccess()
				|| (!"websocket".equals(req.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				"ws://localhost:8807/websocket", null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory
					.sendUnsupportedWebSocketVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
		}
	}
    
    private static void sendHttpResponse(ChannelHandlerContext ctx,
			FullHttpRequest req, DefaultFullHttpResponse res) {
		// 返回应答给客户端
		if (res.getStatus().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),
					CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		// 如果是非Keep-Alive，关闭连接
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!isKeepAlive(req) || res.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
    
    private static boolean isKeepAlive(FullHttpRequest req) {
		return false;
	}


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
    	System.out.println("connet-->channelRead0");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    private Message buildMessage(byte result){
        Message msg = new Message();
        msg.setType(result);
        return msg;
    }
}
