package com.github.wxiaoqi.security.common.util;

import java.util.Map;

public class VerifySign {

	/**
	 * 对参数信息进行顺序加密存储
	 * 
	 * @param params
	 * @param signKey
	 *            秘钥信息
	 * @return
	 */
	public static String signature(Map<String, Object> params, String signKey) {
		StringBuilder signature = new StringBuilder();
		params.forEach((k, v) -> {
			signature.append(v);
		});
		signature.append(signKey);
		return SecurityUtil.encryptSHA(signature.toString());
	}

	/**
	 * 对比加密串结果
	 * @param params
	 * @param signStr
	 * @param signKey
	 * @return
	 */
	public static boolean verify(Map<String, Object> params, String signStr, String signKey) {
		StringBuilder signature = new StringBuilder();
		params.forEach((k, v) -> {
			signature.append(v);
		});
		signature.append(signKey);
		String sign = SecurityUtil.encryptSHA(signature.toString());
		if (sign.equals(signStr)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 对参数信息进行顺序sha1加密存储
	 * 
	 * @param params
	 * @param signKey
	 *            秘钥信息
	 * @return
	 */
	public static String signatureSHA1(Map<String, Object> params, String signKey) {
		StringBuilder signature = new StringBuilder();
		params.forEach((k, v) -> {
			signature.append(v);
		});
		signature.append(signKey);
		return SecurityUtil.encryptSHA1(signature.toString());
	}

}
