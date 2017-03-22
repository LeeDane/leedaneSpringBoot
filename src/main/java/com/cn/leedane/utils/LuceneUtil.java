package com.cn.leedane.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.SimpleFSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.cn.leedane.springboot.SpringUtil;

/**
 * Lucene相关的工具类
 * @author LeeDane
 * 2016年7月12日 上午10:30:09
 * Version 1.0
 */

public class LuceneUtil {
	
	private static LuceneUtil mLuceneUtil;

	private IndexWriter indexWriter;
	
	private IKAnalyzer analyzer;
	
	private SimpleFSDirectory directory;
	
	private IndexReader indexReader;
	
	private DirectoryReader directoryReader;
	
	private IndexSearcher searcher;

	
	private LuceneUtil(){
		indexWriter = (IndexWriter) SpringUtil.getBean("indexWriter");
		analyzer = (IKAnalyzer) SpringUtil.getBean("analyzer");
		directory = (SimpleFSDirectory) SpringUtil.getBean("directory");
	}
	
	public static synchronized LuceneUtil getInstance(){
		if(mLuceneUtil == null){
			mLuceneUtil = new LuceneUtil();
		}
		
		return mLuceneUtil;
	}
	
	/**
	 * 实现近实时查询，不关闭reader，但是Index有变化时，重新获取reader 
	 * @return
	 */
	public IndexSearcher getSearcher() {
		try {
            if(directoryReader==null) {
            	directoryReader = DirectoryReader.open(directory);
            } else {
                DirectoryReader tr = DirectoryReader.openIfChanged(directoryReader);
                if(tr!=null) {
                	directoryReader.close();
                	directoryReader = tr;
                }
            }
            return new IndexSearcher(directoryReader);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 简单的建立索引功能
	 * @param document 文档对象
	 * @return
	 * @throws ParseException
	 */
	public boolean simpleIndexOne(Document document) throws ParseException{
		boolean result = false;		
		analyzer.setUseSmart(false);			
		try {
			indexWriter.addDocument(document);
			//indexWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 简单的建立索引功能
	 * @param document 文档对象
	 * @return
	 * @throws ParseException
	 */
	public boolean simpleIndexMore(List<Document> document) throws ParseException{
		boolean result = false;		
		analyzer.setUseSmart(false);			
		try {
			for(Document d: document){
				indexWriter.addDocument(d);
			}		
			//indexWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 简单的搜索功能
	 * @param fields 搜索的字段
	 * @param keyword  搜索关键字
	 * @param num  需要获取的数量
	 * @return  总的数量
	 * @throws IOException
	 * @throws ParseException
	 */
	public int simpleSearch(String[] fields,String keyword,int num) throws IOException, ParseException{

		int totalHits = 0;
		//indexReader = DirectoryReader.open(directory);
		//searcher = new IndexSearcher(indexReader);
		searcher = getSearcher();
		QueryParser qp = new MultiFieldQueryParser(fields, analyzer); 
		qp.setDefaultOperator(dealQueryParserOperator(keyword)); 
		
		Query query = qp.parse(keyword); //搜索相似度最高的num条记录 
		TopDocs topDocs = searcher.search(query , num); 
		totalHits = topDocs.totalHits; //输出结果 
		
		//对统计结果数量和用户需要的总数量比较取最小的值
		//int size = totalHits > num ? num : totalHits;
		ScoreDoc[] scoreDocs = topDocs.scoreDocs; //scoreDocs的大小是等于totalHits的
		for(ScoreDoc scoreDoc: scoreDocs){
			//System.out.println("RRRRRRRRRRRRRRRRR"+scoreDocs.length);
			Document targetDoc = searcher.doc(scoreDoc.doc);
			System.out.println("文档的docID是：" + scoreDoc.doc);
			if(targetDoc != null){
				for(String f: fields){
					System.out.print("结果是："+targetDoc.get(f) + "---------------->");
				}
				
			}
			System.out.println("\n\r");
		}
		
		System.out.println("****************************************************************");
		
		/*for (int i = 0; i < size; i++){ 
			Document targetDoc = searcher.doc(scoreDocs[i].doc); 
			for(IndexableField field: targetDoc.getFields()){
				String name = field.name();
				for(String fid: fields){
					if(name!= null && name.equals(fid)){
						System.out.println(fid+ "--->" + field.stringValue()); 
					}
				}
			}		
		} */
		return totalHits;
	}
	
	/**
	 * 简单的分页搜索功能
	 * @param fields  搜索的字段
	 * @param keyword  搜索关键字
	 * @param pageIndex  页码
	 * @param pageSize  每页的数量
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public int simpleSearchPageByAfter(String[] fields,String keyword,int pageIndex,int pageSize) throws IOException, ParseException{

		int totalHits = 0;
		indexReader = DirectoryReader.open(directory);
		searcher = new IndexSearcher(indexReader);
		QueryParser qp = new MultiFieldQueryParser(fields, analyzer); 
		
		/*PhraseQuery query = new PhraseQuery();
		query.setSlop(1);
		query.add(new Term("bookcontent",keyword));*/
		
		Term term = new Term("bookcontent",keyword);
		TermQuery query = new TermQuery(term);	
		
		//qp.setDefaultOperator(dealQueryParserOperator(keyword)); 
		//Query query = qp.parse(keyword);
		
		//先获取上一页的最后一个元素
        ScoreDoc lastSd = getLastScoreDoc(pageIndex, pageSize, query, searcher);

		TopDocs topDocs = searcher.searchAfter(lastSd, query , pageSize); 
		totalHits = topDocs.totalHits; //输出结果 
	
		//对统计结果数量和用户需要的总数量比较取最小的值
		//int size = totalHits > num ? num : totalHits;
		ScoreDoc[] scoreDocs = topDocs.scoreDocs; //scoreDocs的大小是等于totalHits的
		for(ScoreDoc scoreDoc: scoreDocs){
			//System.out.println("RRRRRRRRRRRRRRRRR"+scoreDocs.length);
			Document targetDoc = searcher.doc(scoreDoc.doc);
			System.out.println("文档的docID是：" + scoreDoc.doc + "  --->积分" +scoreDoc.score);
			if(targetDoc != null){
				for(String f: fields){
					System.out.print("结果是："+targetDoc.get(f) + "---------------->");
				}
				
			}
			System.out.println("\n\r");
		}
		
		System.out.println("****************************************************************");
		
		/*for (int i = 0; i < size; i++){ 
			Document targetDoc = searcher.doc(scoreDocs[i].doc); 
			for(IndexableField field: targetDoc.getFields()){
				String name = field.name();
				for(String fid: fields){
					if(name!= null && name.equals(fid)){
						System.out.println(fid+ "--->" + field.stringValue()); 
					}
				}
			}		
		} */
		return totalHits;
	}
	
	public int multSearch(){
		return 0;
	}
	
	/**
	 * 处理搜索关键字的操作类型
	 * @param keyword  搜索关键字
	 * @return
	 */
	public Operator dealQueryParserOperator(String keyword) {
		if(keyword.contains("and") || keyword.contains("+"))
			return QueryParser.AND_OPERATOR;
		else{
			//其他采取默认OR操作
			return QueryParser.OR_OPERATOR;
		}		
	}

	/**
	 * 根据页码和分页大小获取上一次的最后一个ScoreDoc
	 * @param pageIndex  页码
	 * @param pageSize  每页大小
	 * @param query  查询
	 * @param searcher2
	 * @return
	 * @throws IOException
	 */
	private ScoreDoc getLastScoreDoc(int pageIndex, int pageSize, Query query,
			IndexSearcher searcher2) throws IOException {
		if(pageIndex==1)
			return null;//如果是第一页就返回空
        int num = pageSize * (pageIndex-1);//获取上一页的数量
        TopDocs tds = searcher2.search(query, num);
        if(tds.scoreDocs.length < pageSize)
        	return null;
        return tds.scoreDocs[num-1];
	}
	
	/**
	 * 更新文档的索引的值
	 * @param field  更新前的域
	 * @param text  更新前的域对应的值
	 * @param doc   更新后的docuent对象
	 * @param isCreate 更新前的条件不存在的情况下，是否仍然新增该条document,默认是false
	 * @return
	 * @throws IOException
	 */
	public boolean updateIndex(String field, String text, Document doc){
		return this.updateIndex(field, text, doc, false);
	}
	
	/**
	 * 更新文档的索引的值
	 * @param field  更新前的域
	 * @param text  更新前的域对应的值
	 * @param doc   更新后的docuent对象
	 * @param isCreate 更新前的条件不存在的情况下，是否仍然新增该条document,true：新增，false:不新增
	 * @return
	 * @throws IOException
	 */
	public boolean updateIndex(String field, String text, Document doc, boolean isCreate){
		try {
			indexReader = DirectoryReader.open(directory);
			searcher = this.getSearcher();
			
			Term term = new Term(field,text);
			TermQuery query = new TermQuery(term);
			
			//先查找是否有存在的记录
			TopDocs topDocs = searcher.search(query, 1);
			if(topDocs.totalHits < 1){
				//没有记录，也不新增，直接返回操作失败
				if(!isCreate)
					return false;
			}
			indexWriter.updateDocument(term,  doc);
			
			 //这两句一定要执行  query
			indexWriter.commit();
			indexWriter.close();  

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;		
		}
	}
	
	/**
	 * 删除文档的索引
	 * @param field
	 * @param text
	 * @param docID
	 * @return
	 * @throws IOException
	 */
	public boolean deleteIndex(int docID){
		boolean success = false;
		try {
			indexReader = DirectoryReader.open(directory);
			//Document doc = indexReader.document(docID);
			//Query q = new 
			//Term t = new Term("title","俏寡妇的养婆田");
			//indexWriter.deleteDocuments(queries);
			indexWriter.commit();
			if(indexWriter != null){
				//indexWriter.close();
			}
			//searcher.
			/*indexWriter.d
			
			 //这两句一定要执行  
			indexWriter.commit();
			indexWriter.close();  
*/
			success = true;
			return success;
		} catch (IOException e) {
			e.printStackTrace();
			return success;		
		}
	}
	
	/**
	 * 删除文档的索引
	 * @param query
	 * @return
	 */
	public boolean deleteIndex(Query query){
		try {
			indexWriter.deleteDocuments(query);
			indexWriter.commit();
			
			if(indexWriter != null){
				indexWriter.close();
			}		
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;		
		}
	}

	/**
	 * 删除文档的索引
	 * @param term
	 * @return
	 */
	public boolean deleteIndex(Term term){
		try {
			indexWriter.deleteDocuments(term);
			indexWriter.commit();
			
			if(indexWriter != null){
				indexWriter.close();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;		
		}
	}

	/**
	 * 强制删除,删除全部
	 */
	public void forceDelete(){
		try {
			indexWriter.forceMergeDeletes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setDirectory(SimpleFSDirectory directory) {
		System.out.println("set directory");
		this.directory = directory;
	}
	
	public SimpleFSDirectory getDirectory() {
		return directory;
	}
	
	public void setIndexWriter(IndexWriter indexWriter) {
		this.indexWriter = indexWriter;
	}
	
	public IndexWriter getIndexWriter() {
		return indexWriter;
	}
	
	public void setAnalyzer(IKAnalyzer analyzer) {
		this.analyzer = analyzer;
	}
	public IKAnalyzer getAnalyzer() {
		return analyzer;
	}
	
	/** 
	 * 传入String类型的数据源，智能提取单词放入Set中 
	 * @param source
	 * @param keyLength 关键字的长度大于等于(默认是2)
	 * @param keyNumber 关键字的数量(默认是5)
	 * @return
	 * @throws IOException
	 */
	public List<String> extract(String source) throws IOException {
		List<String> list =new ArrayList<String>(); //定义一个Set来接收将要截取出来单词 
		if(StringUtil.isNull(source))
			return list;
		
		analyzer = new IKAnalyzer(); //初始化IKAnalyzer 
		analyzer.setUseSmart(true); //将IKAnalyzer设置成智能截取 
		TokenStream tokenStream= analyzer.tokenStream("", new StringReader(source)); //调用tokenStream方法(读取数据源的字符流)
		tokenStream.reset();
		while (tokenStream.incrementToken()) { //循环获得截取出来的单词 
			CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class); //转换为char类型 
			String keWord = charTermAttribute.toString(); //转换为String类型 
			list.add(keWord); //将最终获得的单词放入list集合中 
		} 
		return list; 
	 }  
}
