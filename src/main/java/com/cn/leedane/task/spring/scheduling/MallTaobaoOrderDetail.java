package com.cn.leedane.task.spring.scheduling;

import com.cn.leedane.mall.model.S_OrderTaobaoDetailBean;
import com.cn.leedane.mapper.mall.S_OrderDetailMapper;
import com.cn.leedane.model.mall.S_OrderDetailBean;
import com.cn.leedane.utils.*;
import com.github.liaochong.myexcel.core.SaxExcelReader;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 淘宝商城订单详情的处理
 * @author LeeDane
 * 2018年3月26日 下午1:50:06
 * version 1.0
 */
@Component("mallTaobaoOrderDetail")
public class MallTaobaoOrderDetail extends AbstractScheduling{
	private static Logger logger = Logger.getLogger(MallTaobaoOrderDetail.class);

	@Autowired
	private S_OrderDetailMapper orderDetailMapper;
	@Override
	public void execute() throws SchedulerException {
 		
		long start = System.currentTimeMillis();
		
		JSONObject params = getParams();
		//遍历 webroot/order/taobao目录下的所有未处理的excel文件
		//文件夹的名称
		String pathName = ConstantsUtil.getDefaultSaveFileFolder() +"order"+ File.separator+ "taobao";
		File file = new File(pathName);
		File[] fileList = file.listFiles();
		if(fileList != null && fileList.length > 0){

			for(File f: fileList){
				try{
//					System.out.println(pathName + File.separator + f.getName());
					//备份的文件不需要处理
					if(f.getName().contains("backup"))
						continue;
					// 方式一：全部读取后处理，SAX模式，避免OOM，建议大量数据使用
					List<S_OrderTaobaoDetailBean> result = SaxExcelReader.of(S_OrderTaobaoDetailBean.class)
							.sheet(0) // 0代表第一个，如果为0，可省略该操作，也可sheet("名称")读取
							.rowFilter(row -> row.getRowNum() > 0) // 如无需过滤，可省略该操作，0代表第一行
//						.beanFilter(S_OrderTaobaoDetailBean::isDance) // bean过滤
							.read(f);// 可接收inputStream

					if(CollectionUtil.isNotEmpty(result)){
						//保存到数据库中
						List<S_OrderDetailBean> detailBeans = toDetailBeans(result);
						if(CollectionUtil.isNotEmpty(detailBeans)){
							/*for(S_OrderDetailBean detailBean: detailBeans){
								try {

								}catch (Exception e){
									if(e instanceof SQLIntegrityConstraintViolationException){
										continue;
									}
								}
							}*/
							//批量入库
							int count = orderDetailMapper.insertByBatchOnDuplicate(detailBeans);
							System.out.println("批量入库后返回的数据："+ count);
							if(count > 0){
								//没有错误异常，删除该文件
								FileUtils.moveFile(f, new File(pathName + File.separator + f.getName() + ".backup-" + DateUtil.DateToString(new Date(), "yyyyMMddHHmmss")));
								//删掉原来的文件
								f.deleteOnExit();
							}
						}
					}else{
						//添加备份文件
						FileUtils.moveFile(f, new File(pathName + File.separator + f.getName() + ".backup-error-" + DateUtil.DateToString(new Date(), "yyyyMMddHHmmss")));
					}
				}catch (Exception e){
					e.printStackTrace();
					//添加备份文件
					try {
						FileUtils.moveFile(f, new File(pathName + File.separator + f.getName() + ".backup-error-" + DateUtil.DateToString(new Date(), "yyyyMMddHHmmss")));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

		}

		long end = System.currentTimeMillis();
		logger.info("本次商城订单详情的处理任务总计耗时：" + (end - start) +"毫秒");
	}

	/**
	 * 对象转化
	 * @param result
	 * @return
	 */
	private List<S_OrderDetailBean> toDetailBeans(List<S_OrderTaobaoDetailBean> result) {
		List<S_OrderDetailBean> detailBeans = new ArrayList<>();
		for(S_OrderTaobaoDetailBean r: result){
			S_OrderDetailBean detailBean = new S_OrderDetailBean();
			detailBean.setOrderBuyNumer(r.getOrderBuyNumer());
			detailBean.setOrderCashBack(r.getOrderCashBack());
			detailBean.setOrderCashBackRatio(StringUtil.changeObjectToDouble(r.getOrderCashBackRatio().replace("%", "")));
			detailBean.setOrderCategory(r.getOrderCategory());
			detailBean.setOrderCode(r.getOrderCode());
			detailBean.setOrderSubCode(r.getOrderSubCode());
			detailBean.setOrderCreateTime(r.getOrderCreateTime());
			detailBean.setOrderPayMoney(r.getOrderPayMoney());
			detailBean.setOrderPayTime(r.getOrderPayTime());
			detailBean.setOrderProductId(r.getOrderProductId());
			detailBean.setOrderSettlementMoney(r.getOrderSettlementMoney());
			detailBean.setOrderSettlementTime(r.getOrderSettlementTime());
			detailBean.setOrderType(r.getOrderType());
			detailBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
			detailBean.setCreateTime(new Date());
			detailBean.setCreateUserId(OptionUtil.adminUser.getId());
			detailBean.setOrderSettlementCashBack(r.getOrderSettlementCashBack());
			detailBean.setStatus(getSystemStatus(r.getStatus()));
			detailBean.setProductTitle(r.getProductTitle());
			detailBeans.add(detailBean);
		}
		return detailBeans;
	}

	/**
	 * 将平台的状态转化成系统相对应的平台状态码
	 * @param status
	 * @return
	 */
	private int getSystemStatus(String status) {
		if("已付款".equalsIgnoreCase(status))
			return EnumUtil.MallOrderType.待结算佣金.value;
		if("已结算".equalsIgnoreCase(status))
			return EnumUtil.MallOrderType.待支付佣金.value;
		return 0;
	}

	/*public static void main(String[] args) {
		//遍历 webroot/order/taobao目录下的所有未处理的excel文件
		//文件夹的名称
		String pathName = ConstantsUtil.getDefaultSaveFileFolder() +"order"+ File.separator+ "taobao";
		File file = new File(pathName);
		File[] fileList = file.listFiles();
		if(fileList != null && fileList.length > 0){
			for(File f: fileList){
				System.out.println(pathName + File.separator + f.getName());

				// 方式一：全部读取后处理，SAX模式，避免OOM，建议大量数据使用
				List<S_OrderTaobaoDetailBean> result = SaxExcelReader.of(S_OrderTaobaoDetailBean.class)
						.sheet(0) // 0代表第一个，如果为0，可省略该操作，也可sheet("名称")读取
						.rowFilter(row -> row.getRowNum() > 0) // 如无需过滤，可省略该操作，0代表第一行
//						.beanFilter(ArtCrowd::isDance) // bean过滤
						.read(f);// 可接收inputStream

				result.size();
			}
		}
		//对单个未处理的excel文件都出来
	}*/
}
