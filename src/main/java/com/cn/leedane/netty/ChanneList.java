package com.cn.leedane.netty;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

public class ChanneList {

	public static List<Channel> channels = new ArrayList<Channel>();
	
	public void add(Channel cn){
		channels.add(cn);
	}
}
