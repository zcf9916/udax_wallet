package com.github.wxiaoqi.security.common.util;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.github.wxiaoqi.security.common.context.BaseContextHandler;

/**
 * 实体类相关工具类 解决问题： 1、快速对实体的常驻字段，如：crtUser、crtHost、updUser等值快速注入
 * 
 * @author Ace
 * @version 1.0
 * @date 2016年4月18日
 * @since 1.7
 */
public class EntityUtils {
	/**
	 * 快速将bean的crtUser、crtHost、crtTime、updUser、updHost、updTime附上相关值
	 * 
	 * @param entity
	 *            实体bean
	 * @author 王浩彬
	 */
	public static <T> void setCreatAndUpdatInfo(T entity) {
		setCreateInfo(entity);
		setUpdatedInfo(entity);
	}

	/**
	 * 快速将bean的crtUser、crtHost、crtTime附上相关值
	 * 
	 * @param entity
	 *            实体bean
	 * @author 王浩彬
	 */
	public static <T> void setCreateInfo(T entity) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String hostIp = "";
		String name = "";
		Long id = null;
		if (request != null) {
			hostIp = StringUtils.defaultIfBlank(request.getHeader("userHost"), ClientUtil.getClientIp(request));
			name = StringUtils.trimToEmpty(request.getHeader("userName"));
			name = URLDecoder.decode(name);
			id = request.getHeader("userId") == null ? null : Long.parseLong(request.getHeader("userId"));
		}

		if (StringUtils.isBlank(name)) {
			name = BaseContextHandler.getUsername();
		}
		if (id == null) {
			id = BaseContextHandler.getUserID();
		}

		// 默认属性
		String[] fields = { "crtName", "crtUser", "crtHost", "crtTime" };
		Field field = ReflectionUtils.getAccessibleField(entity, "crtTime");
		// 默认值
		Object[] value = null;
		if (field != null && field.getType().equals(Date.class)) {
			value = new Object[] { name, id, hostIp, new Date() };
		}
		// 填充默认属性值
		setDefaultValues(entity, fields, value);
	}

	/**
	 * 快速将bean的updUser、updHost、updTime附上相关值
	 * 
	 * @param entity
	 *            实体bean
	 * @author 王浩彬
	 */
	public static <T> void setUpdatedInfo(T entity) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String hostIp = "";
		String name = "";
		Long id = null;
		if (request != null) {
			hostIp = StringUtils.defaultIfBlank(request.getHeader("userHost"), ClientUtil.getClientIp(request));
			name = StringUtils.trimToEmpty(request.getHeader("userName"));
			name = URLDecoder.decode(name);
			id = request.getHeader("userId") == null ? null : Long.parseLong(request.getHeader("userId"));
		}

		if (StringUtils.isBlank(name)) {
			name = BaseContextHandler.getUsername();
		}
		if (id == null) {
			id = BaseContextHandler.getUserID();
		}

		// 默认属性
		String[] fields = { "updName", "updUser", "updHost", "updTime" };
		Field field = ReflectionUtils.getAccessibleField(entity, "updTime");
		Object[] value = null;
		if (field != null && field.getType().equals(Date.class)) {
			value = new Object[] { name, id, hostIp, new Date() };
		}
		// 填充默认属性值
		setDefaultValues(entity, fields, value);
	}

	/**
	 * 依据对象的属性数组和值数组对对象的属性进行赋值
	 * 
	 * @param entity
	 *            对象
	 * @param fields
	 *            属性数组
	 * @param value
	 *            值数组
	 * @author 王浩彬
	 */
	private static <T> void setDefaultValues(T entity, String[] fields, Object[] value) {
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			if (ReflectionUtils.hasField(entity, field)) {
				ReflectionUtils.invokeSetter(entity, field, value[i]);
			}
		}
	}

	/**
	 * 根据主键属性，判断主键是否值为空
	 * 
	 * @param entity
	 * @param field
	 * @return 主键为空，则返回false；主键有值，返回true
	 * @author 王浩彬
	 * @date 2016年4月28日
	 */
	public static <T> boolean isPKNotNull(T entity, String field) {
		if (!ReflectionUtils.hasField(entity, field)) {
			return false;
		}
		Object value = ReflectionUtils.getFieldValue(entity, field);
		return value != null && !"".equals(value);
	}

	/**
	 * 实体类转Map
	 * 
	 * @param object
	 * @return
	 */
	public static Map<String, Object> entityToMap(Object object) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field field : object.getClass().getDeclaredFields()) {
			try {
				boolean flag = field.isAccessible();
				field.setAccessible(true);
				Object o = field.get(object);
				map.put(field.getName(), o);
				field.setAccessible(flag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * Map转实体类
	 * 
	 * @param map
	 *            需要初始化的数据，key字段必须与实体类的成员名字一样，否则赋值为空
	 * @param entity
	 *            需要转化成的实体类
	 * @return
	 */
	public static <T> T mapToEntity(Map<String, Object> map, Class<T> entity) {
		T t = null;
		try {
			t = entity.newInstance();
			for (Field field : entity.getDeclaredFields()) {
				if (map.containsKey(field.getName())) {
					boolean flag = field.isAccessible();
					field.setAccessible(true);
					Object object = map.get(field.getName());
					if (object != null && field.getType().isAssignableFrom(object.getClass())) {
						field.set(t, object);
					}
					field.setAccessible(flag);
				}
			}
			return t;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}
}
