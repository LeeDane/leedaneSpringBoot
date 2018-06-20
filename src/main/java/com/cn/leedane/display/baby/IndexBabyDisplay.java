package com.cn.leedane.display.baby;


/**
 * 宝宝首页所有宝宝展示的处理
 * @author LeeDane
 * 2018年6月5日 下午5:46:16
 * version 1.0
 */
public class IndexBabyDisplay{
	
	private int id;
	
	private String nickname;
	
	private String pic;
	
	private boolean current;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}
	
	
	
}
