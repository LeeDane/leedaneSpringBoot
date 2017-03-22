package com.cn.leedane.utils;

/**
 * 分页计算开始和结束页数的工具类(复制原来的blog的代码)
 * @author LeeDane
 * 2016年7月12日 上午10:30:55
 * Version 1.0
 */
public class PagingUtilOld {
	private int pageSize = 0;  //每一页的总条数   默认是10
	private int skipPage = 0;   //记录要跳转的页数
	private int totalNum = 0;   //记录总的数目
	
	private int mo = 0 ;
	
	private int totalPage = 0;  //记录总的页数
	
	private int start = 0 ;   //记录要查询分页的起始位置
	private int each = 0 ;   //记录要查询分页的结束位置
	
	//12,2,4
	public PagingUtilOld(int totalNum , int skipPage ,int pageSize){    //构造函数，接收传来的总数目和当前页数
		this.totalNum = totalNum ;  //如：25
		this.skipPage = skipPage ;   //如：2
		this.pageSize = pageSize; 
	}
	
	public int returnStart(){
		
		if(totalNum % pageSize == 0){ //刚好被整除
			totalPage =totalNum / pageSize ;   //总页数
			mo = totalNum % pageSize;
			
			if(skipPage == totalPage){   //最后一页
				start = 0;
			}else if(skipPage == 1){   //第一页
				start =totalNum - pageSize;
			}else{
				start = (totalPage-skipPage+1) * pageSize-(pageSize - mo);	
			}
		}else{
			totalPage =totalNum / pageSize +1;   //总页数
			mo = totalNum % pageSize;
			
			if(skipPage == totalPage){   //最后一页
				start = 0;
			}else if(skipPage == 1){   //第一页
				start =totalNum - pageSize;
			}else{
				start = (totalPage-skipPage) * pageSize-(pageSize - mo);	
			}
		}
			
		return start;
	}
	
	public int returnEach(){
			
		if(totalNum % pageSize == 0){ //刚好被整除
			totalPage =totalNum / pageSize ;   //总页数
			
			each = pageSize;   //需要的页的数量等于每一页的数量
		}else{
			totalPage =totalNum / pageSize +1;   //总页数
			mo = totalNum % pageSize;
			
			if(totalPage == skipPage){
				each = mo ;
			}else{
				each = pageSize; 
			}
		}
	
		return each;
	}
	
}
