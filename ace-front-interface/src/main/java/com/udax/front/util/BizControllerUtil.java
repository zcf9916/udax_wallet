package com.udax.front.util;

import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.vo.reqvo.PageInfo;
import com.udax.front.vo.reqvo.merchant.MchPreOrderModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


/**
 * 处理action层工具类
 */
@Slf4j
public class BizControllerUtil {

	/**
	 * 把页面的model转换成查询map
	 */
	public static SortedMap<String, Object> modelToMap(Object object)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		SortedMap<String, Object> params = new TreeMap<String,Object>();
		Class<?> cl = object.getClass();
//		LocalDate beginDateTime = null;
//		LocalDate endDateTime = null;
		//获取父类的属性
		while (cl != null){
			Field[] fields = cl.getDeclaredFields();
			for (Field field : fields) {
				String getMethodName = "get" + StringUtils.capitalize(field.getName());
				Method method = cl.getDeclaredMethod(getMethodName);
				Object ob = method.invoke(object);
				if (ob == null || (field.getType() == String.class && StringUtils.isBlank((String) ob))) {
					continue;
				}
				if (field.getType() == BigDecimal.class ) {
					params.put(field.getName(), ((BigDecimal)ob).stripTrailingZeros().toPlainString());
					continue;
				}
				if (field.getType() == Date.class ) {
					params.put(field.getName(), LocalDateUtil.date2LocalDateTime((Date)ob).toString());
					continue;
				}
				if (ob != null) {
					// 参数不为空,放到map
					params.put(field.getName(), ob);
				}
			}
			cl = cl.getSuperclass();
		}

//		if (beginDateTime != null && endDateTime != null) {
//			if (beginDateTime.isAfter(endDateTime)) {
//				throw new IllegalArgumentException("beginDate greater than endDate");
//			}
//		}
		return params;
	}

	/**
	 * 将数据库结果里面的bigdecimal 字段换成string
	 * 
	 * @param source
	 * @param target
	 * @param fieldNames
	 * @return
	 * @throws Exception
	 */
	public static void transBigDecimal2Str(List<?> source, List<?> target, String[] fieldNames) throws Exception {
		int size = source.size();
		for (int i = 0; i < size; i++) {
			Class<?> cl = source.get(i).getClass();
			Class<?> targetCl = target.get(i).getClass();
			for (String name : fieldNames) {
				Method getUserId = cl.getDeclaredMethod("get" + name);
				BigDecimal data = (BigDecimal) getUserId.invoke(source.get(i));
				// 设置
				Method setUserName = targetCl.getMethod("set" + name, String.class);
				setUserName.invoke(target.get(i), data.toPlainString());
			}
		}
	}

	public static <T, V> List<T> transferEntityToListVo(Class<T> cl, List<V> source)
			throws IllegalAccessException, InstantiationException {
		List<T> list = new ArrayList<>();
		if (!StringUtil.listIsBlank(source)) {
			for (V l : source) {
				T t = cl.newInstance();
				list.add(t);
				BeanUtils.copyProperties(l, t);
			}
			;
		}

		return list;
	}
	
//
//	public static <T> TableResultResponse<T> transferEntityToPageVo(Class<T> cl, TableResultResponse page)
//			throws IllegalAccessException, InstantiationException {
//		List target = page.getData();
//		TableResultResponse<T> responseVoPage = new TableResultResponse<T>();
//		List<T> list = new ArrayList<>();
//
//		BeanUtils.copyProperties(page, responseVoPage);
//		if (!StringUtil.listIsBlank(target)) {
//			for (Object v : target) {
//				T t = cl.newInstance();
//				list.add(t);
//				BeanUtils.copyProperties(v, t);
//			}
//		}
//		responseVoPage.setData(list);
//		return responseVoPage;
//	}
		public static void main(String[] args) {
		     BigDecimal test = new BigDecimal("0.01").setScale(8,BigDecimal.ROUND_HALF_UP);
			System.out.println(test.toPlainString());
		}
}
