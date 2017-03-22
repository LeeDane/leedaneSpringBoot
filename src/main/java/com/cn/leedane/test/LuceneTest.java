package com.cn.leedane.test;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.SimpleFSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.FileUtil;
import com.cn.leedane.utils.LuceneUtil;

/**
 * lucene相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:11:58
 * Version 1.0
 * 参考资料：http://www.xdemo.org/lucene4-8-ikanalyzer-springmvc4-jsoup-quartz/
 */
public class LuceneTest extends BaseTest{
	
	@Resource
	private IndexWriter indexWriter;
	
	@Resource
	private IKAnalyzer analyzer;
	
	@Resource
	private SimpleFSDirectory directory;	
	
	@Test
	public void indexTest() throws ParseException{
		System.out.println("开始时间");
		//analyzer.setUseSmart(false);
		try {
			analyzer.tokenStream("bookcontent", "不是#我#做#的");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/**
		 * TextField() 索引+分词  Store.YES:存储，Store.NO: 不存储
		 * StringField() 索引+不分词
		 * StoreField()  只存储
		 */
		Document bookdoc  = new Document();
		//标题选择索引+分词，可以存储
		Field bookNo = new LongField("booktitle", 344333, Store.YES);
		//文章内容一般很大，索引选择分词+索引，网上说一般不存储，可是不存储的话就显示不了这些内容
		Field bookName = new TextField("bookcontent", "不知道为什么", Store.YES);
		//作者名应该保持完整索引，不需要分词。可以存储
		//Field author = new StringField("author", "匿名", Store.YES);
		//时间选择索引，但是不分词，可以存储
		//Field publishdate = new StringField("publishdate", "1990-01-01", Store.YES);
		bookdoc.add(bookNo);
		bookdoc.add(bookName);
		//bookdoc.add(author);
		//bookdoc.add(publishdate);
		
		Document bookdoc1  = new Document();
		//标题选择索引+分词，可以存储
		Field bookNo1 = new LongField("booktitle", 423233, Store.YES);
		//文章内容一般很大，索引选择分词+索引，网上说一般不存储，可是不存储的话就显示不了这些内容
		Field bookName1 = new TextField("bookcontent", "不是我做的", Store.YES);
		bookdoc1.add(bookNo1);
		bookdoc1.add(bookName1);
		try {
			indexWriter.addDocument(bookdoc);
			indexWriter.addDocument(bookdoc1);
			indexWriter.close();
			
			
			System.out.println("建立索引成功");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void searchTest() throws IOException, ParseException{
		
		long startTime = System.currentTimeMillis();
		
		/*try {
			indexReader = DirectoryReader.open(directory);
			searcher = new IndexSearcher(indexReader);
			String keyword = "中国"; //使用QueryParser查询分析器构造Query对象 
			QueryParser qp = new MultiFieldQueryParser(new String[]{"booktitle","bookcontent"}, analyzer); 
			qp.setDefaultOperator(QueryParser.AND_OPERATOR); 
			Query query = qp.parse(keyword); //搜索相似度最高的5条记录 
			TopDocs topDocs = searcher.search(query , 5); 
			System.out.println("命中：" + topDocs.totalHits); //输出结果 
			ScoreDoc[] scoreDocs = topDocs.scoreDocs; 
			for (int i = 0; i < topDocs.totalHits; i++){ 
				Document targetDoc = searcher.doc(scoreDocs[i].doc); 
				for(IndexableField field: targetDoc.getFields()){
					String name = field.name();
					if( name!= null && name.equals("bookcontent")){
						System.out.println("内容：" + field.stringValue()); 
					}
				},"FB1724673
				
			} 
		} catch (Exception e) {
		}*/
		
		System.out.println("总数：" +LuceneUtil.getInstance().simpleSearch(new String[]{"id"}, "327", 50));
		long endTime = System.currentTimeMillis();
		System.out.println("执行程序总计耗时："+ (endTime - startTime) + "毫秒");
	}
	
	@Test
	public void deleteDocument() throws IOException, ParseException{
		
		//删除文档的时候，如果Term中的字段是TextField类型，将删除不成功
		Query qp = NumericRangeQuery.newLongRange("booktitle", new Long(17277233), new Long(17277233), true, true);
		//NumericRangeQuery.
		//qp.setDefaultOperator(QueryParser.AND_OPERATOR); 
		//Query query = qp.parse(""); //搜索相似度最高的5条记录 
		indexWriter.deleteDocuments(qp);
		indexWriter.commit();
		
		//lucene3.5后不建议使用，因为会消耗大量的开销，lucene会根据情况自行处理
		//indexWriter.forceMerge(1, false);
		
		indexWriter.close();
		System.out.println("删除索引成功");
	}
	

	@Test
	public void updateDocument() throws IOException, ParseException{
		
		//删除文档的时候，如果Term中的字段是TextField类型，将删除不成功
		//Query qp = NumericRangeQuery.newLongRange("booktitle", new Long(17277233), new Long(17277233), true, true);
		//NumericRangeQuery.
		//qp.setDefaultOperator(QueryParser.AND_OPERATOR); 
		//Query query = qp.parse(""); //搜索相似度最高的5条记录 
		//indexWriter.updateBinaryDocValue(new Term("bookcontent","我们"), "bookcontent", new BytesRef("我是外星人"));
		//indexReader = DirectoryReader.open(directory);
		Document bookdoc  = new Document();
		//标题选择索引+分词，可以存储
		Field bookNo = new LongField("booktitle", 17277233, Store.YES);
		//文章内容一般很大，索引选择分词+索引，网上说一般不存储，可是不存储的话就显示不了这些内容
		Field bookName = new TextField("bookcontent", "我是外星人9", Store.YES);
		bookdoc.add(bookNo);
		bookdoc.add(bookName);
		
		System.out.println(LuceneUtil.getInstance().updateIndex("booktitle", "17277233", bookdoc));
	}
	
	
	@Test
	public void indexSanwen() throws IOException, ParseException{
		long startTime = System.currentTimeMillis();
		analyzer.setUseSmart(false);
		
		/**
		 * TextField() 索引+分词  Store.YES:存储，Store.NO: 不存储
		 * StringField() 索引+不分词
		 * StoreField()  只存储
		 */
		List<Document> documents = new ArrayList<Document>();
		Document sanwen  = null;

		File root = new File("F://sanwen");
		
		//是文件夹
		if(root != null && root.isDirectory()){
			String[] fs = root.list();
			System.out.println("总文档数："+fs.length);
			String f_originPath;  //原始的文件名
			String f_fullPath;  //完整的文件路径
			String f_title;  //根据文件名解析后的标题
			for (int i = 0; i < fs.length; i++) {
				sanwen = new Document();
				
				f_originPath = fs[i];
				int indexPoint = f_originPath.lastIndexOf(".");
				
				//对没有后缀的文件直接返回
				if(indexPoint == -1) continue;
				
				f_fullPath = root.getAbsolutePath()+"\\" + f_originPath;
				//System.out.println("f_originPath:" + f_originPath);
				f_title = f_originPath.substring(0, indexPoint).replaceAll("　", "").trim();
				//标题选择索引+分词，可以存储
				Field sanwenTitle = new TextField("title", f_title, Store.YES);
				//文章内容一般很大，索引选择分词+索引，网上说一般不存储，可是不存储的话就显示不了这些内容
				Field sanwenContent = new TextField("content",FileUtil.getStringFromPath(f_fullPath), Store.YES);
				//作者名应该保持完整索引，不需要分词。可以存储
				Field sanwenAuthor = new StringField("author", "散文网", Store.YES);
				//时间选择索引，但是不分词，可以存储
				Field sanwenCreateDate = new StringField("createdate", DateUtil.getSystemCurrentTime("yyyy-MM-dd HH:mm:ss"), Store.YES);
				sanwen.add(sanwenTitle);
				sanwen.add(sanwenContent);
				sanwen.add(sanwenAuthor);
				sanwen.add(sanwenCreateDate);
				documents.add(sanwen);
				
				//indexWriter.addDocument(sanwen);
				System.out.println("加载文档"+ i + "-----"+ f_originPath + "---->索引成功");
			 }  
			LuceneUtil.getInstance().simpleIndexMore(documents) ; 
			
			//indexWriter.close();
		}else{
			try {
				throw new ErrorException("不是文件夹");
			} catch (ErrorException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("执行程序总计耗时："+ (endTime - startTime) + "毫秒");
	}
	
	/**
	 * 搜索散文的测试方法
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void searchSanwen() throws IOException, ParseException{
		long startTime = System.currentTimeMillis();
		System.out.println("总数：" +LuceneUtil.getInstance().simpleSearch(new String[]{"title","content"}, "消息", 50));
		long endTime = System.currentTimeMillis();
		System.out.println("执行程序总计耗时："+ (endTime - startTime) + "毫秒");
	}
	
	/**
	 * 更新文档的索引
	 */
	@Test
	public void updateIndexSanwen(){
		//String text = "<div class=\"adcontent\"> <!--<script>_get_content_ad();</script>--></div><p>芝堰古村位于浙江中部兰溪市黄店镇，始建于南宋乾道年间，距今已有近千年历史。是兰溪和建德的交界处，古代严州和婺州的必经之路，驿站这一重要的功能给古代芝堰村带来了繁华。村中的一条古街两旁，至今仍遗留着茶楼酒肆、钱庄当铺、戏院烟馆、澡堂歇店、剃头店、濯足店的旧址。此处建筑格式集元、明、清、民国于一村，被人称作“中国古民居博物馆”。芝堰古村不但旅游资源丰富，<a href=\"http://www.sanwen.net/\" target=\"_blank\">人文</a>历史资源也相当的厚重，这不走到田间地头，友人说当地有一丘田叫“养婆田”，也称“剩婆丘”。这里流传着一个贤德寡妇<a href=\"http://xiaojing.sanwen8.cn/\" target=\"_blank\">孝</a>顺婆婆的催人泪下的感人<a href=\"http://tonghua.sanwen.net/\" target=\"_blank\">故事</a>。</p> <p>话说大明成化年间，芝堰的村东头住着一户姓陈的人家，一家三口过日子，儿子、媳妇和婆婆。只有一间泥墙屋 ，楼下烧饭还养猪。楼上两张床只用一块薄薄的旧床单相隔，讲得难听点，就是<a href=\"http://qingchun.sanwen8.cn/\" target=\"_blank\">年轻</a>夫妻间的那点事也要尽量的小心从事以免尴尬。</p> <p>按理说，“穷在闹市无人问”，一个臭哄哄的人家唯恐避之不及，却偏偏有许多人有事没事的喜欢往泥墙屋里钻，就是村西头有名的大财主陈三金也三天两头的来凑热闹，你说奇怪不奇怪！</p> <p>说出来也就不稀奇了，“<a href=\"http://www.sanwen8.cn/sanwen/love/\" target=\"_blank\">爱</a>美之心人皆有之“。原来这户人家的小媳妇长得实在太漂亮了，美得像个仙女。个子高挑，皮肤白皙，面目清秀，<a href=\"http://xiatian.sanwen8.cn/\" target=\"_blank\">夏</a>天衣着单薄，裸露出的一双玉手像田里刚挖出的鲜藕，巴不得向前咬一口。走路时一对耸立的乳房一颤一颤的，臀部浑圆上翘，惹得一帮光棍直流口水。</p> <p>各位客官要问了，这般如花似玉的姑娘什么人家不好嫁，非要嫁给一个穷光蛋，一朵鲜花插到牛粪上。话也不能讲的这么刻薄，那是人家心好修来的福，也就是说”姻缘古来前世定的”，凭你有万贯家财也是眼红不来的。</p> <p>话说当年，这户人家的儿子叫憨大，在他十岁时<a href=\"http://www.sanwen.net/shige/fuqin/\" target=\"_blank\"></a>就去世了，从小与<a href=\"http://www.sanwen.net/shige/muqin/\" target=\"_blank\">母亲</a>相依为命，做娘的一把屎一把尿总算把儿子抚养成人，眼看二十二也未成亲，憨大以打柴为业，家中并无一分田地。一日起早，憨大像往常一样，腰围刀鞘手拿尖头棍（挑柴用的木棒）去芝堰里面的下慈坞去砍柴，母亲跟着去耙松毛丝当引火柴。到了山腰，走在前头的憨大突然大喊：“娘！不好了。”“大清早一惊一乍的干什么 。”做娘的说归说还是抢步向前观看，只见一姑娘躺在草丛中，<a href=\"http://caodi.sanwen8.cn/\" target=\"_blank\">草地</a>上有明显的血迹。老娘仔细的检查了姑娘的身体，“糟了，被‘连头噗’毒蛇咬了，不马上处置姑娘可能活不了了！”老娘焦急万分。救人如就火呀，“顾不了那么多了，憨大，如果我有什么不测，你一定好好<a href=\"http://www.sanwen.net/suibi/shenghuo/\" target=\"_blank\">生活</a>，将来给我生个大胖小子。”“娘，我听不懂您说得话。”娘缓过神来，自嘲地说：“娘一时糊涂了没给你讲清楚，娘要救人怕中了毒活不过来！”憨大急了，“我不能没有娘，我来救吧！”母亲说：“你一个大男人不行，看蛇咬的不是地方，在大腿根部，你转过脸去吧！”儿子诺诺从命。<span style=\"position:relative;left:-100000px;\">( <a href=\"http://www.sanwen.net/\" target=\"_blank\">文章</a><a href=\"http://www.sanwen.net/\" target=\"_blank\">阅读</a>网：www.sanwen.net )</span></p> <p>母亲快速退下姑娘的裤子，伤口已呈紫黑色，“顾不了那么多了”，大娘闭着眼睛一口吸上伤口，吐出的是又黑又腥的脓血，差点<a href=\"http://xiangxinziji.sanwen8.cn/\" target=\"_blank\">自己</a>也吐了。等到吐出猩红的血色时，老娘叫憨大把衣服撕成条给姑娘包扎。守了一个时辰，姑娘终于醒了，当姑娘发觉自己几乎一丝不挂躺着不禁失声<a href=\"http://tongku.sanwen8.cn/\" target=\"_blank\">痛哭</a>。大娘知道姑娘误解了，忙问：“姑娘是哪里人，一大清早怎么会躺在这。”姑娘说：“我也不知道，我是山下下慈坞村人，今早上山找猪食在这里摔了一跤就什么都不知道了。”“你是被毒蛇咬了，幸好发现得早 。”老娘见姑娘没事放心了，“谢大娘活命之恩，大恩大德容后-----”好了好了，小姑娘家家的哪来这许多礼数。“未等姑娘说完大娘抢过话头，细心的老娘给姑娘穿好裤子叫憨大背着送下山了。</p> <p>事情<a href=\"http://cengjing.sanwen8.cn/\" target=\"_blank\">过去</a>了半年老实的娘俩也差不多忘了，一日芝堰小镇有名的媒婆突然“造访”泥墙屋，说有一桩天大的好事在等着她们。娘俩这一辈子也没想过天上会丢馅饼，就说：“媒婆大人喂，请你别拿我们穷开心了。”媒婆并不生气，一本正经道：“你娘俩还记得半年前救人的事？”这一说倒还有些眉目，但一个仙女肯到我家-------，不行，不行，这不害死了人家嘛！</p> <p>原来姑娘被救后一心想报恩，哪怕再好的人家来做媒就是不答应，<a href=\"http://xiaojing.sanwen8.cn/\" target=\"_blank\">父母</a>犟不过只好倒请媒人保媒。这种天降的好事再不接受就太不识趣了。一边愿嫁，一边是十万分的愿娶。娘家也是穷人家，陪了一床棉被，一只木箱就是全部的嫁妆。</p> <p><a href=\"http://hunyin.sanwen8.cn/\" target=\"_blank\">结婚</a>那天，虽然没有那种气派的场面，但说的夸张一点起码有半个镇上的人来看热闹。当然都来看新娘子的美貌，砍柴的憨大五大三粗的有什么看头。还有的是抱着幸灾乐祸的心理来的，目的是见证一下鲜花是如何插在牛粪上的。</p> <p>婚后，憨大照例砍他的柴，有时挑到厚仁，有时到女埠，实在不好卖就一直挑到兰溪城里去。貌<a href=\"http://www.sanwen.net/\" target=\"_blank\">美的</a>媳妇在家陪伴老娘养猪料理生活，村里一帮没老婆的大男人趁憨大外出卖柴之际，经常到泥墙屋来坐坐，或门槛上，或门前的石头上，说些不三不四撩拨媳妇的风骚话。只要不动手动脚太出格，乡里乡亲的抬头不见低头见，善良的婆媳俩也不会赶他们走。</p> <p>来得最勤快的倒是芝堰村上有名的财主陈三金老爷，本来与憨大家素无往来，自憨大娶了媳妇，便成了八竿子打不着的亲戚，硬说憨大媳妇是他表舅的堂姑妈的外甥女的表妹，按辈分是他的表妹。来时两手不落空，不是糕点就是水果，连饭都吃不饱的人家平时哪会去享受这些东西。见三金老爷到，一帮男人作<a href=\"http://zuowen.sanwen.net/z/112975\" target=\"_blank\">鸟</a>兽散。三金老爷老远就囔囔：“我的好表妹哎，你在家吗？” 每次财主的到来都会令婆媳俩难堪，不让他进门吧，人家有钱有势放下身段来认“亲”从理上讲不过去；让进吧，财主是芝堰村里有名的淫棍上门明显是黄鼠狼给鸡拜年，进了等于“引狼入室”。财主依仗着丈人在县衙做事，仗势欺人无恶不作。面对如此“门神”，婆媳俩只好小心伺候，不能得罪但也不能让他占便宜。</p> <p>打柴是有季节性的，<a href=\"http://chuntian.sanwen8.cn/\" target=\"_blank\">春</a>天草木抽青不能砍，梅<a href=\"http://www.sanwen.net/sanwen/yu/\" target=\"_blank\">雨</a>季节没法砍，<a href=\"http://dongtian.sanwen8.cn/\" target=\"_blank\">冬</a>天下雨下<a href=\"http://www.sanwen.net/shige/xue/\" target=\"_blank\">雪</a>无法砍。一到这个时节，家里的米缸就要裸底了。</p> <p>一个北风呼啸的日子，一家人正为下锅的米<a href=\"http://youshang.sanwen8.cn/\" target=\"_blank\">忧愁</a>，大门“哐当”一声来了“不速之客”陈三金。财主手里拎着十来斤大米笑嘻嘻地说：“我就知道‘表妹’家揭不开锅了，我是‘雪中送炭’来了。”见一家人不怎么热情，财主解嘲地说：“上门不煞客，也不请我坐坐？！”媳妇无奈端了张凳子示意老爷坐下。</p> <p>“表妹夫呀，不是我说你，我表妹长得如花似玉的跟了你真的遭罪了。这样吧，眼看年关快临近了，我家有许多的针线活要做，不妨让表妹到我家帮忙一段<a href=\"http://shijian.sanwen8.cn/\" target=\"_blank\">时间</a>，吃住就在我家。工钱嘛，好说，好说！”花心的财主一直在<a href=\"http://dengni.sanwen8.cn/\" target=\"_blank\">等待</a>良机，就像猎人在等待扑捉猎物的时机。老实的憨大心里一直在愧疚，天仙似的媳妇跟了谁也不会这么苦啊！憨大的眼睛巴巴的望着媳妇。聪慧的媳妇哪有不知道财主的老谋深算，想了想说：“用得上到你家帮工是我的福分，是我们穷人家巴不得的好事。不过，我婆婆腰不好，我每天晚上要帮它按摩按摩，如果你同意我晚上回家我就跟你去，不然-----”“可以，可以。”老爷怕憨大媳妇有什么变卦坏了他的如意算盘连忙说。</p> <p>憨大媳妇到了财主家，财主可不管丑八怪老婆的脸色，吩咐厨房今天炒粉干肉，明天土索面加荷包蛋，后天汤团年糕的。八怪老婆可不笨，财主叫她回娘家住几天她还不知道骚公鸡的个性，怀胎在家闲着没事的她偏偏每天不离身陪着美人说话，就是不让老公有下嘴的机会。财主忌惮丈人家的势力也算半个“妻管严”，老爷每天看着妖娆多姿的美人在眼前晃，到手的肥肉就是吃不着，心里火燎火燎的猫抓似的难受。</p> <p>转眼年关快到了，憨大想这时节柴火好卖而且价格也不低，多卖点柴火也好给媳妇添置点新衣，于是趁雪后的晴天到考坞源高山打柴。考坞源山高林密人迹罕至，山上的柴火特别的茂盛，憨大选对了好地方。大山一样魁梧的憨大想想天仙一样的媳妇觉得自己比董永娶了七仙女还要<a href=\"http://xingfu.sanwen8.cn/\" target=\"_blank\">幸福</a>，想起这些浑身就有使不完的劲，在捆好的柴火上又加了两大块乌钢柴（最耐烧的杂木）。</p> <p>天已漆黑，憨大媳妇回家做好了晚饭还不见丈夫回家，心里焦急万分。打算打着松明（有松脂的可燃松木）去找憨大，这时远处朦朦胧胧的见一个人背着一大坨东西急匆匆往这边走来。临近了，背“东西”的人问：“这里是憨大家吗？”憨大媳妇见问忙答：“是啊是啊，有什么事吗？”“你没看见我背着的是你家老公呀！”背人者气喘吁吁连忙往门里闯，憨大媳妇忙不迭地跟进。</p> <p>原来憨大早上出门砍柴原本中午就能到家，柴挑的重加上雪后路滑，憨大失去平衡，个儿高大的憨大仰八叉摔倒，一担200多斤的柴火压在他身上动弹不得。山高路远，叫天天不应，叫鬼鬼不知。慢慢的精疲力尽的憨大失去了知觉。下王一个在考坞源吊野猪的上山查看才发现奄奄一息的憨大，于是费尽九牛二虎之力才把憨大弄回了家 。</p> <p>家里仅有的几个铜板交给了郎中，憨大的伤还是没有起色，渐渐地山一样的大块头瘦的皮包骨。婆媳俩终日以泪洗面，这样下去总不是个办法呀。憨大媳妇思量了很久，对婆婆说：“我去想办法借点钱。”婆婆说：“像我们家这种情况有谁会借钱给我们？”儿媳妇弱弱地说：“要不，去，去‘表哥’家看看。”婆婆当场否决：“不行不行，你看这些天连个鬼影也不见了。再说，万一-------。”媳妇知道婆婆要说什么，接口道：“死马当活马医，我去试试，我会当心的。”没有更好的办法，再说儿子不得不救啊，婆婆无奈的望着媳妇一步一步向财主家走去。</p> <p>财主老爷有灵毛似的，好像算准了今天要来，早已笑嘻嘻的站在门口相迎，嘴里说：“几天不见，表妹愈发标致了。”憨大媳妇哪来的心思贫嘴，“说正经的，到屋里说，我来找你有事。”孤男寡女的憨大媳妇怕人家看见说闲话，催着财主进了屋。不对！今天这么这屋里静悄悄的，“表哥，我表嫂呢？”老爷听憨大媳妇今天喊表哥心里那个乐呀，好像离<a href=\"http://gongzuojihua.sanwen.net/\" target=\"_blank\">计划</a>又近了一步。“你表嫂昨天就到兰溪娘家去了，佣人也不知跑到哪去了？今天就咱两兄妹，什么事都好说。”财主狡诈地奸笑着。憨大媳妇装着没看见，大着胆说：“表哥，实在不好意思开口，你知道我家憨大病着，想到您这借点钱，明年我做女工还你行吗？”财主故作沉吟：“像你们家这种情况，原本就不该借，借了拿什么还？况且你家男人也是一个快要死的人了。不过，‘不看僧面看佛面’，既然表妹来了，这点面子还是要给的。”“好了，上楼来拿吧！”财主一步跨上楼梯。憨大媳妇尴尬地站在原地，轻声说：“表哥，能不能我不上去了，您辛苦拿一下。”这一下财主不乐意了，财主吃准了憨大媳妇一定要借，来了个欲擒故纵，“借你钱已经够给面子了，你打听一下我陈三金的钱是那么好借的吗？既然表妹不急用那就请便吧！”财主说完还真的抽脚下了楼梯。</p> <p>这一下真的轮到憨大媳妇急了，“哎！我的好表哥，大人不记小人过，小户人家不会说话您就高抬贵手原谅了吧。”财主见鱼儿渐渐上钩，不觉暗自欢喜，“这就对了嘛！”说完，一把抓起玉手向楼梯走去，憨大媳妇羞得满面通红。上的楼来，财主拉着憨大媳妇坐在床沿，“大美人想死我了，自打你来到芝堰我就无时无刻不惦记着你，今日难得要陪我多多说说话哟！”憨大媳妇坐也不是，走也不是，低着头但愿早点拿钱走人。财主见憨大媳妇不啃声以为小媳妇怕羞，大着胆子去解胸前的衣扣，憨大媳妇紧紧捂着，财主不着急，像猫逗老鼠似的，说：“我的大美人啊，钱不借了？不给憨大治病了？”提起卧病在床的丈夫，媳妇眼泪脱眶而出，紧捂着的手松开了，财主可不管憨大媳妇的感受，“大美人这就对啦！我会好好的伺候你的。”面对费尽心机到手的猎物，财主心里充满压制不住的喜悦。</p> <p>财主抽丝剥茧般的慢慢褪去憨大媳妇身上最后一缕衣裳，当一具玉体完全暴露在财主面前，三金老爷不得不为造物主惊叹，我陈三金驭人无数，世间还有这等尤物。憨大媳妇心想就是憨大在青天白日的也没见过自己的躶体，就是晚上和憨大睡，由于与婆婆只隔了一张床单，做那事也是吹灭了灯的。可如今自己的身体却让人家看西洋镜似的欣赏，憨大媳妇羞辱地把头扭向一边。</p> <p>等到财主完事差不多是一个时辰以后的事了，可怜憨大媳妇一对硕大的乳房早已被财主抓得条条血痕，下身隐隐作痛。媳妇含泪接过财主的五两银子的手在颤抖，心里怀恨不已，恨自己的不守妇道，恨财主的落井下石，恨老天的不公正-----</p> <p>忍辱负重还是没有挽回丈夫的病，憨大还是走了，走的是那样的不甘心，一双眼睛紧盯着老母亲。憨大媳妇知道憨大是个大<a href=\"http://xiaojing.sanwen8.cn/\" target=\"_blank\">孝子</a>，吩咐道“憨大，我知道你是舍不下母亲。你放心我一定会照顾好母亲的。”话音刚落，憨大<a href=\"http://yongheng.sanwen8.cn/\" target=\"_blank\">永远</a>的闭上了双眼。</p> <p>不知是巧合还是其他原因，怀胎十月的财主夫人因大出血在憨大去世的同一天也死了，一起死的还有刚出生的婴儿。村里人说憨大福气好，活着有如花似玉的美人陪着，死后与有钱的三金老婆到阴间配夫妻。</p> <p>婆婆失去了儿子，媳妇没有了丈夫，两人心如刀绞，终日以泪洗面，这样的日子真是度日如年，憨大媳妇想如果没有婆婆自己真的会一了百了。</p> <p>憨大死的消息没过多久早已传遍十里八乡，憨大的死没有博得多少人的同情，倒是大美人憨大的媳妇惦记着的人可不少。大大小小的光棍们做<a href=\"http://meng.sanwen8.cn/\" target=\"_blank\">梦</a>都想抱着憨大媳妇睡觉，有钱有势的财主们跃跃欲试想纳憨大媳妇做小。他们可不管婆媳俩的<a href=\"http://www.sanwen.net/sanwen/shanggan/\" target=\"_blank\">悲伤</a>，三天两头往泥墙屋里奔，劝了婆婆劝媳妇，媒婆们更是软磨硬泡搅动三寸不烂之舌，大有非说服憨大媳妇改嫁不可的决心与意志。劝婆婆：“您老年纪大了，可您儿媳貌美年轻正当时呀！您儿子没福气消受您老不能糊涂误了人家的终身啊。再说放着个漂亮的小寡妇在家，人家年轻万一熬不牢做出什么伤风败俗的事您老的老脸往哪搁啊！”媒婆就是媒婆，几句话就说到了老人的痛处，老人想想，理也就是这个理，当然这事还得儿媳妇自己决定。</p> <p>说动了婆婆再来劝媳妇，“大美人呀！憨大走了，人死不能复生。这事倒过来讲，你也解脱了，本来花一样的姑娘哪家不好嫁？姑娘重情重义要报恩，这不情也结了，恩也报了，只怪憨大没福气。你在憨大家苦头也吃够了，也该找个好人家嫁了。”姑娘也是个认死理的人，媒婆把门槛都快磨破了，小寡妇还是没有吐口。</p> <p><a href=\"http://chuntian.sanwen8.cn/\" target=\"_blank\">春天</a>到了，山里的风景很美，到处山花烂漫，桃红柳绿，一派春意盎然。婆婆到村口的池塘洗衣服，泥墙屋鬼鬼祟祟闪进了三金老爷，正在摘菜的憨大媳妇吓了一跳。老爷淫笑着说：“表妹呀！我俩同病相怜，我死了老婆，你死了丈夫，也许是天意啊！不如我们配成夫妻任何？”边说一双手便向她的胸前摸来，憨大媳妇惊恐的站起来往后退，“表哥，请你不要这样，我一个寡妇家家的人家会说闲话的。”财主哈哈大笑：“我是个没有老婆的人，我可不管这些，再说，你装什么贞洁又不是没被我-----”“别说了！”媳妇怕他讲出难听的话赶紧打断。幸好婆婆洗衣服回来，财主见捡不到便宜悻悻而归。</p> <p>陈老爷就像阴魂不散的烂鬼死缠着小寡妇不放，这不，今天又请了这一带最有名的媒婆来说媒了。</p> <p>媒婆神神叨叨的支走了婆婆，对小寡妇说：“大妹子呀，不是我说你，你也是太死心眼了，人往高走，水朝低流，这不是人之常情吗？老公没了守着个老婆婆这生活过得有意思吗？”小寡妇接口道：“人不是畜生，扔下对我有恩的婆婆去过好日子我的良心能安吗？”媒婆的嘴就是厉害，说话特别的损。“凭你一个花朵一样的妇道人家能有什么能耐养活娘俩？况且一帮丑男人整天像苍蝇一般围着你撩拨你，你真的能守身如玉？”小寡妇说：“苦就苦一点，我和婆婆一道谅臭男人也不敢胡来！”</p> <p>媒婆不愧是江湖好手，难缠的人见的多了，但总有办法解决。“大妹子呀！话不能说的那么绝啊，没有男人的日子是很难熬的，你嘴上不说说不定下面早就痒痒了呢。哈哈-----”小寡妇的脸红了，轻声说：“媒婆说笑过头了。” 媒婆大大咧咧地说：“说过头比做过头强呀，做过头说出来就不好听了。”来者不善，媒婆话中有话。“大妹子，我们都是<a href=\"http://nvren.sanwen8.cn/\" target=\"_blank\">女人</a>，我来问你个私密的话，你下面的私处是否有一颗红痣？”说完话的媒婆眼睛圆瞪着小寡妇。小寡妇的脸涨的发紫，尴尬的不敢朝媒婆看。这种私密的事媒婆怎么知道？看来一定是财主告诉她的，完了完了，人家什么都知道了，我今后怎么做人奥！媒婆不紧不慢地说：“实话告诉你，我是受三金老爷的委托来保媒的，条件可以提，但人必须要。你掂量着办吧！我明儿听你回音。”说完，扭着肥大的屁股走了。</p> <p>是<a href=\"http://ye.sanwen8.cn/\" target=\"_blank\">夜</a>，小寡妇辗转反侧夜不成寐，婆婆关切地问：“儿媳妇，睡不着说说话？”“没什么，您老睡吧！”小寡妇应声道。夜深人静，小寡妇思虑良久。是啊！自己的<a href=\"http://rensheng.sanwen.net/\" target=\"_blank\">人生</a>太不幸了。自从毒蛇咬伤报恩嫁给老实本分的憨大，原本想不图荣华富贵但求平平安安度过一生，没想到生出许多变故来。自己不曾生育没给憨大留下后代这已经够遗憾的了，为了给憨大治病“舍身伺狼”憨大地下有知会原谅自己吗？万一陈三金说出那种事不是辱没夫家的门风吗？人言可畏，让人指指点点我还有脸活在世上吗？我死了不要紧，苦命的婆婆怎么活？一连串的问题弄得小寡妇的头都大了。</p> <p>东方晓白，小寡妇慢慢理出点头绪，罢罢罢！为了可怜的婆婆，我只好委屈自己了。主意打定，瞌睡袭来这一觉直睡到日上三竿。</p> <p>打开大门，勤快的媒婆早已等候多时，未及上茶，心急的媒婆连忙问道：“大美人，事情考虑的怎么样了？”小寡妇也不再扭扭妮妮，大大方方地说：“要我改嫁也不是不可以，但我有三个条件：一，八抬大轿抬我过门。”“这个可以，我替老爷做主了。”媒婆应和道。小寡妇继续说：“第二，我每天回婆家一趟照顾婆婆。”</p> <p>“这个？”媒婆面露难色。“不行就拉到！”小寡妇神情坚决。“可以！可以！老爷处我去说”媒婆连忙说。小寡妇继续开价：“第三，叫陈家老爷划出良田两亩写下田契作为婆婆的养老田，这个没得商量！”“哪，哪，我回去与老爷商量商量。”媒婆得到确信乐哈哈的去向主子邀功了。</p> <p>田契到手，婆媳俩抱头痛哭一场，发誓下辈子再做婆媳。</p> <p>小寡妇嫁到陈家虽穿金戴银山珍海味但还是难忘过去的日子，还好婆婆在本村，婆婆在小寡妇的精心料理下活到了八十八，村里人说：“这样贤德的媳妇是婆婆前辈子修福修来的。”</p> <p>从此，儿媳妇给婆婆养老的田就叫“养婆田”，也有人把儿媳妇留给婆婆的田叫“剩婆丘”。</p> <p>明朝的田还在，古代贤德媳妇的美名还在传扬！</p><div style=\"margin: 10px 0;border-top: 1px solid #eee;border-bottom: 1px solid #eee;padding: 10px 0;\"></div>---------------->";
		//String text = "我是四月。";
		//new LuceneUtil().updateIndex("content", text, 58);
		System.out.println("更新成功");
	}
	
	@Test
	public void deleteIndex(){
		LuceneUtil.getInstance().deleteIndex(58);
		System.out.println("删除成功");
	}
	
	@Test
	public void forceDeleteSanwen(){
		LuceneUtil.getInstance().forceDelete();
	}
	
	/**
	 * 搜索散文的分页测试方法
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void searchSanwenPageByAfter() throws IOException, ParseException{
		long startTime = System.currentTimeMillis();
		System.out.println("总数：" +LuceneUtil.getInstance().simpleSearchPageByAfter(new String[]{"booktitle","bookcontent"}, "我", 1, 15));
		long endTime = System.currentTimeMillis();
		System.out.println("执行程序总计耗时："+ (endTime - startTime) + "毫秒");
	}
	public static void main(String[] args) {
		String s = "枯树柴禾燃起的希望作者.html";
		int i = s.lastIndexOf(".");
		System.out.println(i);
		
	}
}
