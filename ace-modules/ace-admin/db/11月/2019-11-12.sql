
INSERT INTO `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES(NULL,'cmsConfigManager','推荐分成配置','38','/config/cmsConfig','','menu','0',NULL,'/marketingManager/configManager/cmsConfigManager',NULL,'_import(\'/config/cmsConfig/index\')',NULL,NULL,NULL,'2019-09-11 14:08:59','Tian');
insert into `base_menu` (`id`, `code`, `title`, `parent_id`, `href`, `icon`, `type`, `order_num`, `description`, `path`, `enabled`, `attr1`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values('115','commissionLog','分成明细管理','56','/assets/commissionLog',NULL,'menu','0','分成明细管理','/assetsManager/commissionLog',NULL,'_import(\'assets/commissionLog/index\')',NULL,NULL,NULL,'2019-03-30 17:07:32','Tian');
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'cmsConfigManager:view','uri','View','/cmsConfig/{*}','114',NULL,NULL,'GET',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'cmsConfigManager:btn_add','button','New','/cmsConfig','114',NULL,NULL,'POST',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'cmsConfigManager:btn_edit','button','Edit','/cmsConfig/{*}','114',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'cmsConfigManager:btn_del','button','Delete','/cmsConfig/{*}','114',NULL,NULL,'DELETE',NULL,NULL,NULL,NULL,NULL,NULL);


ALTER TABLE `wallet`.`front_transfer_detail`
ADD COLUMN `settle_status` TINYINT(1) NULL DEFAULT 0 COMMENT '0:未生成结算记录   1:已生成结算记录' AFTER `remark`;


ALTER TABLE `wallet`.`transfer_order`
ADD COLUMN `settle_status` TINYINT(1) NULL DEFAULT 0 COMMENT '0:未生成结算记录   1:已生成结算记录' AFTER `remark`;

CREATE TABLE `cms_config`
(
  `id`          bigint(10)    NOT NULL AUTO_INCREMENT COMMENT '编号',
  `cms_rate`    decimal(4, 4) NOT NULL COMMENT '分成比例',
  `type`        tinyint(4)    NOT NULL DEFAULT '1' COMMENT '类型 1:第一级用户  2:第二级用户  3:第三级用户 4:推荐用户分成比例  5:白标分成比例',
  `exch_id`     bigint(20)    NOT NULL DEFAULT '0' COMMENT '白标ID',
  `remark`      varchar(20)            DEFAULT NULL COMMENT '级别描述',
  `enable`      tinyint(1)    NOT NULL DEFAULT '1' COMMENT '0 禁用  1正常',
  `create_time` datetime               DEFAULT NULL,
  `parent_id`   bigint(11)             DEFAULT '-1' COMMENT '上级id 默认为-1',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 87
  DEFAULT CHARSET = utf8 COMMENT ='用户分佣级别配置表';


CREATE TABLE `commission_log`
(
  `id`                bigint(10)     NOT NULL AUTO_INCREMENT COMMENT '编号',
  `order_no`          bigint(20)     NOT NULL COMMENT '成交单号',
  `tradeuser_id`      bigint(20)     NOT NULL COMMENT '交易用户ID',
  `order_type`        tinyint(4)     NOT NULL COMMENT '分成类型  1 转账  2 转币',
  `tradeuser_name`    char(20)       NOT NULL COMMENT '交易用户名',
  `receive_user_id`   bigint(20)              DEFAULT '0' COMMENT '收取佣金的用户',
  `receive_user_name` char(20)       NOT NULL COMMENT '交易用户ID',
  `exchange_id`       bigint(20)     NOT NULL COMMENT '白标ID',
  `type`              tinyint(4)     NOT NULL COMMENT '1:第一级用户  2:第二级用户  3:第三级用户 4:白标',
  `symbol`            char(10)       NOT NULL COMMENT '收取的手续费对应的币种',
  `settle_symbol`     char(10)       NOT NULL COMMENT '结算的币种 ',
  `total_cms`         decimal(16, 8) NOT NULL COMMENT '总的手续费',
  `cms_rate`          decimal(6, 4)  NOT NULL COMMENT '手续费分成比例',
  `amount`            decimal(16, 8) NOT NULL COMMENT '得到的手续费分成',
  `settle_amount`     decimal(16, 8) NOT NULL COMMENT '结算的币种手续费分成 ',
  `rate`              decimal(16, 2) NOT NULL COMMENT '手续费币种和结算币种的转换比例 即一个手续费币种=n个结算币种',
  `settle_status`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '结算状态: 0:未结算.1:结算入金成功.',
  `create_time`       datetime       NOT NULL COMMENT '创建时间',
  `order_time`        int(11)        NOT NULL COMMENT '订单时间时间戳',
  PRIMARY KEY (`id`),
  UNIQUE KEY `flow` (`order_no`, `type`),
  KEY `dateIndex` (`order_time`, `receive_user_id`),
  KEY `receive_user_index` (`receive_user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 100128
  DEFAULT CHARSET = utf8 COMMENT ='手续费分成明细表';


insert into `base_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values('33','手续费分成等级','commission_level','1','2019-11-12 11:29:26','2019-11-12 11:43:18','Tian','Tian',NULL);


insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'33','1','直推一级用户','1','commission_level','zh','1','2019-11-12 11:29:43','2019-11-12 11:29:43','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'33','2','第二级用户','2','commission_level','zh','1','2019-11-12 11:30:16','2019-11-12 11:30:16','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'33','3','第三级用户','3','commission_level','zh','1','2019-11-12 11:30:25','2019-11-12 11:30:25','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'33','1','直推一级用户','1','commission_level','en','1','2019-11-12 11:32:16','2019-11-12 11:32:16','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'33','2','第二级用户','2','commission_level','en','1','2019-11-12 11:32:26','2019-11-12 11:32:26','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'33','3','第三级用户','3','commission_level','en','1','2019-11-12 11:32:35','2019-11-12 11:32:35','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'33','4','推荐用户分成比例','4','commission_level','zh','1','2019-11-12 16:18:18','2019-11-12 16:18:18','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'33','4','推荐用户分成比例','4','commission_level','en','1','2019-11-12 16:18:31','2019-11-12 16:18:31','Tian','Tian',NULL);


insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'commissionLog:btn_edit','button','Edit','/commissionLog/{*}','115',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'commissionLog:btn_batch_edit','button','Edit','/commissionLog/{*}','115',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'frontTransferDetailManager:btn_edit','button','Edit','/frontUserManager/{*}','69',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);
insert into `base_element` (`id`, `code`, `type`, `name`, `uri`, `menu_id`, `parent_id`, `path`, `method`, `description`, `language_type`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) values(null,'transferOrderManager:btn_edit','button','Edit','/frontUserManager/{*}','60',NULL,NULL,'PUT',NULL,NULL,NULL,NULL,NULL,NULL);

insert into `base_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values('34','充值地址与链类型关联','recharge_protocol','1','2019-11-19 16:30:16','2019-11-19 16:30:16','Tian','Tian','充值分配地址时,根据链类型分配地址');

insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'34','1','OMNI','USDT','recharge_protocol',NULL,'1','2019-11-19 16:31:48','2019-11-19 16:31:48','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'34','2','ERC20','ETH','recharge_protocol',NULL,'1','2019-11-19 16:32:17','2019-11-19 16:32:17','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'34','3','TRC20','TRX','recharge_protocol',NULL,'1','2019-11-19 16:32:39','2019-11-19 16:32:39','Tian','Tian',NULL);
