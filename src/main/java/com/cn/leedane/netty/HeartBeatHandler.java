package com.cn.leedane.netty;

import java.util.Date;

import com.cn.leedane.utils.DateUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatHandler extends SimpleChannelInboundHandler<Message>{
	 //加入到在线列表，只有在线用户才可以实时推送
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("HeartBeatHandler -->加入在线列表");
        ChanneList.channels.add(ctx.channel());
    	
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
    	System.out.println("channelRead0");
        //如果是心跳包ping，则返回pong
        if(message != null && message.getType() == MessageType.HEARTBEAT_REQ.getValue()){
            Message response = buildMessage(MessageType.HEARTBEAT_RESP.getValue());
            ctx.writeAndFlush(response);
        }else{
            ctx.fireChannelRead(message);
        }
    }
    
    @Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 移除
    	ChanneList.channels.remove(ctx.channel());
		System.out.println(DateUtil.DateToString(new Date())+ "客户端与服务端连接关闭");
	}

    @Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
    
    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("【handlerRemoved】====>"+ctx.channel());
    	ChanneList.channels.remove(ctx);
    }

    /**
     * 这里是保持服务器与客户端长连接  进行心跳检测 避免连接断开
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            PingWebSocketFrame ping = new PingWebSocketFrame();
            switch (stateEvent.state()){
                //读空闲（服务器端）
                case READER_IDLE:
                	System.out.println("【"+ctx.channel().remoteAddress()+"】读空闲（服务器端）");
                    ctx.writeAndFlush(ping);
                    break;
                    //写空闲（客户端）
                case WRITER_IDLE:
                	System.out.println("【"+ctx.channel().remoteAddress()+"】写空闲（客户端）");
                    ctx.writeAndFlush(ping);
                    break;
                case ALL_IDLE:
                	System.out.println("【"+ctx.channel().remoteAddress()+"】读写空闲");
                    break;
            }
        }
    }

    private Message buildMessage(byte result){
        Message msg = new Message();
        msg.setType(result);
        return msg;
    }
}
