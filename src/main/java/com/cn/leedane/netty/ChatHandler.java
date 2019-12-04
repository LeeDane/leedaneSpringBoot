package com.cn.leedane.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 聊天消息的处理
 * @author LeeDane
 * 2017年11月1日 上午11:36:43
 * version 1.0
 */
public class ChatHandler extends SimpleChannelInboundHandler<ChatMessage>{
	 //加入到在线列表，只有在线用户才可以实时推送
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("ChatHandler -->加入在线列表");
        ChanneList.channels.add(ctx.channel());
    	
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage message) throws Exception {
    	System.out.println("ChatHandler ---channelRead0");
        //如果是心跳包ping，则返回pong
        if(message != null && message.getType() == MessageType.HEARTBEAT_REQ.getValue()){
            Message response = buildMessage(MessageType.HEARTBEAT_RESP.getValue());
            ctx.writeAndFlush(response);
        }else{
            ctx.fireChannelRead(message);
        }
    }

    private Message buildMessage(byte result){
        Message msg = new Message();
        msg.setType(result);
        return msg;
    }

}
