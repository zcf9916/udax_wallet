package com.github.wxiaoqi.security.admin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;

public class SetCacheByExchangeIdUtil {

	/* 初始化各个交易所对应的Map */
	public static Map<Long, ArrayList<Object>> getAllMap() {
		Map<Long, ArrayList<Object>> allTypeMap = InstanceUtil.newHashMap();
		for (WhiteExchInfo whiteExchInfo : GetCommonDataUtil.getWhiteExchList()) {
			allTypeMap.put(whiteExchInfo.getId(), InstanceUtil.newArrayList());
		}
		return allTypeMap;
	}

	// 将对象按交易所分别放入Map
	public static void setObjectIntoList(Object object, Long exchangeId, Map<Long, ArrayList<Object>> exchangeMap) {
		if (exchangeMap.containsKey(exchangeId)) {
			ArrayList<Object> exchangeList = exchangeMap.get(exchangeId);
			exchangeList.add(object);
		}
	}

	// 将集合List按交易所分别放入缓存
	public static void setListIntoCache(String className, String key, ArrayList<Object> list) {
		CacheUtil.getCache().set(className + key, list);
	}
}
