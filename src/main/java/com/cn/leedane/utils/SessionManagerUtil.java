package com.cn.leedane.utils;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

public class SessionManagerUtil {
	
	private static SessionManagerUtil sManagerUtil = null;
	
	private HashMap<String, HttpSession> sessions;
	
	private SessionManagerUtil(){
		sessions = new HashMap<String, HttpSession>();
	}
	
	public static synchronized SessionManagerUtil getInstance(){
		if(sManagerUtil == null){
			synchronized (SessionManagerUtil.class) {
				if(sManagerUtil == null){
					sManagerUtil = new SessionManagerUtil();
				}
			}
		}
		return sManagerUtil;
	}
	
	/**
	 * 获取session
	 * @param userid
	 * @return
	 */
	public HttpSession getSession(int userid){
		return sessions.get(String.valueOf(userid));
	}

	/**
	 * 添加session
	 * @param session
	 * @param userid
	 * @return
	 */
	public synchronized boolean addSession(HttpSession session, int userid){
		sessions.put(String.valueOf(userid), session);
		return true;
	}
	
	/**
	 * 移除单个session
	 * @param userid
	 * @return
	 */
	public boolean removeSession(int userid) {
		HttpSession session = sessions.get(String.valueOf(userid));
		if(session != null){
			session.invalidate();
			sessions.remove(String.valueOf(userid));
		}
		return true;
	}
	
	/**
	 * 移除所有session
	 * @param userid
	 * @return
	 */
	public boolean removeAllSession() {
		if(!sessions.isEmpty()){
			for(Entry<String, HttpSession> entry: sessions.entrySet())
				entry.getValue().invalidate();		
			sessions.clear();
		}
		return true;
	}
}
