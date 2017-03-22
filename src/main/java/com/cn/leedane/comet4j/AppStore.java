package com.cn.leedane.comet4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 应用全局存储
 * @author LeeDane
 * 2016年11月28日 下午5:26:28
 * Version 1.0
 */
public class AppStore {

	private static Map<String, Object> mapScanLogin;
	private static Set<String> mapChatRoom;
	
	private static AppStore instance;

	private AppStore(){
		mapScanLogin = new HashMap<String, Object>();
		mapChatRoom = new HashSet<String>();
	}
	public synchronized static AppStore getInstance() {
		if(instance == null){
			synchronized (AppStore.class) {
				if(instance == null){
					instance = new AppStore();
				}
			}
		}
		return instance;
	}

	public void putScanLogin(String key, Object value) {
		mapScanLogin.put(key, value);
	}
	
	public Object getScanLogin(String key) {
		return mapScanLogin.get(key);
	}
	
	public Map<String, Object> getScanLogin() {
		return mapScanLogin;
	} 
	
	
	public void removeScanLoginKey(String key){
		try {
			mapScanLogin.remove(key);
		} catch (Exception e) {
		}
	}

	public void destroyScanLogin() {
		mapScanLogin.clear();
		mapScanLogin = null;
	}
	
	public void addChat(String key){
		mapChatRoom.add(key);
	}
	
	public void removeChat(String key){
		mapChatRoom.remove(key);
	}
	
	public void destroyAllChat(){
		mapChatRoom.clear();
		mapChatRoom = null;
	}
}
