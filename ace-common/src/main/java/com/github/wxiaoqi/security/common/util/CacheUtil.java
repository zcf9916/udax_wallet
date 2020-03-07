package com.github.wxiaoqi.security.common.util;

import com.github.wxiaoqi.security.common.support.cache.CacheManager;

public class CacheUtil {
    private static CacheManager cacheManager;

    public static void setCacheManager(CacheManager cacheManager) {
        CacheUtil.cacheManager = cacheManager;
    }

    public static CacheManager getCache() {
        return cacheManager;
    }


    /** 获取锁 */
    public static boolean tryLock(String key) {
       // int expires = 1000 * PropertiesUtil.getInt("redis.lock.expires", 180);
        int expires = 0;
        return cacheManager.setnx(key, expires);
    }

    /** 获取锁 */
    public static boolean tryLock(String key,int expires) {
        // int expires = 1000 * PropertiesUtil.getInt("redis.lock.expires", 180);

         cacheManager.setnx(key, expires);
        return cacheManager.expire(key,expires);
    }


    /** 获取锁 */
    public static boolean getLock(String key) {
        return cacheManager.lock(key);
    }

    /** 解锁 */
    public static void unlock(String key) {
        cacheManager.unlock(key);
    }

//    /**
//     * 次数检查
//     * @param key
//     * @param seconds 缓存时间
//     * @param frequency 最多次数
//     * @param message 超出次数提示信息
//     */
//    public static void refreshTimes(String key, int seconds, int frequency, String message) {
//        if (getLock(key + "-LOCK")) {
//            try {
//                Integer times = 1;
//                String timesStr = (String)lockManager.get(key);
//                if (DataUtil.isNotEmpty(timesStr)) {
//                    times = Integer.valueOf(timesStr) + 1;
//                    if (times > frequency) {
//                        throw new BusinessException(message);
//                    }
//                }
//                lockManager.set(key, times.toString(), seconds);
//            } finally {
//                unlock(key + "-LOCK");
//            }
//        } else {
//            refreshTimes(key, seconds, frequency, message);
//        }
//    }
}
