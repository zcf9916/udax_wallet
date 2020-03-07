package com.github.wxiaoqi.security.common.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 数据集合排序
 */
public class DataSortUtil {

    private static final Logger logger = LogManager.getLogger();

    /**
     * 升序
     * fiedlName 要排序的属性名
     * 注意要排序的类属性必须为数字类型，否则会异常
     */
    public static <T> void sortAsc(List<T> dataList,String fiedlName){
        Collections.sort(dataList, new Comparator(){
            @Override
            public int compare(Object o1, Object o2) {
                int sort = 0;//默认排序，当异常时使用此值
                try {
                    Field field1 = o1.getClass().getDeclaredField(fiedlName);
                    //能访问私用属性
                    field1.setAccessible(true);
                    Field field2 = o2.getClass().getDeclaredField(fiedlName);
                    field2.setAccessible(true);
                    //当排序码不存在时，默认排在最后
                    Integer stu1= (field1.get(o1) == null) ? Integer.MAX_VALUE : (Integer)field1.get(o1);
                    Integer stu2= (field2.get(o2) == null) ? Integer.MAX_VALUE : (Integer)field2.get(o2);
                    if(stu1 > stu2){
                        sort = 1;
                    }else if(stu1.intValue() == stu2.intValue()){
                        sort = 0;
                    }else{
                        sort = -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("==排序异常"+e);
                }
                return sort;
            }
        });
    }

}
