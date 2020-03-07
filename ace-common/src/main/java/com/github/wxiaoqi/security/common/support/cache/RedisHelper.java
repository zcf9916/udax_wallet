package com.github.wxiaoqi.security.common.support.cache;





import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;

/**
 * Redis缓存辅助类
 *
 * @author ShenHuaJie
 * @version 2016年4月2日 下午4:17:22
 */
@Service
public final class RedisHelper implements CacheManager {
	private RedisSerializer<String> keySerializer;
	private RedisSerializer<Object> valueSerializer;

	@Resource
	private RedisTemplate<Serializable, Serializable> redisTemplate;
	private final Integer EXPIRE = 0;

	//@Autowired
	public RedisHelper(RedisTemplate<Serializable, Serializable> redisTemplate) {
    	this.redisTemplate = redisTemplate;
		this.keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
		this.valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
		CacheUtil.setCacheManager(this);
	}

	public RedisTemplate<Serializable, Serializable> getRedisTemplate() {
		return redisTemplate;
	}

	@Override
	public final Object get(final String key) {
		return redisTemplate.boundValueOps(key).get();
	}

	@Override
	public final Object get(final String key, Integer expire) {
		expire(key, expire);
		return redisTemplate.boundValueOps(key).get();
	}

	@Override
	public final Object getFire(final String key) {
		expire(key, EXPIRE);
		return redisTemplate.boundValueOps(key).get();
	}

	@Override
	public final Set<Object> getAll(final String pattern) {
		Set<Object> values = InstanceUtil.newHashSet();
		Set<Serializable> keys = redisTemplate.keys(pattern);
		for (Serializable key : keys) {
			values.add(redisTemplate.opsForValue().get(key));
		}
		return values;
	}

	@Override
	public final Set<Object> getAll(final String pattern, Integer expire) {
		Set<Object> values = InstanceUtil.newHashSet();
		Set<Serializable> keys = redisTemplate.keys(pattern);
		for (Serializable key : keys) {
			expire(key.toString(), expire);
			values.add(redisTemplate.opsForValue().get(key));
		}
		return values;
	}

	@Override
	public boolean setBit(String key, long offset, boolean value) {
		return redisTemplate.opsForValue().setBit(key,offset,value);
	}

	@Override
	public boolean getBit(String key, long offset) {
		return redisTemplate.opsForValue().getBit(key,offset);
	}

	@Override
	public boolean setSAdd(String key, Serializable... member) {
		redisTemplate.boundSetOps(key).add(member);
		return true;
	}

	@Override
	public Serializable sPop(String key) {
		return redisTemplate.opsForSet().pop(key);
	}

	@Override
	public   boolean setSortSet(String key, Serializable member, double score) {
		return redisTemplate.opsForZSet().add(key,member,score);
		//expire(key, EXPIRE);
	}

	@Override
	public   Double zincrby(String key, Serializable value, double incre) {
		return redisTemplate.opsForZSet().incrementScore(key,value,incre);
		//expire(key, EXPIRE);
	}

	@Override
	public Long zRemRangeByScore(String key, double mixScore, double maxScore) {
		return redisTemplate.opsForZSet().removeRangeByScore(key,mixScore,maxScore);
	}

	@Override
	public Long getSortSetCount(String key) {
		return redisTemplate.opsForZSet().zCard(key);
	}
	@Override
	public Long getSortSetZCount(String key, double mixScore, double maxScore) {
        return redisTemplate.opsForZSet().count(key,mixScore,maxScore);
	}
	public Long  remZsetByRange(String key,Long begin,Long end){
		return redisTemplate.opsForZSet().removeRange(key,begin,end);
	}
	public Set<Serializable>  getZsetRevrange(String key,Long begin,Long end) {
		return redisTemplate.opsForZSet().reverseRange(key, begin, end);
	}

	public Set<ZSetOperations.TypedTuple<Serializable>> getZsetRevrangeByScore(String key, Long begin, Long end){
		return redisTemplate.opsForZSet().reverseRangeWithScores(key,begin,end);
	}


	@Override
	public final void set(final String key, final Serializable value, int seconds) {
		redisTemplate.boundValueOps(key).set(value);
		expire(key, seconds);
	}

	@Override
	public final void set(final String key, final Serializable value) {
		redisTemplate.boundValueOps(key).set(value);
		expire(key, EXPIRE);
	}

	@Override
	public final Boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}

	@Override
	public final void del(final String key) {
		redisTemplate.delete(key);
	}

	@Override
	public final Long delAll(final String pattern) {
		return redisTemplate.delete(redisTemplate.keys(pattern));
	}

	@Override
	public final String type(final String key) {
		return redisTemplate.type(key).getClass().getName();
	}

	/**
	 * 在某段时间后失效
	 *
	 * @return
	 */
	@Override
	public final Boolean expire(final String key, final int seconds) {
		if (seconds > 0) {
			return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
		}
		return false;
	}

	/**
	 * 在某个时间点失效
	 *
	 * @param key
	 * @param unixTime
	 * @return
	 */
	@Override
	public final Boolean expireAt(final String key, final long unixTime) {
		return redisTemplate.expireAt(key, new Date(unixTime));
	}

	@Override
	public final Long ttl(final String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	@Override
	public final void setrange(final String key, final long offset, final String value) {
		redisTemplate.boundValueOps(key).set(value, offset);
	}

	@Override
	public final String getrange(final String key, final long startOffset, final long endOffset) {
		return redisTemplate.boundValueOps(key).get(startOffset, endOffset);
	}

	@Override
	public final Object getSet(final String key, final Serializable value) {
		return redisTemplate.boundValueOps(key).getAndSet(value);
	}

	@Override
	public boolean setnx(String key, Serializable value) {
		RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
		RedisConnection redisConnection = null;
		try {
			redisConnection = RedisConnectionUtils.getConnection(factory);
			if (redisConnection == null) {
				return redisTemplate.boundValueOps(key).setIfAbsent(value);
			}
			return redisConnection.setNX(keySerializer.serialize(key), valueSerializer.serialize(value));
		} finally {
			RedisConnectionUtils.releaseConnection(redisConnection, factory);
		}
	}

	@Override
	public boolean lock(String key) {
		RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
		RedisConnection redisConnection = null;
		try {
			redisConnection = RedisConnectionUtils.getConnection(factory);
			if (redisConnection == null) {
				return redisTemplate.boundValueOps(key).setIfAbsent("0");
			}
			return redisConnection.setNX(keySerializer.serialize(key), valueSerializer.serialize("0"));
		} finally {
			RedisConnectionUtils.releaseConnection(redisConnection, factory);
		}
	}

	@Override
	public void unlock(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void hset(String key, Serializable field, Serializable value) {
		redisTemplate.boundHashOps(key).put(field, value);
	}

	@Override
	public Object hget(String key, Serializable field) {
		return redisTemplate.boundHashOps(key).get(field);
	}

	@Override
	public void hdel(String key, Serializable field) {
		redisTemplate.boundHashOps(key).delete(field);
	}

	@Override
	public Long incr(String key) {
		return redisTemplate.boundValueOps(key).increment(1L);
	}


	@Override
	public Object hgetAll(String key) {
		return redisTemplate.opsForHash().entries(key);
	}


	//获取list大小
	@Override
	public Long getListSize(String key){
		return redisTemplate.opsForList().size(key);
	}
	//像list左边推数据

	@Override
	public Long leftPush(String key, Serializable value){
		return redisTemplate.opsForList().leftPush(key,value);
	}

	@Override
	public Long rightPush(String key, Serializable value){
		return redisTemplate.opsForList().rightPush(key,value);
	}

	@Override
	public Long rightPushList(String key, Collection<Serializable> valueList){
		return redisTemplate.opsForList().rightPushAll(key,valueList);
	}

	@Override
	public Long leftPushList(String key, Collection<Serializable> valueList){
		return redisTemplate.opsForList().leftPushAll(key,valueList);
	}
	//删除list 中指定value
	@Override
	public Long listRemove(String key ,String value){
		return redisTemplate.opsForList().remove(key,0,value);
	}

	//删除list右边数据
	@Override
	public Serializable rightPopList(String key){
		return redisTemplate.opsForList().rightPop(key);
	}
	//获取范围内的list
	@Override
	public List<Serializable>   getRangeList(String key,Long begin ,Long end){
		return redisTemplate.opsForList().range(key,begin,end);
	}

	@Override
	public void publishTopicMsg(String topicName, Serializable value) {
		redisTemplate.convertAndSend(topicName,value);
	}


	/**
	 * 根据前缀删除key
	 * @param prex
	 */
	@Override
	public void deleteAllKeys(String prex) {
		prex = prex+"**";
		Set<Serializable> keys = redisTemplate.keys(prex);
		if (CollectionUtils.isNotEmpty(keys)) {
			redisTemplate.delete(keys);
		}
	}

	// 未完，待续...
}