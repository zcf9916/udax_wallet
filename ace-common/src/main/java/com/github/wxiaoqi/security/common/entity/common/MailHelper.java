package com.github.wxiaoqi.security.common.entity.common;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.wxiaoqi.security.common.support.Assert;
/**
 * 
 * @ClassName: MailHelper
 * @Desc: TODO
 * @author: zhoucf
 * @date: 2018年5月23日 下午1:55:11
 * @version 1.0
 */
public class MailHelper {

	protected static final Logger logger = LogManager.getLogger(MailHelper.class);
	
	public static void sendMailRequest(Email email) {
		try {
			// 验证信息
			Assert.email(email.getSendTo());
			Assert.email(email.getFrom());
			Assert.isNotBlank(email.getPassword(), "SenderPasswd");
			Assert.isNotBlank(email.getName(), "Name");
			//2、创建定义整个应用程序所需的环境信息的 Session 对象
	        Session	session = Session.getInstance(getProp(email));
			// 设置调试信息在控制台打印出来 
			 session.setDebug(false);
			// 3、创建邮件的实例对象
			Message msg = getMimeMessage(session, email);
			// 4、根据session对象获取邮件传输对象Transport
			Transport transport = session.getTransport();
			// 设置发件人的账户名和密码
			transport.connect(email.getName(), email.getPassword());
			transport.sendMessage(msg, msg.getAllRecipients());
			// 监听邮件是否发送成功
			transport.close();
		} catch (Exception e) {
			logger.error("发送邮件失败:" + email.getSendTo(), e);
		}

	}

	/**
	 * 
	 * @Title: getProp
	 * @Desc: 设置基本配置并返回
	 * @param email
	 * @return
	 */
	private static Properties getProp(Email email) {
		Properties props = new Properties();
		// 设置用户的认证方式
		props.setProperty("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		// 设置传输协议
		props.setProperty("mail.transport.protocol", "smtp");
		// 设置发送邮件端口号 25
		props.put("mail.smtp.port", email.getPort());
		// 设置发件人的SMTP服务器地址 smtp.XXX.com
		props.setProperty("mail.smtp.host", email.getHost());
		return props;
	}

	/**
	 * 获得创建一封邮件的实例对象
	 * 
	 * @param session
	 * @return
	 * @throws MessagingException
	 * @throws AddressException
	 */
	private static MimeMessage getMimeMessage(Session session, Email email) throws Exception {
		// 创建一封邮件的实例对象
		MimeMessage msg = new MimeMessage(session);
		// 设置发件人地址
		msg.setFrom(new InternetAddress(email.getFrom()));
		/**
		 * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行 MimeMessage.RecipientType.TO:发送
		 * MimeMessage.RecipientType.CC：抄送 MimeMessage.RecipientType.BCC：密送
		 */
		msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email.getSendTo()));
		
		/* 设置多个收件人
		 * for(int i=0;i<50;i++) { 
		 * msg.addRecipient(MimeMessage.RecipientType.TO,new
		 * InternetAddress("294816099@qq.com"));
		 * msg.addRecipient(MimeMessage.RecipientType.TO,new
		 * InternetAddress("1583607313@qq.com")); }
		 */
		
		// 设置邮件主题
		msg.setSubject(email.getTopic(), "UTF-8");
		// 设置邮件正文
		msg.setContent(email.getBody(), "text/html;charset=UTF-8");
		// 设置邮件的发送时间,默认立即发送
		msg.setSentDate(new Date());

		return msg;
	}

	/**
	 * 格式化模版内容
	 * @param template
	 * @param params
	 * @return
	 */
	public static String replaceTemplateInfo(String template, Map<String,String> params){
		for(String key : params.keySet()){
			String pattern = "{"+key+"}";
			template = template.replaceAll("\\{"+key+"\\}",params.get(key));
		}
		return template;
	}
}
