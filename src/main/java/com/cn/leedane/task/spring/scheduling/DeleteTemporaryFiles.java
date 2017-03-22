package com.cn.leedane.task.spring.scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 删除临时文件夹下面文件
 * @author LeeDane
 * 2016年7月12日 下午3:24:20
 * Version 1.0
 */
@Component("deleteTemporaryFiles")
public class DeleteTemporaryFiles extends BaseScheduling{
	Logger logger = Logger.getLogger(getClass());
	
	//保存无法删除成功的文件路径
	Set<String> noDeletePaths = new HashSet<String>();
	
	
	@Override
	public void execute() throws Exception {
		super.execute();

		long start = System.currentTimeMillis();
		String folder = ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER + "temporary";	
		
		File file = new File(folder);
		if(!file.exists()){
			return;
		}
		
		String[] files = file.list();
		if(files.length > 0){
			
			//获取所有的文件名列表
			List<String> filePaths = Arrays.asList(files);
			List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
			SingleDeleteTask deleteTask;
			System.out.println("总文件数量：" +filePaths.size());
			//派发5个线程执行
			ExecutorService threadpool = Executors.newFixedThreadPool(5);
			for(String filePath: filePaths){
				deleteTask = new SingleDeleteTask(ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER +"temporary//"+ filePath);
				futures.add(threadpool.submit(deleteTask));
			}
			threadpool.shutdown();
			
			for(int i = 0; i < futures.size() ;i++){
				try {
					if(!futures.get(i).get()){
						if(!StringUtil.isNull(filePaths.get(i)))
							noDeletePaths.add(filePaths.get(i));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					futures.get(i).cancel(true);
				} catch (ExecutionException e) {
					e.printStackTrace();
					futures.get(i).cancel(true);
				}
			}
		}
		
		StringBuffer logMessage = new StringBuffer();
		if(noDeletePaths.size() > 0){
			for(String path: noDeletePaths){
				logMessage.append(path);
				logMessage.append("\n\r");
			}
		}else{
			logMessage.append("全部的临时文件已经删除成功");
		}
		
		logger.info(logMessage.toString());
		long end = System.currentTimeMillis();
		System.out.println("执行删除临时文件夹下面的文件总计耗时：" + (end - start) +"毫秒");
		logger.info("执行删除临时文件夹下面的文件总计耗时：" + (end - start) +"毫秒");
	}
	
	class SingleDeleteTask implements Callable<Boolean>{
		String filePath = null;
		public SingleDeleteTask(String filePath) {
			this.filePath = filePath;
		}

		@Override
		public Boolean call() throws Exception {
			
			File f = new File(filePath);

			//不存在的文件直接返回true
			if(!f.exists() || !f.isFile())
				return true;
			
			f.delete();
			return true;
		}
	}
}
