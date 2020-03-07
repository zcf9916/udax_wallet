INSERT INTO `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES('116','distributor','星光分销管理','-1','/ifr','zhineng','dirt','0','星光分销管理','/distributor',NULL,'Layout',NULL,NULL,NULL,'2019-07-25 16:50:35','Tian');
INSERT INTO `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES('117','rebateConfig','邀请返利配置','116','/distributor/rebateConfig',NULL,'menu','0','邀请返利配置','/distributor/rebateConfig',NULL,'_import(\'distributor/rebateConfig/index\')',NULL,NULL,NULL,'2019-03-30 17:07:32','Tian');
INSERT INTO `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES('118','casinoRole','团队利润配置','116','/distributor/casinoRole',NULL,'menu','0','邀请返利配置','/distributor/casinoRole',NULL,'_import(\'distributor/casinoRole/index\')',NULL,NULL,NULL,'2019-03-30 17:07:32','Tian');
INSERT INTO `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES('119','methodConfig','开工模式配置','116','/distributor/methodConfig',NULL,'menu','0','开工模式配置','/distributor/methodConfig',NULL,'_import(\'distributor/methodConfig/index\')',NULL,NULL,NULL,'2019-03-30 17:07:32','Tian');
INSERT INTO `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES('120','casinoRole','分销参数管理','116','/distributor/casinoParam',NULL,'menu','0','邀请返利配置','/distributor/casinoParam',NULL,'_import(\'distributor/casinoParam/index\')',NULL,NULL,NULL,'2019-03-30 17:07:32','Tian');
INSERT INTO `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES('121','casinoUserInfo','星光会员管理','116','/distributor/casinoUserInfo',NULL,'menu','0','邀请返利配置','/distributor/casinoUserInfo',NULL,'_import(\'distributor/casinoUserInfo/index\')',NULL,NULL,NULL,'2019-03-30 17:07:32','Tian');
INSERT INTO `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES('122','casinoCommission','返佣订单管理','116','/distributor/casinoCommission',NULL,'menu','0','邀请返利配置','/distributor/casinoCommission',NULL,'_import(\'distributor/casinoCommission/index\')',NULL,NULL,NULL,'2019-03-30 17:07:32','Tian');
INSERT INTO `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES('123','casinoCommissionLog','订单明细管理','116','/distributor/casinoCommissionLog',NULL,'menu','0','订单明细管理','/distributor/casinoCommissionLog',NULL,'_import(\'distributor/casinoCommissionLog/index\')',NULL,NULL,NULL,'2019-03-30 17:07:32','Tian');



insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'rebateConfig:view','uri','View','/rebateConfig/{*}','117',NULL,NULL,'GET',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'rebateConfig:btn_add','button','New','/rebateConfig','117',NULL,NULL,'POST',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'rebateConfig:btn_edit','button','Edit','/rebateConfig/{*}','117',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'rebateConfig:btn_del','button','Delete','/rebateConfig/{*}','117',NULL,NULL,'DELETE',NULL,NULL,NULL,NULL,NULL,NULL);


insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoRole:view','uri','View','/casinoRole/{*}','118',NULL,NULL,'GET',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoRole:btn_add','button','New','/casinoRole','118',NULL,NULL,'POST',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoRole:btn_edit','button','Edit','/casinoRole/{*}','118',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoRole:btn_del','button','Delete','/casinoRole/{*}','118',NULL,NULL,'DELETE',NULL,NULL,NULL,NULL,NULL,NULL);


insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoParam:view','uri','View','/casinoParam/{*}','120',NULL,NULL,'GET',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoParam:btn_add','button','New','/casinoParam','120',NULL,NULL,'POST',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoParam:btn_edit','button','Edit','/casinoParam/{*}','120',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoParam:btn_del','button','Delete','/casinoParam/{*}','120',NULL,NULL,'DELETE',NULL,NULL,NULL,NULL,NULL,NULL);


insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'methodConfig:view','uri','View','/methodConfig/{*}','119',NULL,NULL,'GET',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'methodConfig:btn_add','button','New','/methodConfig','119',NULL,NULL,'POST',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'methodConfig:btn_edit','button','Edit','/methodConfig/{*}','119',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'methodConfig:btn_del','button','Delete','/methodConfig/{*}','119',NULL,NULL,'DELETE',NULL,NULL,NULL,NULL,NULL,NULL);

insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoUserInfo:btn_edit','button','Edit','/casinoUserInfo/{*}','121',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'casinoUserInfo:btn_edit_level','button','Edit','/casinoUserInfo/{*}','121',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);


CREATE TABLE `casino_commission` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `order_no` bigint(20) NOT NULL COMMENT '订单号 自动生成',
  `userId` bigint(20) NOT NULL DEFAULT '0' COMMENT '产生此订单用户',
  `user_name` char(50) NOT NULL COMMENT '用户名称',
  `method_type` tinyint(4) NOT NULL COMMENT '开工模式:1:与平台按比例分成 2:会员与客户对赌 3:邀请返利',
  `total_amount` decimal(16,8) NOT NULL COMMENT '总业绩',
  `member_amount` decimal(16,8) DEFAULT NULL COMMENT '会员分红 会员订单分红',
  `platform_amount` decimal(16,8) DEFAULT NULL COMMENT '平台分红',
  `straight_amout` decimal(16,8) DEFAULT NULL COMMENT '直推分红',
  `indirect_amout` decimal(16,8) DEFAULT NULL COMMENT '间接分红',
  `president_amount` decimal(16,8) DEFAULT NULL COMMENT '副总裁分红',
  `manager_amount` decimal(16,8) DEFAULT NULL COMMENT '副总经理',
  `create_time` datetime NOT NULL COMMENT '订单时间',
  `order_time` bigint(11) NOT NULL COMMENT '订单时间时间戳',
  PRIMARY KEY (`id`),
  KEY `order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=100252 DEFAULT CHARSET=utf8 COMMENT='返利订单表';

CREATE TABLE `casino_commission_log` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `order_no` bigint(20) NOT NULL COMMENT '订单号 自动生成',
  `direct_user_id` bigint(20) DEFAULT '0' COMMENT '业绩的直推用户ID,如果为平台分成者为空',
  `direct_name` char(20) DEFAULT NULL COMMENT '直推用户名,如果为平台分成者为空',
  `settle_type` tinyint(4) DEFAULT NULL COMMENT '结算方式  0 与平台按比例分成 1 会员与客户对赌',
  `amount` decimal(16,8) NOT NULL COMMENT '分到的业绩',
  `total_amount` decimal(16,8) NOT NULL COMMENT '总业绩',
  `cms_type` tinyint(4) NOT NULL COMMENT '分成类型  1 推荐返佣  2 业绩分成',
  `cms_rate` decimal(6,4) NOT NULL COMMENT '分成比例',
  `receive_user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '收取分成的用户id',
  `receive_user_name` char(20) NOT NULL COMMENT '收取分成的用户名',
  `exchange_id` bigint(20) NOT NULL COMMENT '白标ID',
  `currency` char(10) DEFAULT NULL COMMENT '佣金的货币单位',
  `settle_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '结算状态: 0:未结算.1:已结算.',
  `create_time` datetime NOT NULL COMMENT '订单时间',
  `order_time` int(11) NOT NULL COMMENT '订单时间时间戳',
  `role_type` tinyint(4) DEFAULT NULL COMMENT '角色类型 * 1 直推用户 2间接推荐人 3白标分成 4 副总经理 5副总裁 6 会员自己',
  PRIMARY KEY (`id`),
  KEY `dateIndex` (`order_time`,`receive_user_id`),
  KEY `receive_user_index` (`receive_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100327 DEFAULT CHARSET=utf8 COMMENT='赌场分成明细表';

CREATE TABLE `casino_method_config` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_cms_rate` decimal(4,4) DEFAULT NULL COMMENT '会员分成比例',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型 1:与平台按比例分 2会员与客户对赌',
  `platform_cms_rate` decimal(4,4) DEFAULT NULL COMMENT '平台分成比例',
  `fixed_value` decimal(16,8) DEFAULT NULL COMMENT '类型为2时,收取的固定服务费',
  `direct_user_rate` decimal(4,4) NOT NULL COMMENT '直推用户享受的比例',
  `indirect_user_rate` decimal(4,4) NOT NULL COMMENT '间接用户享受的比例',
  `exch_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '白标ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0 禁用  1正常',
  `remark` varchar(128) DEFAULT NULL COMMENT '级别描述',
  PRIMARY KEY (`id`),
  KEY `type_index` (`type`,`exch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8 COMMENT='赌场邀请返利分佣配置表';

CREATE TABLE `casino_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `casino_key` varchar(128) NOT NULL COMMENT '参数key',
  `casino_value` varchar(128) NOT NULL COMMENT '参数value',
  `exch_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '白标ID',
  `remark` varchar(128) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8 COMMENT='赌场用户参数表';

CREATE TABLE `casino_rebate_config` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `cms_rate` decimal(4,4) NOT NULL COMMENT '分成比例',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型 1:直推用户  2:间接推荐人  3:白标分成',
  `exch_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '白标ID',
  `remark` varchar(128) DEFAULT NULL COMMENT '级别描述',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0 禁用  1正常',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8 COMMENT='赌场邀请返利分佣配置表';


CREATE TABLE `casino_role` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `cms_rate` decimal(4,4) NOT NULL COMMENT '额外利润分成比例',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型 1.副总经理  2 副总裁',
  `direct_child` int(11) NOT NULL DEFAULT '0' COMMENT '副总经理直推达标人数',
  `all_child` int(11) NOT NULL DEFAULT '0' COMMENT '副总经理有效达标人数',
  `remark` varchar(128) DEFAULT NULL COMMENT '级别描述',
  `exch_id` tinyint(4) DEFAULT '1' COMMENT '白标',
  `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '0 禁用  1正常',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8 COMMENT='赌场角色配置表';


CREATE TABLE `casino_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `casino_name` char(32) DEFAULT NULL COMMENT '赌场账户',
  `settle_type` tinyint(4) DEFAULT NULL COMMENT '结算方式  0 与平台按比例分成 1 会员与客户对赌',
  `user_id` bigint(20) NOT NULL,
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
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0 未激活用户   1 精英用户   2 副总经理  3  副总裁',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0 不能享有收益   1 可以享有收益',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_index` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8 COMMENT='赌场用户信息'