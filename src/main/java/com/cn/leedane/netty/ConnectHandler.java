package com.cn.leedane.netty;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConnectHandler extends  SimpleChannelInboundHandler<Message>{
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
