package com.cn.leedane.stock;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行方法
 * 
 * 
 * 1、首先确定开始的千位数点(就是当期就算出来的第一个点，必须小于最新一期，由于要计算四个，不能超过4位)
 * 2、计算所有开始点的组合(就是根据开始点，从其他的中获取一个到4个跟开始点随机组合)
 * 3、确定千位数结束点(期数必须是大小开始点的期数，不然无法计算，必须是第一个位置)，从组合中过滤掉不符合结束点的列表
 * 4、组合向下移动(每次移动1到(最新一期减去当前一期的位置)个位置)，直到移动到即将开奖的一期，同时对这个
 * 		做判断，如果不是刚好是最新开奖的日期将不做处理。把当前左右符合条件的组合保存下来
 * 5、对4结果中每个组合做算法运算(如果只有两个数，则做加法、减法、乘法、除法运算，如果是三个或者三个以上，做加法、乘法、做减法：取
 * 		其中最大的跟其他的做加法比较，做除法：取最大的数跟其他的做乘法运算比较)，找出所有组合中算法结果一样的组合列表
 * 6、对5中的列表做向右一位，根据5的算法计算百位、十位、个位。
 * 7、输出最终结果
 * @author LeeDane
 * 2018年7月3日 下午2:48:16
 * version 1.0
 */
public class Excute {
	
	/**
	 * 当前期数的索引
	 */
	private static int currIndex;

	public static List<QXStock> allStocks = new ArrayList<QXStock>();
	private final static int START_CALCULATE_NPER = 18062;  //开始计算的期数,将从此位置开始计算
	private final static int CURRENT_CALCULATE_NPER = 18082;  //即将开奖的期数
	private final static int COMBIE_DEPP = 4; //计算组合的时候的深度，必须大于1的整数
	private final static int EACH_DOWN_DEPP = 4;//每次将组合向下最大的移动数量
	private final static boolean ONLY_COMPARE_END = true;//是否开始只比较最后的数字，如5+5=10，结尾是0，不管是20、30都将看做是相等,这个只有在是整数情况才有效
	private static List<Point> points = new ArrayList<Point>();
	private static Map<String, Point> pointMap = new HashMap<String, Point>();
	static{
		currIndex = allStocks.size();
		List<Integer> numbers = new ArrayList<Integer>();
		numbers.add(2);numbers.add(6);numbers.add(8);numbers.add(3);numbers.add(4);numbers.add(7);numbers.add(3);
		allStocks.add(new QXStock(18062, numbers));
		numbers = new ArrayList<Integer>();
		
		numbers.add(1);numbers.add(9);numbers.add(5);numbers.add(1);numbers.add(5);numbers.add(8);numbers.add(1);
		allStocks.add(new QXStock(18063, numbers));
		numbers = new ArrayList<Integer>();
		
		numbers.add(9);numbers.add(6);numbers.add(1);numbers.add(1);numbers.add(0);numbers.add(3);numbers.add(4);
		allStocks.add(new QXStock(18064, numbers));
		numbers = new ArrayList<Integer>();
		
		numbers.add(2);numbers.add(6);numbers.add(4);numbers.add(1);numbers.add(3);numbers.add(0);numbers.add(0);
		allStocks.add(new QXStock(18065, numbers));
		numbers = new ArrayList<Integer>();
		
		numbers.add(5);numbers.add(5);numbers.add(0);numbers.add(3);numbers.add(4);numbers.add(5);numbers.add(0);
		allStocks.add(new QXStock(18066, numbers));
		numbers = new ArrayList<Integer>();
		
		numbers.add(7);numbers.add(5);numbers.add(4);numbers.add(7);numbers.add(3);numbers.add(5);numbers.add(6);
		allStocks.add(new QXStock(18067, numbers));
		numbers = new ArrayList<Integer>();
		
		numbers.add(2);numbers.add(7);numbers.add(7);numbers.add(6);numbers.add(4);numbers.add(4);numbers.add(9);
		allStocks.add(new QXStock(18068, numbers));
		numbers = new ArrayList<Integer>();
		numbers.add(1);numbers.add(0);numbers.add(0);numbers.add(4);numbers.add(3);numbers.add(0);numbers.add(8);
		allStocks.add(new QXStock(18069, numbers));
		numbers = new ArrayList<Integer>();
		numbers.add(6);numbers.add(6);numbers.add(5);numbers.add(9);numbers.add(4);numbers.add(2);numbers.add(9);
		allStocks.add(new QXStock(18070, numbers));
		numbers = new ArrayList<Integer>();
		numbers.add(0);numbers.add(5);numbers.add(0);numbers.add(6);numbers.add(3);numbers.add(6);numbers.add(6);
		allStocks.add(new QXStock(18071, numbers));
		numbers = new ArrayList<Integer>();
		numbers.add(7);numbers.add(9);numbers.add(6);numbers.add(5);numbers.add(7);numbers.add(5);numbers.add(8);
		allStocks.add(new QXStock(18072, numbers));
		numbers = new ArrayList<Integer>();
		numbers.add(3);numbers.add(9);numbers.add(6);numbers.add(5);numbers.add(8);numbers.add(0);numbers.add(0);
		allStocks.add(new QXStock(18073, numbers));
		numbers = new ArrayList<Integer>();
		numbers.add(1);numbers.add(4);numbers.add(8);numbers.add(6);numbers.add(0);numbers.add(9);numbers.add(7);
		allStocks.add(new QXStock(18074, numbers));
		
		numbers = new ArrayList<Integer>();
		numbers.add(1);numbers.add(6);numbers.add(6);numbers.add(2);numbers.add(7);numbers.add(3);numbers.add(8);
		allStocks.add(new QXStock(18075, numbers));
		
		numbers = new ArrayList<Integer>();
		numbers.add(3);numbers.add(1);numbers.add(1);numbers.add(2);numbers.add(8);numbers.add(2);numbers.add(9);
		allStocks.add(new QXStock(18076, numbers));
		
		numbers = new ArrayList<Integer>();
		numbers.add(6);numbers.add(1);numbers.add(5);numbers.add(3);numbers.add(0);numbers.add(4);numbers.add(7);
		allStocks.add(new QXStock(18077, numbers));	
		
		numbers = new ArrayList<Integer>();
		numbers.add(3);numbers.add(4);numbers.add(8);numbers.add(8);numbers.add(0);numbers.add(1);numbers.add(4);
		allStocks.add(new QXStock(18078, numbers));
		
		numbers = new ArrayList<Integer>();
		numbers.add(1);numbers.add(1);numbers.add(7);numbers.add(5);numbers.add(9);numbers.add(5);numbers.add(1);
		allStocks.add(new QXStock(18079, numbers));
		
		numbers = new ArrayList<Integer>();
		numbers.add(8);numbers.add(3);numbers.add(4);numbers.add(4);numbers.add(0);numbers.add(4);numbers.add(9);
		allStocks.add(new QXStock(18080, numbers));
		
		numbers = new ArrayList<Integer>();
		numbers.add(5);numbers.add(4);numbers.add(1);numbers.add(8);numbers.add(8);numbers.add(6);numbers.add(4);
		allStocks.add(new QXStock(18081, numbers));
	}
	
	/**
	 * 初始化
	 */
	public static void init(){
		//计算从这个位置开始网上4期的数据
		int start = START_CALCULATE_NPER - 4;
		//获取左右的点
		for(QXStock stock: allStocks){
			if(stock.getNper() > start){
				for(int i = 0; i < stock.getNumbers().size(); i++){
					Point p = new Point(stock.getNper(), i, stock.getNumbers().get(i));
					points.add(p);
					pointMap.put(stock.getNper()+""+i, p);
				}
			}
		}
	}
	
	/**
	 * 获取所有可以做开始点的数据列表
	 * 首先确定开始的千位数点(就是当期就算出来的第一个点，必须小于最新一期，由于要计算四个，不能超过4位)
	 * @param no
	 * @return
	 */
	public static List<Point> getStartPoints(){
		List<Point> points = new ArrayList<Point>();
		for(QXStock stock: allStocks){
			if(stock.getNper() >= START_CALCULATE_NPER){
				for(int i = 0; i < 4; i++){
					points.add(new Point(stock.getNper(), i, stock.getNumbers().get(i)));
				}
			}
		}
		
		return points;
	}
	
	/**
	 * 获取结束点
	 * @param no
	 * @return
	 */
	public int getEndPoint(int no){
		int start = 0;
		int end = 0;
		
		if(end <= start)
			return -1;
		
		return end;
	}
	public static void main(String[] args) {
		//单数是乌拉圭，双数是法国，包括负数
		/*String vv = String.valueOf(Math.random() * 100);
		vv = vv.substring(0, vv.indexOf("."));
		System.out.println(vv);*/
		//List<Integer> iL = new ArrayList<Integer>();
        //new Excute().combine("", iL,  m);
        //System.out.println("total : " + total);
		/*try {
			getX509CerCate("D:\\201root.cer");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		init();
		List<Point> startPoints = getStartPoints();
		int total = 0;
		
		//获取第一位的所有有效的集合列表
		List<Operate> operates = new ArrayList<Operate>();
		for(Point startPoint: startPoints){
			//当前是计算哪个位置的数字
			int index = 0;
			//根据开始点计算出组合的点
			List<Point> startCombinePoints = getAllCombilePoints(startPoint, index);
			List<List<Point>> finalCombilePoints = new ArrayList<List<Point>>();
			for(int i = 1; i <= COMBIE_DEPP ; i++){
				//对上面的点跟组合算法
				List<List<Point>> resultPoints = new ArrayList<List<Point>>();
				combine(resultPoints, new ArrayList<Point>(), new ArrayList<Point>(), startCombinePoints, i);
				
				//过滤不合格的组合
				filterCombile(finalCombilePoints, startPoint, resultPoints, index);
			}
			
			//System.out.println(finalCombilePoints);
			
			//经过向下移动后筛选和过滤后的集合
			List<Combines> mobileCombiles = new ArrayList<Combines>();
			
			//对组合的点每次做向下平移1~EACH_DOWN_DEPP个点，依次直到能找到即将开奖的点刚好是最后的点，获得相应的组合集合，
			for(List<Point> finalCombilePoint: finalCombilePoints){
				for(int mobileNumber = 1 ; mobileNumber <= EACH_DOWN_DEPP; mobileNumber++){
					downCal(mobileCombiles, finalCombilePoint, mobileNumber);
				}
			}
			
			total += mobileCombiles.size();
			//对组合做运算、评分、保留下符合以上条件的组合
			
			List<Operate> operates0 = new ArrayList<Operate>();
			for(Combines combine: mobileCombiles){
				combine.getPoints();
				operateCal(operates0, combine);
				
			}
			operates.addAll(operates0);
		}
		System.out.println("总数是："+ total);
		System.out.println("总数1是："+ operates.size());

		//最终的开奖结果
		List<Result> results = new ArrayList<Result>();
		//对5中的列表做向右一位，根据5的算法计算百位、十位、个位。
		for(Operate operate: operates){
			
			//收集所有成功的点列表
			List<Operate> successOperates = new ArrayList<Operate>();
			//是否每次向右移动都能成功
			boolean success = true;
			//移动到相应的位置
			for(int move = 1; move < 4; move++){
				Operate newOperate = moveRight(operate, move);
				if(newOperate != null){
					successOperates.add(newOperate);
				}else{
					success = false;
					break;//调整当前的循环
				}
			}
			
			if(success){
				Result result = new Result();
				successOperates.add(0, operate);
				//取第一个的算法作为
				result.setComputeResults(successOperates.get(0).getComputeResults());
				result.setOperates(successOperates);
				result.setScore(0);
				result.setScoreDesc("");
				String number = "";
				for(Operate so: successOperates){
					number += so.getPoints().get(so.getPoints().size() -1).get(so.getPoints().get(so.getPoints().size() -1).size() -1).getValue();
				}
				result.setDeep(successOperates.get(0).getPoints().get(0).size());
				result.setStep(successOperates.get(0).getPoints().size());
				result.setNumber(number);
				results.add(result);
			}
		}
		
		System.out.println("符合的开奖总数是："+ results.size());
		for(Result rs: results){
			System.out.println("预测"+ CURRENT_CALCULATE_NPER +"期开奖:步数-->"+ rs.getStep() +", 深度-->"+ rs.getDeep() +", 结果是-->"+ rs.getNumber());
		}
	}
	
	/**
	 * 将每组集合点向右移动相应的位置
	 * @param operate
	 * @param move
	 * @return 返回符合条件的对象
	 */
	private static Operate moveRight(Operate operate, int move){
		
		List<List<Point>> lists = operate.getPoints();
		//经过向下移动后筛选和过滤后的集合
		//List<Combines> mobileCombiles = new ArrayList<Combines>();
		List<List<Point>> newLists = new ArrayList<List<Point>>();
		List<ComputeResult> computeResults = new ArrayList<ComputeResult>();
		boolean canDeal = false;
		int score = 0;
		String scoreDesc = "";
		
		for(ComputeResult result: operate.getComputeResults()){
			boolean equal = true;
			ComputeResult computeResult = new ComputeResult(result.getType(), 0.00f, 0.00f);
			for(List<Point> list: lists){
				List<Point> newList = new ArrayList<Point>();
				for(Point p: list){
					if(p.getNper() != CURRENT_CALCULATE_NPER)
						//newList.add(new Point(p.getNper(), p.getIndex()+move, -1));
						newList.add(pointMap.get(p.getNper() + "" + (p.getIndex()+ move)));
					else
						newList.add(new Point(p.getNper(), p.getIndex()+move, -1));
				}
				newLists.add(newList);
				//有最后一期的组合将不参加运算
				if(newList.get(newList.size() -1).getNper() == CURRENT_CALCULATE_NPER)
					continue;
				ComputeResult tResult = compute(result.getType(), newList);
				//不等的情况下直接退出循环操作
				if(tResult.getResult() == 999.00f || computeResult.getResult() != tResult.getResult()){
					equal = false;
					break;
				}
				computeResult = tResult;
				//对点做运算
				
			}
			
			if(equal/* && combine.getPoints().get(0).size() == 2 && calculateType == 0*/){
				//计算最后预测的数字
				List<Point> lastCombines = newLists.get(newLists.size() - 1);
				int lastCombineSize = lastCombines.size();
				//Point lastPoint = lastCombines.get(lastCombineSize -1);
				
				int value = -1;
				//加法
				if(result.getType() == 0){
					value = getAddX(computeResult, lastCombines);
				}
				
				//减法
				/*if(calculateType == 1){
					value = (int) (computeResult.getSourceResult() + lastCombines.get(0).getValue());
				}*/
				
				//乘法
				/*if(calculateType == 2){
					value = getMultX(computeResult, lastCombines);
				}*/
				
				if(value >= 0){
					lastCombines.get(lastCombineSize - 1).setValue(value);
					canDeal = true;
					computeResults.add(computeResult);
				}
			}
		}
		
		if(canDeal){
			Operate rightOperate = new Operate();
			rightOperate.setComputeResults(computeResults);
			rightOperate.setMobile(operate.getMobile());
			rightOperate.setPoints(newLists);
			rightOperate.setScore(score);
			rightOperate.setScoreDesc(scoreDesc);
			return rightOperate;
		}
		return null;
	}
	
	/**
	 * 对每一列的组合列表执行运算操作
	 * 如果只有两个数，则做加法、减法、乘法、除法运算，如果是三个或者三个以上，做加法、乘法、做减法：取
	 * 		其中最大的跟其他的做加法比较，做除法：取最大的数跟其他的做乘法运算比较
	 * @param operates  保存已经处理好的结果，
	 * @param combine
	 */
	private static void operateCal(List<Operate> operates, Combines combine) {
		
		boolean canDeal = false;
		List<ComputeResult> computeResults = new ArrayList<ComputeResult>();
		int score = 0;
		String scoreDesc = "";
		for(int calculateType = 0; calculateType < 4; calculateType++){
			boolean equal = true;
			ComputeResult computeResult = new ComputeResult(calculateType, 0.00f, 0.00f);
			//float calResult = 0.00f;//保存测试结果
			for(List<Point> points: combine.getPoints()){
				
				//有最后一期的组合将不参加运算
				if(points.get(points.size() -1).getNper() == CURRENT_CALCULATE_NPER)
					continue;
				ComputeResult tResult = compute(calculateType, points);
				//不等的情况下直接退出循环操作
				if(tResult.getResult() == 999.00f || computeResult.getResult() != tResult.getResult()){
					equal = false;
					break;
				}
				computeResult = tResult;
			}
			if(equal/* && combine.getPoints().get(0).size() == 2 && calculateType == 0*/){
				//计算最后预测的数字
				List<Point> lastCombines = combine.getPoints().get(combine.getPoints().size() -1);
				int lastCombineSize = lastCombines.size();
				//Point lastPoint = lastCombines.get(lastCombineSize -1);
				
				int value = -1;
				//加法
				if(calculateType == 0){
					value = getAddX(computeResult, lastCombines);
				}
				
				//减法
				/*if(calculateType == 1){
					value = (int) (computeResult.getSourceResult() + lastCombines.get(0).getValue());
				}*/
				
				//乘法
				/*if(calculateType == 2){
					value = getMultX(computeResult, lastCombines);
				}*/
				
				if(value >= 0){
					lastCombines.get(lastCombineSize - 1).setValue(value);
					canDeal = true;
					computeResults.add(computeResult);
				}
			}
		}
		
		if(canDeal){
			Operate operate = new Operate();
			operate.setComputeResults(computeResults);
			operate.setMobile(combine.getMobile());
			operate.setPoints(combine.getPoints());
			operate.setScore(score);
			operate.setScoreDesc(scoreDesc);
			operates.add(operate);
		}
		
	}
	
	/**
	 * 求加法中的未知数
	 * @param computeResult
	 * @param lastCombines 最后的点
	 * @return
	 */
	private static int getAddX(ComputeResult computeResult, List<Point> lastCombines){
		int val = -1;
		if(ONLY_COMPARE_END){
			val = (int) computeResult.getResult();
			int total = 0;
			for(int i = 0; i < lastCombines.size() -1; i++){
				total += lastCombines.get(i).getValue();
			}
			
			while(val < total){
				val = val + 10;
			}
			
			val = val - total;
		}else{
			val = (int) computeResult.getSourceResult();
			for(int i = 0; i < lastCombines.size() -1; i++)
				val = val - lastCombines.get(i).getValue();
		}
		return val >= 0 && val < 10 ? val: -1; //返回的是0~9的数字
	}
	
	/**
	 * 求减法中的未知数
	 * @param computeResult -1   9 8 x
	 * @param lastCombines
	 * @return
	 */
	private static int getSubtractionX(ComputeResult computeResult, List<Point> lastCombines){
		//难点：如何知道最后的数是最大数还是最小数
		int val = -1;
		
		//先假设最后的数字是最大数
		if(ONLY_COMPARE_END){
			val = (int) computeResult.getResult();
			int total = 0;
			for(int i = 0; i < lastCombines.size() -1; i++){
				total += lastCombines.get(i).getValue();
			}
			
			while(val < total){
				val = val + 10;
			}
			
			val = val - total;
		}else{
			val = (int) computeResult.getSourceResult();
			for(int i = 0; i < lastCombines.size() -1; i++)
				val = val - lastCombines.get(i).getValue();
		}
		return val >= 0 && val < 10 ? val: -1; //返回的是0~9的数字
	}
	
	/**
	 * 求乘法中的未知数
	 * @param computeResult
	 * @param lastCombines 最后的点 0 1 0 1 
	 * @return
	 */
	private static int getMultX(ComputeResult computeResult, List<Point> lastCombines){
		int val = -1;
		if(ONLY_COMPARE_END){
			val = (int) computeResult.getResult();
			int total = 0;
			for(int i = 0; i < lastCombines.size() -1; i++){
				total *= lastCombines.get(i).getValue();
			}
			
			while(val < total){
				val = val + 10;
			}
			
			val = val - total;
		}else{
			val = (int) computeResult.getSourceResult();
			for(int i = 0; i < lastCombines.size() -1; i++)
				val = val - lastCombines.get(i).getValue();
		}
		return val >= 0 && val < 10 ? val: -1; //返回的是0~9的数字
	}
	
	/**
	 * 开始计算
	 *如果只有两个数，则做加法、减法、乘法、除法运算，如果是三个或者三个以上，做加法、乘法、做减法：取
	 * 		其中最大的跟其他的做加法比较，做除法：取最大的数跟其他的做乘法运算比较
	 * 
	 * 0：加法， 1减法： 2乘法 3、除法
	 * @param calculateType
	 * @param points 
	 * @return  返回最终运算后的结果，如果无法运算等将返回-999.04 , -998.05表示多个的情况下的结果统一
	 */
	private static ComputeResult compute(int calculateType, List<Point> oldPoints){
		
		List<Point> newPoints = new ArrayList<Point>();
		newPoints.addAll(oldPoints);
		int size = newPoints.size();

		//从大到小排好序的数据
		Collections.sort(newPoints, new Comparator<Point>(){

			@Override
			public int compare(Point p0, Point p1) {
				int value0 = (p0 == null ? 0: p0.getValue());
				int value1 = (p1 == null ? 0: p1.getValue());
				//if(p1 == null)
					//System.out.println(111);
				return value1 - value0;
			}
			
		});
		
		ComputeResult computeResult = new ComputeResult(calculateType, -999.04f, 0.00f);
		boolean canDeal = false;
		if(size == 2){
			if(calculateType == 0){
				computeResult.setResult(newPoints.get(0).getValue() + newPoints.get(1).getValue());
				computeResult.setSourceResult(newPoints.get(0).getValue() + newPoints.get(1).getValue());
				canDeal = true;
			}
			
			/*if(calculateType == 1){
				computeResult.setResult(newPoints.get(1).getValue() - newPoints.get(0).getValue());
				computeResult.setSourceResult(newPoints.get(1).getValue() - newPoints.get(0).getValue());
				canDeal = true;		
			}*/
			
			if(calculateType == 2){
				//有零开始就不乘了，没有意义
				if(newPoints.get(1).getValue() != 0.0f){
					computeResult.setResult(newPoints.get(1).getValue() * newPoints.get(0).getValue());
					computeResult.setSourceResult(newPoints.get(1).getValue() * newPoints.get(0).getValue());
					canDeal = true;
				}
			}
			
			/*if(calculateType == 3){
				if(newPoints.get(0).getValue() != 0){
					computeResult.setResult(newPoints.get(1).getValue() / newPoints.get(0).getValue());
					computeResult.setSourceResult(newPoints.get(1).getValue() / newPoints.get(0).getValue());
					canDeal = true;
				}
			}*/
		}else{
			if(calculateType == 0){
				float count = 0.0f;
				for(int data = 0; data < size; data++){
					count +=  newPoints.get(data).getValue();
				}
				computeResult.setResult(count);
				computeResult.setSourceResult(count);
				canDeal = true;
			}
			
			/*if(calculateType == 1){
				float count = 0.0f;
				for(int data = 1; data < size; data++){
					count +=  newPoints.get(data).getValue();//小的数相加
				}
				canDeal = count == newPoints.get(0).getValue();
				if(canDeal){
					computeResult.setResult(-998.05f);
					computeResult.setSourceResult(-998.05f);
				}		
			}*/
			
			if(calculateType == 2){
				//最后一个不能是0
				if(newPoints.get(size - 1).getValue() != 0){
					float count = 0.0f;
					for(int data = 0; data < size; data++){
						count *=  newPoints.get(data).getValue();
					}
					computeResult.setResult(count);
					computeResult.setSourceResult(count);
					canDeal = true;
				}
			}
			
			/*if(calculateType == 3){
				float count = 0.0f;
				for(int data = 1; data < size; data++){
					count *=  newPoints.get(data).getValue();//小的数相乘
				}
				canDeal = count == newPoints.get(0).getValue();
				if(canDeal){
					computeResult.setResult(-998.05f);
					computeResult.setSourceResult(-998.05f);
				}
					
			}*/
		}
		
		if(canDeal){
			//只需要比较最后一位数即可
			if(ONLY_COMPARE_END){
				String vl = String.valueOf(computeResult.getResult());
				int index = vl.indexOf(".");
				String v2 = vl.substring(index + 1, vl.length());
				if(Integer.parseInt(v2) == 0){
					//取倒数的最后一位整数返回
					computeResult.setResult(Float.parseFloat(vl.substring(index-1, index)));
				}
			}
		}
		return computeResult;
	}

	/**
	 * 组合向下移动(每次移动1到EACH_DOWN_DEPP个位置)，直到移动到即将开奖的一期，同时对这个
	 * 做判断，如果不是刚好是最新开奖的日期将不做处理。把当前左右符合条件的组合保存下来
	 * @param mobileCombiles
	 * @param combilePoint
	 * @param mobileNumber 移动的步数
	 */
	private static void downCal(List<Combines> mobileCombiles, List<Point> mobilePoints, int mobileNumber){
		
		List<List<Point>> combilePoints = new ArrayList<List<Point>>();
		combilePoints.add(mobilePoints);
		//获取当前点的最后的期数
		int lastNper = mobilePoints.get(mobilePoints.size() - 1).getNper();
		
		boolean canDeal = false;
		while((lastNper = lastNper + mobileNumber) <= CURRENT_CALCULATE_NPER){
			List<Point> mobilePoints1 = new ArrayList<Point>();
			for(Point p: mobilePoints){
				Point newPoint = pointMap.get((p.getNper() + mobileNumber) + "" + p.getIndex());
				if(newPoint == null){
					if(lastNper == CURRENT_CALCULATE_NPER){
						canDeal = true;
						newPoint = new Point(lastNper, p.getIndex(), -1);
						mobilePoints1.add(newPoint);
					}
				}else{
					mobilePoints1.add(newPoint);
				}
				
			}
			mobilePoints = mobilePoints1;
			combilePoints.add(mobilePoints1);
		}
		
		//上面的处理已经把无法计算刚好是最新开奖期数的数据了,必须有两个以上
		if(canDeal && combilePoints.size() > 1){
			Combines combines = new Combines();
			combines.setMobile(mobileNumber);
			combines.setPoints(combilePoints);
			mobileCombiles.add(combines);
		}
	}
	
	/**
	 * 过滤掉不符合的组合列表
	 * 确定千位数结束点(期数必须是大小开始点的期数，不然无法计算，位置必须跟当前的点位一致)，从组合中过滤掉不符合结束点的列表
	 * @param startPoint
	 * @param resultPoints
	 * @param index
	 * @return
	 */
	private static void filterCombile(List<List<Point>> finalCombilePoints, Point startPoint,
			List<List<Point>> resultPoints, int index) {
		//List<List<Point>> rePoints = new ArrayList<List<Point>>();
		for(List<Point> combilePoint: resultPoints){
			//最后一期的期数不是比开始期数大的并且当前的位置跟组合计算开始的位置一致，全部删除掉
			Point lastPoint = combilePoint.get(combilePoint.size() - 1);
			if(lastPoint.getNper() > startPoint.getNper()
					&& lastPoint.getIndex() == index){
				List<Point> cps = new ArrayList<Point>();
				cps.add(startPoint);
				cps.addAll(combilePoint);
				finalCombilePoints.add(cps);
			}
		}
		//return rePoints;
	}

	/**
	 * 获取计算组合的点
	 * @param startPoint
	 * @param index (0表示的是千位、1表示百位、2表示十位、3表示个位)
	 * @return
	 */
	private static List<Point> getAllCombilePoints(Point startPoint, int index) {
		List<Point> points = new ArrayList<Point>();
		int start = startPoint.getNper() - 4;
		int end =  startPoint.getNper() + 4;
		//获取左右的点
		for(QXStock stock: allStocks){
			if(stock.getNper() > start && stock.getNper() < end){
				int inS = 0 + index;
				int inE = 4 + index;
				for(int i = inS; i < inE; i++){
					
					//除掉当前的点
					if(stock.getNper() == startPoint.getNper() && i == index)
						continue;
					points.add(new Point(stock.getNper(), i, stock.getNumbers().get(i)));
				}
			}
		}
		return points;
	}

	/**
	 * 计算所有开始点的组合(就是根据开始点，从其他的中获取一个到4个跟开始点随机组合)
	 * @param points 最终结果的点
	 * @param oldPoints
	 * @param allPoints  计算的集合列表的点
	 * @param m 组合数
	 */
    private static void combine(List<List<Point>> resultPoints, List<Point> points, List<Point> oldPoints, List<Point> allPoints, int m) {
        if(m == 0) {
            //System.out.println(points);
            resultPoints.add(points);
            points = new ArrayList<Point>();
            return;
        }
        List<Point> oldPoints2;
        for(int i = 0; i < allPoints.size(); i++) {
        	oldPoints2 = new ArrayList<Point>();
        	oldPoints2.addAll(oldPoints);
            if(!oldPoints.contains(allPoints.get(i))) {
                //String str = s + is[i];
            	List<Point> points1 = new ArrayList<Point>();
            	points1.addAll(points);
            	points1.add(allPoints.get(i));
                oldPoints2.add(allPoints.get(i));
                combine(resultPoints, points1, oldPoints2, allPoints, m-1);
            }
        }
    }
	
	/*private static char[] is = new char[] { '1', '2', '4', '5', '6', '7', '8', '9', '5', '9', '0', '2'};
    private static int total;
    private static int m = 8;
    *//**
     * 获取组合数
     * @param s
     * @param iL
     * @param m
     *//*
    private void combine(String s, List<Integer> iL, int m) {
        if(m == 0) {
            System.out.println(s);
            total++;
            return;
        }
        List<Integer> iL2;
        for(int i = 0; i < is.length; i++) {
            iL2 = new ArrayList<Integer>();
            iL2.addAll(iL);
            if(!iL.contains(i)) {
                String str = s + is[i];
                iL2.add(i);
                combine(str, iL2, m-1);
            }
        }
    }*/
    
    /**
	 * @author God
	 * @cerPath Java读取Cer证书信息
	 * @throws Exception 
	 * @return X509Cer对象
	 */
	public static X509Certificate getX509CerCate(String cerPath) throws Exception {
		X509Certificate x509Certificate = null;
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		FileInputStream fileInputStream = new FileInputStream(cerPath);
		x509Certificate = (X509Certificate) certificateFactory.generateCertificate(fileInputStream);
		fileInputStream.close();
		System.out.println("读取Cer证书信息...");
		System.out.println("x509Certificate_SerialNumber_序列号___:"+x509Certificate.getSerialNumber());
		System.out.println("x509Certificate_getIssuerDN_发布方标识名___:"+x509Certificate.getIssuerDN()); 
		System.out.println("x509Certificate_getSubjectDN_主体标识___:"+x509Certificate.getSubjectDN());
		System.out.println("x509Certificate_getSigAlgOID_证书算法OID字符串___:"+x509Certificate.getSigAlgOID());
		System.out.println("x509Certificate_getNotBefore_证书有效期___:"+x509Certificate.getNotAfter());
		System.out.println("x509Certificate_getSigAlgName_签名算法___:"+x509Certificate.getSigAlgName());
		System.out.println("x509Certificate_getVersion_版本号___:"+x509Certificate.getVersion());
		System.out.println("x509Certificate_getPublicKey_公钥___:"+x509Certificate.getPublicKey());
		return x509Certificate;
	}

}
