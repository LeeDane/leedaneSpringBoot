package com.cn.leedane.redis.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;

import com.cn.leedane.redis.config.RedisConfig;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * redis的操作类
 * @author LeeDane
 * 2015年12月30日 下午3:36:04
 * Version 1.0
 */
public class RedisUtil{
	private Logger logger = Logger.getLogger(getClass());
	
	private volatile static RedisUtil redisUtil;

	private RedisUtil(){} //私有化构造方法
	
	//Redis服务器IP
	private static String IP;
    
	//Redis的端口号
	private static int PORT;
	    
	//访问密码
	//private static String AUTH;
	    
	//可用连接实例的最大数目，默认值为8；
	//如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	private static int MAX_ACTIVE;
	    
	//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE;
	    
	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT;
	    
	//private static int TIMEOUT;
	     
	//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = true;
	    
	private volatile static JedisPool jedisPool = null;
		
	/**
	* 初始化Redis连接池
	*/
	static {

		initData();
		try {
				JedisPoolConfig config = new JedisPoolConfig();
				config.setMaxActive(MAX_ACTIVE);
				config.setMaxIdle(MAX_IDLE);
				config.setMaxWait(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, IP, PORT);
			//jedisPool = new JedisPool(config, IP, PORT, TIMEOUT, AUTH);
		} catch (Exception e) {
			e.printStackTrace();
	        }
		}
   
	/**
	 * 获取redisUtil实例
	 * @return
	 */
	public static synchronized RedisUtil getInstance(){
		if(redisUtil == null){
			synchronized (RedisUtil.class){
				if(redisUtil == null){
					redisUtil = new RedisUtil();
				}
			}
		}
		return redisUtil;
	}
	
	/**
	* 获取Jedis实例
	* @return
	*/
	public synchronized static Jedis getJedis() {
		try {
			if(jedisPool != null){
					Jedis resource = jedisPool.getResource();
					return resource;
			}else {
				return null;
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 初始化数据
	 */
	private static void initData() {
		IP = RedisConfig.getIp();
		PORT = RedisConfig.getPort();
		MAX_ACTIVE = RedisConfig.getMaxActive();
		MAX_IDLE = RedisConfig.getMaxIdle();
		MAX_WAIT = RedisConfig.getMaxWait();
		//TIMEOUT = RedisConfig.getTimeOut();
		//AUTH = RedisConfig.getAuth();
	}

	/**
	* 释放jedis资源
	* @param jedis
	*/
	public static void returnResource(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}
	
	
	
	/**
	 * 获取list集合
	 * @param key
	 * @param t
	 * @return
	 */
	public <T> List<T> getList(String key, Class<T> t){
		return new ArrayList<T>();
	}
	
	/**
	 * 获取list集合（分页）
	 * @param key
	 * @param t
	 * @param pageSize
	 * @param startNo
	 * @return
	 */
	public <T> List<T> getList(String key, Class<T> t, int pageSize, int startNo){
		return new ArrayList<T>();
	}
	
	/********************************* String *****************************/
	/**
	 * 添加字符串数据
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean addString(String key, String member){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis !=null){
				jedis.set(key, member);
				result = true;
			}
		} catch (Exception e) {
			logger.error("addString()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 获取字符串数据
	 * @param key
	 * @return
	 */
	public String getString(String key){
		if(!isPing()) return null;
		
		Jedis jedis = getJedis();
		try {
			if(jedis != null)
				return jedis.get(key);
		} catch (Exception e) {
			logger.error("getString()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 添加序列化数据
	 * @param key
	 * @param seriamember 序列化
	 * @return
	 */
	public boolean addSerialize(String key, byte[] seriamember){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis !=null){
				jedis.set(key.getBytes(), seriamember);
				result = true;
			}
		} catch (Exception e) {
			logger.error("addSerialize()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 获取序列化数据
	 * @param key
	 * @return 
	 */
	public byte[] getSerialize(byte[] key){
		if(!isPing()) return null;
		
		Jedis jedis = getJedis();
		try {
			if(jedis != null)
				return jedis.get(key);
		} catch (Exception e) {
			logger.error("getSerialize()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 判断单个键是否存在
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key){
		if(!isPing()) return false;
		
		Jedis jedis = getJedis();
		try {
			if(jedis != null)
				return jedis.exists(key);
		} catch (Exception e) {
			logger.error("hasKey()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return false;
	}
	
	
	/********************************* Map *****************************/
	/**
	 * 添加map集合数据
	 * @param key
	 * @param map
	 * @return
	 */
	public boolean addMap(String key, Map<String, String> map){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.hmset(key, map);
				result = true;
			}
		} catch (Exception e) {
			logger.error("addMap()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		
		return result;
	}
	
	/**
	 * 删除map集合中多个字段
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean deleteMapField(String key, String ...field){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.hdel(key, field);
				result = true;
			}
		} catch (Exception e) {
			logger.error("deleteMapField()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		
		return result;
	}
	
	/**
	 * 获取map集合数据，为list
	 * @param key
	 * @param fileds
	 * @return
	 */
	public List<String> getMap(String key, String ...fileds){
		Jedis jedis = getJedis();
		try {
			if(jedis != null)
				return jedis.hmget(key, fileds);
		} catch (Exception e) {
			logger.error("getMap()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return null;
	}
	
	/********************************* List *****************************/
	/**
	 * 添加list列表数据
	 * @param key
	 * @param strs
	 * @return 返回插入后总的元素的数量(长度)
	 */
	public long addList(String key, List<String> strs){
		
		long total = -1;
		
		if(strs == null)
			return total;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				total = jedis.lpush(key, listToArray(strs));
			}
		} catch (Exception e) {
			logger.error("addList()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return total;
	}
	
	private String[] listToArray(List<String> list){
		String[] array = new String[list.size()];
		for(int i = 0; i< list.size(); i++){
			array[i] = list.get(0);
		}
		return array;
	}
	
	/**
	 * 获取指定范围的列表数据
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> getListByRange(String key, long start, long end){
		Jedis jedis = getJedis();
		try {
			if(jedis != null)
				return jedis.lrange(key, start, end);
		} catch (Exception e) {
			logger.error("getListByRange()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return new ArrayList<String>();
	}
	

	/**
	 * 获取所有的List列表数据
	 * @param key
	 * @return
	 */
	public List<String> getList(String key){
		return getListByRange(key, 0, -1);
	}
	
	/**
	 * 获取指定key内元素的总数
	 * @param key
	 * @return
	 */
	public long getListTotal(String key){
		long total = -1;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				total = jedis.llen(key);
			}
		} catch (Exception e) {
			logger.error("getListTotal()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return total;
	}
	
	/**
	 * 获取指定key内指定位置的元素
	 * @param key
	 * @return
	 */
	public String getListByIndex(String key, long index){
		String value = null;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				value = jedis.lindex(key, index);
			}
		} catch (Exception e) {
			logger.error("getListByIndex()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return value;
	}
	
	
	/********************************* Set *****************************/
	/**
	 * 添加set集合数据
	 * @param key
	 * @param members
	 * @return
	 */
	public boolean addSet(String key, String... members){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis !=null){
				jedis.sadd(key, members);	
				result = true;
			}
		} catch (Exception e) {
			logger.error("addSet()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		
		return result;
	}
	
	/**
	 * 获取set集合
	 * @param key
	 * @return
	 */
	public Set<String> getSet(String key){
		Set<String> value = null;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				value = jedis.smembers(key);
			}
		} catch (Exception e) {
			logger.error("getSet()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 判断某个成员是否在set集合中
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean isInSet(String key, String member){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				result = jedis.sismember(key, member);
			}
		} catch (Exception e) {
			logger.error("isInSet()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 获取Set指定key内元素的总数
	 * @param key
	 * @return 不存在返回0
	 */
	public long getSetTotal(String key){
		long total = -1;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				String type = jedis.type(key);
				if("set".equalsIgnoreCase(type)){
					total = jedis.scard(key);
				}
			}
		} catch (Exception e) {
			logger.error("getSetTotal()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return total;
	}
	
	/**
	 * 将指定set移动到另外一个set
	 * @param srckey
	 * @param dstkey
	 * @param member
	 * @return
	 */
	public long setMove(String srckey, String dstkey, String member){
		long total = -1;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				total = jedis.smove(srckey, dstkey, member);
			}
		} catch (Exception e) {
			logger.error("setMove()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return total;
	}
	
	/**
	 * 删除Set集合中多个元素
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean deleteSetField(String key, String ...field){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.srem(key, field);
				result = true;
			}
		} catch (Exception e) {
			logger.error("deleteSetField()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		
		return result;
	}
	
	/********************************* zset 有序集合*****************************/
	/**
	 * 增加
	 * 返回值是成功增加的元素的个数，如果member存在，则score会覆盖原有的分数
	 * @param key
	 * @param scoreMembers
	 * @return
	 */
	public boolean zadd(String key, Map<Double, String> scoreMembers){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.zadd(key, scoreMembers);
				result = true;
			}
		} catch (Exception e) {
			logger.error("zadd()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		
		return result;
	}
	/**
	 * 查询指定大小的数据
	 * @param desc true从大到小，false表示小到大
	 * @param key
	 * @param start 闭区间，开始位置的索引
	 * @param end  闭区间，结束位置的索引
	 * @return
	 */
	public Set<String> getLimit(boolean desc, String key, long start, long end){
		Set<String> set = new HashSet<String>();
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				if(desc)
					set = jedis.zrevrange(key, start, end);
				else
					set = jedis.zrange(key, start, end);
			}
		} catch (Exception e) {
			logger.error("getLimit()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		
		return set;
	}
	
	/**
	 * 排序获取数据
	 * @param key
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<Object> sort(String key, int start, int pageSize){
		List<Object> list = new ArrayList<Object>();
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				SortingParams params = new SortingParams();
				params.desc();
				params.alpha();
				params.limit(start, pageSize);
				//params.get("id*");
				List<String> liststr = jedis.sort(key, params);
				if(liststr != null && liststr.size() >0){
					for(String str: liststr)
						list.add(JsonUtil.jsonToObject(str));
				}
			}
		} catch (Exception e) {
			logger.error("sort()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return list;
	}
	
	/**
	 * 获取集合的元素的数量
	 * @param key
	 * @return
	 */
	public long getZSetCard(String key){
		Jedis jedis = getJedis();
		long count = 0 ;
		try {
			if(jedis != null){
				count = jedis.zcard(key);
			}
		} catch (Exception e) {
			logger.error("getZSetCard()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return count;
	}
	
	
	/********************************* 公共*****************************/
	
	/**
	 * 获取所有的key
	 * @param pattern 为空将获取*
	 * @return
	 */
	public Set<String> keys(String pattern){
		Set<String> keys = new HashSet<String>();
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				if(StringUtil.isNull(pattern))
					pattern = "*";
				keys = jedis.keys(pattern);
			}
		} catch (Exception e) {
			logger.error("keys()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return keys;
	}
	
	/**
	 * 过期键值(改变原来的值，注意：原来的类型不是String的情况下请慎用)
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public boolean expire(String key, String value, int seconds){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.set(key, StringUtil.isNull(value) ? "0000": value);
				jedis.expire(key, seconds);
				result = true;
			}
		} catch (Exception e) {
			logger.error("expire()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 过期键值(不改变原值)
	 * @param key
	 * @param seconds
	 * @return
	 */
	public boolean expire(String key, int seconds){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.expire(key, seconds);
				result = true;
			}
		} catch (Exception e) {
			logger.error("expire()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}

	/**
	 * 清除所有的缓存
	 * @return
	 */
	public boolean clearAll(){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.flushAll();
				result = true;
			}
		} catch (Exception e) {
			logger.error("clearAll()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 设置键的失效时间
	 * @return
	 */
	public boolean setex(String key, int seconds, String value){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.setex(key, seconds, value);
				result = true;
			}
		} catch (Exception e) {
			logger.error("setex()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}
	
	/**
	 * 删除指定key
	 * @param key
	 * @return 返回剩余的总数
	 */
	public boolean delete(String ... key){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			jedis.del(key);
			result = true;
		} catch (Exception e) {
			logger.error("delete()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}
	
	
	/**
	 * 检查网络是否连接
	 * @return
	 */
	public boolean isPing(){
		Jedis jedis = getJedis();
		try {
			if(jedis !=null){
				jedis.ping();
				return true;
			}
		} catch (Exception e) {
			logger.error("Redis服务ping不通", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		
		return false;
	}

	/**
	 * 对key自增1
	 * @param key
	 * @return
	 */
	public boolean incr(String key){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.incr(key);//自增1
				result = true;
			}
		} catch (Exception e) {
			logger.error("incr()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}

	/**
	 * 对key自减1
	 * @param key
	 * @return
	 */
	public boolean decr(String key){
		boolean result = false;
		Jedis jedis = getJedis();
		try {
			if(jedis != null){
				jedis.decr(key);//自减1
				result = true;
			}
		} catch (Exception e) {
			logger.error("incr()", e);
			e.printStackTrace();
		}finally{
			RedisUtil.returnResource(jedis);
		}
		return result;
	}

}
