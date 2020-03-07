package com.github.wxiaoqi.security.common.support.cache;


import org.springframework.data.redis.core.ZSetOperations;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CacheManager {
    Object get(final String key);

    Set<Object> getAll(final String pattern);

    void set(final String key, final Serializable value, int seconds);

    void set(final String key, final Serializable value);

    Boolean exists(final String key);

    void del(final String key);

    Long delAll(final String pattern);

    String type(final String key);

    Boolean expire(final String key, final int seconds);

    Boolean expireAt(final String key, final long unixTime);

    Long ttl(final String key);

    Object getSet(final String key, final Serializable value);

    boolean lock(String key);

    void unlock(String key);

    void hset(String key, Serializable field, Serializable value);

    Object hget(String key, Serializable field);

    Object hgetAll(String key);

    void hdel(String key, Serializable field);

    boolean setnx(String key, Serializable value);

    Long incr(String key);

    void setrange(String key, long offset, String value);

    String getrange(String key, long startOffset, long endOffset);

    Object get(String key, Integer expire);

    Object getFire(String key);

    Set<Object> getAll(String pattern, Integer expire);

    boolean setBit(String key, long offset, boolean value);

    boolean getBit(String key, long offset);



    boolean setSAdd(String key, Serializable... member);//设置集合
    Serializable sPop(String key);//集合中返回一个随机数
    //有序集合
    boolean setSortSet(String key, Serializable member, double score);


    Double zincrby(String key, Serializable value, double incre);

    Long zRemRangeByScore(String key, double mixScore, double maxScore);

    Long getSortSetCount(String key);

    Long getSortSetZCount(String key, double mixScore, double maxScore);

     Long  remZsetByRange(String key, Long begin, Long end);
     Set<Serializable>  getZsetRevrange(String key, Long begin, Long end);


    //获取list大小
    public Long getListSize(String key);
    //像list左边推数据
    public Long leftPush(String key, Serializable value);

    //像list左边推数据
    public Long rightPush(String key, Serializable value);

    //像list左边推数据
    public Long rightPushList(String key,  Collection<Serializable> valueList);
    //像list左边推数据
    public Long leftPushList(String key, Collection<Serializable> valueList);

    //删除list中指定的key
    public Long listRemove(String key ,String value);

    //删除list右边数据
    public Serializable rightPopList(String key);
    //获取范围内的list
    public List<Serializable> getRangeList(String key, Long begin, Long end);

    /**
     * 向topic发布消息
     * @param topicName 主题名字
     * @param value 具体对象
     */
    public void publishTopicMsg(String topicName, Serializable value);

    void deleteAllKeys(String s);
}
