package com.github.wxiaoqi.security.common.util;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.*;
import com.github.wxiaoqi.security.common.entity.common.BaseSms;
import com.github.wxiaoqi.security.common.entity.common.Email;
import com.github.wxiaoqi.security.common.entity.common.MailHelper;
import com.github.wxiaoqi.security.common.entity.common.SendMsg;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.EmailTemplateType;
import com.github.wxiaoqi.security.common.enums.SendMsgType;
import com.github.wxiaoqi.security.common.enums.ValidCodeType;
import com.github.wxiaoqi.security.common.enums.VerificationType;
import com.github.wxiaoqi.security.common.mapper.admin.*;
import com.github.wxiaoqi.security.common.support.Assert;
import com.github.wxiaoqi.security.common.support.security.coder.MDCoder;
import com.github.wxiaoqi.security.common.util.generator.IdWorker;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 发送短信或邮件工具类
 *
 * @author Tang
 * @since 2019-03-19
 */
@Component
@Lazy(false)
public class SendUtil implements ApplicationContextAware {

    protected final static Logger logger = LogManager.getLogger();
    private static final String defaultDictValue = "1";
    private static final Long defaultWhiteExchId = 6L; //线上UDX白标Id为6
    static ApplicationContext applicationContext;
    private static SendUtil sendUtil;

    /** SMTP服务协议有各种限制,在新的限制，最多三个并发连接允许在同一时间发送电子邮件。
     * 如果应用程序尝试使用多个连接在同一时间发送三个以上的消息时，每个连接将收到发送方线程越界错误
     * 所以在此用单线程化的线程池排队发送,防止并发错误,漏掉邮件*/
    //private static ExecutorService excutorService = Executors.newCachedThreadPool();
    private static ExecutorService excutorService = Executors.newFixedThreadPool(3);//3个定长线程执行任务


    @Autowired
    private KeyConfiguration keyConfiguration;

    @PostConstruct
    public void init() {
        sendUtil = this;
        this.keyConfiguration = keyConfiguration;
        // 缓存是否测试环境变量
        CacheUtil.getCache().set(Constants.CommonType.IF_TEST_ENV,
                this.keyConfiguration.getIfTestEnv() == null ? false : this.keyConfiguration.getIfTestEnv());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SendUtil.applicationContext = applicationContext;
    }

    /**
     * 短信和邮件整合发送方法
     *
     * @param msgType      短信发送场景类型,参考枚举类SendMsgType
     * @param emailTemType 邮件模板枚举类型,参考枚举类EmailTemplateType
     * @param dictValue    数据字典键值,通过键值选择相同字典类型下不同内容,从页面下拉获取,若为空则为null"
     * @param params       params为获取短信内容中转义符s%所需替换的参数，内容根据发送类型，作为数据字典的类型，取字典对应的内容
     */
    public static void sendSmsOrEmail(String msgType, String emailTemType, FrontUser user, String dictValue,
                                      Object... params) {
        try {

            String emailContent = getContentByDictData(emailTemType,
                    getExchangeById(user.getUserInfo().getExchangeId()).getLanguage(), dictValue);

            String emailTitle = getContentByDictData(emailTemType + Constants.DirtTypeConstant.TITLE,
                    getExchangeById(user.getUserInfo().getExchangeId()).getLanguage(), dictValue);

            emailContent = String.format(emailContent, params);

            if (VerificationType.OPEN.value() == user.getUserInfo().getIsValidPhone()) {// 如果开启了手机验证,发送短信通知

                String msgKey = SendMsgType.valueOfMsgType(msgType).name();// 枚举类型值,对应字典类型值
                // 根据短信发送类型值，获取短信发送内容
                String smsContent = getContentByDictData(msgKey,
                        getExchangeById(user.getUserInfo().getExchangeId()).getLanguage(), dictValue);

                if (StringUtils.isBlank(smsContent)) {// 若为空则说明短信内容和邮件内容相同,取邮件内容值
                    smsContent = String.format(emailContent, params);
                } else {
                    smsContent = String.format(smsContent, params);
                }

                // 调用短信发送方法
                sendSmsWithContent(msgType, user.getUserInfo().getLocationCode() + "-" + user.getMobile(), smsContent,
                        user.getUserInfo().getExchangeId());
            }
            if (VerificationType.OPEN.value() == user.getUserInfo().getIsValidEmail()) {// 如果开启了邮箱验证,发送邮箱通知
                // 调用邮件发送方法
                sendEmailRequest(emailTemType, user.getEmail(), emailContent, user.getUserInfo().getExchangeId(),
                        emailTitle);
            }
        } catch (Exception e) {
            logger.error("SendUtil.sendSmsOrEmail错误" + e.getMessage(), e);
        }
    }

    /**
     * 邮件发送公共方法
     *
     * @param emailTemType 邮件模板枚举类型,参考枚举类EmailTemplateType
     * @param emailTo      用户邮箱账号
     * @param whiteExchId  白标（交易所）Id
     * @param dictValue    数据字典键值,通过键值选择相同字典类型下不同内容,从页面下拉获取,若为空则为null"
     * @param params       params为获取短信内容所需的参数，内容根据msgType，作为数据字典的类型，取字典对应的内容
     */
    public static void sendEmail(String emailTemType, String emailTo, Long whiteExchId, String dictValue,
                                 Object... params) {
        try {

            String emailContent = getContentByDictData(emailTemType, getExchangeById(whiteExchId).getLanguage(),
                    dictValue);

            String emailTitle = getContentByDictData(emailTemType + Constants.DirtTypeConstant.TITLE,
                    getExchangeById(whiteExchId).getLanguage(), dictValue);

            emailContent = String.format(emailContent, params);

            sendEmailRequest(emailTemType, emailTo, emailContent, whiteExchId, emailTitle);
        } catch (Exception e) {
            logger.error("SendUtil.sendEmail错误" + e.getMessage(), e);
        }
    }

    /**
     * 短信发送方法
     *
     * @param msgType     短信发送场景类型,参考枚举类SendMsgType
     * @param phone       区号-手机,格式 如"86-137*****"*
     * @param whiteExchId 交易所id，根据交易所ID获取数据字典对应的短信内容"
     * @param dictValue   数据字典键值,通过键值选择相同字典类型下不同内容,从页面下拉获取,若为空则为null"
     * @param params      params为获取短信内容所需的参数，内容根据msgType，作为数据字典的类型，取字典对应的内容
     */
    public static void sendSms(String msgType, String phone, Long whiteExchId, String dictValue, Object... params) {
        try {
            String key = SendMsgType.valueOfMsgType(msgType).name();// 枚举类型值,对应字典类型值
            String smsContent = getContentByDictData(key, getExchangeById(whiteExchId).getLanguage(), dictValue);
            smsContent = String.format(smsContent, params);
            sendSmsWithContent(msgType, phone, smsContent, whiteExchId);
        } catch (Exception e) {
            logger.error("SendUtil.sendSms错误" + e.getMessage(), e);
        }
    }

    /**
     * 发送短信验证码(只适用发送短信验证码功能,因为发送内容一样)
     *
     * @param msgType      短信发送场景类型,参考枚举类SendMsgType
     * @param locationCode 手机区号-,格式 如"86"*
     * @param mobile       手机号,格式 如"137*****"*
     * @param whiteExchId  交易所id，根据交易所ID获取数据字典对应的短信内容"
     */
    public static void sendVerifyCodeBySms(String msgType, String locationCode, String mobile, Long whiteExchId) {
        try {

            String sendMobile = locationCode + "-" + mobile;
            SendMsg sendMsg = new SendMsg();
            sendMsg.setMsgType(ValidCodeType.PHONE_CODE.value());
            sendMsg.setBizType(SendMsgType.valueOfMsgType(msgType).value());
            sendMsg.setPhone(sendMobile);
            String randomCode = "111111"; // 测试环境验证码默认111111
            if (!sendUtil.keyConfiguration.getIfTestEnv()) {
                randomCode = String.valueOf(RandomUtils.nextInt(123456, 999999));
            }
            sendMsg.setLocationCode(locationCode);
            sendMsg.setSmsCode(randomCode);
            // CacheBizUtil.setCacheSmgMsg(mobile,sendMsg);
            // 缓存发送验证码
            CacheUtil.getCache().set(Constants.CommonType.SMSCODE + mobile, JSON.toJSONString(sendMsg), 300);

            String key = SendMsgType.USER_REG.name();// 因为验证码内容一样，所以取注册时候发送验证码内容
            String smsContent = getContentByDictData(key, getExchangeById(whiteExchId).getLanguage(), null);
            smsContent = String.format(smsContent, randomCode);

            // 发送验证码
            sendSmsWithContent(msgType, sendMobile, smsContent, whiteExchId);
        } catch (Exception e) {
            logger.error("SendUtil.sendVerifyCodeBySms错误" + e.getMessage(), e);
        }
    }

    private static void sendSmsWithContent(String msgType, String phone, String smsContent, Long whiteExchId) {
        try {
            if (!sendUtil.keyConfiguration.getIfTestEnv()) {// 线上环境才发短信
                SendMsg sendMsg = new SendMsg();
                sendMsg.setMsgType(ValidCodeType.PHONE_CODE.value());// 参考枚举类ValidCodeType,此参数表示发送的是短信，在此并无实际意义
                sendMsg.setBizType(msgType);
                if (phone.indexOf("-") != -1) {
                    String[] localPhone = phone.split("-");
                    sendMsg.setLocationCode(localPhone[0]);
                    sendMsg.setPhone(localPhone[1]);
                }
                sendMsg.setContent(smsContent);
                BaseSmsConfig smsConfig = getSmsConfig(whiteExchId);// 获取短信配置
                sendSmsRequet(sendMsg, smsConfig);// 发送短信
            }
        } catch (Exception e) {
            logger.error("SendUtil.sendSmsWithContent错误" + e.getMessage(), e);
        }

    }

    private static void sendEmailRequest(String emailTemType, String emailTo, String mailContent, Long whiteExchId,
                                         String title) {
        try {
            // 获取邮件相关配置
            BaseEmailConfig emailConfig = getEmailConfig(whiteExchId);
            BaseEmailTemplate emailTemplate = getEmailTemplate(emailTemType, whiteExchId, title);

            if (emailConfig != null) {
                Email email = new Email();
                // 发送人账号密码,发送和收件人邮箱
                email.setName(emailConfig.getSenderName());
                email.setPassword(emailConfig.getSenderPassword());
                email.setFrom(emailConfig.getSenderAccount());
                email.setSendTo(emailTo);
                email.setHost(emailConfig.getSmtpHost());
                email.setPort(emailConfig.getSmtpPort());
                // 邮件主题和内容
                Map<String, String> temParams = new HashMap<String, String>();
                temParams.put("MailContent", mailContent);
                email.setBody(StringUtils.isNotEmpty(emailTemplate.getTemplate())
                        ? MailHelper.replaceTemplateInfo(emailTemplate.getTemplate(), temParams)
                        : mailContent);
                email.setTopic(StringUtils.isNotBlank(title) ? title : emailTemplate.getEmailTitle());
                excutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        MailHelper.sendMailRequest(email);
                    }
                });
            }

        } catch (Exception e) {
            logger.error("SendUtil.sendEmail()错误" + e.getMessage(), e);
        }
    }

    /**
     * 发送邮件提醒提币审核人员和用户审核人员
     *
     *  @param emailTemType 邮件模板枚举类型,参考枚举类EmailTemplateType
     *  @param whiteExchId 白标Id
     * @param params
     */
    @SuppressWarnings("unchecked")
    public static void sendEmailNotice(String emailTemType,Long whiteExchId, Object... params) {
        try {
            // 查出所有审核管理人员的信息
            HashMap<String, List<BaseEmailAuditor>> emailMap = (HashMap<String, List<BaseEmailAuditor>>) CacheUtil
                    .getCache().get(Constants.CacheServiceType.EmailAuditor + ":map");

            // 缓存没有从DB获取数据
            if (emailMap == null || emailMap.isEmpty()) {
                BaseEmailAuditorMapper emailAuditorMapper = applicationContext.getBean(BaseEmailAuditorMapper.class);
                List<BaseEmailAuditor> emailAuditorList = null;
                emailMap = new HashMap<String, List<BaseEmailAuditor>>();
                // 初始化时将把对象设置进缓存
                emailAuditorList = emailAuditorMapper.selectAll();
                if (StringUtil.listIsNotBlank(emailAuditorList)) {
                    for (BaseEmailAuditor emailAuditor : emailAuditorList) {
                        if (emailMap.containsKey(emailAuditor.getAuditorRole())) {
                            List<BaseEmailAuditor> emailList = emailMap.get(emailAuditor.getAuditorRole());
                            emailList.add(emailAuditor);
                        } else {
                            List<BaseEmailAuditor> emailList = new ArrayList<BaseEmailAuditor>();
                            emailList.add(emailAuditor);
                            emailMap.put(emailAuditor.getAuditorRole(), emailList);
                        }
                    }
                }
                CacheUtil.getCache().set(Constants.CacheServiceType.EmailAuditor + ":map", emailMap);
            }

            BaseEmailAuditor toAuditor = null;
            if (emailMap.containsKey(emailTemType)) {
                List<BaseEmailAuditor> managerList = emailMap.get(emailTemType);
                if (StringUtil.listIsNotBlank(managerList)) {
                    for (BaseEmailAuditor emailAuditor : managerList) {
                        if (null != emailAuditor && whiteExchId.equals(emailAuditor.getWhiteExchId())) {
                            toAuditor = emailAuditor;

                        }
                    }
                }
            }
            if(toAuditor!=null&&StringUtils.isNotBlank(toAuditor.getEmailAccount())){//多个账户以逗号分隔
                String mailContent = toAuditor.getEmailContent();
                mailContent = String.format(mailContent, params);
                String []EmailAccountArr = toAuditor.getEmailAccount().split(",");
                for(String emailAccount:EmailAccountArr){
                    // 调用邮件发送方法
                    sendEmailRequest(emailTemType, emailAccount, mailContent,whiteExchId, toAuditor.getEmailTitle());
                }
            }
        } catch (Exception e) {
            logger.error("推送审核信息到接收人邮箱时异常:", e);
        }
    }

    private static void sendSmsRequet(SendMsg sendMsg, BaseSmsConfig smsConfig) throws Exception {
        if(smsConfig == null){
            return;
        }
        Map<String, Object> formMap = new HashMap<String, Object>();
        if (smsConfig.getWhiteExchId() == 6 || smsConfig.getWhiteExchId() == 13) { // 6为UDX  13为澳门王者精英
            String timestamp = DateUtil.format(new Date(), DateUtil.DATE_PATTERN.YYYYMMDDHHMMSS);
            String sign = MDCoder.MD5Encode(smsConfig.getSmsPlatAccount() + smsConfig.getSmsPlatPassword() + timestamp,
                    "UTF-8");
            formMap.put("userid", "19379");
            formMap.put("timestamp", timestamp);
            formMap.put("sign", sign);
            formMap.put("mobile", sendMsg.getPhone());
            formMap.put("content", smsConfig.getSenderSignature() + sendMsg.getContent());
            formMap.put("sendTime", "");
            formMap.put("action", "send");
            formMap.put("extno", "");
            HttpUtils.asynpostForm(smsConfig.getSmsPlatUrl(), formMap);
        }

        // 新增短信记录
        BaseSms record = new BaseSms();
        record.setBizId(IdWorker.get32UUID());
        record.setSendState("1");
        record.setType(sendMsg.getBizType());
        record.setPhone(sendMsg.getLocationCode() + "-" + sendMsg.getPhone());
        record.setContent(sendMsg.getContent());
        record.setUpdTime(new Date());
        record.setCrtTime(new Date());
        BaseSmsMapper baseSmsMapper = applicationContext.getBean(BaseSmsMapper.class);
        //EntityUtils.setCreatAndUpdatInfo(record);
        baseSmsMapper.insertSelective(record);
    }

    /**
     * 根据国家编码获取短信配置
     *
     * @param whiteExchId 交易所Id
     */
    private static BaseSmsConfig getSmsConfig(Long whiteExchId) throws Exception {
        String key = Constants.CacheServiceType.SmsConfig + whiteExchId;
        BaseSmsConfig config = null;
        if (CacheUtil.getCache().exists(key)) {
            config = (BaseSmsConfig) CacheUtil.getCache().get(Constants.CacheServiceType.SmsConfig + whiteExchId);
        }
        if (config == null) {// 缓存没有查数据库
            BaseSmsConfigMapper smsConfigMapper = applicationContext.getBean(BaseSmsConfigMapper.class);
            List<BaseSmsConfig> configList = smsConfigMapper.selectAll();
            if (configList.isEmpty()) {
                throw new RuntimeException("缺少短信平台配置.");
            }
            for(BaseSmsConfig smsConfig : configList){
                if(whiteExchId.equals(smsConfig.getWhiteExchId())){
                    config = smsConfig;
                }
            }
            if(config == null){
                logger.error("当前交易所缺少短信平台配置,交易所id为："+whiteExchId);
            }
        }
        return config;
    }

    /**
     * 根据交易所获取邮件模板
     *
     * @param whiteExchId 用户邮箱账号
     */
    private static BaseEmailTemplate getEmailTemplate(String emailTemType, Long whiteExchId, String emailTitle)
            throws Exception {
        BaseEmailTemplateMapper emailTemplateMapper = applicationContext.getBean(BaseEmailTemplateMapper.class);
        List<BaseEmailTemplate> emailTemplateList = (List<BaseEmailTemplate>) CacheUtil.getCache()
                .get(Constants.CacheServiceType.EmailTemplate + ":list");
        if (StringUtil.listIsBlank(emailTemplateList)) {// 缓存没有查数据库
            emailTemplateList = emailTemplateMapper.selectAll();
            // 将list放入缓存
            CacheUtil.getCache().set(Constants.CacheServiceType.EmailTemplate + ":list",
                    (ArrayList<BaseEmailTemplate>) emailTemplateList);
        }
        if (StringUtil.listIsBlank(emailTemplateList)) {
            throw new IllegalArgumentException(Resources.getMessage("PARAM_IS_NULL", "EmailTemplate"));
        }

        if (whiteExchId == null) {
            whiteExchId = defaultWhiteExchId;// 若为空，取默认交易所的配置
        }
        BaseEmailTemplate returnTemplate = null;
        for (BaseEmailTemplate emailTemplate : emailTemplateList) {
            if (emailTemType.equals(emailTemplate.getTemplateName())) { // whiteExchId.equals(emailTemplate.getWhiteExchId())
                if (whiteExchId.equals(emailTemplate.getWhiteExchId())) {
                    Assert.isNotBlank_V2(emailTemplate.getTemplate(), "EmailTemplate");
                    returnTemplate = emailTemplate;
                }
            }
        }
        if (returnTemplate == null) {// 后台没有自定义模板,则用通用模板
            for (BaseEmailTemplate emailTemplate : emailTemplateList) {
                if (EmailTemplateType.COMMON_TEMPLATE.value().equals(emailTemplate.getTemplateName())) { // whiteExchId.equals(emailTemplate.getWhiteExchId())
                    if (whiteExchId.equals(emailTemplate.getWhiteExchId())) {
                        Assert.isNotBlank_V2(emailTemplate.getTemplate(), "EmailTemplate");
                        returnTemplate = emailTemplate;
                    }
                }
            }
        }
        if (returnTemplate == null) {// 当前交易所没有配任何模板，则取集合第一个模板
            Assert.isNotBlank_V2(emailTemplateList.get(0).getTemplate(), "EmailTemplate");
            returnTemplate = emailTemplateList.get(0);
        }

        // 后台不强制录入邮件标题,若未填写则用通用的模板,邮件标题取数据字典里定义的方法
        if (returnTemplate != null && StringUtils.isBlank(returnTemplate.getEmailTitle())) {
            returnTemplate.setEmailTitle(emailTitle);
        }
        return returnTemplate;
    }

    /**
     * 根据交易所获取邮件配置
     *
     * @param whiteExchId 用户邮箱账号
     */
    private static BaseEmailConfig getEmailConfig(Long whiteExchId) throws Exception {
        BaseEmailConfigMapper emailConfigmapper = applicationContext.getBean(BaseEmailConfigMapper.class);
        List<BaseEmailConfig> emailConfigList = (List<BaseEmailConfig>) CacheUtil.getCache()
                .get(Constants.CacheServiceType.EmailConfig + ":list");
        if (StringUtil.listIsBlank(emailConfigList)) {// 缓存没有查数据库
            emailConfigList = emailConfigmapper.selectAll();
            // 将list放入缓存
            CacheUtil.getCache().set(Constants.CacheServiceType.EmailConfig + ":list",
                    (ArrayList<BaseEmailConfig>) emailConfigList);
        }
        if (StringUtil.listIsBlank(emailConfigList)) {
            throw new IllegalArgumentException(Resources.getMessage("PARAM_IS_NULL", "EmailConfig"));
        }

        if (whiteExchId == null) {
            whiteExchId = defaultWhiteExchId;// 若为空，取默认交易所的配置
        }
        BaseEmailConfig returnEmailConfig = null;
        for (BaseEmailConfig emailConfig : emailConfigList) {
            if (whiteExchId.equals(emailConfig.getWhiteExchId())) {
                Assert.isNotBlank_V2(emailConfig.getSmtpHost(), "smtpHost");
                Assert.isNotBlank_V2(emailConfig.getSmtpPort(), "smtpPort");
                Assert.isNotBlank_V2(emailConfig.getSenderAccount(), "sendAccout");
                Assert.isNotBlank_V2(emailConfig.getSenderPassword(), "sendPassword");
                returnEmailConfig = emailConfig;
            }
        }

        if (returnEmailConfig == null) {// 默认取集合第一个
            Assert.isNotBlank_V2(emailConfigList.get(0).getSmtpHost(), "smtpHost");
            Assert.isNotBlank_V2(emailConfigList.get(0).getSmtpPort(), "smtpPort");
            Assert.isNotBlank_V2(emailConfigList.get(0).getSenderAccount(), "sendAccout");
            Assert.isNotBlank_V2(emailConfigList.get(0).getSenderPassword(), "sendPassword");
            returnEmailConfig = emailConfigList.get(0);
        }
        return returnEmailConfig;
    }

    private static WhiteExchInfo getExchangeById(Long id) {

        // 根据交易所Id缓存对象
        WhiteExchInfo whiteExchInfo = (WhiteExchInfo) CacheUtil.getCache()
                .get(Constants.CacheServiceType.WHITE_EXCH_INFO + id);

        if (whiteExchInfo == null) {// 从DB取
            WhiteExchInfoMapper whiteExchInfoMapper = applicationContext.getBean(WhiteExchInfoMapper.class);
            whiteExchInfo = whiteExchInfoMapper.selectByPrimaryKey(id);
        }
        return whiteExchInfo;
    }

    private static String getContentByDictData(String key, String language, String dictValue) {
        try {
            if (StringUtils.isBlank(dictValue)) {
                dictValue = defaultDictValue;// 字典键值默认为1
            }
            if (key.contains(Constants.DirtTypeConstant.TITLE)) {// 取标题内容,字典键值默认为1
                dictValue = defaultDictValue;// 字典键值默认为1
            }
            String content = null;
            // 根据 字典类型+字典键值+ 语言
            DictData dictData = (DictData) CacheUtil.getCache()
                    .get(Constants.CacheServiceType.DICT_DATA_DATA + key + dictValue + language);
            if (dictData == null) {// 从db取值
                DictDataMapper dictDataMapper = applicationContext.getBean(DictDataMapper.class);
                Example example = new Example(DictData.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("dictType", key);
                criteria.andEqualTo("dictValue", dictValue);
                criteria.andEqualTo("languageType", language);
                List<DictData> list = dictDataMapper.selectByExample(example);
                if (StringUtil.listIsNotBlank(list)) {
                    content = list.get(0).getRemark();
                    // 若描述信息为空,则取字典标签的值
                    content = StringUtils.isNotBlank(content) ? content : list.get(0).getDictLabel();
                }
            } else {
                content = dictData.getRemark();
                // 若描述信息为空,则取字典标签的值
                content = StringUtils.isNotBlank(content) ? content : dictData.getDictLabel();
            }
            return content;
        } catch (Exception e) {
            logger.error("调用getContentByDictData错误" + e.getMessage(), e);
            return null;
        }
    }

    //用户提币或者区块链提币异常时,向钉钉APP群推送及时消息
    public static void noticeManager(String templateType,String userName){
        if (!sendUtil.keyConfiguration.getIfTestEnv()) {
            HashMap<String, List<BaseEmailAuditor>> emailMap= (HashMap<String, List<BaseEmailAuditor>>) CacheUtil.getCache().get(Constants.CacheServiceType.EmailAuditor + ":map");
            String message=null;
            if (StringUtil.mapIsBlank(emailMap) && emailMap.get(templateType).size()==0){
                //未获取到配置的发送内容
                if (EmailTemplateType.WITHDRAW_AUDIT_REMIND.value().equals(templateType)){
                    //审核通知
                    message="管理员您好,用户"+userName+"已经发起提币申请,请及时审核,谢谢。";
                }else {
                    message="管理员您好,用户"+userName+"的提币请求区块链处理异常,请到[用户提现管理]重新审核,谢谢。";
                }
                logger.error("用户"+userName+"提币获取钉钉通知内容失败!!");
            }else {
                message=String.format(emailMap.get(templateType).get(0).getEmailContent(),userName);
            }
            //获取钉钉请求URL
            String dingDingToken = getContentByDictData(Constants.DirtTypeConstant.NOTICE_MANAGER, "zh", "1");
            if (StringUtils.isNotBlank(dingDingToken)){
                Map<String,Object> json=new HashMap();
                Map<String,Object> text=new HashMap();
                json.put("msgtype","text");
                text.put("content","wallet :"+message);
                json.put("text",text);
                // 发送post请求
                String response = SendHttps.sendPostByMap(dingDingToken, json);
                System.out.println(response);
            }
        }
    }

    public static void main(String[] args) {
        String timestamp = DateUtil.format(new Date(), DateUtil.DATE_PATTERN.YYYYMMDDHHMMSS);
        String sign;
        try {
            sign = MDCoder.MD5Encode("liu2211003liu11111111" + timestamp, "UTF-8");
            Map<String, Object> formMap = new HashMap<String, Object>();
            formMap.put("userid", "19379");
            formMap.put("timestamp", timestamp);
            formMap.put("sign", sign);
            formMap.put("mobile", "15989507323");
            formMap.put("content", "【Vision】尊敬的zz您好,由于您的身份证明图片太小无法看清，你的身份认证未通过，请重新提交资料。");
            formMap.put("sendTime", "");
            formMap.put("action", "send");
            formMap.put("extno", "");
            System.out.println(HttpUtils.postForm("http://123.57.51.191:8888/V2sms.aspx", formMap));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}
