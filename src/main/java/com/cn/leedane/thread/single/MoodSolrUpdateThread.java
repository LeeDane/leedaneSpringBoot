package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.MoodSolrHandler;
import com.cn.leedane.model.MoodBean;

/**
 * 心情solr修改索引异步任务
 * @author LeeDane
 * 2017年6月7日 上午9:39:00
 * version 1.0
 */
public class MoodSolrUpdateThread implements Runnable{
	
	private MoodBean moodBean;
	public MoodSolrUpdateThread(MoodBean moodBean) {
		this.moodBean = moodBean;
	}

	@Override
	public void run() {
		MoodSolrHandler.getInstance().updateBean(moodBean);
	}

}
