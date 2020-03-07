

-- ----------------------------
-- Table structure for base_dept
-- ----------------------------
DROP TABLE IF EXISTS `base_dept`;
CREATE TABLE `base_dept` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL,
  `pids` varchar(50) DEFAULT NULL,
  `simple_name` varchar(50) DEFAULT NULL,
  `full_name` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_sort` (`sort`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of base_dept
-- ----------------------------
INSERT INTO `base_dept` VALUES ('1', null, null, 'Development', '开发部', '开发部', null, '1', '2019-02-21 14:32:52', null, '2019-04-19 15:47:16', 'Tian');


-- ----------------------------
-- Table structure for base_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `base_dict_data`;
CREATE TABLE `base_dict_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dict_id` varchar(64) NOT NULL COMMENT '字典类型ID',
  `sort` int(11) NOT NULL COMMENT '排序号',
  `dict_label` varchar(500) NOT NULL COMMENT '字典标签',
  `dict_value` varchar(100) NOT NULL COMMENT '字典键值',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
  `language_type` varchar(100) DEFAULT NULL,
  `status` tinyint(4) NOT NULL COMMENT '1.正常;0.停用',
  `crt_time` datetime DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(128) DEFAULT NULL,
  `crt_name` varchar(128) DEFAULT NULL,
  `remark` varchar(128) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `joint_index` (`dict_value`,`dict_type`,`language_type`),
  KEY `index_status` (`status`) USING BTREE,
  KEY `index_sort` (`sort`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- Records of base_dict_data
-- ----------------------------
INSERT INTO `base_dict_data` VALUES ('1', '1', '1', '中文', 'zh', 'language', '', '1', '2019-03-15 17:56:37', '2019-03-15 19:25:09', 'Tian', 'Tian', '测试');
INSERT INTO `base_dict_data` VALUES ('2', '1', '2', 'English', 'en', 'language', '', '1', '2019-03-15 17:56:59', '2019-03-16 16:49:26', 'Tian', 'Tian', 'ss');
INSERT INTO `base_dict_data` VALUES ('3', '3', '1', '固定值', '1', 'charge_value', 'zh', '1', '2019-03-15 18:00:47', '2019-03-15 18:00:47', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('4', '3', '1', 'Transfer Summary', '1', 'charge_value', 'en', '1', '2019-03-15 18:01:14', '2019-05-17 06:03:00', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('5', '3', '3', '点差', '3', 'charge_value', 'zh', '1', '2019-03-15 18:01:33', '2019-03-19 20:44:51', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('6', '3', '3', 'Fixed Value', '3', 'charge_value', 'en', '1', '2019-03-15 18:01:47', '2019-05-17 06:03:10', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('7', '3', '2', '比例', '2', 'charge_value', 'zh', '1', '2019-03-15 18:02:11', '2019-03-19 20:45:00', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('8', '3', '2', 'Ratio', '2', 'charge_value', 'en', '1', '2019-03-15 18:02:32', '2019-05-17 06:03:23', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('9', '4', '1', 'ETH', 'ETH', 'symbol_list', null, '1', '2019-03-23 16:28:20', '2019-03-23 16:28:20', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('10', '4', '2', 'EOS', 'EOS', 'symbol_list', null, '1', '2019-03-23 16:28:44', '2019-03-23 16:28:44', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('11', '4', '3', 'UDT', 'UDT', 'symbol_list', null, '1', '2019-03-23 16:29:17', '2019-03-23 16:29:17', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('12', '5', '1', '身份证号与姓名不匹配', '1', 'User_Audit', 'zh', '1', '2019-03-27 15:24:47', '2019-04-23 17:00:54', 'Tang', 'Tian', '尊敬的%s你好,由于你的身份证号与姓名不匹配，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('13', '5', '1', 'ID Numbers and Name Do Not Match', '1', 'User_Audit', 'en', '1', '2019-03-27 15:24:58', '2019-05-17 06:03:43', 'Tian', 'Tian', '尊敬的%s你好,由于你的身份证号与姓名不匹配，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('14', '5', '2', '身份证号与国家不匹配', '2', 'User_Audit', 'zh', '1', '2019-03-27 15:25:16', '2019-04-23 17:01:12', 'Tang', 'Tian', '尊敬的%s你好,由于你的身份证号与国家不匹配，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('15', '5', '2', 'ID Numbers and Country Do Not Match', '2', 'User_Audit', 'en', '1', '2019-03-27 15:25:25', '2019-05-17 06:04:27', 'Tian', 'Tian', '尊敬的%s你好,由于你的身份证号与国家不匹配，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('16', '5', '3', '证件照不符合规范', '3', 'User_Audit', 'zh', '1', '2019-03-27 15:25:38', '2019-05-17 06:36:28', 'Tian', 'Tian', '尊敬的 %s 你好,由于证件照不符合规范，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('17', '5', '3', 'ID Card Is Not Valid', '3', 'User_Audit', 'en', '1', '2019-03-27 15:25:52', '2019-05-17 06:36:47', 'Tian', 'Tian', '尊敬的%s你好,由于 ID Card Is Not Valid，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('18', '5', '4', '资料信息不完善', '4', 'User_Audit', 'zh', '1', '2019-03-27 15:26:14', '2019-05-17 06:37:06', 'Tian', 'Tian', '尊敬的%s你好,由于你的资料信息不完善，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('19', '5', '4', 'Information Is Incomplete', '4', 'User_Audit', 'en', '1', '2019-03-27 15:26:25', '2019-05-17 06:37:10', 'Tian', 'Tian', '尊敬的%s你好,由于你的资料信息不完善，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('20', '5', '5', '证件号码不存在', '5', 'User_Audit', 'zh', '1', '2019-03-27 15:26:49', '2019-05-17 06:37:37', 'Tian', 'Tian', '尊敬的%s你好,由于你的证件号码不存在，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('21', '5', '5', 'ID Numbers Do Not Exist', '5', 'User_Audit', 'en', '1', '2019-03-27 15:27:00', '2019-05-17 06:37:41', 'Tian', 'Tian', '尊敬的%s你好,由于你的证件号码不存在，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('22', '5', '6', '身份证明图片太小无法看清', '6', 'User_Audit', 'zh', '1', '2019-03-27 15:27:13', '2019-05-17 06:37:56', 'Tian', 'Tian', '尊敬的%s你好,由于你的身份证明图片太小无法看清，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('23', '5', '6', 'Picture Of The ID Is Too Small To Clearly Identify', '6', 'User_Audit', 'en', '1', '2019-03-27 15:28:02', '2019-05-17 06:38:07', 'Tian', 'Tian', '尊敬的%s你好,由于你的身份证明图片太小无法看清，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('24', '5', '7', '上传的身份证资料被遮盖', '7', 'User_Audit', 'zh', '1', '2019-03-27 15:28:14', '2019-05-17 06:38:42', 'Tian', 'Tian', '尊敬的%s你好,由于上传的身份证资料被遮盖，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('25', '5', '7', 'The Uploaded ID Information Is Covered', '7', 'User_Audit', 'en', '1', '2019-03-27 15:28:34', '2019-05-17 06:38:46', 'Tian', 'Tian', '尊敬的%s你好,由于上传的身份证资料被遮盖，你的身份认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('26', '6', '1', '身份证号与姓名不匹配', '1', 'Merchant_Review', 'zh', '1', '2019-03-28 16:16:31', '2019-05-22 18:14:58', 'Tian', 'Tian', '尊敬的%s你好,由于你的身份证号与姓名不匹配，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('27', '6', '1', 'ID Numbers and Name Do Not Match', '1', 'Merchant_Review', 'en', '1', '2019-03-28 16:16:43', '2019-05-22 18:15:04', 'Tian', 'Tian', '尊敬的%s你好,由于你的身份证号与姓名不匹配，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('28', '6', '2', '营业执照图片太小无法看清', '2', 'Merchant_Review', 'zh', '1', '2019-03-28 16:17:53', '2019-05-22 18:15:08', 'Tian', 'Tian', '尊敬的%s你好,由于你的营业执照图片太小无法看清，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('29', '6', '2', 'Picture Of The Business License Is Too Small to Clearly Identify', '2', 'Merchant_Review', 'en', '1', '2019-03-28 16:18:03', '2019-05-22 18:15:12', 'Tian', 'Tian', '尊敬的%s你好,由于你的营业执照图片太小无法看清，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('30', '6', '3', '资料信息不完善', '3', 'Merchant_Review', 'zh', '1', '2019-03-28 16:18:20', '2019-05-22 18:15:16', 'Tian', 'Tian', '尊敬的%s你好,由于你的资料信息不完善，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('31', '6', '3', 'Information Is Incomplete', '3', 'Merchant_Review', 'en', '1', '2019-03-28 16:18:31', '2019-05-22 18:15:19', 'Tian', 'Tian', '尊敬的%s你好,由于你的资料信息不完善，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('32', '6', '4', '证件号码不存在', '4', 'Merchant_Review', 'zh', '1', '2019-03-28 16:18:48', '2019-05-22 18:15:23', 'Tian', 'Tian', '尊敬的%s你好,由于你的证件号码不存在，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('33', '6', '4', 'ID Numbers Do Not Exist', '4', 'Merchant_Review', 'en', '1', '2019-03-28 16:19:05', '2019-05-22 18:15:26', 'Tian', 'Tian', '尊敬的%s你好,由于你的证件号码不存在，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('34', '6', '5', '上传的营业执照资料被遮盖', '5', 'Merchant_Review', 'zh', '1', '2019-03-28 16:20:01', '2019-05-22 18:15:29', 'Tian', 'Tian', '尊敬的%s你好,由于上传的营业执照资料被遮盖，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('35', '6', '5', 'The Uploaded ID Information Is Covered', '5', 'Merchant_Review', 'en', '1', '2019-03-28 16:20:12', '2019-05-22 18:15:33', 'Tian', 'Tian', '尊敬的%s你好,由于上传的营业执照资料被遮盖，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('37', '6', '6', '营业执照不符合规范', '6', 'Merchant_Review', 'zh', '1', '2019-03-28 16:21:09', '2019-05-22 18:15:39', 'Tian', 'Tian', '尊敬的%s你好,由于上传营业执照不符合规范，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('38', '6', '6', 'ID Card Is Not Valid', '6', 'Merchant_Review', 'en', '1', '2019-03-28 16:21:20', '2019-05-22 18:15:43', 'Tian', 'Tian', '尊敬的%s你好,由于上传营业执照不符合规范，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('39', '6', '7', '法人名称与营业执照不匹配', '7', 'Merchant_Review', 'zh', '1', '2019-03-28 16:23:49', '2019-05-22 18:15:48', 'Tian', 'Tian', '尊敬的%s你好,由于你的法人名称与营业执照不匹配，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('40', '6', '7', 'The Juridical Name And Business Licese Do Not Match', '7', 'Merchant_Review', 'en', '1', '2019-03-28 16:24:15', '2019-05-22 18:15:52', 'Tian', 'Tian', '尊敬的%s你好,由于你的法人名称与营业执照不匹配，你的商户认证未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('41', '7', '1', '提币地址错误', '1', 'Withdraw_Backauth', 'zh', '1', '2019-04-01 16:26:40', '2019-05-22 18:17:02', 'Tian', 'Tian', '尊敬的%s你好,由于你的提币地址错误，你的提币未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('42', '7', '1', '提币地址错误', '1', 'Withdraw_Backauth', 'en', '1', '2019-04-01 16:26:56', '2019-05-22 18:17:08', 'Tian', 'Tian', '尊敬的%s你好,由于你的提币地址错误，你的提币未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('43', '7', '2', '交易数据异常', '2', 'Withdraw_Backauth', 'zh', '1', '2019-04-01 16:27:40', '2019-05-22 18:17:11', 'Tian', 'Tian', '尊敬的%s你好,由于你的交易数据异常，你的提币未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('44', '7', '2', '交易数据异常', '2', 'Withdraw_Backauth', 'en', '1', '2019-04-01 16:27:50', '2019-05-22 18:17:18', 'Tian', 'Tian', '尊敬的%s你好,由于你的交易数据异常，你的提币未通过，请重新提交资料。');
INSERT INTO `base_dict_data` VALUES ('54', '8', '1', '商家认证通知标题', '1', 'Merchant_Review_title', 'zh', '1', '2019-04-22 17:41:26', '2019-05-22 18:19:15', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('55', '8', '1', '商家认证通知标题 en', '1', 'Merchant_Review_title', 'en', '1', '2019-04-22 17:41:39', '2019-05-22 18:19:20', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('56', '9', '1', '用户实名认证通知', '1', 'User_Audit_title', 'zh', '1', '2019-04-22 17:41:53', '2019-04-23 17:17:23', 'Tang', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('57', '10', '1', '用户提现审核通知', '1', 'Withdraw_Backauth_title', 'zh', '1', '2019-04-22 17:42:23', '2019-05-22 18:20:06', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('58', '10', '1', 'User Cash Withdraw Verification Notification', '1', 'Withdraw_Backauth_title', 'en', '1', '2019-04-22 17:42:34', '2019-05-22 18:20:10', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('59', '9', '1', 'User’s Real Name Verification Notification', '1', 'User_Audit_title', 'en', '1', '2019-04-22 17:44:02', '2019-05-17 06:20:29', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('62', '11', '1', '您好，您的安全验证码为:%s<br>出于安全原因，该验证码将于10分钟后失效。请勿将验证码透露给他人。<br>如果您没有进行该操作，请立即修改登录密码。', '1', 'Register', 'zh', '1', '2019-04-23 10:44:32', '2019-04-23 10:56:16', 'Tang', 'Tang', null);
INSERT INTO `base_dict_data` VALUES ('63', '11', '1', 'Dear User,Your Security Code is :%s<br>This security code is valid for 10 mins. Please do not reveal this code to another person.<br>If you have not made this request, please login to your account and change your password immediately.', '1', 'Register', 'en', '1', '2019-04-23 10:53:03', '2019-04-23 10:56:20', 'Tang', 'Tang', null);
INSERT INTO `base_dict_data` VALUES ('64', '12', '1', '【UDAX】安全验证', '1', 'Register_title', 'zh', '1', '2019-04-23 11:01:41', '2019-04-23 11:01:41', 'Tang', 'Tang', null);
INSERT INTO `base_dict_data` VALUES ('65', '12', '1', '【UDAX】Safety verification', '1', 'Register_title', 'en', '1', '2019-04-23 11:03:01', '2019-04-23 17:36:16', 'Tang', 'Tang', null);
INSERT INTO `base_dict_data` VALUES ('67', '13', '2', '人民币(CNY)', 'CNY', 'fb_symbol', 'zh', '1', '2019-04-28 11:12:24', '2019-04-28 16:27:54', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('68', '13', '2', '人民币(CNY) en ', 'CNY', 'fb_symbol', 'en', '1', '2019-04-28 11:12:53', '2019-04-28 16:27:57', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('69', '13', '1', '美元(USD)', 'USD', 'fb_symbol', 'zh', '1', '2019-04-28 11:13:46', '2019-04-28 11:14:56', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('70', '13', '1', '美元(USD) en', 'USD', 'fb_symbol', 'en', '1', '2019-04-28 11:15:26', '2019-04-28 16:27:49', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('71', '13', '3', '韩元(KRW)', 'KRW', 'fb_symbol', 'zh', '1', '2019-04-28 11:16:05', '2019-04-28 11:16:05', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('72', '13', '3', '韩元(KRW) en', 'KRW', 'fb_symbol', 'en', '1', '2019-04-28 11:16:30', '2019-04-28 11:16:30', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('73', '13', '4', '日元(JPY)', 'JPY', 'fb_symbol', 'zh', '1', '2019-04-28 11:17:06', '2019-04-28 11:17:06', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('74', '13', '4', '日元(JPY) en', 'JPY', 'fb_symbol', 'en', '1', '2019-04-28 11:17:25', '2019-04-28 11:19:18', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('75', '13', '5', '欧元(EUR)', 'EUR', 'fb_symbol', 'zh', '1', '2019-04-28 11:17:59', '2019-04-28 11:17:59', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('76', '13', '5', '欧元(EUR) en', 'EUR', 'fb_symbol', 'en', '1', '2019-04-28 11:18:32', '2019-04-28 11:18:32', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('77', '4', '4', 'IFR', 'IFR', 'symbol_list', null, '1', '2019-05-27 17:12:04', '2019-05-27 17:13:12', 'Tian', 'Tian', null);
INSERT INTO `base_dict_data` VALUES ('81', '13', '6', 'INBIQ', 'INBIQ', 'fb_symbol', 'zh', '1', '2019-06-18 11:57:03', '2019-06-18 11:57:03', 'pubaoyu', 'pubaoyu', null);
INSERT INTO `base_dict_data` VALUES ('82', '13', '6', 'INBIQ', 'INBIQ', 'fb_symbol', 'en', '1', '2019-06-18 11:57:28', '2019-06-18 11:57:28', 'pubaoyu', 'pubaoyu', null);
INSERT INTO `base_dict_data` VALUES ('83', '15', '1', '通知用户充钱', '1', 'CommunityChargeNotice', 'zh', '1', '2019-06-24 11:58:57', '2019-06-24 11:58:57', 'Tang', 'Tang', '尊敬的%s你好,你参投的UD智能社区方案：%s即将到期，请及时进行下一轮的充值，超时账户将被锁定，请知悉，谢谢！');
INSERT INTO `base_dict_data` VALUES ('84', '15', '1', '通知用户充钱', '1', 'CommunityChargeNotice', 'en', '1', '2019-06-24 11:58:57', '2019-06-24 11:58:57', 'Tang', 'Tang', '尊敬的%s你好,你参投的UD智能社区方案：%s即将到期，请及时进行下一轮的充值，超时账户将被锁定，请知悉，谢谢！');
INSERT INTO `base_dict_data` VALUES ('85', '16', '1', 'UD社区方案到期通知', '1', 'CommunityChargeNotice_title', 'zh', '1', '2019-06-24 13:49:21', '2019-06-24 13:49:21', 'Tang', 'Tang', null);
INSERT INTO `base_dict_data` VALUES ('86', '16', '1', 'UD社区方案到期通知', '1', 'CommunityChargeNotice_title', 'en', '1', '2019-06-24 13:49:37', '2019-06-24 13:49:37', 'Tang', 'Tang', null);

-- ----------------------------
-- Table structure for base_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `base_dict_type`;
CREATE TABLE `base_dict_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dict_name` varchar(100) NOT NULL COMMENT '字典名称',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
  `status` tinyint(4) NOT NULL COMMENT '1.正常;0.停用',
  `crt_time` datetime DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(128) DEFAULT NULL,
  `crt_name` varchar(128) DEFAULT NULL,
  `remark` varchar(128) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dict_type` (`dict_type`),
  KEY `idx_dict_type_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- Records of base_dict_type
-- ----------------------------
INSERT INTO `base_dict_type` VALUES ('1', '语言', 'language', '1', '2019-03-14 17:26:48', '2019-03-14 18:00:03', 'Tian', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('3', '手续费类型', 'charge_value', '1', '2019-03-15 17:58:25', '2019-03-15 17:58:25', 'Tian', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('4', '公链代币列表', 'symbol_list', '1', '2019-03-23 16:26:37', '2019-03-23 16:26:37', 'Tian', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('5', '用户审核不通过原因', 'User_Audit', '1', '2019-03-27 15:24:18', '2019-04-23 16:37:32', 'Tang', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('6', '商家审核不通过原因', 'Merchant_Review', '1', '2019-03-28 16:02:08', '2019-05-22 18:14:51', 'Tian', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('7', '提币审核不通过原因', 'Withdraw_Backauth', '1', '2019-04-01 16:23:58', '2019-05-22 18:19:54', 'Tian', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('8', '商家认证通知标题(title)', 'Merchant_Review_title', '1', '2019-04-22 15:30:06', '2019-05-22 18:19:09', 'Tian', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('9', '用户实名认证通知(title)', 'User_Audit_title', '1', '2019-04-22 15:32:47', '2019-04-23 16:39:39', 'Tang', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('10', '用户提现审核通知(title)', 'Withdraw_Backauth_title', '1', '2019-04-22 15:33:55', '2019-05-22 18:20:01', 'Tian', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('11', '注册验证码通知', 'Register', '1', '2019-04-23 10:30:37', '2019-04-23 10:56:09', 'Tang', 'Tang', '邮件和短信注册，验证码通知内容');
INSERT INTO `base_dict_type` VALUES ('12', '验证码邮件标题', 'Register_title', '1', '2019-04-23 10:57:31', '2019-04-23 10:57:31', 'Tang', 'Tang', null);
INSERT INTO `base_dict_type` VALUES ('13', '法币币种', 'fb_symbol', '1', '2019-04-28 10:57:22', '2019-04-28 10:57:22', 'Tian', 'Tian', null);
INSERT INTO `base_dict_type` VALUES ('15', 'UD社区通知用户充钱', 'CommunityChargeNotice', '1', '2019-06-24 11:48:14', '2019-06-24 11:48:14', 'Tang', 'Tang', null);
INSERT INTO `base_dict_type` VALUES ('16', 'UD社区通知用户充钱(邮件标题)', 'CommunityChargeNotice_title', '1', '2019-06-24 13:47:25', '2019-06-24 13:48:19', 'Tang', 'Tang', null);

-- ----------------------------
-- Table structure for base_element
-- ----------------------------
DROP TABLE IF EXISTS `base_element`;
CREATE TABLE `base_element` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL COMMENT '资源编码',
  `type` varchar(255) DEFAULT NULL COMMENT '资源类型',
  `name` varchar(255) DEFAULT NULL COMMENT '资源名称(使用英文录入)',
  `uri` varchar(255) DEFAULT NULL COMMENT '资源路径',
  `menu_id` varchar(11) DEFAULT NULL COMMENT '资源关联菜单',
  `parent_id` varchar(255) DEFAULT NULL,
  `path` varchar(2000) DEFAULT NULL COMMENT '资源树状检索路径',
  `method` varchar(10) DEFAULT NULL COMMENT '资源请求类型',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `language_type` varchar(50) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of base_element
-- ----------------------------
INSERT INTO `base_element` VALUES ('3', 'userManager:btn_add', 'button', 'New', '/user', '1', null, null, 'POST', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('4', 'userManager:btn_edit', 'button', 'Edit', '/user/{*}', '1', null, null, 'PUT', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('5', 'userManager:btn_del', 'button', 'Delete', '/user/{*}', '1', null, null, 'DELETE', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('9', 'menuManager:element', 'uri', 'View', '/element', '6', null, null, 'GET', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('10', 'menuManager:btn_add', 'button', 'New', '/menu/{*}', '6', null, null, 'POST', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('11', 'menuManager:btn_edit', 'button', 'Edit', '/menu/{*}', '6', '', '', 'PUT', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('12', 'menuManager:btn_del', 'button', 'Delete', '/menu/{*}', '6', '', '', 'DELETE', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('13', 'menuManager:btn_element_add', 'button', 'New element', '/element', '6', null, null, 'POST', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('14', 'menuManager:btn_element_edit', 'button', 'Edit element', '/element/{*}', '6', null, null, 'PUT', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('15', 'menuManager:btn_element_del', 'button', 'Delete element', '/element/{*}', '6', null, null, 'DELETE', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('16', 'groupManager:btn_add', 'button', 'New', '/group', '7', null, null, 'POST', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('17', 'groupManager:btn_edit', 'button', 'Edit', '/group/{*}', '7', null, null, 'PUT', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('18', 'groupManager:btn_del', 'button', 'Delete', '/group/{*}', '7', null, null, 'DELETE', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('19', 'groupManager:btn_userManager', 'button', 'Associated broker', '/group/{*}/user', '7', null, null, 'PUT', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('20', 'groupManager:btn_resourceManager', 'button', 'assign permissions', '/group/{*}/authority', '7', null, null, 'GET', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('21', 'groupManager:menu', 'uri', 'Assignment menu', '/group/{*}/authority/menu', '7', null, null, 'POST', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('22', 'groupManager:element', 'uri', 'resource allocation', '/group/{*}/authority/element', '7', null, null, 'POST', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('23', 'userManager:view', 'uri', 'View', '/user/{*}', '1', '', '', 'GET', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('24', 'menuManager:view', 'uri', 'View', '/menu/{*}', '6', '', '', 'GET', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('27', 'menuManager:element_view', 'uri', 'View', '/element/{*}', '6', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('28', 'groupManager:view', 'uri', 'View', '/group/{*}', '7', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('32', 'groupTypeManager:view', 'uri', 'View', '/groupType/{*}', '8', null, null, 'GET', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('33', 'groupTypeManager:btn_add', 'button', 'New', '/groupType', '8', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('34', 'groupTypeManager:btn_edit', 'button', 'Edit', '/groupType/{*}', '8', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('35', 'groupTypeManager:btn_del', 'button', 'Delete', '/groupType/{*}', '8', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('41', 'gateLogManager:view', 'button', 'View', '/gateLog', '27', null, null, 'GET', '', null, null, null, null, null);
INSERT INTO `base_element` VALUES ('47', 'deptManager:view', 'uri', 'View', '/dpet/{*}', '35', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('48', 'deptManager:btn_add', 'button', 'New', '/dept', '35', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('49', 'deptManager:btn_edit', 'button', 'Edit', '/dept/{*}', '35', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('50', 'deptManager:btn_del', 'button', 'Delete', '/dept/{*}', '35', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('51', 'userManager:btn_update', 'button', 'Updat password', '/user/{*}', '1', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('52', 'groupManager:btn_exchInfoManager', 'uri', 'Broker attribute', '/whiteExchInfo', '7', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('53', 'paramManager:view', 'uri', 'View', '/param/{*}', '36', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('54', 'paramManager:btn_add', 'button', 'New', '/param', '36', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('55', 'paramManager:btn_edit', 'button', 'Edit', '/param/{*}', '36', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('56', 'paramManager:btn_del', 'button', 'Delete', '/param/{*}', '36', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('57', 'basicSymbolManager:view', 'uri', 'View', '/basicSymbol/{*}', '39', null, null, 'GET', null, null, null, null, '2019-05-08 17:01:16', 'Tian');
INSERT INTO `base_element` VALUES ('58', 'basicSymbolManager:btn_add', 'button', 'New', '/basicSymbol', '39', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('59', 'basicSymbolManager:btn_edit', 'button', 'Edit', '/basicSymbol/{*}', '39', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('60', 'basicSymbolManager:btn_del', 'button', 'Delete', '/basicSymbol/{*}', '39', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('65', 'rechargeWithdrawManager:view', 'uri', 'View', '/rechargeWithdraw/{*}', '40', null, null, 'GET', null, null, null, null, '2019-04-19 15:06:34', 'Tian');
INSERT INTO `base_element` VALUES ('66', 'rechargeWithdrawManager:btn_add', 'button', 'New', '/rechargeWithdraw', '40', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('67', 'rechargeWithdrawManager:btn_edit', 'button', 'Edit', '/rechargeWithdraw/{*}', '40', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('68', 'rechargeWithdrawManager:btn_del', 'button', 'Delete', '/rechargeWithdraw/{*}', '40', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('69', 'chargeTemplateManager:view', 'uri', 'View', '/chargeTemplate/{*}', '41', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('70', 'chargeTemplateManager:btn_add', 'button', 'New', '/chargeTemplate', '41', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('71', 'chargeTemplateManager:btn_edit', 'button', 'Edit', '/chargeTemplate/{*}', '41', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('72', 'chargeTemplateManager:btn_del', 'button', 'Delete', '/chargeTemplate/{*}', '41', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('77', 'basicSymbolManager:btn_currency', 'button', 'Fee setting', '/basicSymbol/{*}               ', '39', null, null, 'POST', null, null, null, null, '2019-04-24 18:03:55', 'pubaoyu');
INSERT INTO `base_element` VALUES ('78', 'currencyTransferManager:view', 'uri', 'View', '/chargeTemplate/{*}', '42', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('79', 'currencyTransferManager:btn_add', 'button', 'New', '/chargeTemplate', '42', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('80', 'currencyTransferManager:btn_edit', 'button', 'Edit', '/chargeTemplate/{*}', '42', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('81', 'currencyTransferManager:btn_del', 'button', 'Delete', '/chargeTemplate/{*}', '42', null, null, 'DELETE', null, null, null, null, '2019-04-16 20:52:18', 'Tian');
INSERT INTO `base_element` VALUES ('82', 'frontCountryManager:view', 'uri', 'View', '/frontCountry/{*}', '43', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('83', 'frontCountryManager:btn_add', 'button', 'New', '/frontCountry', '43', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('84', 'frontCountryManager:btn_edit', 'button', 'Edit', '/frontCountry/{*}', '43', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('85', 'frontCountryManager:btn_del', 'button', 'Delete', '/frontCountry/{*}', '43', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('86', 'menuManager:btn_menuTitle_add', 'button', 'New', '/menuTitle', '6', null, null, 'POST', null, null, '2019-03-14 10:21:57', 'Tian', '2019-03-14 10:22:09', 'Tian');
INSERT INTO `base_element` VALUES ('87', 'menuManager:btn_menuTitle_del', 'button', 'Delete menuTitle', '/menuTitle/{*}', '6', null, null, 'DELETE', null, null, '2019-03-14 10:22:59', 'Tian', '2019-03-14 10:22:59', 'Tian');
INSERT INTO `base_element` VALUES ('88', 'menuManager:btn_menuTitle_edit', 'button', 'Edit menuTitle', '/menuTitle/{*}', '6', null, null, 'PUT', null, null, '2019-03-14 10:23:51', 'Tian', '2019-03-14 10:23:51', 'Tian');
INSERT INTO `base_element` VALUES ('89', 'menuManager:menuTitle_view', 'uri', 'View', '/menuTitle/{*}', '6', null, null, 'GET', null, null, '2019-03-14 10:24:42', 'Tian', '2019-03-14 10:25:39', 'Tian');
INSERT INTO `base_element` VALUES ('90', 'dictTypeManager:view', 'uri', 'View', '/dictType/{*}', '46', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('91', 'dictTypeManager:btn_add', 'button', 'New', '/dictType', '46', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('92', 'dictTypeManager:btn_edit', 'button', 'Edit', '/dictType/{*}', '46', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('93', 'dictTypeManager:btn_del', 'button', 'Delete', '/dictType/{*}', '46', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('94', 'emailSend:view', 'uri', 'View', '/emailSend/{*}', '45', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('95', 'emailSend:btn_add', 'button', 'New', '/emailSend', '45', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('96', 'emailSend:btn_edit', 'button', 'Edit', '/emailSend/{*}', '45', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('97', 'emailSend:btn_del', 'button', 'Delete', '/emailSend/{*}', '45', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('98', 'dictTypeManager:dictData', 'uri', 'View', '/dictData/{*}', '46', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('99', 'dictTypeManager:btn_dictData_add', 'button', 'New', '/dictData', '46', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('100', 'dictTypeManager:btn_dictData_edit', 'button', 'Edit', '/dictData/{*}', '46', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('101', 'dictTypeManager:btn_dictData_del', 'button', 'Delete', '/dictData/{*}', '46', null, null, 'DELETE', null, null, null, null, '2019-04-19 16:32:44', 'Tian');
INSERT INTO `base_element` VALUES ('102', 'emailTemplate:view', 'uri', 'View', '/emailTemplate/{*}', '47', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('103', 'emailTemplate:btn_add', 'button', 'New', '/emailTemplate', '47', null, null, 'POST', null, null, null, null, '2019-04-16 20:23:19', 'Tian');
INSERT INTO `base_element` VALUES ('104', 'emailTemplate:btn_edit', 'button', 'Edit', '/emailTemplate/{*}', '47', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('105', 'emailTemplate:btn_del', 'button', 'Delete', '/emailTemplate/{*}', '47', null, null, 'DELETE', null, null, null, null, '2019-05-16 15:34:50', 'Tian');
INSERT INTO `base_element` VALUES ('106', 'offerInfoManager:view', 'uri', 'View', '/offerInfo/{*}', '48', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('107', 'offerInfoManager:btn_add', 'button', 'New', '/offerInfo', '48', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('108', 'offerInfoManager:btn_edit', 'button', 'Edit', '/offerInfo/{*}', '48', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('109', 'offerInfoManager:btn_del', 'button', 'Delete', '/offerInfo/{*}', '48', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('110', 'emailAuditor:view', 'uri', 'View', '/emailAuditor/{*}', '49', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('111', 'emailAuditor:btn_add', 'button', 'New', '/emailAuditor', '49', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('112', 'emailAuditor:btn_edit', 'button', 'Edit', '/emailAuditor/{*}', '49', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('113', 'emailAuditor:btn_del', 'button', 'Delete', '/emailAuditor/{*}', '49', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('114', 'smsConfig:view', 'uri', 'View', '/smsConfig/{*}', '51', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('115', 'smsConfig:btn_add', 'button', 'New', '/smsConfig', '51', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('116', 'smsConfig:btn_edit', 'button', 'Edit', '/smsConfig/{*}', '51', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('117', 'smsConfig:btn_del', 'button', 'Delete', '/smsConfig/{*}', '51', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('118', 'userInfo:view', 'uri', 'View', '/userInfo/{*}', '53', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('119', 'userInfo:btn_add', 'button', 'New', '/userInfo', '53', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('120', 'userInfo:btn_edit', 'button', 'Edit', '/userInfo/{*}', '53', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('121', 'userInfo:btn_del', 'button', 'Delete', '/userInfo/{*}', '53', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('122', 'offerInfoManager:btn_update', 'button', 'Review', '/offerInfo/{*}', '48', null, null, 'PUT', null, null, '2019-03-26 11:17:39', 'test0001', '2019-03-26 11:17:39', 'test0001');
INSERT INTO `base_element` VALUES ('123', 'userInfo:btn_view', 'button', 'Details', '/userInfo/{*}', '53', null, null, 'PUT', null, null, '2019-03-26 17:58:15', 'Tian', '2019-06-19 19:49:56', 'Tian');
INSERT INTO `base_element` VALUES ('124', 'fundProductInfo:view', 'uri', 'View', '/fundProductInfo/{*}', '55', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('125', 'fundProductInfo:btn_add', 'button', 'New', '/fundProductInfo', '55', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('126', 'fundProductInfo:btn_edit', 'button', 'Edit', '/fundProductInfo/{*}', '55', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('127', 'fundProductInfo:btn_del', 'button', 'Delete', '/fundProductInfo/{*}', '55', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('128', 'withdrawManager:view', 'uri', 'View', '/userAssets/{*}', '58', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('129', 'withdrawManager:btn_Details', 'button', 'Details', '/userAssets/{*}', '58', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('130', 'withdrawManager:btn_edit', 'button', 'Edit', '/userAssets/{*}', '58', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('131', 'fundManageInfo:view', 'uri', 'View', '/fundManageInfo/{*}', '59', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('132', 'fundManageInfo:btn_add', 'button', 'New', '/fundManageInfo', '59', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('133', 'fundManageInfo:btn_edit', 'button', 'Edit', '/fundManageInfo/{*}', '59', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('134', 'fundManageInfo:btn_del', 'button', 'Delete', '/fundManageInfo/{*}', '59', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('135', 'fundStrategy:view', 'uri', 'View', '/fundStrategy/{*}', '61', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('136', 'fundStrategy:btn_add', 'button', 'New', '/fundStrategy', '61', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('137', 'fundStrategy:btn_edit', 'button', 'Edit', '/fundStrategy/{*}', '61', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('138', 'fundStrategy:btn_del', 'button', 'Delete', '/fundStrategy/{*}', '61', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('139', 'fundSettlement:view', 'uri', 'View', '/fundSettlement/{*}', '62', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('140', 'fundSettlement:btn_settle', 'button', 'Settlement', '/fundSettlement', '62', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('141', 'fundSettlement:btn_detail', 'button', 'Details', '/fundSettlement', '62', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('142', 'fundProductInfo:set_rate', 'button', 'Setting income', '/fundProductInfo/{*}', '55', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('143', 'merchantManager:btn_edit', 'button', 'Fee setting', '/merchant/{*}', '63', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('144', 'advertManager:view', 'uri', 'View', '/advertManager/{*}', '65', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('145', 'advertManager:btn_add', 'button', 'New', '/advertManager', '65', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('146', 'advertManager:btn_edit', 'button', 'Edit', '/advertManager/{*}', '65', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('147', 'advertManager:btn_del', 'button', 'Delete', '/advertManager/{*}', '65', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('148', 'helpType:view', 'uri', 'View', '/helpType/{*}', '67', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('149', 'helpType:btn_add', 'button', 'New', '/helpType', '67', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('150', 'helpType:btn_edit', 'button', 'Edit', '/helpType/{*}', '67', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('151', 'helpType:btn_del', 'button', 'Delete', '/helpType/{*}', '67', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('152', 'helpContent:view', 'uri', 'View', '/helpContent/{*}', '68', null, null, 'GET', null, null, null, null, '2019-04-17 15:00:22', 'Tian');
INSERT INTO `base_element` VALUES ('153', 'helpContent:btn_add', 'button', 'New', '/helpContent', '68', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('154', 'helpContent:btn_edit', 'button', 'Edit', '/helpContent/{*}', '68', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('155', 'helpContent:btn_del', 'button', 'Delete', '/helpContent/{*}', '68', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('156', 'frontNoticeManager:view', 'uri', 'View', '/frontNotice/{*}', '72', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('157', 'frontNoticeManager:btn_add', 'button', 'New', '/frontNotice', '72', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('158', 'frontNoticeManager:btn_edit', 'button', 'Edit', '/frontNotice/{*}', '72', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('159', 'frontNoticeManager:btn_del', 'button', 'Delete', '/frontNotice/{*}', '72', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('160', 'fundProductInfo:btn_Detail', 'button', 'Details', '/fundProductInfo/{*}', '55', null, null, 'GET', null, null, '2019-04-17 17:11:58', 'Tian', '2019-04-17 17:11:58', 'Tian');
INSERT INTO `base_element` VALUES ('161', 'baseVersionManager:view', 'uri', 'View', '/baseVersion/{*}', '73', null, null, 'GET', null, null, null, null, '2019-04-19 16:44:48', 'Tian');
INSERT INTO `base_element` VALUES ('162', 'baseVersionManager:btn_add', 'button', 'New', '/baseVersion', '73', null, null, 'POST', null, null, null, null, '2019-04-19 16:44:52', 'Tian');
INSERT INTO `base_element` VALUES ('163', 'baseVersionManager:btn_edit', 'button', 'Edit', '/baseVersion/{*}', '73', null, null, 'PUT', null, null, null, null, '2019-04-19 16:44:56', 'Tian');
INSERT INTO `base_element` VALUES ('164', 'baseVersionManager:btn_del', 'button', 'Delete', '/baseVersion/{*}', '73', null, null, 'DELETE', null, null, null, null, '2019-04-19 16:44:59', 'Tian');
INSERT INTO `base_element` VALUES ('165', 'hedgeDetail:view', 'uri', 'View', '/hedgeDetail/{*}', '74', null, null, 'GET', null, null, null, null, '2019-04-17 15:00:22', 'Tian');
INSERT INTO `base_element` VALUES ('166', 'hedgeDetail:btn_edit', 'button', 'Edit', '/hedgeDetail/{*}', '74', null, null, 'PUT', null, null, null, null, '2019-04-19 18:09:52', 'Tian');
INSERT INTO `base_element` VALUES ('167', 'basicSymbolImage:view', 'uri', 'View', '/basicSymbolImage/{*}', '77', null, null, 'GET', null, null, null, null, '2019-04-23 17:37:48', 'Tian');
INSERT INTO `base_element` VALUES ('168', 'basicSymbolImage:btn_add', 'button', 'New', '/basicSymbolImage', '77', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('169', 'basicSymbolImage:btn_edit', 'button', 'Edit', '/basicSymbolImage/{*}', '77', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('170', 'basicSymbolImage:btn_del', 'button', 'Delete', '/basicSymbolImage/{*}', '77', null, null, 'DELETE', null, null, null, null, '2019-04-23 17:37:53', 'Tian');
INSERT INTO `base_element` VALUES ('171', 'cfgSymbolDescription:view', 'uri', 'View', '/cfgSymbolDescription/{*}', '82', null, null, 'GET', null, null, null, null, '2019-04-28 13:49:26', 'Tian');
INSERT INTO `base_element` VALUES ('172', 'cfgSymbolDescription:btn_add', 'button', 'New', '/cfgSymbolDescription', '82', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('173', 'cfgSymbolDescription:btn_edit', 'button', 'Edit', '/cfgSymbolDescription/{*}', '82', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('174', 'cfgSymbolDescription:btn_del', 'button', 'Delete', '/cfgSymbolDescription/{*}', '82', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('175', 'cfgDescriptionTemplate:view', 'uri', 'View', '/cfgDescriptionTemplate/{*}', '81', null, null, 'GET', null, null, null, null, '2019-04-17 15:00:22', 'Tian');
INSERT INTO `base_element` VALUES ('176', 'cfgDescriptionTemplate:btn_add', 'button', 'New', '/cfgDescriptionTemplate', '81', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('177', 'cfgDescriptionTemplate:btn_edit', 'button', 'Edit', '/cfgDescriptionTemplate/{*}', '81', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('178', 'cfgDescriptionTemplate:btn_del', 'button', 'Delete', '/cfgDescriptionTemplate/{*}', '81', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('179', 'symbolExch:btn_edit', 'button', 'Edit', '/symbolExch/{*}', '83', null, null, 'POST', null, null, null, null, '2019-05-05 11:38:08', 'Tian');
INSERT INTO `base_element` VALUES ('180', 'transferExch:btn_edit', 'button', 'Edit', '/transferExch/{*}', '84', null, null, 'POST', null, null, null, null, '2019-05-31 09:59:45', 'Tian');
INSERT INTO `base_element` VALUES ('181', 'waitWithdraw:btn_edit', 'button', 'Edit', '/blockchain/{*}', '90', null, null, 'POST', null, null, null, null, '2019-05-05 11:38:08', 'Tian');
INSERT INTO `base_element` VALUES ('182', 'withdrawAddress:btn_add', 'button', 'New', '/withdrawAddress', '89', null, null, 'POST', null, null, null, null, '2019-05-20 14:15:41', 'Tian');
INSERT INTO `base_element` VALUES ('183', 'bonusConfig:view', 'uri', 'View', '/bonusConfig/{*}', '93', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('184', 'bonusConfig:btn_add', 'button', 'New', '/bonusConfig', '93', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('185', 'bonusConfig:btn_edit', 'button', 'Edit', '/bonusConfig/{*}', '93', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('186', 'bonusConfig:btn_del', 'button', 'Delete', '/bonusConfig/{*}', '93', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('187', 'udParam:view', 'uri', 'View', '/udParam/{*}', '94', null, null, 'GET', null, null, null, null, '2019-06-13 20:42:33', 'Tian');
INSERT INTO `base_element` VALUES ('188', 'udParam:btn_add', 'button', 'New', '/udParam', '94', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('189', 'udParam:btn_edit', 'button', 'Edit', '/udParam/{*}', '94', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('190', 'udParam:btn_del', 'button', 'Delete', '/udParam/{*}', '94', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('191', 'hPurchaseLevel:view', 'uri', 'View', '/hPurchaseLevel/{*}', '95', null, null, 'GET', null, null, null, null, '2019-06-10 19:20:34', 'Tian');
INSERT INTO `base_element` VALUES ('192', 'hPurchaseLevel:btn_add', 'button', 'New', '/hPurchaseLevel', '95', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('193', 'hPurchaseLevel:btn_edit', 'button', 'Edit', '/hPurchaseLevel/{*}', '95', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('194', 'hPurchaseLevel:btn_del', 'button', 'Delete', '/hPurchaseLevel/{*}', '95', null, null, 'DELETE', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('195', 'configInfo:view', 'uri', 'View', '/blockchain/{*}', '100', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('196', 'configInfo:btn_add', 'button', 'New', '/blockchain', '100', null, null, 'POST', null, null, null, null, '2019-06-13 16:07:39', 'Tian');
INSERT INTO `base_element` VALUES ('197', 'configInfo:btn_edit', 'button', 'Edit', '/blockchain/{*}', '100', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('198', 'configInfo:btn_del', 'button', 'Delete', '/blockchain/{*}', '100', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('199', 'chargeAddress:view', 'uri', 'View', '/blockchain/{*}', '102', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('200', 'chargeAddress:btn_add', 'button', 'New', '/blockchain', '102', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('201', 'chargeAddress:btn_edit', 'button', 'Edit', '/blockchain/{*}', '102', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('202', 'chargeAddress:btn_del', 'button', 'Delete', '/blockchain/{*}', '102', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('203', 'UDOrderDetails:btn_edit', 'button', 'Edit', '/UDOrderDetails/{*}', '98', null, null, 'POST', null, null, null, null, '2019-06-15 14:20:37', null);
INSERT INTO `base_element` VALUES ('204', 'UDQueue:btn_edit', 'button', 'Edit', '/UDQueue/{*}', '101', null, null, 'POST', null, null, null, null, '2019-06-15 14:27:08', null);
INSERT INTO `base_element` VALUES ('209', 'nodeAward:view', 'uri', 'View', '/nodeAward/{*}', '106', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('210', 'nodeAward:btn_add', 'button', 'New', '/nodeAward', '106', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('211', 'nodeAward:btn_edit', 'button', 'Edit', '/nodeAward/{*}', '106', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('212', 'nodeAward:btn_del', 'button', 'Delete', '/nodeAward/{*}', '106', null, null, 'DELETE', null, null, null, null, null, null);

-- ----------------------------
-- Table structure for base_email_auditor
-- ----------------------------
DROP TABLE IF EXISTS `base_email_auditor`;
CREATE TABLE `base_email_auditor` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '邮件编号',
  `auditor_name` varchar(32) DEFAULT NULL COMMENT '审核人员名字',
  `auditor_role` varchar(32) DEFAULT NULL COMMENT '审核人员角色(如提币审核管理员、用户审核管理员等)',
  `email_account` varchar(32) DEFAULT NULL COMMENT '审核人员邮箱账号',
  `email_title` varchar(256) NOT NULL COMMENT '发送标题',
  `email_content` text NOT NULL COMMENT '发送内容',
  `remark_` varchar(500) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='后台邮件审核人员表';

-- ----------------------------
-- Records of base_email_auditor
-- ----------------------------
INSERT INTO `base_email_auditor` VALUES ('1', 'Tang', 'USER_AUDIT', '294816099@qq.com', '无题', '管理员你好,用户%s已经发起身份认证申请,请及时审核,谢谢。', null, '2019-04-19 18:05:57', 'Tang', '2019-04-19 18:05:57', 'Tang');

-- ----------------------------
-- Table structure for base_email_config
-- ----------------------------
DROP TABLE IF EXISTS `base_email_config`;
CREATE TABLE `base_email_config` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '邮件配置编号',
  `smtp_host` varchar(32) NOT NULL COMMENT 'SMTP服务器',
  `smtp_port` varchar(8) NOT NULL COMMENT 'SMTP服务器端口',
  `sender_name` varchar(64) NOT NULL COMMENT '名称',
  `sender_account` varchar(32) NOT NULL COMMENT '发邮件邮箱账号',
  `sender_password` varchar(32) NOT NULL COMMENT '发邮件邮箱密码',
  `white_exch_id` bigint(11) NOT NULL DEFAULT '1' COMMENT '白标(交易所)Id',
  `remark_` varchar(300) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='邮件配置表';

-- ----------------------------
-- Records of base_email_config
-- ----------------------------
INSERT INTO `base_email_config` VALUES ('1', 'smtp.office365.com', '587', 'exchange@udax.kr', 'exchange@udax.kr', 'udaxkremail8888~', '1', null, '2019-03-29 16:24:59', 'admin', '2019-05-09 11:19:47', 'TangHK');

-- ----------------------------
-- Table structure for base_email_template
-- ----------------------------
DROP TABLE IF EXISTS `base_email_template`;
CREATE TABLE `base_email_template` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '邮件模板编号',
  `email_title` varchar(128) DEFAULT NULL COMMENT '邮件标题',
  `template_name` varchar(128) NOT NULL COMMENT '模板名称',
  `template` text NOT NULL COMMENT '模板内容',
  `white_exch_id` bigint(11) DEFAULT NULL COMMENT '白标(交易所)Id',
  `remark_` varchar(300) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='邮件模板表';

-- ----------------------------
-- Records of base_email_template
-- ----------------------------
INSERT INTO `base_email_template` VALUES ('2', null, 'CommonTemplate', '<div id=\"contentDiv\" class=\"body\" style=\"position: relative; font-size: 14px; height: auto; padding: 15px 15px 10px 15px; z-index: 1; zoom: 1; line-height: 1.7;\">\n<div id=\"qm_con_body\">\n<div id=\"mailContentContainer\" class=\"qmbox qm_con_body_content qqmail_webmail_only\">&nbsp;&nbsp;&nbsp;\n<table style=\"border: 1px solid #cdcdcd; width: 640px; margin: auto; font-size: 12px; color: #1e2731; line-height: 20px;\" cellspacing=\"0\" cellpadding=\"0\">\n<tbody>\n<tr>\n<td style=\"background-color: #4169e1; height: 55px; padding: 30px 0;\" colspan=\"3\" align=\"center\"><a href=\"https://www.udax.kr\" target=\"_blank\" rel=\"noopener\"><img src=\"http://192.168.1.198/upload/\\admin\\c5fe748d-9aa4-4cb6-8a33-e486c04fb6a6.png\" /></a></td>\n</tr>\n<tr>\n<td width=\"20\">&nbsp;</td>\n<td style=\"line-height: 40px;\">【Wallet】{MailContent}</td>\n<td width=\"20\">&nbsp;</td>\n</tr>\n<tr>\n<td width=\"20\">&nbsp;</td>\n<td><br />WalletTeam<br /><a href=\"https://www.udax.hk\" target=\"_blank\" rel=\"noopener\">https://www.udax_</a>wallet</td>\n<td width=\"20\">&nbsp;</td>\n</tr>\n</tbody>\n</table>\n</div>\n</div>\n<!-- --></div>', '1', '公用模板,每个交易所都需自定义一个公用模板，任何邮件不指定其他模板，则公用此模板，此标题不需填，后台根据特定内容自己制定.', '2019-03-29 15:39:38', 'admin', '2019-05-23 14:09:04', 'Tian');

-- ----------------------------
-- Table structure for base_group
-- ----------------------------
DROP TABLE IF EXISTS `base_group`;
CREATE TABLE `base_group` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL COMMENT '角色编码',
  `name` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `parent_id` bigint(11) NOT NULL COMMENT '上级节点',
  `path` varchar(2000) DEFAULT NULL COMMENT '树状关系',
  `type` char(1) DEFAULT NULL COMMENT '类型',
  `group_type` int(11) NOT NULL COMMENT '角色组类型',
  `description` varchar(255) DEFAULT NULL,
  `broker` varchar(128) DEFAULT NULL COMMENT '经纪商名称',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of base_group
-- ----------------------------
INSERT INTO `base_group` VALUES ('1', 'adminRole', '管理员', '-1', '/adminRole', null, '1', 'ADMIN', null, null, null, '2019-05-22 10:22:59', 'Tian');


-- ----------------------------
-- Table structure for base_group_leader
-- ----------------------------
DROP TABLE IF EXISTS `base_group_leader`;
CREATE TABLE `base_group_leader` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(11) DEFAULT NULL,
  `user_id` bigint(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of base_group_leader
-- ----------------------------
INSERT INTO `base_group_leader` VALUES ('1', '1', '1');


-- ----------------------------
-- Table structure for base_group_member
-- ----------------------------
DROP TABLE IF EXISTS `base_group_member`;
CREATE TABLE `base_group_member` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `group_id` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_user` varchar(255) DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `crt_host` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_user` varchar(255) DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `upd_host` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;




-- ----------------------------
-- Table structure for base_group_type
-- ----------------------------
DROP TABLE IF EXISTS `base_group_type`;
CREATE TABLE `base_group_type` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL COMMENT '编码',
  `name` varchar(255) DEFAULT NULL COMMENT '类型名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of base_group_type
-- ----------------------------
INSERT INTO `base_group_type` VALUES ('1', 'role', '平台', 'role', null, null, '2019-04-30 13:53:03', 'UDAX-HK');

-- ----------------------------
-- Table structure for base_menu
-- ----------------------------
DROP TABLE IF EXISTS `base_menu`;
CREATE TABLE `base_menu` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL COMMENT '路径编码',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `parent_id` bigint(11) NOT NULL COMMENT '父级节点',
  `href` varchar(255) DEFAULT NULL COMMENT '资源路径',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `type` char(10) DEFAULT NULL,
  `order_num` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `path` varchar(500) DEFAULT NULL COMMENT '菜单上下级关系',
  `enabled` char(1) DEFAULT NULL COMMENT '启用禁用',
  `attr1` varchar(255) DEFAULT NULL,
  `language_type` varchar(50) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Unique_title` (`title`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of base_menu
-- ----------------------------
INSERT INTO `base_menu` VALUES ('1', 'userManager', '用户管理', '5', '/admin/user', '', 'menu', '0', '', '/baseManager/userManager', null, '_import(\'admin/user/index\')', null, null, null, '2019-04-16 16:48:07', 'Tian');
INSERT INTO `base_menu` VALUES ('5', 'baseManager', '基础配置管理', '-1', '/admin', 'basicinfo', 'dirt', '0', '', '/baseManager', null, 'Layout', null, null, null, '2019-06-10 15:54:36', 'Tian');
INSERT INTO `base_menu` VALUES ('6', 'menuManager', '菜单管理', '5', '/admin/menu', '', 'menu', '0', '', '/baseManager/menuManager', null, '_import(\'admin/menu/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('7', 'groupManager', '权限分配管理', '5', '/admin/group', '', 'menu', '0', '', '/baseManager/groupManager', null, 'import(\'admin/group/index\')', null, null, null, '2019-04-15 20:19:43', 'Tian');
INSERT INTO `base_menu` VALUES ('8', 'groupTypeManager', '角色类型管理', '5', '/admin/groupType', '', 'menu', '0', '', '/baseManager/groupTypeManager', null, '_import(\'admin/groupType/index\')', null, null, null, '2019-04-19 16:27:21', 'Tian');
INSERT INTO `base_menu` VALUES ('27', 'gateLogManager', '操作日志', '5', '/admin/gateLog', '', 'menu', '0', '', '/baseManager/gateLogManager', null, '_import(\'admin/gateLog/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('35', 'deptManager', '部门管理', '5', '/admin/dept', '', 'menu', '0', '部门管理', '/baseManager/deptManager', null, '_import(\'admin/dept/index\')', null, null, null, '2019-05-06 11:03:29', 'Tian');
INSERT INTO `base_menu` VALUES ('36', 'paramManager', '系统参数', '5', '/admin/param', '', 'menu', '0', '系统参数', '/baseManager/paramManager', null, '_import(\'admin/param/index\')', null, null, null, '2019-04-25 11:40:47', 'Tian');
INSERT INTO `base_menu` VALUES ('37', 'marketingManager', '营销管理', '-1', '/marketing', 'yingxiao', 'dirt', '0', null, '/marketingManager', null, 'Layout', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('38', 'configManager', '交易配置管理', '-1', '/config', 'jiaoyi', 'dirt', '0', null, '/marketingManager/configManager', null, 'Layout', null, null, null, '2019-06-11 18:22:27', 'Tian');
INSERT INTO `base_menu` VALUES ('39', 'basicSymbolManager', '基础货币管理', '38', '/config/basicSymbol', '', 'menu', '0', null, '/marketingManager/configManager/basicSymbolManager', null, '_import(\'/marketing/config/basicSymbol/index\')', null, null, null, '2019-05-31 16:01:34', 'Tian');
INSERT INTO `base_menu` VALUES ('40', 'rechargeWithdrawManager', '充提币管理', '38', '/config/rechargeWithdraw', '', 'menu', '0', null, '/marketingManager/configManager/rechargeWithdrawManager', null, '_import(\'/config/rechargeWithdraw/index\')', null, null, null, '2019-06-05 13:36:11', 'Tian');
INSERT INTO `base_menu` VALUES ('41', 'chargeTemplateManager', '手续费模板', '38', '/config/chargeTemplate', '', 'menu', '0', null, '/marketingManager/configManager/chargeTemplateManager', null, '_import(\'/config/chargeTemplate/index\')', null, null, null, '2019-05-31 16:01:51', 'Tian');
INSERT INTO `base_menu` VALUES ('42', 'currencyTransferManager', '货币转换管理', '38', '/config/currencyTransfer', '', 'menu', '0', null, '/adminSys/marketingManager/configManager/currencyTransferManager', null, '_import(\'/config/currencyTransfer/index\')', null, null, null, '2019-05-31 16:01:59', 'Tian');
INSERT INTO `base_menu` VALUES ('43', 'frontCountryManager', '国家管理', '5', '/admin/frontCountry', '', 'menu', '0', '国家管理', '/adminSys/baseManager/frontCountryManager', null, '_import(\'admin/frontCountry/index\')', null, null, null, '2019-04-23 17:37:43', 'Tian');
INSERT INTO `base_menu` VALUES ('44', 'emailManager', '邮件管理', '37', '/email', '', 'dirt', '0', null, '/marketingManager/emailManager', null, 'Layout', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('45', 'emailSend', '邮件发送配置', '44', '/email/emailSend', '', 'menu', '0', null, '/marketingManager/emailManager/emailSend', null, 'Layout', null, null, null, '2019-04-17 14:57:30', 'Tian');
INSERT INTO `base_menu` VALUES ('46', 'dictTypeManager', '数据字典', '5', '/admin/dictType', '', 'menu', '0', '数据字典', '/adminSys/baseManager/dictTypeManager', null, '_import(\'admin/dictType/index\')', null, null, null, '2019-04-16 16:44:57', 'Tian');
INSERT INTO `base_menu` VALUES ('47', 'emailTemplate', '邮件模板设置', '44', '/email/emailTemplate', '', 'menu', '0', '邮件模板', '/marketingManager/emailManager/emailTemplate', null, 'Layout', null, null, null, '2019-04-17 15:00:13', 'Tian');
INSERT INTO `base_menu` VALUES ('48', 'offerInfoManager', '用户报价管理', '38', '/config/offerInfo', '', 'menu', '0', null, '/adminSys/marketingManager/configManager/offerInfoManager', null, '_import(\'/config/offerInfo/index\')', null, null, null, '2019-04-01 14:39:33', 'Tian');
INSERT INTO `base_menu` VALUES ('49', 'emailAuditor', '邮件审核管理', '44', '/email/emailAuditor', '', 'menu', '0', '邮件审核', '/marketingManager/emailManager/emailAuditor', null, 'Layout', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('50', 'smsManager', '短信管理', '37', '/sms', '', 'dirt', '0', null, '/marketingManager/smsManager', null, 'Layout', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('51', 'smsConfig', '短信发送配置', '50', '/sms/smsConfig', '', 'menu', '0', null, '/marketingManager/smsManager/smsConfig', null, 'Layout', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('52', 'frontUserManager', '用户信息管理', '-1', '/frontUser', 'fa-user', 'dirt', '0', '用户信息管理', '/userManager', null, 'Layout', null, null, null, '2019-06-19 19:49:50', 'Tian');
INSERT INTO `base_menu` VALUES ('53', 'userInfoManager', '用户信息详情', '52', '/frontUser/userInfo', '', 'menu', '0', '用户信息详情', '/userManager/userInfoManager', null, '_import(\'/frontUser/userInfo/index\')', null, null, null, '2019-04-02 20:04:40', 'Tian');
INSERT INTO `base_menu` VALUES ('54', 'fundProductManager', '跟单交易管理', '-1', '/fundProduct', 'gendan', 'dirt', '0', '跟单交易管理', '/fundProductManager', null, 'Layout', null, null, null, '2019-06-17 14:21:23', 'Tian');
INSERT INTO `base_menu` VALUES ('55', 'fundProductInfo', '跟单产品列表', '54', '/fundProduct/fundProductInfo', '', 'menu', '0', '跟单产品列表', '/fundProduct/fundProductInfo', null, '_import(\'/fundProduct/fundProductInfo/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('56', 'assetsManager', '资产管理', '-1', '/assets', 'zichan', 'dirt', '0', '资产管理', '/assetsManager', null, 'Layout', null, null, null, '2019-06-17 16:58:32', 'Tian');
INSERT INTO `base_menu` VALUES ('57', 'userRechargeManager', '入金记录管理', '56', '/assets/userRecharge', null, 'menu', '0', '入金管理', '/assetsManager/userRechargeManager', null, '_import(\'assets/userRecharge/index\')', null, null, null, '2019-03-30 17:07:32', 'Tian');
INSERT INTO `base_menu` VALUES ('58', 'withdrawManager', '用户提现管理', '56', '/assets/withdraw', null, 'menu', '0', '用户提现管理', '/assetsManager/withdrawManager', null, '_import(\'assets/withdraw/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('59', 'fundManageInfo', '跟单管理人员', '54', '/fundProduct/fundManageInfo', '', 'menu', '0', '跟单管理人员', '/fundProduct/fundManageInfo', null, '_import(\'/fundProduct/fundManageInfo/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('60', 'transferOrderManager', '用户转账管理', '56', '/assets/transferOrder', null, 'menu', '0', '用户转账管理', '/assetsManager/transferOrderManager', null, null, null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('61', 'fundStrategy', '跟单策略管理', '54', '/fundProduct/fundStrategy', '', 'menu', '0', '跟单策略管理', '/fundProduct/fundStrategy', null, '_import(\'/fundProduct/fundStrategy/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('62', 'fundSettlement', '跟单结算', '54', '/fundProduct/fundSettlement', '', 'menu', '0', '跟单结算', '/fundProduct/fundSettlement', null, '_import(\'/fundProduct/fundSettlement/index\')', null, null, null, '2019-04-12 11:21:10', 'admin');
INSERT INTO `base_menu` VALUES ('63', 'merchantManager', '商家信息管理', '52', '/frontUser/merchant', '', 'menu', '0', '商家信息管理', '/userManager/merchantManager', null, '_import(\'/frontUser/merchant/index\')', null, null, null, '2019-04-02 20:04:40', 'Tian');
INSERT INTO `base_menu` VALUES ('64', 'frontAccountManager', '用户资产管理', '56', '/assets/frontAccount', null, 'menu', '0', '用户资产管理', '/assetsManager/frontAccountManager', null, '_import(\'assets/frontAccount/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('65', 'advertManager', '广告管理', '37', '/advert', '', 'dirt', '0', null, '/marketing/advert', null, '', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('66', 'helpInfo', '帮助信息', '37', '/helpInfo', '', 'dirt', '0', null, '/marketing/helpInfo', null, 'Layout', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('67', 'helpType', '帮助类型', '66', '/helpInfo/helpType', '', 'dirt', '0', null, '/marketing/helpInfo/helpType', null, '_import(\'helpInfo/helpType/index\')', null, null, null, '2019-04-10 20:56:52', 'Tang');
INSERT INTO `base_menu` VALUES ('68', 'helpContent', '帮助内容', '66', '/helpInfo/helpContent', '', 'dirt', '0', null, '/marketing/helpInfo/helpContent', null, '_import(\'helpInfo/helpContent/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('69', 'frontTransferDetailManager', '币币交易管理', '56', '/assets/frontTransferDetail', null, 'menu', '0', '币币交易管理', '/assetsManager/frontTransferDetailManager', null, '_import(\'assets/frontTransferDetail/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('70', 'mchTradeDetailManager', '商户交易管理', '56', '/assets/mchTradeDetail', null, 'menu', '0', '币币交易管理', '/assetsManager/mchTradeDetailManager', null, '_import(\'assets/mchTradeDetail/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('71', 'mchRefundDetailManager', '退款订单管理', '56', '/assets/mchRefundDetail', null, 'menu', '0', '商户退款管理', '/assetsManager/mchRefundDetailManager', null, '_import(\'assets/mchRefundDetail/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('72', 'frontNoticeManager', '公告管理', '37', '/frontNotice', '', 'dirt', '0', null, '/marketing/frontNoticeManager', null, 'Layout', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('73', 'baseVersionManager', '版本控制', '5', '/admin/baseVersion', '', 'menu', '0', '', '/baseManager/baseVersionManager', null, '_import(\'admin/baseVersion/index\')', null, null, null, '2019-04-28 13:49:20', 'Tian');
INSERT INTO `base_menu` VALUES ('74', 'hedgeDetailManager', '交易对冲管理', '38', '/config/hedgeDetail', '', 'menu', '0', null, '/marketingManager/configManager/hedgeDetailManager', null, '_import(\'/marketing/config/hedgeDetail/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('75', 'dataReportManager', '数据报表', '-1', '/dataReport', 'shujutubiao', 'dirt', '0', '数据报表管理', '/dataReportManager', null, 'Layout', null, null, null, '2019-04-25 10:51:34', 'test001');
INSERT INTO `base_menu` VALUES ('76', 'withdrawReportManager', '提币汇总', '75', '/dataReport/withdraw', null, 'menu', '0', '提币汇总', '/dataReportManager/withdrawReportManager', null, '_import(\'dataReport/withdraw/index\')', null, null, null, '2019-03-30 17:07:32', 'Tian');
INSERT INTO `base_menu` VALUES ('77', 'basicSymbolImageManager', '币种图标管理', '38', '/config/basicSymbolImage', '', 'menu', '0', null, '/marketingManager/configManager/basicSymbolImageManager', null, '_import(\'/marketing/config/basicSymbolImage/index\')', null, null, null, '2019-05-31 16:02:09', 'Tian');
INSERT INTO `base_menu` VALUES ('78', 'smsSendList', '短信发送列表', '50', '/sms/smsSendList', '', 'menu', '0', null, '/marketingManager/smsManager/smsSendList', null, 'Layout', null, null, null, '2019-04-24 13:39:47', 'Tang');
INSERT INTO `base_menu` VALUES ('79', 'rechargeReportManager', '入金汇总', '75', '/dataReport/rechargeReport', null, 'menu', '0', '入金汇总', '/dataReportManager/rechargeReportManager', null, '_import(\'dataReport/rechargeReport/index\')', null, null, null, '2019-03-30 17:07:32', 'Tian');
INSERT INTO `base_menu` VALUES ('80', 'assetAccountReportManager', '资产汇总', '75', '/dataReport/assetAccount', null, 'menu', '0', '入金汇总', '/dataReportManager/assetAccountReportManager', null, '_import(\'dataReport/assetAccountReport/index\')', null, null, null, '2019-03-30 17:07:32', 'Tian');
INSERT INTO `base_menu` VALUES ('81', 'cfgDescriptionTemplateManager', '币种描述模板', '38', '/config/cfgDescriptionTemplate', '', 'menu', '0', null, '/marketingManager/configManager/cfgDescriptionTemplateManager', null, '_import(\'/config/cfgDescriptionTemplate/index\')', null, null, null, '2019-05-31 16:02:16', 'Tian');
INSERT INTO `base_menu` VALUES ('82', 'cfgSymbolDescriptionManager', '币种描述管理', '38', '/config/cfgSymbolDescription', '', 'menu', '0', null, '/marketingManager/configManager/cfgSymbolDescriptionManager', null, '_import(\'/config/cfgSymbolDescription/index\')', null, null, null, '2019-05-31 16:02:26', 'Tian');
INSERT INTO `base_menu` VALUES ('83', 'symbolExchManager', '币种权限分配', '38', '/config/symbolExch', '', 'menu', '0', null, '/marketingManager/configManager/symbolExchManager', null, '_import(\'/config/symbolExch/index\')', null, null, null, '2019-05-31 16:02:33', 'Tian');
INSERT INTO `base_menu` VALUES ('84', 'transferExchManager', '交易对权限分配', '38', '/config/transferExch', '', 'menu', '0', null, '/marketingManager/configManager/transferExchManager', null, '_import(\'/config/transferExch/index\')', null, null, null, '2019-05-31 16:02:40', 'Tian');
INSERT INTO `base_menu` VALUES ('85', 'transferReportManager', '转账汇总', '75', '/dataReport/transfer', null, 'menu', '0', '转账汇总', '/dataReportManager/transferReportManager', null, '_import(\'dataReport/transfer/index\')', null, null, null, '2019-03-30 17:07:32', 'Tang');
INSERT INTO `base_menu` VALUES ('86', 'inAndExReportManager', '平台收支汇总', '75', '/dataReport/inAndExReport', null, 'menu', '0', '平台收支汇总', '/dataReportManager/inAndExReportManager', null, '_import(\'dataReport/inAndExReport/index\')', null, null, null, '2019-03-30 17:07:32', 'Tang');
INSERT INTO `base_menu` VALUES ('87', 'blockchainWithdrawManager', '区块链提币', '-1', '/blockchainWithdraw', 'tibi', 'dirt', '0', '数据报表管理', '/blockchainWithdrawManager', null, 'Layout', null, null, null, '2019-05-15 15:32:52', 'Tian');
INSERT INTO `base_menu` VALUES ('88', 'blockchainCoinManager', '区块链币种', '87', '/blockchainWithdraw/blockchainCoin', null, 'menu', '0', '区块链币种', '/blockchainWithdrawManager/blockchainCoinManager', null, '_import(\'blockchainWithdraw/blockchainCoin/index\')', null, null, null, '2019-03-30 17:07:32', 'Tang');
INSERT INTO `base_menu` VALUES ('89', 'withdrawAddressManager', '提币地址管理', '87', '/blockchainWithdraw/withdrawAddress', null, 'menu', '0', '区块链币种', '/blockchainWithdrawManager/withdrawAddressManager', null, '_import(\'blockchainWithdraw/withdrawAddress/index\')', null, null, null, '2019-03-30 17:07:32', 'Tang');
INSERT INTO `base_menu` VALUES ('90', 'waitWithdrawManager', '待提币管理', '87', '/blockchainWithdraw/waitWithdraw', null, 'menu', '0', '区块链币种', '/blockchainWithdrawManager/waitWithdrawManager', null, '_import(\'blockchainWithdraw/waitWithdraw/index\')', null, null, null, '2019-03-30 17:07:32', 'Tang');
INSERT INTO `base_menu` VALUES ('91', 'recordManager', '出入金记录查询', '87', '/blockchainWithdraw/record', null, 'menu', '0', '区块链币种', '/blockchainWithdrawManager/recordManager', null, '_import(\'blockchainWithdraw/record/index\')', null, null, null, '2019-03-30 17:07:32', 'Tang');
INSERT INTO `base_menu` VALUES ('92', 'blockchainConfigManager', '区块链配置', '-1', '/blockchainConfig', 'qukuailian', 'dirt', '0', '区块链配置', '/blockchainConfigManager', null, 'layout', null, null, null, '2019-06-12 18:03:00', 'Tian');
INSERT INTO `base_menu` VALUES ('93', 'bonusConfigManager', '节点分成管理', '99', '/UDSmartCommunity/bonusConfig', null, 'menu', '0', '分成比例', '/UDSmartCommunityManager/bonusConfig', null, '_import(\'UDSmartCommunity/bonusConfig/index\')', null, null, null, '2019-06-18 16:28:14', 'Tian');
INSERT INTO `base_menu` VALUES ('94', 'udParamManager', '社区参数管理', '99', '/UDSmartCommunity/udParam', null, 'menu', '0', '参数', '/UDSmartCommunityManager/udParam', null, '_import(\'UDSmartCommunity/udParam/index\')', null, null, null, '2019-06-14 16:45:23', 'Tian');
INSERT INTO `base_menu` VALUES ('95', 'hPurchaseLevelManager', '申购节点管理', '99', '/UDSmartCommunity/hPurchaseLevel', null, 'menu', '0', '申购等级', '/UDSmartCommunityManager/hPurchaseLevelManager', null, '_import(\'UDSmartCommunity/hPurchaseLevel/index\')', null, null, null, '2019-06-18 16:28:36', 'Tian');
INSERT INTO `base_menu` VALUES ('96', 'udUserManager', '社区用户管理', '99', '/UDSmartCommunity/userInfo', null, 'menu', '0', 'UD社区用户管理', '/UDSmartCommunityManager/udUserManager', null, '_import(\'UDSmartCommunity/userInfo/index\')', null, null, null, '2019-06-11 15:41:12', 'Tian');
INSERT INTO `base_menu` VALUES ('97', 'lockDetailManager', '解锁明细管理', '99', '/UDSmartCommunity/lockDetail', null, 'menu', '0', '解锁', '/UDSmartCommunityManager/lockDetailManager', null, '_import(\'UDSmartCommunity/lockDetail/index\')', null, null, null, '2019-06-14 14:09:49', 'Tian');
INSERT INTO `base_menu` VALUES ('98', 'UDOrderDetails', '匹配订单详情', '99', '/UDSmartCommunity/UDOrderDetails', null, 'menu', '0', 'UD订单详情', '/UDSmartCommunityManager/UDOrderDetails', null, '_import(\'UD/UDOrderDetails/index\')', null, null, null, '2019-06-06 16:48:00', 'Tang');
INSERT INTO `base_menu` VALUES ('99', 'UDSmartCommunityManager', 'UD智能社区', '-1', '/UDSmartCommunity', 'zhineng', 'dirt', '0', 'UD智能社区管理', '/UDSmartCommunityManager', null, 'Layout', null, null, null, '2019-06-10 18:04:37', 'Tian');
INSERT INTO `base_menu` VALUES ('100', 'configInfo', '汇聚地址管理', '92', '/blockchainConfig/configInfo', '', 'menu', '0', '配置信息', '/blockchainConfig/configInfo', null, '_import(\'blockchainConfig/configInfo/index\')', null, null, null, '2019-06-12 18:03:34', 'Tian');
INSERT INTO `base_menu` VALUES ('101', 'UDQueue', '申购排单详情', '99', '/UDSmartCommunity/UDQueue', null, 'menu', '0', '申购排单详情', '/UDSmartCommunityManager/UDQueue', null, '_import(\'UD/UDQueue/index\')', null, null, null, '2019-06-06 16:48:00', 'Tang');
INSERT INTO `base_menu` VALUES ('102', 'chargeAddressManager', '手续费地址管理', '92', '/blockchainConfig/chargeAddress', null, 'menu', '0', '手续费地址管理', '/blockchainConfig/chargeAddress', null, '_import(\'blockchainConfig/chargeAddress/index\')', null, null, null, null, null);
INSERT INTO `base_menu` VALUES ('103', 'UDCommissionDetail', '分成明细详情', '99', '/UDSmartCommunity/UDCommissionDetail', null, 'menu', '0', '分成明细详情', '/UDSmartCommunityManager/UDCommissionDetail', null, '_import(\'UD/UDCommissionDetail/index\')', null, null, null, '2019-06-06 16:48:00', 'Tang');
INSERT INTO `base_menu` VALUES ('104', 'UDCommissionReport', '分成报表统计', '99', '/UDSmartCommunity/UDCommissionReport', null, 'menu', '0', '分成报表统计', '/UDSmartCommunityManager/UDCommissionReport', null, '_import(\'UD/UDCommissionReport/index\')', null, null, null, '2019-06-06 16:48:00', 'Tang');
INSERT INTO `base_menu` VALUES ('105', 'valuationModeManager', '计价方式分配', '38', '/config/valuationMode', '', 'menu', '0', null, '/marketingManager/configManager/valuationModeManager', null, '_import(\'/config/valuationMode/index\')', null, null, null, '2019-05-31 16:02:40', 'Tian');
INSERT INTO `base_menu` VALUES ('106', 'nodeAwardManager', '节点奖励配置', '99', '/config/nodeAward', '', 'menu', '0', null, '/UDSmartCommunity/nodeAwardManager', null, '_import(\'UD/nodeAward/index\')', null, null, null, '2019-05-31 16:02:40', 'Tian');

-- ----------------------------
-- Table structure for base_menu_title
-- ----------------------------
DROP TABLE IF EXISTS `base_menu_title`;
CREATE TABLE `base_menu_title` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(128) DEFAULT NULL COMMENT '名称',
  `language_type` varchar(128) DEFAULT NULL COMMENT '语言',
  `menu_id` bigint(11) NOT NULL COMMENT '菜单id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of base_menu_title
-- ----------------------------
INSERT INTO `base_menu_title` VALUES ('1', 'User Management', 'en', '1');
INSERT INTO `base_menu_title` VALUES ('3', 'Menu Management', 'en', '6');
INSERT INTO `base_menu_title` VALUES ('4', 'Permission Configuration Management', 'en', '7');
INSERT INTO `base_menu_title` VALUES ('5', 'User Types Management', 'en', '8');
INSERT INTO `base_menu_title` VALUES ('6', 'Log Operation', 'en', '27');
INSERT INTO `base_menu_title` VALUES ('7', 'Department Management', 'en', '35');
INSERT INTO `base_menu_title` VALUES ('8', 'Statistics Parameter', 'en', '36');
INSERT INTO `base_menu_title` VALUES ('9', 'Country Management', 'en', '43');
INSERT INTO `base_menu_title` VALUES ('10', 'Basic Configuration Management', 'en', '5');
INSERT INTO `base_menu_title` VALUES ('15', 'Marketing Management', 'en', '37');
INSERT INTO `base_menu_title` VALUES ('16', 'Transaction Management', 'en', '38');
INSERT INTO `base_menu_title` VALUES ('17', 'Basic Currency Management', 'en', '39');
INSERT INTO `base_menu_title` VALUES ('18', 'Deposit / Withdraw Management', 'en', '40');
INSERT INTO `base_menu_title` VALUES ('19', 'Transaction Fee Sample', 'en', '41');
INSERT INTO `base_menu_title` VALUES ('20', 'Currency Exchange Management', 'en', '42');
INSERT INTO `base_menu_title` VALUES ('21', 'Email Management', 'en', '44');
INSERT INTO `base_menu_title` VALUES ('22', 'Email Sending Configuration', 'en', '45');
INSERT INTO `base_menu_title` VALUES ('23', 'Information Dictionary', 'en', '46');
INSERT INTO `base_menu_title` VALUES ('24', 'Client Quoted Price Management', 'en', '48');
INSERT INTO `base_menu_title` VALUES ('25', 'Email Sample Configuration', 'en', '47');
INSERT INTO `base_menu_title` VALUES ('26', 'Email Verification Management', 'en', '49');
INSERT INTO `base_menu_title` VALUES ('27', 'SMS Management', 'en', '50');
INSERT INTO `base_menu_title` VALUES ('28', 'SMS Sending Configuration', 'en', '51');
INSERT INTO `base_menu_title` VALUES ('29', 'User’s Information Detail', 'en', '53');
INSERT INTO `base_menu_title` VALUES ('30', 'User’s Information Management', 'en', '52');
INSERT INTO `base_menu_title` VALUES ('31', 'Assets Management', 'en', '56');
INSERT INTO `base_menu_title` VALUES ('32', 'Income Record Management', 'en', '57');
INSERT INTO `base_menu_title` VALUES ('33', 'User Cash Withdraw Management', 'en', '58');
INSERT INTO `base_menu_title` VALUES ('34', 'Documentary Transaction Management', 'en', '54');
INSERT INTO `base_menu_title` VALUES ('35', 'User Transfer Management', 'en', '60');
INSERT INTO `base_menu_title` VALUES ('36', 'Merchant Information Management', 'en', '63');
INSERT INTO `base_menu_title` VALUES ('37', 'User Asset Management', 'en', '64');
INSERT INTO `base_menu_title` VALUES ('38', 'Token Transaction Management', 'en', '69');
INSERT INTO `base_menu_title` VALUES ('39', 'Advertising Management', 'en', '65');
INSERT INTO `base_menu_title` VALUES ('40', 'Notification Management', 'en', '72');
INSERT INTO `base_menu_title` VALUES ('41', '数据报表en', 'en', '75');
INSERT INTO `base_menu_title` VALUES ('42', 'Withdraw Summary', 'en', '76');
INSERT INTO `base_menu_title` VALUES ('43', 'Income Summary', 'en', '79');
INSERT INTO `base_menu_title` VALUES ('44', 'Asset Summary', 'en', '80');
INSERT INTO `base_menu_title` VALUES ('45', 'Currency Description Sample', 'en', '81');
INSERT INTO `base_menu_title` VALUES ('46', 'Currency Description Management', 'en', '82');
INSERT INTO `base_menu_title` VALUES ('47', 'Currency Authorization Configuration', 'en', '83');
INSERT INTO `base_menu_title` VALUES ('48', 'Version Update Management', 'en', '73');
INSERT INTO `base_menu_title` VALUES ('49', 'Transfer Summary', 'en', '85');
INSERT INTO `base_menu_title` VALUES ('50', 'Merchant Transaction Management', 'en', '70');
INSERT INTO `base_menu_title` VALUES ('51', 'Order Refund Management', 'en', '71');
INSERT INTO `base_menu_title` VALUES ('52', 'Documentary Product Management', 'en', '55');
INSERT INTO `base_menu_title` VALUES ('53', 'Documentary Managing Staff', 'en', '59');
INSERT INTO `base_menu_title` VALUES ('54', 'Documentary Strategy Management', 'en', '61');
INSERT INTO `base_menu_title` VALUES ('55', 'Documentray Settlement', 'en', '62');
INSERT INTO `base_menu_title` VALUES ('56', 'Transaction Hedge Management', 'en', '74');
INSERT INTO `base_menu_title` VALUES ('57', 'Currency Icon Management', 'en', '77');
INSERT INTO `base_menu_title` VALUES ('58', 'Transaction Authorization Configuration', 'en', '84');
INSERT INTO `base_menu_title` VALUES ('59', '区块链提币', 'en', '87');
INSERT INTO `base_menu_title` VALUES ('60', '区块链币种', 'en', '88');
INSERT INTO `base_menu_title` VALUES ('61', '提币地址管理', 'en', '89');
INSERT INTO `base_menu_title` VALUES ('62', '待提币管理', 'en', '90');
INSERT INTO `base_menu_title` VALUES ('63', '出入金记录查询', 'en', '91');
INSERT INTO `base_menu_title` VALUES ('69', '节点分成管理', 'en', '93');
INSERT INTO `base_menu_title` VALUES ('71', '社区参数', 'en', '94');
INSERT INTO `base_menu_title` VALUES ('72', '区块链配置', 'en', '92');
INSERT INTO `base_menu_title` VALUES ('73', '汇聚地址管理', 'en', '100');
INSERT INTO `base_menu_title` VALUES ('74', '手续费地址管理', 'en', '102');
INSERT INTO `base_menu_title` VALUES ('75', 'UD智能社区', 'en', '99');
INSERT INTO `base_menu_title` VALUES ('76', '申购节点管理', 'en', '95');
INSERT INTO `base_menu_title` VALUES ('77', '社区用户管理', 'en', '96');
INSERT INTO `base_menu_title` VALUES ('78', '解锁明细管理', 'en', '97');
INSERT INTO `base_menu_title` VALUES ('79', '匹配订单详情', 'en', '98');
INSERT INTO `base_menu_title` VALUES ('80', '申购排单详情', 'en', '101');
INSERT INTO `base_menu_title` VALUES ('81', '分成明细详情', 'en', '103');
INSERT INTO `base_menu_title` VALUES ('82', '分成报表统计', 'en', '104');
INSERT INTO `base_menu_title` VALUES ('83', '计价方式分配', 'en', '105');
INSERT INTO `base_menu_title` VALUES ('84', '节点奖励配置', 'en', '106');

-- ----------------------------
-- Table structure for base_param
-- ----------------------------
DROP TABLE IF EXISTS `base_param`;
CREATE TABLE `base_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '参数编号',
  `param_key` varchar(50) DEFAULT NULL COMMENT '参数键名',
  `param_value` varchar(300) DEFAULT NULL COMMENT '参数键值',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `status` tinyint(4) DEFAULT '1',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_key` (`param_key`) USING BTREE,
  KEY `index_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8 COMMENT='全局参数表';

-- ----------------------------
-- Records of base_param
-- ----------------------------
INSERT INTO `base_param` VALUES ('46', 'exchange_lock_password_basic', '1', '用户登录次数锁定时长，单位为小时', '1', null, null, '2019-04-19 15:47:32', 'Tian');
INSERT INTO `base_param` VALUES ('47', 'exchange_lock_basic', '24', '用户修改信息后的锁定时间', '1', null, null, null, null);
INSERT INTO `base_param` VALUES ('48', 'token_expire', '3600', 'token 失效时间', '1', null, null, null, null);
INSERT INTO `base_param` VALUES ('49', 'trans_order_expire', '7200', '转账订单失效时间', '1', null, null, null, null);
INSERT INTO `base_param` VALUES ('50', 'mch_order_expire', '7200', '商户订单失效时间', '1', null, null, null, null);
INSERT INTO `base_param` VALUES ('51', 'trans_coin_order_expire', '7200', '转币订单失效时间', '1', null, null, null, null);

-- ----------------------------
-- Table structure for base_resource_authority
-- ----------------------------
DROP TABLE IF EXISTS `base_resource_authority`;
CREATE TABLE `base_resource_authority` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `authority_id` varchar(255) DEFAULT NULL COMMENT '角色ID',
  `authority_type` varchar(255) DEFAULT NULL COMMENT '角色类型',
  `resource_id` varchar(255) DEFAULT NULL COMMENT '资源ID',
  `resource_type` varchar(255) DEFAULT NULL COMMENT '资源类型',
  `parent_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8426 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of base_resource_authority
-- ----------------------------
INSERT INTO `base_resource_authority` VALUES ('287', '1', 'group', '5', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('294', '1', 'group', '5', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('313', '1', 'group', '16', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('314', '1', 'group', '17', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('315', '1', 'group', '18', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('317', '1', 'group', '20', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('318', '1', 'group', '21', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('319', '1', 'group', '22', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('371', '1', 'group', '23', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('374', '1', 'group', '28', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('375', '1', 'group', '23', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('378', '1', 'group', '5', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('388', '1', 'group', '16', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('389', '1', 'group', '18', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('390', '1', 'group', '17', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('392', '1', 'group', '20', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('393', '1', 'group', '28', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('394', '1', 'group', '22', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('395', '1', 'group', '21', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('401', '1', 'group', '30', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('402', '1', 'group', '30', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('403', '1', 'group', '31', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('421', '1', 'group', '31', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('422', '1', 'group', '30', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('436', '1', 'group', '32', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('437', '1', 'group', '33', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('438', '1', 'group', '34', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('439', '1', 'group', '35', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('464', '1', 'group', '30', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('465', '1', 'group', '31', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('466', '1', 'group', '30', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('467', '1', 'group', '31', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('468', '1', 'group', '30', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('469', '1', 'group', '31', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('470', '1', 'group', '30', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('471', '1', 'group', '31', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('472', '1', 'group', '40', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('492', '1', 'group', '30', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('493', '1', 'group', '31', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('494', '1', 'group', '40', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('666', '1', 'group', '41', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('689', '1', 'group', '43', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('691', '1', 'group', '44', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('722', '1', 'group', '41', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('774', '1', 'group', '3', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('775', '1', 'group', '4', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('812', '1', 'group', '19', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('924', '1', 'group', '42', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('945', '1', 'group', '45', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('956', '1', 'group', '46', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('986', '1', 'group', '47', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('987', '1', 'group', '48', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('988', '1', 'group', '49', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('989', '1', 'group', '50', 'button', '-1');


INSERT INTO `base_resource_authority` VALUES ('1345', '1', 'group', '51', 'button', '-1');


INSERT INTO `base_resource_authority` VALUES ('1782', '1', 'group', '52', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1812', '1', 'group', '53', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1813', '1', 'group', '56', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1814', '1', 'group', '54', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1815', '1', 'group', '55', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1849', '1', 'group', '57', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1850', '1', 'group', '59', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1851', '1', 'group', '58', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1852', '1', 'group', '60', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1891', '1', 'group', '66', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1892', '1', 'group', '68', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1893', '1', 'group', '67', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1894', '1', 'group', '65', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1915', '1', 'group', '69', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1916', '1', 'group', '70', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1917', '1', 'group', '72', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1918', '1', 'group', '71', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('1996', '1', 'group', '77', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2015', '1', 'group', '80', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2016', '1', 'group', '78', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2017', '1', 'group', '79', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2018', '1', 'group', '81', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('2047', '1', 'group', '82', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2048', '1', 'group', '83', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2049', '1', 'group', '85', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2050', '1', 'group', '84', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('2260', '1', 'group', '12', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2261', '1', 'group', '9', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2262', '1', 'group', '11', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2263', '1', 'group', '10', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2264', '1', 'group', '13', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2265', '1', 'group', '14', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2266', '1', 'group', '15', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2267', '1', 'group', '24', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2268', '1', 'group', '27', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2269', '1', 'group', '86', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2271', '1', 'group', '88', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2272', '1', 'group', '87', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2335', '1', 'group', '94', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2340', '1', 'group', '95', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2341', '1', 'group', '96', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2342', '1', 'group', '97', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('2418', '1', 'group', '90', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2419', '1', 'group', '92', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2420', '1', 'group', '91', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2421', '1', 'group', '93', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2422', '1', 'group', '98', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2423', '1', 'group', '101', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2424', '1', 'group', '99', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2515', '1', 'group', '103', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2516', '1', 'group', '102', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2517', '1', 'group', '104', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2518', '1', 'group', '105', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2557', '1', 'group', '107', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2558', '1', 'group', '108', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2559', '1', 'group', '106', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2560', '1', 'group', '109', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2606', '1', 'group', '112', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2607', '1', 'group', '111', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2608', '1', 'group', '110', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2609', '1', 'group', '113', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2636', '1', 'group', '117', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2637', '1', 'group', '115', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2638', '1', 'group', '114', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2639', '1', 'group', '116', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2765', '1', 'group', '118', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2766', '1', 'group', '119', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2767', '1', 'group', '120', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2768', '1', 'group', '121', 'button', '-1');


INSERT INTO `base_resource_authority` VALUES ('2891', '1', 'group', '122', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('2918', '1', 'group', '123', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3034', '1', 'group', '125', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3035', '1', 'group', '124', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3036', '1', 'group', '127', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3037', '1', 'group', '126', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3069', '1', 'group', '130', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3070', '1', 'group', '128', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3071', '1', 'group', '129', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3103', '1', 'group', '133', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3104', '1', 'group', '131', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3105', '1', 'group', '132', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3106', '1', 'group', '134', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3264', '1', 'group', '138', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3265', '1', 'group', '135', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3266', '1', 'group', '136', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3267', '1', 'group', '137', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3302', '1', 'group', '139', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3303', '1', 'group', '140', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3339', '1', 'group', '141', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3375', '1', 'group', '142', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('3524', '1', 'group', '143', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3626', '1', 'group', '144', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3627', '1', 'group', '146', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3628', '1', 'group', '145', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3629', '1', 'group', '147', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3668', '1', 'group', '148', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3669', '1', 'group', '149', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3670', '1', 'group', '150', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3671', '1', 'group', '151', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3712', '1', 'group', '153', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3713', '1', 'group', '152', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3714', '1', 'group', '155', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3715', '1', 'group', '154', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('3966', '1', 'group', '157', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3967', '1', 'group', '158', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3968', '1', 'group', '156', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('3969', '1', 'group', '159', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4015', '1', 'group', '160', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('4156', '1', 'group', '161', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4157', '1', 'group', '162', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4158', '1', 'group', '163', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4159', '1', 'group', '164', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4206', '1', 'group', '166', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4207', '1', 'group', '165', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4304', '1', 'group', '168', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4305', '1', 'group', '169', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4306', '1', 'group', '167', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4307', '1', 'group', '170', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('4636', '1', 'group', '172', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4637', '1', 'group', '171', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4638', '1', 'group', '173', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4639', '1', 'group', '174', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4640', '1', 'group', '176', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4641', '1', 'group', '175', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4642', '1', 'group', '178', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('4643', '1', 'group', '177', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('5068', '1', 'group', '100', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('5074', '1', 'group', '89', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('5250', '1', 'group', '180', 'button', '-1');



INSERT INTO `base_resource_authority` VALUES ('6355', '1', 'group', '181', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('6420', '1', 'group', '182', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('6460', '1', 'group', '179', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('6525', '1', 'group', '184', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('6526', '1', 'group', '183', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('6527', '1', 'group', '185', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('6901', '1', 'group', '186', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('6902', '1', 'group', '188', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('6903', '1', 'group', '187', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('6904', '1', 'group', '189', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('6905', '1', 'group', '190', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7049', '1', 'group', '192', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7050', '1', 'group', '191', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7051', '1', 'group', '193', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7052', '1', 'group', '194', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('7531', '1', 'group', '195', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7532', '1', 'group', '196', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7533', '1', 'group', '198', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7534', '1', 'group', '197', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7686', '1', 'group', '199', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7687', '1', 'group', '200', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7688', '1', 'group', '201', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7689', '1', 'group', '202', 'button', '-1');

INSERT INTO `base_resource_authority` VALUES ('7891', '1', 'group', '203', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('7969', '1', 'group', '204', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('8047', '1', 'group', '208', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('8048', '1', 'group', '206', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('8049', '1', 'group', '205', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('8050', '1', 'group', '207', 'button', '-1');


INSERT INTO `base_resource_authority` VALUES ('8303', '1', 'group', '212', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('8304', '1', 'group', '209', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('8305', '1', 'group', '210', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('8306', '1', 'group', '211', 'button', '-1');
INSERT INTO `base_resource_authority` VALUES ('8307', '1', 'group', '44', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8308', '1', 'group', '88', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8309', '1', 'group', '45', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8310', '1', 'group', '89', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8311', '1', 'group', '46', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8312', '1', 'group', '47', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8313', '1', 'group', '48', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8314', '1', 'group', '49', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8315', '1', 'group', '90', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8316', '1', 'group', '91', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8317', '1', 'group', '92', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8318', '1', 'group', '93', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8319', '1', 'group', '50', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8320', '1', 'group', '94', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8321', '1', 'group', '51', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8322', '1', 'group', '95', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8323', '1', 'group', '52', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8324', '1', 'group', '96', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8325', '1', 'group', '53', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8326', '1', 'group', '97', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8327', '1', 'group', '54', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8328', '1', 'group', '98', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8329', '1', 'group', '55', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8330', '1', 'group', '99', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8331', '1', 'group', '56', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8332', '1', 'group', '57', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8333', '1', 'group', '58', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8334', '1', 'group', '59', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8335', '1', 'group', '-1', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8336', '1', 'group', '1', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8337', '1', 'group', '5', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8338', '1', 'group', '6', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8339', '1', 'group', '7', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8340', '1', 'group', '8', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8341', '1', 'group', '60', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8342', '1', 'group', '61', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8343', '1', 'group', '62', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8344', '1', 'group', '63', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8345', '1', 'group', '64', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8346', '1', 'group', '65', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8347', '1', 'group', '66', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8348', '1', 'group', '67', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8349', '1', 'group', '68', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8350', '1', 'group', '69', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8351', '1', 'group', '27', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8352', '1', 'group', '70', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8353', '1', 'group', '71', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8354', '1', 'group', '72', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8355', '1', 'group', '73', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8356', '1', 'group', '74', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8357', '1', 'group', '75', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8358', '1', 'group', '76', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8359', '1', 'group', '77', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8360', '1', 'group', '78', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8361', '1', 'group', '35', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8362', '1', 'group', '79', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8363', '1', 'group', '36', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8364', '1', 'group', '37', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8365', '1', 'group', '38', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8366', '1', 'group', '39', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8367', '1', 'group', '100', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8368', '1', 'group', '101', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8369', '1', 'group', '102', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8370', '1', 'group', '103', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8371', '1', 'group', '104', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8372', '1', 'group', '105', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8373', '1', 'group', '106', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8374', '1', 'group', '80', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8375', '1', 'group', '81', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8376', '1', 'group', '82', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8377', '1', 'group', '83', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8378', '1', 'group', '40', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8379', '1', 'group', '84', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8380', '1', 'group', '41', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8381', '1', 'group', '85', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8382', '1', 'group', '42', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8383', '1', 'group', '86', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8384', '1', 'group', '43', 'menu', '-1');
INSERT INTO `base_resource_authority` VALUES ('8385', '1', 'group', '87', 'menu', '-1');


-- ----------------------------
-- Table structure for base_sms
-- ----------------------------
DROP TABLE IF EXISTS `base_sms`;
CREATE TABLE `base_sms` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `biz_id` varchar(64) NOT NULL COMMENT '平台编号',
  `type` varchar(32) NOT NULL COMMENT '类型',
  `phone` varchar(20) NOT NULL COMMENT '接收短信号码',
  `content` varchar(300) NOT NULL COMMENT '短信内容',
  `send_state` varchar(1) NOT NULL COMMENT '发送状态0-未成功 1-成功',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COMMENT='短信记录表';



-- ----------------------------
-- Table structure for base_sms_config
-- ----------------------------
DROP TABLE IF EXISTS `base_sms_config`;
CREATE TABLE `base_sms_config` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `white_exch_id` bigint(11) NOT NULL DEFAULT '1' COMMENT '白标交易所id',
  `country_code` varchar(10) NOT NULL DEFAULT '86' COMMENT '国家编号(0086)',
  `sms_plat_url` varchar(128) DEFAULT NULL COMMENT '短信平台地址',
  `sms_plat_account` varchar(255) DEFAULT NULL COMMENT '短信平台帐号',
  `sms_plat_password` varchar(64) DEFAULT NULL COMMENT '短信平台密码',
  `sender_signature` varchar(32) DEFAULT NULL COMMENT '发送短信签名',
  `remark_` varchar(300) DEFAULT NULL COMMENT '备注',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of base_sms_config
-- ----------------------------
INSERT INTO `base_sms_config` VALUES ('1', '1', '86', 'https://api.surem.com/intl/text', 'udaxkrsms', '82-GFX-OH', '【udax】', '12344', '2019-03-18 17:27:41', 'Tang', '2019-05-09 11:20:01', 'TangHK');
INSERT INTO `base_sms_config` VALUES ('3', '2', '60', 'https://www.sms123.net/xmlgateway.php', '.php	copycash.customerservice@gmail.com', '	8888', '【udax】8888', '2341', '2019-03-18 17:38:57', 'Tang', '2019-03-18 17:39:03', 'Tang');

-- ----------------------------
-- Table structure for base_user
-- ----------------------------
DROP TABLE IF EXISTS `base_user`;
CREATE TABLE `base_user` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `birthday` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `mobile_phone` varchar(255) DEFAULT NULL,
  `tel_phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `sex` char(1) DEFAULT NULL,
  `type` char(1) DEFAULT NULL,
  `status` char(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `dept_id` bigint(11) DEFAULT NULL,
  `dept_name` varchar(255) DEFAULT NULL,
  `group_id` bigint(11) DEFAULT NULL COMMENT '角色类型id',
  `top_parent_id` bigint(11) NOT NULL COMMENT '顶级白标Id',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of base_user
-- ----------------------------
INSERT INTO `base_user` VALUES ('1', 'admin', '$2a$12$S/yLlj9kzi5Dgsz97H4rAekxrPlk/10eXp1lUJcAVAx.2M9tOpWie', '2019-03-07', 'admin', '321', '133266526', '6546', '262@63.com', '男', null, null, '', '1', '开发部', '1', '-1', null, null, '2019-04-15 17:49:11', 'Tian');


-- ----------------------------
-- Table structure for base_version
-- ----------------------------
DROP TABLE IF EXISTS `base_version`;
CREATE TABLE `base_version` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `version_code` varchar(10) NOT NULL COMMENT '版本号',
  `url` varchar(150) NOT NULL COMMENT '版本下载url',
  `content` varchar(400) NOT NULL COMMENT '版本改动内容',
  `title` varchar(50) NOT NULL COMMENT '版本标题',
  `update_install` tinyint(1) NOT NULL COMMENT '是否强制更新 1:是; 2:不是',
  `version_channel` tinyint(1) NOT NULL COMMENT '版本渠道（系统枚举 android or ios）',
  `exch_id` bigint(11) NOT NULL COMMENT '交易所id',
  `crt_time` datetime DEFAULT NULL COMMENT '创建时间',
  `crt_name` varchar(255) DEFAULT NULL COMMENT '创建人',
  `upd_time` datetime DEFAULT NULL COMMENT '更新时间',
  `upd_name` varchar(255) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;



-- ----------------------------
-- Table structure for basic_symbol
-- ----------------------------
DROP TABLE IF EXISTS `basic_symbol`;
CREATE TABLE `basic_symbol` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `currency_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1.数字货币;2.法定货币',
  `is_quote` tinyint(4) NOT NULL COMMENT '币种是否支持用户报价: 0 不支持 ,1支持',
  `symbol` varchar(30) NOT NULL,
  `image_url` varchar(128) NOT NULL,
  `status` tinyint(4) NOT NULL COMMENT '1.正常;0.停用',
  `remark` varchar(30) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_symbol` (`symbol`) USING BTREE,
  KEY `index_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COMMENT='货币信息表';


-- ----------------------------
-- Table structure for basic_symbol_image
-- ----------------------------
DROP TABLE IF EXISTS `basic_symbol_image`;
CREATE TABLE `basic_symbol_image` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `exchange_id` bigint(11) NOT NULL COMMENT '交易所id',
  `image_url` varchar(300) NOT NULL COMMENT '白标币种图标地址',
  `basic_symbol_id` bigint(11) NOT NULL COMMENT '币种主键id',
  `symbol` varchar(30) DEFAULT NULL COMMENT '币种名称',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `remark` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for cfg_charge_template
-- ----------------------------
DROP TABLE IF EXISTS `cfg_charge_template`;
CREATE TABLE `cfg_charge_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '手续费id',
  `charge_type` tinyint(4) DEFAULT NULL COMMENT '1. 固定值;2.比例;3:点差',
  `charge_value` decimal(20,8) DEFAULT NULL COMMENT '手续费固定值',
  `status` tinyint(4) DEFAULT NULL COMMENT '1.正常;0.停用',
  `remark` varchar(30) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='手续费模板表';

-- ----------------------------
-- Records of cfg_charge_template
-- ----------------------------
INSERT INTO `cfg_charge_template` VALUES ('1', '2', '0.00300000', '1', '商户默认模板 比例:0.003 %', null, null, '2019-05-09 19:26:07', 'Tian');

-- ----------------------------
-- Table structure for cfg_currency_charge
-- ----------------------------
DROP TABLE IF EXISTS `cfg_currency_charge`;
CREATE TABLE `cfg_currency_charge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `real_user_level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '对应用户级别',
  `currency_type` tinyint(4) NOT NULL COMMENT '1.数字货币; 2.法定货币;',
  `symbol` varchar(30) NOT NULL COMMENT '货币代码',
  `trade_charge_id` bigint(20) DEFAULT '1' COMMENT '交易手续费id(默认1)',
  `dc_withdraw_charge_id` bigint(20) DEFAULT '1' COMMENT '提币手续费id(默认1)',
  `lt_withdraw_charge_id` bigint(20) DEFAULT '1' COMMENT '出金手续费id (默认1)',
  `spread` decimal(20,8) DEFAULT NULL COMMENT '交易点差',
  `crt_time` datetime DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `remark` varchar(30) DEFAULT NULL,
  `exch_id` bigint(11) DEFAULT NULL COMMENT '交易所id(-1表示默认手续费)',
  `basic_symbol_id` bigint(11) DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Unique_symbol_exchid` (`exch_id`,`basic_symbol_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COMMENT='货币配置表';



-- ----------------------------
-- Table structure for cfg_currency_transfer
-- ----------------------------
DROP TABLE IF EXISTS `cfg_currency_transfer`;
CREATE TABLE `cfg_currency_transfer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transfer_type` tinyint(4) NOT NULL COMMENT '1.平台接收; 2.用户报价接收',
  `src_symbol` varchar(30) NOT NULL COMMENT '例: BTC',
  `dst_symbol` varchar(30) NOT NULL COMMENT '例: USDT',
  `symbol` varchar(30) DEFAULT NULL COMMENT '例: BTC/USDT,不需对冲的可不填 (暂时没用)',
  `hedge_flag` tinyint(4) NOT NULL COMMENT '1.需要对冲;2.不需对冲',
  `min_trans_amount` decimal(20,8) NOT NULL COMMENT '原货币单次最小转币量',
  `max_trans_amount` decimal(20,8) NOT NULL COMMENT '原货币单次最大转币量',
  `offset_price` decimal(20,8) DEFAULT NULL COMMENT '成交偏差最大值',
  `charge_id` bigint(11) NOT NULL,
  `charge_dc_code` varchar(30) NOT NULL COMMENT '手续费币种',
  `sort` int(20) DEFAULT NULL,
  `status` tinyint(4) NOT NULL COMMENT '1.正常;0.停用',
  `crt_time` datetime DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(128) DEFAULT NULL,
  `crt_name` varchar(128) DEFAULT NULL,
  `remark` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_TSD` (`src_symbol`,`dst_symbol`),
  KEY `Index_SC` (`src_symbol`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COMMENT='通用数字货币转换配置表';


-- ----------------------------
-- Table structure for cfg_dc_recharge_withdraw
-- ----------------------------
DROP TABLE IF EXISTS `cfg_dc_recharge_withdraw`;
CREATE TABLE `cfg_dc_recharge_withdraw` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(30) NOT NULL COMMENT '货币代码',
  `status` tinyint(4) DEFAULT NULL COMMENT '1.正常;0.停用;',
  `recharge_status` tinyint(4) DEFAULT '0' COMMENT '充币状态 1.可用;0.停用',
  `withdraw_status` tinyint(4) DEFAULT '0' COMMENT '提币状态 1.可用;0.停用',
  `min_withdraw_amount` decimal(20,8) NOT NULL COMMENT '最小提币数量',
  `max_withdraw_amount` decimal(20,8) NOT NULL COMMENT '最大提币数量',
  `min_recharge_amount` decimal(20,8) NOT NULL COMMENT '最小充币数量',
  `max_withdraw_day` decimal(20,8) DEFAULT NULL COMMENT '当日最大提币限额',
  `exch_id` bigint(20) DEFAULT '-1' COMMENT '-1 表示钱包默认配置  其他表示交易所配置',
  `system_config` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:表示系统配置 1:表示交易所配置',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `remark` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_symbol` (`symbol`,`exch_id`,`system_config`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COMMENT='货币配置表';



-- ----------------------------
-- Table structure for cfg_description_template
-- ----------------------------
DROP TABLE IF EXISTS `cfg_description_template`;
CREATE TABLE `cfg_description_template` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `withdraw_desp` text NOT NULL COMMENT '通用的提币提示信息模板',
  `recharge_desp` text NOT NULL COMMENT '通用的充币描述信息模板',
  `transfer_desp` text NOT NULL COMMENT '用户与用户之间的转账描述信息',
  `language_type` varchar(10) NOT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `remark` varchar(128) DEFAULT NULL COMMENT '描述信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='币种提币充值描述信息通用模板';

-- ----------------------------
-- Records of cfg_description_template
-- ----------------------------
INSERT INTO `cfg_description_template` VALUES ('1', '<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">温馨提示</span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull; 最小提币数量为：%s %s。</span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull; 最大提币限额为：%s %s。</span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull; 当日最大提币限额为：%s %s。</span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull; 为保障资金安全，当您账户安全策略变更、密码修改、使用新地址提币，我们会对提币进行人工审核，请耐心等待工作人员电话或邮件联系。</span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull; 请务必确认电脑及浏览器安全，防止信息被篡改或泄露。</span></p>', '<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">温馨提示</span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull; 请勿向上述地址充值任何非 %s 资产，否则资产将不可找回。</span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull; 最小充值金额：%s %s，小于最小金额的充值将不会上账且无法退回。</span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull; 您的充值地址不会经常改变，可以重复充值；如有更改，我们会尽量通过网站公告或邮件通知您。 </span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull; 请务必确认电脑及浏览器安全，防止信息被篡改或泄露.</span></p>', '<p>转账模板sdfsdfdsfsfsdfsdfsdfsdf</p>', 'zh', '2019-04-28 14:29:55', 'Tian', '2019-06-10 10:26:03', 'pubaoyu', '中文代币描述信息');
INSERT INTO `cfg_description_template` VALUES ('2', '<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">Tips</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&nbsp;</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Minimum withdrawal amount: %s %s.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Maximum withdrawal amount: %s&nbsp; %s.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\"><span style=\"font-family: 微软雅黑, \'Microsoft YaHei\'; font-size: 14px;\">&bull;当日最大提币限额为：%s %s。</span></p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; For security reason, when you change security settings, change password and use new withdraw address, we will manually review your withdrawal. Please wait for phone calls or emails from our staff.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Please make sure that your computer and browser are secure and your information is protected from being tampered or leaked.</p>', '<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">Tips</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&nbsp;</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Please don&rsquo;t deposit any other digital assets except %s to the above address.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Minimum deposit amount: %s %s. Any deposits less than the minimum will not be credited or refunded.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Your deposit address won&rsquo;t be changed frequently. Any changes, we will inform you by announcement or email.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Please make sure that your computer and browser are secure and your information is protected from being tampered or leaked.</p>', '<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">Tips</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Please don&rsquo;t deposit any other digital assets except %s to the above address.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Depositing to the above address requires confirmations of the entire network. It will arrive after 8confirmations, and it will be available to withdraw after 12confirmations.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Minimum deposit amount: %s %s. Any deposits less than the minimum will not be credited or refunded.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Your deposit address won&rsquo;t be changed frequently. Any changes, we will inform you by announcement or email.</p>\n<p style=\"margin: 5px 0px; color: #000000; font-family: sans-serif; font-size: 16px; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial;\">&bull; Please make sure that your computer and browser are secure and your information is protected from being tampered or leaked.</p>', 'en', '2019-04-28 14:51:26', 'Tian', '2019-05-31 04:34:22', 'admin', '英文币种提示信息');

-- ----------------------------
-- Table structure for cfg_symbol_description
-- ----------------------------
DROP TABLE IF EXISTS `cfg_symbol_description`;
CREATE TABLE `cfg_symbol_description` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(30) NOT NULL COMMENT '代币',
  `symbol_id` bigint(11) NOT NULL COMMENT '代币主键',
  `exchange_id` bigint(11) NOT NULL COMMENT '交易所id',
  `withdraw_desp` text NOT NULL COMMENT '币种提币描述信息',
  `recharge_desp` text NOT NULL COMMENT '币种充值描述',
  `transfer_desp` text NOT NULL COMMENT '用户与用户之间的转账描述信息',
  `language_type` varchar(30) NOT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `remark` varchar(128) DEFAULT NULL COMMENT '描述信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='币种提币充值描述信息表';


-- ----------------------------
-- Table structure for dc_asset_account
-- ----------------------------
DROP TABLE IF EXISTS `dc_asset_account`;
CREATE TABLE `dc_asset_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `symbol` varchar(30) NOT NULL COMMENT '代币代码',
  `total_amount` decimal(20,8) DEFAULT NULL COMMENT '总资产',
  `available_amount` decimal(20,8) DEFAULT NULL COMMENT '可用资产',
  `freeze_amount` decimal(20,8) DEFAULT NULL COMMENT '冻结资产',
  `wait_confirm_amount` decimal(20,8) DEFAULT NULL COMMENT '待确认结算资产',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `umac` varchar(50) DEFAULT NULL COMMENT 'umac数据校验',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key1` (`user_id`,`symbol`)
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=utf8mb4 COMMENT='数字货币钱包账户表';



-- ----------------------------
-- Table structure for dc_asset_account_log
-- ----------------------------
DROP TABLE IF EXISTS `dc_asset_account_log`;
CREATE TABLE `dc_asset_account_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `symbol` varchar(30) NOT NULL COMMENT '变动币种',
  `direction` tinyint(4) NOT NULL COMMENT '0:收入  1:支出',
  `trans_no` varchar(32) DEFAULT NULL COMMENT '钱包业务流水号',
  `type` tinyint(4) NOT NULL COMMENT '1.转账给平台其他用户 2.收到转账  3.转入基金账户 4.从基金账户转出\n5.提现成功  6.转换币支出 7.转换币收入  8. 充值 9.用户给商户支付款项\n10.商户收到用户支付的款项 11.商户退款支出 12.用户收到退款\n13.提现冻结 14.用户报价冻结 ',
  `amount` decimal(20,8) NOT NULL COMMENT '实际收入/支出数量',
  `charge_amount` decimal(20,8) DEFAULT '0.00000000' COMMENT '交易涉及到的手续费',
  `charge_symbol` varchar(30) DEFAULT NULL COMMENT '所收手续费币种',
  `create_time` datetime NOT NULL COMMENT '流水生成时间',
  `pre_total` decimal(20,8) NOT NULL COMMENT '变动前总资产',
  `pre_available` decimal(20,8) NOT NULL COMMENT '变动前可用资产',
  `pre_freeze` decimal(20,8) NOT NULL COMMENT '变动前冻结资产',
  `pre_waitconfirm` decimal(20,8) NOT NULL COMMENT '变动前待结算资产',
  `after_total` decimal(20,8) NOT NULL COMMENT '变动后总资产',
  `after_available` decimal(20,8) NOT NULL COMMENT '变动后可用资产',
  `after_freeze` decimal(20,8) NOT NULL COMMENT '变动后冻结资产',
  `after_waitconfirm` decimal(20,8) NOT NULL COMMENT '变动后待结算资产',
  `remark` varchar(128) DEFAULT NULL,
  `usdt_rate` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '代币和usdt的实时汇率',
  `usdt_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '等价于usdt的数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_trans` (`trans_no`,`type`,`user_id`),
  KEY `index_charge` (`user_id`,`symbol`,`type`,`direction`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2036 DEFAULT CHARSET=utf8mb4 COMMENT='数字货币钱包账户流水表';



-- ----------------------------
-- Table structure for front_advert
-- ----------------------------
DROP TABLE IF EXISTS `front_advert`;
CREATE TABLE `front_advert` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '标题头',
  `url` text NOT NULL COMMENT '广告图片地址',
  `exchange_id` bigint(20) NOT NULL COMMENT '关联交易所',
  `status` tinyint(1) DEFAULT NULL,
  `extend_url` varchar(200) DEFAULT NULL COMMENT '扩展链接',
  `language_type` varchar(10) NOT NULL COMMENT '语言',
  `client_type` tinyint(4) DEFAULT NULL COMMENT '客户端类型1 pc端、2:app端等)',
  `sort` int(20) DEFAULT NULL COMMENT '排序号',
  `remark` varchar(30) DEFAULT NULL COMMENT '备注信息',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_exchange` (`exchange_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for front_country
-- ----------------------------
DROP TABLE IF EXISTS `front_country`;
CREATE TABLE `front_country` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `code` varchar(20) NOT NULL COMMENT '国家编码(china)',
  `name` varchar(30) NOT NULL COMMENT '国家名称',
  `country_code` varchar(10) NOT NULL COMMENT '国家编号(0086)',
  `status` tinyint(4) DEFAULT NULL COMMENT '1:正常;0禁用',
  `country_image` varchar(128) DEFAULT NULL COMMENT '国家图片地址',
  `sort` int(11) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(128) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COMMENT='国家表';


-- 国家表

INSERT INTO `front_country` VALUES (1, 'China', '中国', '86', 1, 'http://wallet.udax.pro/upload//admin/b14ffdbd-e5e0-48a7-b588-8686779fac62.png', 1, NULL, NULL, '2019-6-5 09:05:29', 'pubaoyu');
INSERT INTO `front_country` VALUES (2, 'America', 'America', '001', 1, 'http://wallet.udax.pro/upload//admin/867fa7d0-c5d6-4663-980f-aa580a63ea5c.png', 2, NULL, NULL, '2019-5-29 06:23:18', 'pubaoyu');
INSERT INTO `front_country` VALUES (5, 'Japan', 'にっぽんこく', '81', 1, 'http://wallet.udax.pro/upload//admin/4804f05c-dff9-4649-9755-62adfd669e39.png', 3, NULL, NULL, '2019-5-29 07:17:22', 'pubaoyu');
INSERT INTO `front_country` VALUES (6, 'United Kingdom', 'United Kingdom', '44', 1, 'http://wallet.udax.pro/upload//admin/aadabec7-7b4b-4e55-8859-03001b7e611d.png', 4, NULL, NULL, '2019-5-29 06:25:32', 'pubaoyu');
INSERT INTO `front_country` VALUES (7, 'Korea', '한국', '82', 1, 'http://wallet.udax.pro/upload//admin/91ca2494-fe3d-4447-a809-aa20904fa5fe.png', 5, NULL, NULL, '2019-5-29 06:25:51', 'pubaoyu');
INSERT INTO `front_country` VALUES (8, 'Hong Kong(China)', '中国香港', '852', 1, 'http://wallet.udax.pro/upload//admin/05984487-8c3b-4c3e-ae09-9c927fce083f.png', 6, NULL, NULL, '2019-5-23 03:15:44', 'admin');
INSERT INTO `front_country` VALUES (9, 'Indonesia', 'Indonesia', '62', 1, 'http://wallet.udax.pro/upload//admin/6339308f-fac2-4fac-9d9b-8d2ae48ef1d9.png', 7, NULL, NULL, '2019-5-29 06:26:30', 'pubaoyu');
INSERT INTO `front_country` VALUES (10, 'Malaysia', 'Malaysia', '60', 1, 'http://wallet.udax.pro/upload//admin/e1c6acbc-36c8-44c9-a3a2-6346d69f6149.png', 8, NULL, NULL, '2019-5-29 06:26:53', 'pubaoyu');
INSERT INTO `front_country` VALUES (11, 'Philippines', 'Republika ng Pilipinas', '63', 1, 'http://wallet.udax.pro/upload//admin/07e231f9-ff8c-44b0-bf75-d4d43f808814.png', 9, NULL, NULL, '2019-5-29 06:34:38', 'pubaoyu');
INSERT INTO `front_country` VALUES (12, 'Singapore', 'Singapore', '65', 1, 'http://wallet.udax.pro/upload//admin/6a862d58-633c-4376-83c3-a8b5bcada1f7.png', 10, NULL, NULL, '2019-5-29 06:38:05', 'pubaoyu');
INSERT INTO `front_country` VALUES (13, 'Germany', 'Deutschland', '49', 1, 'http://wallet.udax.pro/upload//admin/c5396c2a-e646-4845-992d-1d0dd1e152ce.png', 11, NULL, NULL, '2019-5-29 06:38:30', 'pubaoyu');
INSERT INTO `front_country` VALUES (14, 'Canada', 'Canada', '1', 1, 'http://wallet.udax.pro/upload//admin/549942ca-9225-43ee-a641-5d0820a507e1.png', 12, NULL, NULL, '2019-5-29 06:38:56', 'pubaoyu');
INSERT INTO `front_country` VALUES (15, 'France', 'France', '33', 1, 'http://wallet.udax.pro/upload//admin/653be97c-3424-401e-bac0-853cc9e49029.png', 13, NULL, NULL, '2019-5-29 06:39:19', 'pubaoyu');
INSERT INTO `front_country` VALUES (16, 'Sweden', 'Sverige', '46', 1, 'http://wallet.udax.pro/upload//admin/467ff790-9c5e-49ff-baba-2aa34fb5fa74.png', 14, NULL, NULL, '2019-5-29 06:39:40', 'pubaoyu');
INSERT INTO `front_country` VALUES (17, 'Switzerland', 'Switzerland', '41', 1, 'http://wallet.udax.pro/upload//admin/471ebc68-440b-408f-ad60-7b776c1eb710.png', 15, NULL, NULL, '2019-5-29 06:39:58', 'pubaoyu');
INSERT INTO `front_country` VALUES (18, 'New Zealand', 'New Zealand', '64', 1, 'http://wallet.udax.pro/upload//admin/46df54fc-6060-4c05-a527-54341659d17c.png', 16, NULL, NULL, '2019-5-29 06:41:07', 'pubaoyu');
INSERT INTO `front_country` VALUES (19, 'Argentina', 'Argentina', '54', 1, 'http://wallet.udax.pro/upload//admin/1943dc7c-f66c-4ac4-9985-4a68e9dda46e.png', 17, NULL, NULL, '2019-5-29 06:41:40', 'pubaoyu');
INSERT INTO `front_country` VALUES (20, 'Australia', 'Australia', '61', 1, 'http://wallet.udax.pro/upload//admin/8e417d13-b2cd-409d-9e53-9b7dae71de9f.png', 18, NULL, NULL, '2019-5-29 06:42:07', 'pubaoyu');
INSERT INTO `front_country` VALUES (21, 'Austria', 'Austria', '43', 1, 'http://wallet.udax.pro/upload//admin/96ff70b0-0d5d-4e60-a464-edf6afa6657c.png', 19, NULL, NULL, '2019-5-29 06:42:26', 'pubaoyu');
INSERT INTO `front_country` VALUES (22, 'Brazil', 'Brasil', '55', 1, 'http://wallet.udax.pro/upload//admin/39144ce6-de72-488e-afbb-40fbe4be78a9.png', 20, NULL, NULL, '2019-5-29 06:42:52', 'pubaoyu');
INSERT INTO `front_country` VALUES (23, 'Chile', 'Chile', '56', 1, 'http://wallet.udax.pro/upload//admin/83d60c46-67ef-45a2-9c27-382030a7287b.png', 21, NULL, NULL, '2019-5-29 06:44:13', 'pubaoyu');
INSERT INTO `front_country` VALUES (24, 'Colombia', 'Colombia', '57', 1, 'http://wallet.udax.pro/upload//admin/0f5da5f3-4914-4629-a62a-b3b8938853db.png', 22, NULL, NULL, '2019-5-29 06:44:27', 'pubaoyu');
INSERT INTO `front_country` VALUES (25, 'Czech Republic', 'Česko', '420', 1, 'http://wallet.udax.pro/upload//admin/3d47ebc7-3e18-415c-acd6-7f4c6f87176c.png', 23, NULL, NULL, '2019-5-29 06:44:52', 'pubaoyu');
INSERT INTO `front_country` VALUES (26, 'Denmark', 'Danmark', '45', 1, 'http://wallet.udax.pro/upload//admin/9e62853e-22c1-47e7-8867-d39428d16c9c.png', 24, NULL, NULL, '2019-5-29 06:45:25', 'pubaoyu');
INSERT INTO `front_country` VALUES (27, 'Egypt', 'مصر', '20', 1, 'http://wallet.udax.pro/upload//admin/d3e28f17-070b-4a98-a47d-6df8d2eb3c24.png', 25, NULL, NULL, '2019-5-29 06:46:25', 'pubaoyu');
INSERT INTO `front_country` VALUES (28, 'Finland', 'Suomi', '358', 1, 'http://wallet.udax.pro/upload//admin/43451244-cc1f-4fba-9246-065750c1c6a0.png', 26, NULL, NULL, '2019-5-29 06:47:06', 'pubaoyu');
INSERT INTO `front_country` VALUES (29, 'Greece', 'Ελλάδα', '30', 1, 'http://wallet.udax.pro/upload//admin/8d8688ed-8068-464c-86fe-3b3066b2fdee.png', 27, NULL, NULL, '2019-5-29 06:47:28', 'pubaoyu');
INSERT INTO `front_country` VALUES (30, 'Greenland', 'Grønland', '299', 1, 'http://wallet.udax.pro/upload//admin/8746f717-fca1-4e76-8e7e-4596f3eca612.png', 28, NULL, NULL, '2019-5-29 06:50:41', 'pubaoyu');
INSERT INTO `front_country` VALUES (31, 'Hungary', 'Magyarország', '36', 1, 'http://wallet.udax.pro/upload//admin/c7ec86eb-eb5d-46ad-a9f9-dac15a68c967.png', 29, NULL, NULL, '2019-5-29 06:51:03', 'pubaoyu');
INSERT INTO `front_country` VALUES (32, 'Iceland', 'Ísland', '354', 1, 'http://wallet.udax.pro/upload//admin/c989aa9f-35f2-4225-8ed8-c4325c50aab0.png', 30, NULL, NULL, '2019-5-29 06:51:23', 'pubaoyu');
INSERT INTO `front_country` VALUES (33, 'India', 'इंडिया', '91', 1, 'http://wallet.udax.pro/upload//admin/4d308b56-e53e-424c-826a-65d173fd25ae.png', 31, NULL, NULL, '2019-5-29 06:51:57', 'pubaoyu');
INSERT INTO `front_country` VALUES (34, 'Ireland', 'Éire', '353', 1, 'http://wallet.udax.pro/upload//admin/e9bab45b-e486-4533-9ca9-b24ccbc48285.png', 32, NULL, NULL, '2019-5-29 06:52:15', 'pubaoyu');
INSERT INTO `front_country` VALUES (35, 'Israel', 'ישראל', '972', 1, 'http://wallet.udax.pro/upload//admin/ba41a204-fe4c-4a81-9f3e-f6b8508748f1.png', 33, NULL, NULL, '2019-5-29 06:52:38', 'pubaoyu');
INSERT INTO `front_country` VALUES (36, 'Italy', 'Italia', '39', 1, 'http://wallet.udax.pro/upload//admin/7cd3ac7c-d380-46b4-b2c6-9964eb91abb0.png', 34, NULL, NULL, '2019-5-29 06:53:07', 'pubaoyu');
INSERT INTO `front_country` VALUES (37, 'Jordan', 'الأردن', '962', 1, 'http://wallet.udax.pro/upload//admin/d6d918a3-bf0f-49bf-86d1-866bd547da18.png', 35, NULL, NULL, '2019-5-29 06:53:34', 'pubaoyu');
INSERT INTO `front_country` VALUES (38, 'Maldives', 'ދިވެހިރާއްޖެ', '960', 1, 'http://wallet.udax.pro/upload//admin/ffdc70c6-2085-4ae4-ad1d-295a9413eb8f.png', 36, NULL, NULL, '2019-5-29 06:58:35', 'pubaoyu');
INSERT INTO `front_country` VALUES (39, 'Mexico', 'México', '52', 1, 'http://wallet.udax.pro/upload//admin/5095a699-c233-408f-8cf0-b63a0f584221.png', 37, NULL, NULL, '2019-5-29 06:59:17', 'pubaoyu');
INSERT INTO `front_country` VALUES (40, 'Netherlands', 'Nederland', '31', 1, 'http://wallet.udax.pro/upload//admin/4f2e65e5-eb73-4f3c-b7b5-4acbadbf942e.png', 38, NULL, NULL, '2019-5-29 07:00:31', 'pubaoyu');
INSERT INTO `front_country` VALUES (41, 'Norway', 'Norge', '47', 1, 'http://wallet.udax.pro/upload//admin/c585abdb-7ab4-40e7-ae90-6f077d360193.png', 39, NULL, NULL, '2019-5-29 07:01:36', 'pubaoyu');
INSERT INTO `front_country` VALUES (42, 'Panama', 'Panamá', '507', 1, 'http://wallet.udax.pro/upload//admin/a02594f7-dbd5-4c85-b95e-f64ca58d8d6b.png', 40, NULL, NULL, '2019-5-29 07:03:35', 'pubaoyu');
INSERT INTO `front_country` VALUES (43, 'Peru', 'Perú', '51', 1, 'http://wallet.udax.pro/upload//admin/52d3702c-9604-4a35-aa21-b24794b1ae5e.png', 41, NULL, NULL, '2019-5-29 07:04:29', 'pubaoyu');
INSERT INTO `front_country` VALUES (44, 'Poland', 'Polska', '48', 1, 'http://wallet.udax.pro/upload//admin/e26d4bf2-81fb-4641-883e-7a2e7b7c9a51.png', 42, NULL, NULL, '2019-5-29 07:04:48', 'pubaoyu');
INSERT INTO `front_country` VALUES (45, 'Portugal', 'Portugal', '351', 1, 'http://wallet.udax.pro/upload//admin/14b90340-7e54-44bb-bbab-d0e7ab226fbc.png', 43, NULL, NULL, '2019-5-29 07:05:16', 'pubaoyu');
INSERT INTO `front_country` VALUES (46, 'Russia', 'Россия', '7', 1, 'http://wallet.udax.pro/upload//admin/008b1dad-6098-4652-80b2-ac341edc258b.png', 44, NULL, NULL, '2019-5-29 07:08:50', 'pubaoyu');
INSERT INTO `front_country` VALUES (47, 'Romania', 'România', '40', 1, 'http://wallet.udax.pro/upload//admin/deffa3b1-7b03-4f93-a3b8-3d4c09e909d7.png', 45, NULL, NULL, '2019-5-29 07:10:24', 'pubaoyu');
INSERT INTO `front_country` VALUES (48, 'Saudi Arabia', 'السعودية', '966', 1, 'http://wallet.udax.pro/upload//admin/7142d611-425c-48af-8644-11cc8441ffb2.png', 46, NULL, NULL, '2019-5-29 07:11:47', 'pubaoyu');
INSERT INTO `front_country` VALUES (49, 'South Africa', 'Suid-Afrika', '27', 1, 'http://wallet.udax.pro/upload//admin/4bbcaeb0-0565-4550-8810-4961a0556d3e.png', 47, NULL, NULL, '2019-5-29 07:12:46', 'pubaoyu');
INSERT INTO `front_country` VALUES (50, 'Spain', 'España', '34', 1, 'http://wallet.udax.pro/upload//admin/942c951f-deb5-456b-be5b-6b07a14a4fbe.png', 48, NULL, NULL, '2019-5-29 07:13:38', 'pubaoyu');
INSERT INTO `front_country` VALUES (51, 'Thailand', 'ประเทศไทย', '66', 1, 'http://wallet.udax.pro/upload//admin/df8313fb-9ad7-4844-96cc-ed517fca2ef1.png', 49, NULL, NULL, '2019-5-29 07:14:17', 'pubaoyu');
INSERT INTO `front_country` VALUES (52, 'Turkey', 'Türkiye', '90', 1, 'http://wallet.udax.pro/upload//admin/ccbb2b02-c306-4533-ae79-e11921becc8b.png', 50, NULL, NULL, '2019-5-29 07:15:02', 'pubaoyu');
INSERT INTO `front_country` VALUES (53, 'Ukraine', 'Україна', '380', 1, 'http://wallet.udax.pro/upload//admin/a85cca7e-c05a-41a2-9c84-790dc5e1a48c.png', 51, NULL, NULL, '2019-5-29 07:15:44', 'pubaoyu');



-- ----------------------------
-- Table structure for front_help_content
-- ----------------------------
DROP TABLE IF EXISTS `front_help_content`;
CREATE TABLE `front_help_content` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `exchange_id` bigint(10) NOT NULL COMMENT '交易所ID',
  `type_id` bigint(10) NOT NULL COMMENT '帮助类型id',
  `help_title` varchar(50) NOT NULL COMMENT '帮助标题',
  `help_content` text NOT NULL COMMENT '帮助内容',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用 1:启用，0:弃用',
  `sort` tinyint(1) DEFAULT NULL COMMENT '排序号',
  `language_type` varchar(10) NOT NULL COMMENT '语言(如:zj,en)',
  `client_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:pc 1:APP',
  `remark` varchar(300) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COMMENT='帮助内容表';


-- ----------------------------
-- Table structure for front_help_type
-- ----------------------------
DROP TABLE IF EXISTS `front_help_type`;
CREATE TABLE `front_help_type` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `exchange_id` bigint(10) NOT NULL COMMENT '交易所ID',
  `type_name` varchar(30) NOT NULL COMMENT '帮助类型名称',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用 1:启用，0:弃用',
  `remark` varchar(300) DEFAULT NULL,
  `language_type` varchar(10) NOT NULL COMMENT '语言类型(如:zh,en)',
  `sort` tinyint(1) DEFAULT NULL COMMENT '排序号',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='帮助类型表';


-- ----------------------------
-- Table structure for front_notice
-- ----------------------------
DROP TABLE IF EXISTS `front_notice`;
CREATE TABLE `front_notice` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `notice_title` varchar(150) DEFAULT NULL COMMENT '公告标题',
  `notice_level` tinyint(1) NOT NULL COMMENT '公告级别（1:普通，2:重要，3:紧急）',
  `status` tinyint(1) NOT NULL COMMENT '发布状态(0:发布，1:隐藏)',
  `content` text COMMENT '发布的内容信息（以文本编辑器编辑）',
  `exchange_id` bigint(20) NOT NULL COMMENT '关联交易所',
  `client_type` tinyint(4) NOT NULL COMMENT '''1 pc 2 app',
  `language_type` varchar(10) NOT NULL COMMENT '语言',
  `remark` varchar(128) DEFAULT NULL COMMENT '备注信息',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for front_recharge
-- ----------------------------
DROP TABLE IF EXISTS `front_recharge`;
CREATE TABLE `front_recharge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_address` varchar(50) NOT NULL COMMENT 'token地址',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `recharge_amount` decimal(16,8) DEFAULT NULL COMMENT '充值金额',
  `symbol` varchar(20) NOT NULL COMMENT '货币编码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(4) DEFAULT NULL COMMENT '充值状态0:充值成功,1:充值失败',
  `order_id` bigint(20) NOT NULL COMMENT '充值流水号',
  `block_order_id` varchar(64) DEFAULT NULL COMMENT '区块链订单ID',
  `fee_symbol` varchar(20) DEFAULT NULL COMMENT '区块链汇聚手续费币种',
  `recharge_fee` decimal(16,8) DEFAULT NULL COMMENT '区块链汇聚手续费',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1.普通充值  2.商户充值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8 COMMENT='用户充值记录';


-- ----------------------------
-- Table structure for front_token_address
-- ----------------------------
DROP TABLE IF EXISTS `front_token_address`;
CREATE TABLE `front_token_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_address` varchar(100) NOT NULL COMMENT 'token地址',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID，用户发起充值时分配',
  `symbol` varchar(20) NOT NULL COMMENT '货币编码',
  `proxy_code` varchar(10) NOT NULL COMMENT '白标标识',
  `merchant_user` varchar(32) DEFAULT NULL COMMENT '商户用户',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enable` tinyint(1) DEFAULT NULL COMMENT '是否启用0:未启用 1:启用',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1.普通用户地址  2.商户地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_1` (`symbol`,`user_address`),
  UNIQUE KEY `symbo_address_unique` (`user_id`,`merchant_user`,`symbol`)
) ENGINE=InnoDB AUTO_INCREMENT=3831 DEFAULT CHARSET=utf8 COMMENT='用户钱包地址表';




-- ----------------------------
-- Table structure for front_transfer_detail
-- ----------------------------
DROP TABLE IF EXISTS `front_transfer_detail`;
CREATE TABLE `front_transfer_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '转出人userid',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `trans_target_type` tinyint(4) NOT NULL COMMENT '1.平台; 2.个人;  ',
  `hedge_flag` tinyint(4) NOT NULL COMMENT '1.需要对冲;2.不需要对冲',
  `receive_user_id` bigint(20) DEFAULT NULL COMMENT '买方user_id（吃单方）',
  `src_currency_type` tinyint(4) NOT NULL COMMENT '1.数字货币;2.法币',
  `src_symbol` varchar(30) NOT NULL,
  `src_amount` decimal(20,8) NOT NULL,
  `dst_currency_type` tinyint(4) NOT NULL COMMENT '1.数字货币;2.法币',
  `dst_symbol` varchar(30) NOT NULL,
  `dst_amount` decimal(20,8) NOT NULL COMMENT '转换得到目标币的数量',
  `charge_currency_code` varchar(30) NOT NULL COMMENT '收取的手续费币种',
  `charge_amount` decimal(20,8) NOT NULL COMMENT '收取的手续费金额',
  `trans_price` decimal(20,8) NOT NULL COMMENT '交易价格',
  `create_time` datetime NOT NULL COMMENT '记录生成时间',
  `expire_time` datetime NOT NULL COMMENT '订单失效时间',
  `update_time` datetime DEFAULT NULL COMMENT '成交时间',
  `status` tinyint(1) NOT NULL COMMENT '1:待转换   2:转换成功',
  `remark` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_unique` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='货币转换明细表';



-- ----------------------------
-- Table structure for front_user
-- ----------------------------
DROP TABLE IF EXISTS `front_user`;
CREATE TABLE `front_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(30) NOT NULL,
  `user_type` tinyint(4) NOT NULL COMMENT '1,普通用户;2,商家;',
  `user_level` tinyint(4) NOT NULL,
  `user_status` tinyint(4) NOT NULL COMMENT '1.未激活;2,正常;3,冻结;',
  `user_pwd` varchar(128) NOT NULL,
  `trade_pwd` varchar(128) DEFAULT NULL,
  `salt` varchar(20) DEFAULT NULL COMMENT '盐值',
  `uid` varchar(20) NOT NULL COMMENT '用户唯一ID',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(20) DEFAULT NULL COMMENT '用户手机号',
  `login_err_times` int(3) DEFAULT '0',
  `login_ip` varchar(50) DEFAULT NULL COMMENT '登录IP',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid_unique` (`uid`),
  UNIQUE KEY `user_name_unique` (`user_name`),
  UNIQUE KEY `email_mobile_unique` (`email`,`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8mb4 COMMENT='钱包用户表';


-- ----------------------------
-- Table structure for front_user_info
-- ----------------------------
DROP TABLE IF EXISTS `front_user_info`;
CREATE TABLE `front_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `nick_name` varchar(30) DEFAULT NULL,
  `id_card` varchar(30) DEFAULT NULL,
  `id_card_img_zm` varchar(256) DEFAULT NULL COMMENT '身份证正面',
  `id_card_img_fm` varchar(256) DEFAULT NULL COMMENT '身份证反面',
  `portrait` varchar(256) DEFAULT NULL COMMENT '头像地址',
  `first_name` varchar(20) DEFAULT NULL COMMENT '姓',
  `real_name` varchar(20) DEFAULT NULL COMMENT '名',
  `is_valid` tinyint(1) DEFAULT NULL COMMENT '是否已实名认证(0未上传，1待认证，2已认证)',
  `bind_domain` varchar(50) DEFAULT NULL COMMENT '绑定具体区域（按域名划分）',
  `recommond_code` varchar(20) DEFAULT NULL COMMENT '邀请人推荐码',
  `parent_id` bigint(12) DEFAULT '0' COMMENT '父类Id',
  `top_id` bigint(12) DEFAULT '0' COMMENT '最上级的用户ID',
  `level_code` varchar(200) DEFAULT NULL COMMENT '层级码',
  `visit_code` char(32) NOT NULL COMMENT '邀请码',
  `exchange_id` int(11) NOT NULL DEFAULT '1' COMMENT '白标ID',
  `is_withdraw` tinyint(1) DEFAULT '0' COMMENT '是否允许提币(修改信息后在一段时间内不允许提币)',
  `is_valid_phone` tinyint(4) NOT NULL DEFAULT '0',
  `is_valid_email` tinyint(4) NOT NULL DEFAULT '0',
  `location_code` varchar(10) DEFAULT NULL COMMENT '国家编号',
  `location_country` varchar(20) DEFAULT NULL,
  `level` int(11) NOT NULL,
  `dict_data` bigint(20) DEFAULT NULL COMMENT '审核不通过的具体原因',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `visit_code_UNIQUE` (`visit_code`),
  UNIQUE KEY `Index_USER_ID` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';



-- ----------------------------
-- Table structure for front_withdraw
-- ----------------------------
DROP TABLE IF EXISTS `front_withdraw`;
CREATE TABLE `front_withdraw` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trans_no` varchar(20) NOT NULL COMMENT '提现流水号',
  `user_address` varchar(256) NOT NULL COMMENT 'token地址',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `trade_amount` decimal(16,8) DEFAULT NULL COMMENT '提现金额',
  `transaction_id` varchar(150) DEFAULT NULL COMMENT '区块链转账事物id',
  `symbol` varchar(20) NOT NULL COMMENT '货币编码',
  `basic_symbol` varchar(20) DEFAULT NULL COMMENT '基础代币编码',
  `charge_amount` decimal(16,8) NOT NULL COMMENT '提现手续费',
  `arrival_amoumt` decimal(16,8) NOT NULL COMMENT '到账金额',
  `confirmations` int(10) DEFAULT '0' COMMENT '区块确认数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL COMMENT '提现状态0:待审核,1:已审核2:区块链已扫描提现信息，3:转账中 ,4提现审核未通过转账失败,5转账成功,6:预出金资金冻结失败,7提交提币申请，初始状态',
  `dict_data` bigint(20) DEFAULT NULL COMMENT '审核不通过具体原因',
  `proxy_code` varchar(10) NOT NULL COMMENT '白标标识',
  `mch_order_no` varchar(32) NOT NULL DEFAULT '' COMMENT '商户订单号/外部流水号',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1.普通提现  2.商户提现',
  `nonce_str` varchar(32) NOT NULL COMMENT '商户随机字符串',
  `fee_symbol` varchar(20) DEFAULT NULL,
  `withdraw_fee` decimal(16,8) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tranno` (`trans_no`),
  UNIQUE KEY `orderno` (`mch_order_no`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8 COMMENT='用户提现管理';

-- ----------------------------
-- Records of front_withdraw
-- ----------------------------

-- ----------------------------
-- Table structure for front_withdraw_add
-- ----------------------------
DROP TABLE IF EXISTS `front_withdraw_add`;
CREATE TABLE `front_withdraw_add` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `withdraw_add` varchar(256) NOT NULL COMMENT '提币地址',
  `symbol` varchar(10) NOT NULL COMMENT '币种名称',
  `type` tinyint(4) NOT NULL COMMENT '1.普通提现  2.商户提现',
  `remark` varchar(500) DEFAULT NULL COMMENT '地址描述',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0.失效 1。有效',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_index` (`user_id`,`symbol`,`withdraw_add`)
) ENGINE=InnoDB AUTO_INCREMENT=306 DEFAULT CHARSET=utf8 COMMENT='提币地址管理';

-- ----------------------------
-- Records of front_withdraw_add
-- ----------------------------

-- ----------------------------
-- Table structure for fund_account_assert
-- ----------------------------
DROP TABLE IF EXISTS `fund_account_assert`;
CREATE TABLE `fund_account_assert` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `dc_code` varchar(30) NOT NULL COMMENT '代币代码',
  `total_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '代币总数量',
  `available_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '代币可用数量',
  `freeze_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '代币冻结数量',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `umac` varchar(1024) NOT NULL COMMENT '加密检验信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_UID_DC` (`user_id`,`dc_code`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fund_account_assert
-- ----------------------------

-- ----------------------------
-- Table structure for fund_account_assert_log
-- ----------------------------
DROP TABLE IF EXISTS `fund_account_assert_log`;
CREATE TABLE `fund_account_assert_log` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `fund_id` bigint(20) DEFAULT NULL COMMENT '基金产品id',
  `trans_no` varchar(32) NOT NULL COMMENT '平台流水号',
  `dc_code` varchar(30) NOT NULL COMMENT '代币代码',
  `change_type` tinyint(4) NOT NULL COMMENT '0 未知 .1 申购冻结 2.申购解冻 3. 申购手续费 4. 清盘收益增减 6.转出到币币账户 6.从币币账户转入',
  `change_amount` decimal(20,8) NOT NULL COMMENT '变更数量',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tranNo_unique` (`trans_no`)
) ENGINE=InnoDB AUTO_INCREMENT=511 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fund_account_assert_log
-- ----------------------------

-- ----------------------------
-- Table structure for fund_manage_info
-- ----------------------------
DROP TABLE IF EXISTS `fund_manage_info`;
CREATE TABLE `fund_manage_info` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `exchange_id` bigint(10) NOT NULL COMMENT '交易所ID',
  `manage_info` varchar(400) NOT NULL COMMENT '管理人介绍',
  `idea` varchar(400) NOT NULL COMMENT '理念',
  `team_info` varchar(500) NOT NULL COMMENT '团队介绍',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enable` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0.禁用;1启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fund_manage_info
-- ----------------------------

-- ----------------------------
-- Table structure for fund_product_info
-- ----------------------------
DROP TABLE IF EXISTS `fund_product_info`;
CREATE TABLE `fund_product_info` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `exchange_id` bigint(10) NOT NULL COMMENT '交易所ID',
  `fund_id` bigint(20) NOT NULL COMMENT '基金产品id',
  `fund_name` varchar(30) NOT NULL COMMENT '产品名称',
  `strategy_id` bigint(20) NOT NULL COMMENT '策略类型',
  `subscripe_rate` decimal(5,4) NOT NULL DEFAULT '0.0000' COMMENT '认购费率',
  `dc_code` varchar(30) NOT NULL COMMENT '代币代码',
  `expect_scale` decimal(20,8) NOT NULL COMMENT '预期规模',
  `expect_profit` decimal(10,4) NOT NULL COMMENT '预期收益,百分比',
  `min_buy_num` decimal(20,8) NOT NULL COMMENT '最小申购数量',
  `actual_scale` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '实际规模',
  `proportion` decimal(5,4) NOT NULL DEFAULT '1.0000' COMMENT '投资者分成比例',
  `over_range` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1.可以超额认购; 2.不允许超额认购',
  `status` tinyint(4) NOT NULL COMMENT '1.已发布;2.募集中;3.申购结束;4.已启动;5.清盘中;6.已清盘',
  `manager_id` bigint(20) NOT NULL COMMENT '管理团队信息id',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `buy_starttime` datetime NOT NULL COMMENT '申购开始时间',
  `buy_endtime` datetime NOT NULL COMMENT '申购结束时间',
  `cycle_starttime` datetime NOT NULL COMMENT '封闭开始时间',
  `cycle_endtime` datetime NOT NULL COMMENT '封闭结束时间',
  `run_starttime` datetime DEFAULT NULL COMMENT '运行开始时间',
  `run_endtime` datetime DEFAULT NULL COMMENT '运行结束时间',
  `clear_time` datetime DEFAULT NULL COMMENT '清盘时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enable` tinyint(4) DEFAULT '1' COMMENT '0.禁用;1启用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `fund_ID_UNI` (`fund_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fund_product_info
-- ----------------------------

-- ----------------------------
-- Table structure for fund_product_profilt_info
-- ----------------------------
DROP TABLE IF EXISTS `fund_product_profilt_info`;
CREATE TABLE `fund_product_profilt_info` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `exchange_id` bigint(10) NOT NULL,
  `fund_id` bigint(20) NOT NULL COMMENT '基金产品Id',
  `symbol` varchar(30) NOT NULL COMMENT '交易队代码',
  `curr_profilt` decimal(10,4) DEFAULT NULL COMMENT '实际收益率',
  `year_profilt` decimal(10,4) DEFAULT NULL COMMENT '年化收益率',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_FI` (`fund_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fund_product_profilt_info
-- ----------------------------

-- ----------------------------
-- Table structure for fund_purchase_info
-- ----------------------------
DROP TABLE IF EXISTS `fund_purchase_info`;
CREATE TABLE `fund_purchase_info` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `order_no` bigint(20) NOT NULL COMMENT '申购单号',
  `exchange_id` bigint(12) NOT NULL COMMENT '白标ID',
  `fund_id` bigint(20) NOT NULL COMMENT '基金产品Id',
  `fund_name` varchar(30) NOT NULL COMMENT '基金名称',
  `user_id` bigint(20) NOT NULL COMMENT '用户Id',
  `dc_code` varchar(30) NOT NULL COMMENT '代币代码',
  `order_volume` decimal(20,8) NOT NULL COMMENT '申购数量',
  `order_chrge` decimal(20,8) NOT NULL COMMENT '申购手续费',
  `return_volume` decimal(20,8) DEFAULT NULL COMMENT '返还的总数量',
  `profilt_volume` decimal(20,8) DEFAULT NULL COMMENT '收益数量(可能为负数)',
  `yield` decimal(10,4) DEFAULT NULL COMMENT '收益率',
  `status` tinyint(4) NOT NULL COMMENT '1.已认购; 2.运行中; 3.结算中; 4.已结算',
  `order_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `settle_time` datetime DEFAULT NULL COMMENT '结算时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_OrderID` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of fund_purchase_info
-- ----------------------------

-- ----------------------------
-- Table structure for fund_strategy
-- ----------------------------
DROP TABLE IF EXISTS `fund_strategy`;
CREATE TABLE `fund_strategy` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `exchange_id` bigint(10) NOT NULL COMMENT '交易所ID',
  `strategy_type` varchar(30) NOT NULL COMMENT '策略类型',
  `strategy_info` varchar(200) NOT NULL COMMENT '策略描述',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enable` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0.禁用;1启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='基金策略表';

-- ----------------------------
-- Records of fund_strategy
-- ----------------------------

-- ----------------------------
-- Table structure for gate_log
-- ----------------------------
DROP TABLE IF EXISTS `gate_log`;
CREATE TABLE `gate_log` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `menu` varchar(255) DEFAULT NULL COMMENT '菜单',
  `opt` varchar(255) DEFAULT NULL COMMENT '操作',
  `uri` varchar(255) DEFAULT NULL COMMENT '资源路径',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `user_id` bigint(11) DEFAULT NULL,
  `exchange_id` bigint(11) DEFAULT NULL,
  `crt_host` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1158 DEFAULT CHARSET=utf8mb4;



-- ----------------------------
-- Table structure for generator_id
-- ----------------------------
DROP TABLE IF EXISTS `generator_id`;
CREATE TABLE `generator_id` (
  `id_` smallint(10) NOT NULL AUTO_INCREMENT COMMENT '自增序号',
  `k` varchar(30) NOT NULL COMMENT '自增序列KEY值',
  `v` varchar(20) NOT NULL COMMENT '自增序列VALUE值',
  `incre_len` int(11) NOT NULL DEFAULT '1' COMMENT '自增的步长',
  `VERSION` int(11) NOT NULL COMMENT '乐观锁版本',
  `remark_` varchar(20) DEFAULT NULL COMMENT '级别描述',
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='自增ID生成表';

-- ----------------------------
-- Records of generator_id
-- ----------------------------
INSERT INTO `generator_id` VALUES ('3', 'VISIT_CODE', 'A000XS', '3', '116', '邀请码递增序列');
INSERT INTO `generator_id` VALUES ('4', 'MERCHNAT_ACCOUNT', '1000000057', '3', '20', '商户号递增序列');

-- ----------------------------
-- Table structure for h_bonus_config
-- ----------------------------
DROP TABLE IF EXISTS `h_bonus_config`;
CREATE TABLE `h_bonus_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level` int(11) NOT NULL COMMENT '直接间隔代数',
  `profit_rate` decimal(4,4) NOT NULL COMMENT '收取利润的%  ',
  `remark` varchar(300) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `level_Unique` (`level`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='用户利润分成比例配置表';



-- ----------------------------
-- Table structure for h_commission_detail
-- ----------------------------
DROP TABLE IF EXISTS `h_commission_detail`;
CREATE TABLE `h_commission_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `receive_user_id` bigint(20) NOT NULL,
  `order_no` varchar(32) NOT NULL,
  `order_profit` decimal(16,8) NOT NULL COMMENT '用户订单总利润',
  `bonus_config_id` bigint(20) NOT NULL COMMENT '对应的收益配置表的id',
  `profit_rate` decimal(16,4) NOT NULL COMMENT '对应的利润分配比例',
  `profit` decimal(16,8) NOT NULL COMMENT '实际可以分配的利润',
  `create_time` datetime NOT NULL,
  `level_id` bigint(20) NOT NULL COMMENT '申购等级',
  `amount` decimal(16,8) NOT NULL COMMENT '申购总量',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-用户利润分成,1-平台利润分成,2-节点奖利润分配,3-全球奖利润分配',
  `level_name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_orderNo` (`order_no`,`receive_user_id`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='分润详情表';



-- ----------------------------
-- Table structure for h_commission_relation
-- ----------------------------
DROP TABLE IF EXISTS `h_commission_relation`;
CREATE TABLE `h_commission_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '产生利润的用户id',
  `level` int(11) NOT NULL COMMENT '产生利润的代数',
  `receive_user_id` bigint(20) NOT NULL COMMENT '可能享有分成的用户id',
  `receive_level` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COMMENT='分润对照表';



-- ----------------------------
-- Table structure for h_daily_profit_detail
-- ----------------------------
DROP TABLE IF EXISTS `h_daily_profit_detail`;
CREATE TABLE `h_daily_profit_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `level_name` varchar(32) NOT NULL COMMENT '方案名称',
  `level_id` bigint(20) NOT NULL,
  `profit` decimal(16,8) NOT NULL COMMENT '收益',
  `order_no` varchar(32) NOT NULL,
  `create_time` datetime NOT NULL,
  `settle_time` datetime NOT NULL COMMENT '对应的结算日期(与订单号构成唯一索引  防止重复计算)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_index` (`order_no`,`settle_time`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COMMENT='每日收益流水表';



-- ----------------------------
-- Table structure for h_node_award
-- ----------------------------
DROP TABLE IF EXISTS `h_node_award`;
CREATE TABLE `h_node_award` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `base_profit_rate` decimal(10,4) NOT NULL COMMENT '按利润的百分比作为分成基数',
  `child_num` int(10) NOT NULL COMMENT '达到节点等级需要的有效用户数量',
  `rate` decimal(10,4) NOT NULL COMMENT '节点奖励抽取的比例',
  `global_rate` decimal(10,4) NOT NULL COMMENT '全球利润抽取的比例',
  `remark` varchar(50) NOT NULL COMMENT '节点描述',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for h_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `h_order_detail`;
CREATE TABLE `h_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `level_name` varchar(32) NOT NULL COMMENT '方案名称',
  `level_id` bigint(20) NOT NULL COMMENT '申购的方案Id',
  `lock_amount` decimal(16,8) NOT NULL COMMENT '锁定金额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `purchase_time` datetime NOT NULL COMMENT '申购时间',
  `symbol` varchar(16) NOT NULL COMMENT '币种',
  `settle_day` int(11) NOT NULL COMMENT '总共运行天数',
  `current_settle_day` int(11) NOT NULL COMMENT '当前运行天数',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '利润结算状态: 0-未结算完成,1-利润已汇总完成  2.利润已结算完成',
  `last_order_no` varchar(32) DEFAULT NULL COMMENT '上一轮的订单id',
  `interest` decimal(16,4) NOT NULL COMMENT '日息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_orderno` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='ud订单详情表';



-- ----------------------------
-- Table structure for h_param
-- ----------------------------
DROP TABLE IF EXISTS `h_param`;
CREATE TABLE `h_param` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `ud_key` varchar(50) NOT NULL,
  `ud_value` varchar(50) NOT NULL,
  `remark` varchar(50) NOT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of h_param
-- ----------------------------
INSERT INTO `h_param` VALUES ('1', 'UNLOCK_AMOUNT', '200', '解锁金额', null, null, null, null);
INSERT INTO `h_param` VALUES ('2', 'EXPIRE', '30', '用户解锁之后的有效期', null, null, null, null);
INSERT INTO `h_param` VALUES ('3', 'PROFIT_RATE', '0.3', '用户利润平台分成比例', null, null, null, null);

-- ----------------------------
-- Table structure for h_purchase_level
-- ----------------------------
DROP TABLE IF EXISTS `h_purchase_level`;
CREATE TABLE `h_purchase_level` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(30) NOT NULL COMMENT '申购名称',
  `amount` decimal(16,8) NOT NULL COMMENT '申购金额',
  `trigger_num` int(10) NOT NULL COMMENT '触发匹配数量人数',
  `match_num` int(10) NOT NULL COMMENT '匹配人数',
  `amount_limit` decimal(16,8) NOT NULL COMMENT '已结算总投入金额限制(低于这个数量的不可玩)',
  `interest` decimal(16,4) NOT NULL COMMENT '日息',
  `is_open` tinyint(4) NOT NULL COMMENT '是否开放 0:关闭,1:开放',
  `run_time` int(10) NOT NULL COMMENT '方案运行天数',
  `wait_time` int(10) NOT NULL COMMENT '客户排单最长等待期限(天)',
  `earliest_start_time` datetime NOT NULL COMMENT '下一轮的最早开始时间',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `desp` varchar(5000) NOT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='申购等级表';


-- ----------------------------
-- Table structure for h_queue
-- ----------------------------
DROP TABLE IF EXISTS `h_queue`;
CREATE TABLE `h_queue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '冻结资产',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `level_name` varchar(32) NOT NULL COMMENT '方案名称',
  `level_id` bigint(20) NOT NULL COMMENT '申购的方案Id',
  `lock_amount` decimal(16,8) NOT NULL COMMENT '锁定金额',
  `symbol` varchar(16) NOT NULL,
  `last_order_no` varchar(32) DEFAULT NULL COMMENT '用户上一轮排队的订单号',
  `create_time` datetime NOT NULL COMMENT '排队时间',
  `status` tinyint(4) NOT NULL COMMENT '0.失效   1.等待匹配中 2.已匹配',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_orderno` (`order_no`),
  KEY `lastOrderNo_index` (`last_order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='ud  队列表';


-- ----------------------------
-- Table structure for h_settled_profit
-- ----------------------------
DROP TABLE IF EXISTS `h_settled_profit`;
CREATE TABLE `h_settled_profit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `order_no` varchar(32) NOT NULL COMMENT '对应订单号',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '结算状态  0.未汇总完数据  1.已汇总完数据待结算  2.已分配用户利润并记录其它用户的分成记录',
  `freeze_amount` decimal(16,8) NOT NULL COMMENT '冻结金额',
  `freeze_profit` decimal(16,8) NOT NULL COMMENT '冻结利润',
  `symbol` varchar(16) NOT NULL,
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `if_queue_next_order` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否进行下一轮的排队了    0 否  1 是',
  `level_id` bigint(20) NOT NULL,
  `level_name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_orderno` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='ud待结算利润表';


-- ----------------------------
-- Table structure for h_unlock_detail
-- ----------------------------
DROP TABLE IF EXISTS `h_unlock_detail`;
CREATE TABLE `h_unlock_detail` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(10) NOT NULL,
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `amount` decimal(16,4) NOT NULL COMMENT '解锁金额',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='解锁明细表';


-- ----------------------------
-- Table structure for h_user_info
-- ----------------------------
DROP TABLE IF EXISTS `h_user_info`;
CREATE TABLE `h_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '用户状态(1.可用  0锁定)',
  `recommond_code` varchar(20) NOT NULL COMMENT '邀请人推荐码',
  `parent_id` bigint(12) NOT NULL DEFAULT '0' COMMENT '邀请人ID',
  `top_id` bigint(12) NOT NULL DEFAULT '0' COMMENT '最上级的用户ID',
  `visit_code` char(32) NOT NULL COMMENT '邀请码',
  `exchange_id` bigint(20) NOT NULL COMMENT '白标ID',
  `level` int(11) NOT NULL DEFAULT '1' COMMENT '属于团队中的第几代',
  `direct_child` int(11) NOT NULL DEFAULT '0' COMMENT '直推有效用户',
  `all_child` int(11) NOT NULL DEFAULT '0' COMMENT '所有有效用户',
  `total_amount` decimal(16,8) NOT NULL DEFAULT '0.00000000' COMMENT '总结算资金量(冻结的不算  一定要结算了之后才算有效)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_valid` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0.非有效用户   1.有效用户(参与队列锁定币  才能算有效用户)',
  `auto_repeat` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0.不复投  1复投',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_index` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for hedge_detail
-- ----------------------------
DROP TABLE IF EXISTS `hedge_detail`;
CREATE TABLE `hedge_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trade_time` datetime DEFAULT NULL COMMENT '成交时间',
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `src_symbol` varchar(30) NOT NULL COMMENT '源代币',
  `dst_symbol` varchar(30) NOT NULL COMMENT '目标币',
  `src_amount` decimal(20,8) NOT NULL COMMENT '源数量',
  `dst_amount` decimal(20,8) NOT NULL COMMENT '转换得到目标币的数量',
  `trans_price` decimal(20,8) NOT NULL COMMENT '交易价格',
  `symbol` varchar(30) DEFAULT NULL COMMENT '交易对',
  `out_symbol` varchar(30) DEFAULT NULL COMMENT '外部交易对',
  `direction` int(11) DEFAULT NULL COMMENT '成交方向',
  `hedge_time` datetime DEFAULT NULL COMMENT '初次对冲时间',
  `hedge_order_id` varchar(30) DEFAULT NULL COMMENT '外部委托单号',
  `hedge_price` decimal(20,8) DEFAULT NULL COMMENT '对冲委托价格',
  `hedge_trade_price` decimal(20,8) DEFAULT NULL COMMENT '对冲成交价格',
  `hedge_status` int(11) DEFAULT '0' COMMENT '自动对冲状态',
  `hedge_status_text` varchar(32) DEFAULT NULL COMMENT '对冲状态描述',
  `hedge_update_time` datetime DEFAULT NULL COMMENT '对冲状态更新时间',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  `admin_dealed` int(11) DEFAULT '0' COMMENT '是否已人工处理',
  PRIMARY KEY (`id`),
  KEY `Index_Symbol` (`symbol`)
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for lt_asset_account
-- ----------------------------
DROP TABLE IF EXISTS `lt_asset_account`;
CREATE TABLE `lt_asset_account` (
  `id_` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `lt_code` varchar(30) NOT NULL COMMENT 'Legal tender',
  `totol_amount` double(20,8) DEFAULT NULL,
  `available_amount` double(20,8) DEFAULT NULL,
  `freeze_amount` double(20,8) DEFAULT NULL,
  `wait_confirm_amount` double(20,8) DEFAULT NULL,
  `remark` varchar(30) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_user` varchar(255) DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_user` varchar(255) DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法定货币钱包账户';

-- ----------------------------
-- Records of lt_asset_account
-- ----------------------------

-- ----------------------------
-- Table structure for lt_asset_account_log
-- ----------------------------
DROP TABLE IF EXISTS `lt_asset_account_log`;
CREATE TABLE `lt_asset_account_log` (
  `id_` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `lt_code` varchar(30) NOT NULL,
  `change_type` tinyint(4) DEFAULT NULL,
  `change_amount` double(20,8) DEFAULT NULL,
  `remark` varchar(30) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_user` varchar(255) DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_user` varchar(255) DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法定货币钱包账户流水表';

-- ----------------------------
-- Records of lt_asset_account_log
-- ----------------------------

-- ----------------------------
-- Table structure for mch_notify
-- ----------------------------
DROP TABLE IF EXISTS `mch_notify`;
CREATE TABLE `mch_notify` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mch_id` bigint(20) NOT NULL COMMENT '商户id',
  `notify_id` bigint(20) NOT NULL COMMENT '校验id',
  `type` tinyint(4) NOT NULL COMMENT '1.预下单标示  2.下单成功通知标示  3.充值成功通知标示  4.提现成功通知标示 5.退款成功通知标示',
  `create_time` datetime NOT NULL COMMENT '订单时间',
  `order_no` varchar(32) NOT NULL DEFAULT '' COMMENT '相关订单号',
  `count` int(11) NOT NULL DEFAULT '0' COMMENT '回调次数',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0.回调失败  1.回调成功',
  `callback_url` varchar(256) NOT NULL,
  `notify_str` varchar(2048) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_unique` (`notify_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COMMENT='校验id列表';



-- ----------------------------
-- Table structure for mch_refund_detail
-- ----------------------------
DROP TABLE IF EXISTS `mch_refund_detail`;
CREATE TABLE `mch_refund_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wallet_order_no` varchar(32) NOT NULL COMMENT '钱包退款订单号',
  `mch_order_no` varchar(32) NOT NULL COMMENT '商户退款订单号',
  `ori_wallet_order_no` varchar(32) NOT NULL COMMENT '钱包支付订单号(退款源订单号)',
  `ori_mch_order_no` varchar(32) NOT NULL COMMENT '商户支付订单号(退款源商户订单号)',
  `mch_no` bigint(20) NOT NULL COMMENT '商户号',
  `mch_id` bigint(20) NOT NULL COMMENT '商户id',
  `mch_user_id` bigint(20) NOT NULL COMMENT '商户对应用户ID',
  `refund_account_type` tinyint(4) NOT NULL COMMENT '1.未结算资金退款(默认) 2.可用余额退款',
  `user_id` bigint(20) DEFAULT NULL COMMENT '退款的用户ID',
  `symbol` varchar(8) NOT NULL COMMENT '代币代码',
  `amount` decimal(20,8) NOT NULL COMMENT '订单代币数量',
  `refund_remark` varchar(128) DEFAULT NULL COMMENT '退款原因',
  `ip` varchar(64) NOT NULL,
  `notify_url` varchar(256) NOT NULL COMMENT '退款结果通知url',
  `total_amount` decimal(20,8) NOT NULL COMMENT '原订单‌总金额',
  `nonce_str` varchar(32) NOT NULL COMMENT '用户的随机字符串',
  `create_time` datetime NOT NULL COMMENT '生成订单时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `wallet_UNIQUE` (`wallet_order_no`),
  UNIQUE KEY `mch_UNIQUE` (`mch_order_no`,`mch_id`),
  KEY `ori_mchNO_index` (`ori_mch_order_no`),
  KEY `ori_wallet_orderNo_index` (`ori_wallet_order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='交易订单表';


-- ----------------------------
-- Table structure for mch_trade_detail
-- ----------------------------
DROP TABLE IF EXISTS `mch_trade_detail`;
CREATE TABLE `mch_trade_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wallet_order_no` varchar(32) NOT NULL COMMENT '1.钱包订单号',
  `mch_order_no` varchar(32) NOT NULL COMMENT '商户订单号',
  `mch_no` bigint(20) NOT NULL COMMENT '商户号',
  `mch_id` bigint(20) NOT NULL COMMENT '商户id',
  `mch_user_id` bigint(20) NOT NULL COMMENT '商户对应用户ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '消费的用户ID',
  `symbol` varchar(8) NOT NULL COMMENT '代币代码',
  `amount` decimal(20,8) NOT NULL COMMENT '订单代币数量',
  `refund_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '实际退款数量',
  `charge_symbol` varchar(10) NOT NULL COMMENT '手续费代币代码',
  `charge_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费',
  `body` varchar(128) NOT NULL COMMENT '商品描述',
  `ip` varchar(64) NOT NULL,
  `actual_ip` varchar(64) NOT NULL,
  `trade_type` varchar(16) NOT NULL,
  `status` tinyint(4) NOT NULL COMMENT '1.待付款;2.已付款;3. 已付款,部分退款中;4.已付款,全额退款',
  `order_time` datetime NOT NULL COMMENT '生成订单时间',
  `expire_time` datetime NOT NULL COMMENT '订单失效时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `nonce_str` varchar(32) NOT NULL COMMENT '随机字符串',
  `notify_url` varchar(256) NOT NULL COMMENT '通知结果回调地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `mch_UNIQUE` (`mch_id`,`mch_order_no`),
  UNIQUE KEY `wallet_UNIQUE` (`wallet_order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb4 COMMENT='交易订单表';


-- ----------------------------
-- Table structure for merchant
-- ----------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `mch_name` varchar(30) NOT NULL,
  `mch_status` tinyint(4) NOT NULL COMMENT '0.未激活;1.提交审核;2,正常;3,冻结; ',
  `mch_level` tinyint(4) DEFAULT NULL COMMENT '预留字段',
  `mch_license_zm` varchar(1024) NOT NULL COMMENT '商家营业执照 正面',
  `mch_license_fm` varchar(1024) DEFAULT NULL COMMENT '商家营业执照 反面',
  `mch_no` bigint(20) NOT NULL COMMENT '商家接入API分配的商户号',
  `secret_key` varchar(32) NOT NULL COMMENT '密钥',
  `public_key` varchar(2048) DEFAULT NULL COMMENT '验证签名公钥',
  `bind_address` varchar(2048) DEFAULT NULL COMMENT '以逗号分隔的多个ip',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `dict_data` bigint(20) DEFAULT NULL COMMENT '商户审核失败原因 数据字典主键',
  `withdraw_callback` varchar(256) DEFAULT NULL COMMENT '提币回调地址',
  `recharge_callback` varchar(256) DEFAULT NULL COMMENT '充币回调地址',
  `charge_id` bigint(8) NOT NULL DEFAULT '1' COMMENT '手续费模板id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_USER_ID` (`user_id`),
  UNIQUE KEY `Index_USER_ID1` (`mch_no`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COMMENT='商家信息表';



-- ----------------------------
-- Table structure for symbol_exch
-- ----------------------------
DROP TABLE IF EXISTS `symbol_exch`;
CREATE TABLE `symbol_exch` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `exch_id` bigint(11) NOT NULL,
  `symbol_id` bigint(11) NOT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `exch_symbol` (`exch_id`,`symbol_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=136 DEFAULT CHARSET=utf8mb4 COMMENT='基础币种与交易所中间表';



-- ----------------------------
-- Table structure for transfer_exch
-- ----------------------------
DROP TABLE IF EXISTS `transfer_exch`;
CREATE TABLE `transfer_exch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `exch_id` bigint(11) NOT NULL DEFAULT '-1' COMMENT '交易所id',
  `transfer_id` bigint(11) NOT NULL COMMENT '货币转换配置表id',
  `charge_id` bigint(11) DEFAULT NULL,
  `max_trans_amount` decimal(20,8) DEFAULT NULL COMMENT '最大转币数量(交易所配置)',
  `min_trans_amount` decimal(20,8) DEFAULT NULL COMMENT '最小转币数量(交易所配置)',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COMMENT='货币转换与交易所中间表';



-- ----------------------------
-- Table structure for transfer_list
-- ----------------------------
DROP TABLE IF EXISTS `transfer_list`;
CREATE TABLE `transfer_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '转账用户Id',
  `receive_user_id` bigint(20) NOT NULL COMMENT '收款用户Id',
  `receive_user_name` varchar(30) NOT NULL COMMENT '收款用户名',
  `create_time` datetime NOT NULL COMMENT '记录生成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `list_unique` (`user_id`,`receive_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COMMENT='转账关系表';



-- ----------------------------
-- Table structure for transfer_order
-- ----------------------------
DROP TABLE IF EXISTS `transfer_order`;
CREATE TABLE `transfer_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '转账用户Id',
  `user_name` varchar(30) NOT NULL COMMENT '转账用户名',
  `receive_user_id` bigint(20) NOT NULL COMMENT '收款用户Id',
  `receive_user_name` varchar(30) NOT NULL COMMENT '收款用户名',
  `symbol` varchar(30) NOT NULL COMMENT '代币',
  `amount` decimal(20,8) NOT NULL COMMENT '转账数量',
  `charge_amount` decimal(20,8) NOT NULL COMMENT '手续费数量',
  `arrival_amount` decimal(20,8) NOT NULL COMMENT '实际到账数量',
  `status` tinyint(4) NOT NULL COMMENT '1:待转账   2:已转账',
  `create_time` datetime NOT NULL COMMENT '记录生成时间',
  `expire_time` datetime NOT NULL COMMENT '失效时间',
  `update_time` datetime DEFAULT NULL COMMENT '订单完成时间',
  `remark` varchar(128) DEFAULT NULL COMMENT '转账备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_unique` (`order_no`),
  KEY `transfer_index` (`user_id`,`receive_user_id`),
  KEY `receive_index` (`receive_user_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COMMENT='转账记录表';



-- ----------------------------
-- Table structure for user_login_log
-- ----------------------------
DROP TABLE IF EXISTS `user_login_log`;
CREATE TABLE `user_login_log` (
  `id_` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `login_type` varchar(10) DEFAULT NULL COMMENT '登陆方式(1:web、2:android、3:ios)',
  `login_ip` varchar(50) NOT NULL COMMENT '登陆地址',
  `login_status` tinyint(1) NOT NULL COMMENT '登陆状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` varchar(1) DEFAULT NULL,
  `update_by` varchar(1) DEFAULT NULL,
  `enable_` tinyint(1) DEFAULT NULL,
  `remark_` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_`)
) ENGINE=InnoDB AUTO_INCREMENT=5790 DEFAULT CHARSET=utf8 COMMENT='用户等级表';



-- ----------------------------
-- Table structure for user_offer_info
-- ----------------------------
DROP TABLE IF EXISTS `user_offer_info`;
CREATE TABLE `user_offer_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `src_symbol` varchar(30) NOT NULL COMMENT '原货币',
  `dst_symbol` varchar(30) NOT NULL COMMENT '目的货币',
  `order_price` decimal(20,8) NOT NULL COMMENT '报价价格',
  `order_volume` decimal(20,8) NOT NULL COMMENT '源报价数量',
  `remain_volume` decimal(20,8) DEFAULT NULL COMMENT '剩余数量',
  `admin_id` bigint(20) NOT NULL COMMENT '后台申请人id',
  `status` tinyint(4) DEFAULT NULL COMMENT '1.正常;0.已撤销;',
  `remark` varchar(128) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_U_S_D` (`src_symbol`,`dst_symbol`) USING BTREE,
  KEY `index_USER_ID` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='货币转换用户报价表';


-- ----------------------------
-- Table structure for valuation_mode
-- ----------------------------
DROP TABLE IF EXISTS `valuation_mode`;
CREATE TABLE `valuation_mode` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `dict_data_id` bigint(11) NOT NULL COMMENT '数据字典主键',
  `exch_id` bigint(11) NOT NULL COMMENT '交易所id',
  `default_symbol` varchar(10) DEFAULT NULL COMMENT '交易所默认计价方式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COMMENT='计价方式中间表';



-- ----------------------------
-- Table structure for white_exch_info
-- ----------------------------
DROP TABLE IF EXISTS `white_exch_info`;
CREATE TABLE `white_exch_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `white_name` varchar(30) NOT NULL COMMENT '白标名称',
  `language` varchar(30) NOT NULL COMMENT '语言',
  `lt_code` varchar(128) NOT NULL COMMENT '计价默认币种',
  `remark_` varchar(128) DEFAULT NULL COMMENT '备注',
  `domain_name` varchar(128) DEFAULT NULL COMMENT '域名',
  `own_time_zone` varchar(128) DEFAULT NULL COMMENT '白标所属时区',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 1: 默认启用 0 :禁用',
  `src_symbol_id` varchar(200) DEFAULT NULL COMMENT '报价源货币 如:BTC,BCH,LTC,UDT''',
  `dst_symbol` varchar(128) DEFAULT NULL COMMENT '报价目标货币货币 如:CCASH,',
  `group_id` bigint(11) DEFAULT NULL COMMENT '角色id',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `relate_user_id` bigint(20) DEFAULT NULL COMMENT '白标关联的前端用户',
  PRIMARY KEY (`id`)````
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='白标表';




ALTER TABLE `wallet`.`h_order_detail`
ADD COLUMN `charge` DECIMAL(12,4) NOT NULL DEFAULT 0 AFTER `interest`,
ADD COLUMN `profit` DECIMAL(12,4) NOT NULL DEFAULT 0 AFTER `charge`;

INSERT INTO `wallet`.`h_param` (`ud_key`, `ud_value`, `remark`) VALUES ('VISIT_CODE', '0Q0000', '默认邀请码');

ALTER TABLE `wallet`.`h_commission_detail`
  CHANGE `profit_rate` `profit_rate` DECIMAL(16,8) NOT NULL COMMENT '对应的利润分配比例';

ALTER TABLE `wallet`.`h_user_info`
ADD COLUMN `child_invest` DECIMAL(16,8) NOT NULL DEFAULT 0 AFTER `auto_repeat`;

INSERT INTO `base_dict_type` VALUES (15, 'UD社区通知用户充钱', 'CommunityChargeNotice', 1, '2019-6-24 11:48:14', '2019-6-24 11:48:14', 'Tang', 'Tang', NULL);
INSERT INTO `base_dict_type` VALUES (16, 'UD社区通知用户充钱(邮件标题)', 'CommunityChargeNotice_title', 1, '2019-6-24 13:47:25', '2019-6-24 13:48:19', 'Tang', 'Tang', NULL);
INSERT INTO `base_dict_type` VALUES (17, '发送短信验证码', 'USER_REG', 1, '2019-6-28 18:34:35', '2019-6-28 18:34:35', 'Tang', 'Tang', NULL);

INSERT INTO `base_dict_data` VALUES (83, '15', 1, '通知用户充钱', '1', 'CommunityChargeNotice', 'zh', 1, '2019-6-24 11:58:57', '2019-6-24 11:58:57', 'Tang', 'Tang', '尊敬的%s你好,你参投的UD智能社区方案：%s即将到期，请及时进行下一轮的充值，超时账户将被锁定，请知悉，谢谢！');
INSERT INTO `base_dict_data` VALUES (84, '15', 1, '通知用户充钱', '1', 'CommunityChargeNotice', 'en', 1, '2019-6-24 11:58:57', '2019-6-24 11:58:57', 'Tang', 'Tang', '尊敬的%s你好,你参投的UD智能社区方案：%s即将到期，请及时进行下一轮的充值，超时账户将被锁定，请知悉，谢谢！');

INSERT INTO `base_dict_data` VALUES (85, '16', 1, 'UD社区方案到期通知', '1', 'CommunityChargeNotice_title', 'zh', 1, '2019-6-24 13:49:21', '2019-6-24 13:49:21', 'Tang', 'Tang', NULL);
INSERT INTO `base_dict_data` VALUES (86, '16', 1, 'UD社区方案到期通知', '1', 'CommunityChargeNotice_title', 'en', 1, '2019-6-24 13:49:37', '2019-6-24 13:49:37', 'Tang', 'Tang', NULL);

INSERT INTO `base_dict_data` VALUES (87, '17', 1, '短信验证码', '1', 'USER_REG', 'zh', 1, '2019-6-28 18:35:56', '2019-6-28 18:35:56', 'Tang', 'Tang', '您的短信验证码为%s');
INSERT INTO `base_dict_data` VALUES (88, '17', 2, '短信验证码', '1', 'USER_REG', 'en', 1, '2019-6-28 18:37:14', '2019-6-28 18:37:14', 'Tang', 'Tang', '您的短信验证码为%s');



ALTER TABLE `wallet`.`h_node_award`
ADD COLUMN `child_invest` DECIMAL(16,8) NOT NULL DEFAULT 0 AFTER `rate`;

ALTER TABLE `wallet`.`h_user_info`
ADD COLUMN `add_amount` DECIMAL(16,8) NOT NULL DEFAULT 0 COMMENT '额外增加的投资额  用于等级变更' AFTER `child_invest`;

-- 2019-7-5 基础货币表增加字段  ( 线上sql已执行)
ALTER TABLE `wallet`.`basic_symbol`
ADD COLUMN `decimal_places` int(10) NOT NULL DEFAULT '8' COMMENT '前端换算保留小数位(1-8)' AFTER `remark`;

-- UD社区 用户管理新增资源权限
INSERT INTO `base_element` VALUES ('213', 'udUserManager:view', 'uri', 'View', '/userInfo/{*}', '96', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('214', 'udUserManager:btn_edit', 'button', 'Edit', '/userInfo/{*}', '96', null, null, 'PUT', null, null, null, null, null, null);


-- 2019-7-8 对冲表添加字段
ALTER TABLE `wallet`.`hedge_detail`
ADD COLUMN `proxy_code` varchar (20) DEFAULT NULL COMMENT '白标标识' AFTER `admin_dealed`;

`add_node_amount` decimal(16,8) NOT NULL DEFAULT '0.00000000' COMMENT '额外增加的投资额  用于节点奖的等级变更',
-- 2019-7-10 用户表添加字段
ALTER TABLE `wallet`.`h_user_info`
ADD COLUMN `add_node_amount` decimal(16,8) NOT NULL DEFAULT '0.00000000' COMMENT '额外增加的投资额  用于节点奖的等级变更' AFTER `add_amount`;

ALTER TABLE `wallet`.`h_user_info`
ADD COLUMN `add_node_amount` DECIMAL(16,8) NOT NULL DEFAULT 0 COMMENT '额外增加的投资额  用于节点奖的等级变更' AFTER `add_amount`;