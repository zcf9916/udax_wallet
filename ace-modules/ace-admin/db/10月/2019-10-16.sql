CREATE TABLE `user_symbol_lock_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `symbol` varchar(20) NOT NULL,
  `lock_id` bigint(20) NOT NULL,
  `free_amount` decimal(24,8) NOT NULL COMMENT '释放数量',
  `update_time` datetime DEFAULT NULL COMMENT '实际释放的时间',
  `create_time` datetime NOT NULL COMMENT '计算出的释放的时间',
  `is_free` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已释放  0 否  1是',
  `free_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 自动释放   2  手动释放',
  `free_by` varchar(128) DEFAULT NULL COMMENT '手动释放人',
  `version` int(11) NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni` (`symbol`,`user_id`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=481 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_symbol_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `symbol` varchar(30) NOT NULL COMMENT '币种',
  `total_amount` decimal(24,8) NOT NULL COMMENT '总数量',
  `freed_amount` decimal(24,8) NOT NULL DEFAULT '0.00000000' COMMENT '已释放数量',
  `freed_time` int(10) NOT NULL DEFAULT '0' COMMENT '已释放次数',
  `is_freed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否释放完成: 0:否 1:是',
  `freed_scale` decimal(4,4) DEFAULT NULL COMMENT '释放比例',
  `total_time` int(10) DEFAULT NULL COMMENT '总释放次数',
  `has_detail` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否生成释放明细 0:否 1:是',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `version` varchar(45) NOT NULL DEFAULT '1' COMMENT '版本号',
  `freed_cycle` int(20) NOT NULL COMMENT '释放周期(天)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni` (`symbol`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;


ALTER TABLE `wallet`.`symbol_exch`
ADD COLUMN  `symbol` varchar(30) DEFAULT NULL COMMENT '币种' AFTER `symbol_id`;


ALTER TABLE `wallet`.`symbol_exch`
ADD COLUMN  `freed_cycle` int   (20) DEFAULT NULL COMMENT '释放周期(天)' AFTER `symbol`;

ALTER TABLE `wallet`.`symbol_exch`
ADD COLUMN  `freed_number` int   (10) DEFAULT NULL COMMENT '释放次数' AFTER `freed_cycle`;


ALTER TABLE `wallet`.`symbol_exch`
ADD COLUMN  `has_lock` tinyint   (4) DEFAULT '0' COMMENT '是否锁定充值' AFTER `freed_number`;

ALTER TABLE `wallet`.`white_exch_info`
ADD COLUMN  `register_type` TINYINT   (4) DEFAULT '1' COMMENT '注册类型:1:全部 2:只允许手机注册 3:只允许邮箱注册' AFTER `domain_name`;

INSERT INTO `base_element` VALUES (null, 'userInfo:freedLock', 'button', 'freedLock', '/userInfo/{*}', '53', null, null, 'PUT', null, null, '2019-03-26 17:58:15', 'Tian', '2019-06-19 19:49:56', 'Tian');
INSERT INTO `base_element` VALUES (null, 'userInfo:freedLockAll', 'button', 'freedLockAll', '/userInfo/{*}', '53', null, null, 'PUT', null, null, '2019-03-26 17:58:15', 'Tian', '2019-06-19 19:49:56', 'Tian');
INSERT INTO `base_element` VALUES (null, 'userSymbolLock:view', 'uri', 'View', '/userSymbolLock/{*}', '112', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES (null, 'userSymbolLockDetail:view', 'uri', 'View', '/userSymbolLockDetail/{*}', '113', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES (null, 'userSymbolLockDetail:manualFreedAssets', 'button', 'manualFreedAssets', '/userSymbolLockDetail/{*}', '113', null, null, 'PUT', null, null, '2019-03-26 17:58:15', 'Tian', '2019-06-19 19:49:56', 'Tian');


INSERT INTO `base_menu` VALUES (112, 'userSymbolLock', '用户锁仓记录', '56', '/assets/userSymbolLock', null, 'menu', '0', '用户锁仓记录', '/assetsManager/userSymbolLock', null, '_import(\'assets/userSymbolLock/index\')', null, null, null, '2019-03-30 17:07:32', 'Tian');
INSERT INTO `base_menu` VALUES (113, 'userSymbolLockDetail', '锁仓释放明细', '56', '/assets/userSymbolLockDetail', null, 'menu', '0', '锁仓释放明细', '/assetsManager/userSymbolLockDetail', null, '_import(\'assets/userSymbolLockDetail/index\')', null, null, null, '2019-03-30 17:07:32', 'Tian');


insert into `base_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values('26','释放到账通知','UnlockAssertTemplate','1','2019-10-17 11:15:47','2019-10-17 11:15:47','Tian','Tian',NULL);
insert into `base_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values('27','释放到账通知(标题)','UnlockAssertTemplate_title','1','2019-10-17 11:20:19','2019-10-17 11:20:19','Tian','Tian',NULL);

INSERT INTO `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) VALUES(NULL,'26','1','释放到账通知','1','UnlockAssertTemplate','zh','1','2019-10-17 11:19:06','2019-10-17 11:19:39','Tian','Tian','您好 %s，您本期所释放的%s %s已到账，请注意查收');
INSERT INTO `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) VALUES(NULL,'26','2','释放到账通知','1','UnlockAssertTemplate','en','1','2019-10-17 11:19:35','2019-10-17 11:19:35','Tian','Tian','您好 %s，您本期所释放的%s %s已到账，请注意查收');
INSERT INTO `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) VALUES(NULL,'27','1','释放到账通知(标题)','1','UnlockAssertTemplate_title','zh','1','2019-10-17 11:20:51','2019-10-17 11:20:51','Tian','Tian','释放到账通知');
INSERT INTO `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) VALUES(NULL,'27','2','释放到账通知(标题)','1','UnlockAssertTemplate_title','en','1','2019-10-17 11:21:13','2019-10-17 11:21:13','Tian','Tian','释放到账通知');