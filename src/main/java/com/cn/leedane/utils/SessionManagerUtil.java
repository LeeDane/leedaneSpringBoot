package com.cn.leedane.utils;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

public class SessionManagerUtil {
	
	private static SessionManagerUtil sManagerUtil = null;

	/**
	 * key表示用户ID， List表示一个用户可以对应多个session
	 */
	private HashMap<String, List<Session>> userSessions;
	private HashMap<Serializable, String> userSessionMap; //session跟用户ID的一对一映射
	
	private SessionManagerUtil(){
		userSessions = new HashMap<>();
		userSessionMap = new HashMap<>();
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
	public List<Session> getSession(int userid){
		return userSessions.get(String.valueOf(userid));
	}

	/**
	 * 添加session
	 * @param session
	 * @param userid
	 * @return
	 */
	public synchronized boolean addSession(Session session, int userid){
		if(session == null || userid < 0)
			return false;
		List<Session> sessions = userSessions.get(userid + "");
		if(CollectionUtil.isEmpty(sessions))
			sessions = new ArrayList<>();
		sessions.add(session);
		userSessions.put(userid + "", sessions);
		userSessionMap.put(session.getId(), userid+"");
		return true;
	}

	/**
	 * 获取所有的在线用户
	 * @return
	 */
	public HashMap<String, List<Session>> getAllActives(){
		return userSessions;
	}

	/**
	 * 获取所有的在线用户数量(根据session数量计算
	 * @return
	 */
	public int getAllActivesNumber(){
		return userSessionMap.size();
	}

	/**
	 * 移除单个session
	 * @param session
	 * @param userid
	 * @return
	 */
	public boolean removeSession(Session session, int userid) {
		if(session == null || userid < 0)
			return false;
		userSessionMap.remove(session.getId());
		List<Session> sessions = userSessions.get(userid +"");
		if(CollectionUtil.isNotEmpty(sessions)){
			int removeIndex = -1;
			for(int i = 0; i < sessions.size(); i++){
				if(sessions.get(i).getId() == session.getId()){
					removeIndex = i;
					break;
				}
			}
			if(removeIndex >= 0 && sessions.size() > removeIndex){
				try{
					sessions.remove(removeIndex);
					if(sessions.size() == 0){
						userSessions.remove(userid + "");
					}else{
						userSessions.put(userid + "", sessions);
					}
					sessions.get(removeIndex).stop();
				}catch (UnknownSessionException e){
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * 移除单个session
	 * @param session
	 * @return
	 */
	public boolean removeSession(Session session) {
		if(session == null)
			return false;
		return removeSession(session, StringUtil.changeObjectToInt(userSessionMap.get(session.getId())));
	}

	/**
	 * 移除单个用户的全部session
	 * @param userId
	 * @return
	 */
	public boolean removeUser(int userId) {
		if(userId < 1)
			return false;

		List<Session> sessions = userSessions.get(userId +"");
		if(CollectionUtil.isNotEmpty(sessions)){
			for(Session session: sessions){
				try {
					userSessionMap.remove(session.getId());
					session.stop();
				}catch (UnknownSessionException e){
					e.printStackTrace();
				}
			}
			userSessions.remove(userId +"");
		}
		return true;
	}
	
	/**
	 * 移除所有session
	 * @return
	 */
	public boolean removeAllSession() {
		if(!userSessions.isEmpty()){
			for(Entry<String, List<Session>> entry: userSessions.entrySet()){
				List<Session> sessions = entry.getValue();
				if(CollectionUtil.isNotEmpty(sessions)){
					for(Session session: sessions){
						try {
							session.stop();
						}catch (UnknownSessionException e){
							e.printStackTrace();
						}
					}
				}
			}
			userSessions.clear();
		}
		return true;
	}
}
