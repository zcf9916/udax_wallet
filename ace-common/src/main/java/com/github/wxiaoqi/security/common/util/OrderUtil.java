package com.github.wxiaoqi.security.common.util;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.util.generator.IdGenerator;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OrderUtil {



	/**
	 * 转币订单号
	 * 时间戳(毫秒)+两位随机数
	 * @return
	 */
	public static String  generateTransferCoinOrder(){
		LocalDateTime localDateTime = LocalDateTime.now();
		String currentTime = (localDateTime.toLocalDate().toString() + localDateTime.toLocalTime().toString()).replaceAll("[^0-9]","");
		Random ran = new Random();
		int  random = ran.nextInt(90)  + 10;
		//时间戳+2位随机数
		String orderNo = Constants.OrderPreFix.TRANSFER_COIN + currentTime + "" + random;
		return orderNo;
	}


	/**
	 * 基金订单号
	 * 时间戳(毫秒)+两位随机数
	 * @return
	 */
	public static Long  generateFundOrder(){
		LocalDateTime localDateTime = LocalDateTime.now();
		String currentTime = (localDateTime.toLocalDate().toString() + localDateTime.toLocalTime().toString()).replaceAll("[^0-9]","");
		Random  ran = new Random();
		int  random = ran.nextInt(90)  + 10;
		//时间戳+2位随机数
		Long orderNo = Long.parseLong(currentTime + "" + random);
		return orderNo;
	}

	/**
	 * 钱包订单号
	 * 时间戳(毫秒)+两位随机数
	 * @return
	 */
	public static String  generateMchOrder(){

		return Constants.OrderPreFix.MCH + IdGenerator.nextId();
	}


	/**
	 * 转账订单号
	 * 时间戳(毫秒)+两位随机数
	 * @return
	 */
	public static String  generateTransferOrder(){
		LocalDateTime localDateTime = LocalDateTime.now();
		String currentTime = (localDateTime.toLocalDate().toString() + localDateTime.toLocalTime().toString()).replaceAll("[^0-9]","");
		Random ran = new Random();
		int  random = ran.nextInt(90)  + 10;
		//时间戳+2位随机数
		String orderNo = Constants.OrderPreFix.TRANSFER + currentTime + "" + random;
		return orderNo;
	}

}
