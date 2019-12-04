package com.cn.leedane.netty;


import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class PushServer {
	public void bind() throws Exception{
		
		//这里使用的是 Reactor多线程模型
        EventLoopGroup bossGroup = new NioEventLoopGroup(); //一个线程
        EventLoopGroup workGroup = new NioEventLoopGroup(); //CPU核心数x2
        ServerBootstrap bs = new ServerBootstrap();
        bs.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                    	ChannelPipeline p = channel.pipeline();
                        // HttpServerCodec：将请求和应答消息解码为HTTP消息
                        p.addLast("http-codec",new HttpServerCodec());
                        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
                        p.addLast("aggregator",new HttpObjectAggregator(65536));
                        // ChunkedWriteHandler：向客户端发送HTML5文件
                        p.addLast("http-chunked",new ChunkedWriteHandler());
                        
                        p.addLast(new ObjectEncoder());
                        p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        //心跳超时
                        p.addLast(new ReadTimeoutHandler(100));
                        
                        p.addLast(new HeartBeatHandler());
                        
                       // 配置通道处理  来进行业务处理
                        p.addLast(new ConnectHandler());
                    }
                });
        bs.bind(8897).sync();
        System.out.println("com.cn.leedane.netty.PushServer 8897 start....");
    }

    //消息推送
    public void push(){
        List<Channel> channels = ChanneList.channels;
        System.out.println("push 消息 + " + channels.size());
        Message message = new Message();
        message.setType(MessageType.MSG_PUSH.getValue());
        PushMsg pushMsg = new PushMsg();
        pushMsg.setAuthor_name("中新社");
        pushMsg.setDate("2017-04-12 13:51");
        pushMsg.setThumbnail_pic_s("http:\\/\\/05.imgmini.eastday.com\\/mobile\\/20170412\\/20170412135121_ff0cae3d2601191a77afa948a8424142_1_mwpm_03200403.jpeg");
        pushMsg.setTitle("法国安娜思托保健品进军亚洲市场");
        pushMsg.setUrl("http:\\/\\/mini.eastday.com\\/mobile\\/170412135121788.html");
        message.setMsg(pushMsg);
        for (Channel channel : channels){
            channel.writeAndFlush(message);
        }
    }

   /* public static void main(String[] args) throws Exception{
        PushServer pushServer = new PushServer();
        pushServer.bind();
    }*/
}
