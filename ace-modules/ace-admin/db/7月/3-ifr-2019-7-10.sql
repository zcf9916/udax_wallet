-- 线上表结构 需先删除

CREATE TABLE `ifr_plan` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '方案名称',
  `time_period` int(10) NOT NULL COMMENT '时间周期',
  `interest` decimal(16,4) NOT NULL COMMENT '利息',
  `desp` varchar(5000) DEFAULT NULL COMMENT '描述信息',
  `status` tinyint(4) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='ifr 方案配置表';

-- 线上表结构 需先删除

CREATE TABLE `ifr_pay_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `campaign_id` varchar(30) NOT NULL COMMENT '活动id',
  `referral_id` varchar(30) NOT NULL COMMENT '邀请人id',
  `user_id` bigint(11) NOT NULL COMMENT '邮箱',
  `country` varchar(30) DEFAULT NULL COMMENT '国家',
  `currency` varchar(10) NOT NULL COMMENT '币种符号(如VND,THB,IDR)',
  `units` decimal(16,2) NOT NULL COMMENT '充值数量',
  `amount` decimal(20,2) NOT NULL COMMENT '换算的USD法币价值,需要加上点差',
  `period` int(11) NOT NULL COMMENT '持股周期(对应字典表)',
  `pay_status` tinyint(4) NOT NULL COMMENT '支付状态0:待支付,1:支付成功,2:支付失败',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COMMENT='IFR现金充值';


-- 线上表结构 需先删除

CREATE TABLE `ifr_exchange_rate` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(10) NOT NULL COMMENT '法币币种',
  `exchange_rate` decimal(16,8) NOT NULL COMMENT '与美元的汇率',
  `crt_time` datetime DEFAULT NULL,
  `crt_name` varchar(255) DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='ifr 汇率配置表';


-- 菜单
INSERT INTO `base_menu` VALUES ('107', 'ifrManager', 'ifr余额宝', '-1', '/ifr', 'zhineng', 'dirt', '0', 'ifr余额宝', '/ifrManager', null, 'Layout', null, null, null, '2019-06-10 18:04:37', 'Tian');
INSERT INTO `base_menu` VALUES ('108', 'ifrPlanManager', 'ifr方案配置', '107', '/ifr/firPlan', '', 'menu', '0', null, '/ifrManager/ifrPlanManager', null, '_import(\'ifr/firPlan/index\')', null, null, null, '2019-05-31 16:02:40', 'Tian');
INSERT INTO `base_menu` VALUES ('109', 'ifrExchangeRateManager', 'ifr汇率配置', '107', '/ifr/ifrExchangeRate', '', 'menu', '0', null, '/ifrManager/ifrExchangeRateManager', null, '_import(\'ifr/ifrExchangeRate/index\')', null, null, null, '2019-05-31 16:02:40', 'Tian');
INSERT INTO `base_menu` VALUES ('110', 'ifrPayOrderManager', 'ifr订单详情', '107', '/ifr/ifrPayOrder', '', 'menu', '0', null, '/ifrManager/ifrPayOrderManager', null, '_import(\'ifr/ifrPayOrder/index\')', null, null, null, '2019-05-31 16:02:40', 'Tian');
-- 资源
INSERT INTO `base_element` VALUES ('215', 'ifrPlanManager:view', 'uri', 'View', '/firPlan/{*}', '108', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('216', 'ifrPlanManager:btn_add', 'button', 'New', '/firPlan', '108', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('217', 'ifrPlanManager:btn_edit', 'button', 'Edit', '/firPlan/{*}', '108', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('218', 'ifrPlanManager:btn_del', 'button', 'Delete', '/firPlan/{*}', '108', null, null, 'DELETE', null, null, null, null, null, null);


INSERT INTO `base_element` VALUES ('219', 'ifrExchangeRateManager:view', 'uri', 'View', '/ifrExchangeRate/{*}', '109', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('220', 'ifrExchangeRateManager:btn_add', 'button', 'New', '/ifrExchangeRate', '109', null, null, 'POST', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('221', 'ifrExchangeRateManager:btn_edit', 'button', 'Edit', '/ifrExchangeRate/{*}', '109', null, null, 'PUT', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES ('222', 'ifrExchangeRateManager:btn_del', 'button', 'Delete', '/ifrExchangeRate/{*}', '109', null, null, 'DELETE', null, null, null, null, null, null);


INSERT INTO `base_element` VALUES ('225', 'ifrPayOrderManager:view', 'uri', 'View', '/ifrPayOrder/{*}', '110', null, null, 'GET', null, null, null, null, null, null);

-- 字典
INSERT INTO `base_dict_type` VALUES (18, 'IFR方案周期', 'IFR_PLAN_CYCLE', 1, '2019-7-4 10:30:22', '2019-7-4 14:05:07', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (89, '18', 1, '一个月', '1', 'IFR_PLAN_CYCLE', 'zh', 1, '2019-7-4 10:30:38', '2019-7-4 14:05:11', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (90, '18', 2, '三个月', '3', 'IFR_PLAN_CYCLE', 'zh', 1, '2019-7-4 10:31:01', '2019-7-4 14:05:23', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (91, '18', 3, '六个月', '6', 'IFR_PLAN_CYCLE', 'zh', 1, '2019-7-4 10:31:19', '2019-7-4 14:05:26', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (92, '18', 4, '十二个月', '12', 'IFR_PLAN_CYCLE', 'zh', 1, '2019-7-4 10:31:45', '2019-7-4 14:05:30', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (93, '18', 5, '二十四个月', '24', 'IFR_PLAN_CYCLE', 'zh', 1, '2019-7-4 10:32:00', '2019-7-4 14:05:34', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (94, '18', 1, 'One month', '1', 'IFR_PLAN_CYCLE', 'en', 1, '2019-7-4 14:00:14', '2019-7-4 14:05:39', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (95, '18', 2, 'Three months', '3', 'IFR_PLAN_CYCLE', 'en', 1, '2019-7-4 14:00:47', '2019-7-4 14:05:42', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (96, '18', 3, 'Six months', '6', 'IFR_PLAN_CYCLE', 'en', 1, '2019-7-4 14:01:21', '2019-7-4 14:05:47', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (97, '18', 4, 'Sixteen months', '12', 'IFR_PLAN_CYCLE', 'en', 1, '2019-7-4 14:02:03', '2019-7-4 14:05:51', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (98, '18', 5, 'Two or four months', '24', 'IFR_PLAN_CYCLE', 'en', 1, '2019-7-4 14:02:41', '2019-7-4 14:05:54', 'Tian', 'Tian', NULL);