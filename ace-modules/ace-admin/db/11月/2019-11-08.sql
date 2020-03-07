


insert into `base_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values('32','多个链名称','protocol_type_list','1','2019-11-08 09:37:38','2019-11-08 09:37:38','Tian','Tian',NULL);


insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'32','1','OMNI','OMNI','protocol_type_list',NULL,'1','2019-11-08 09:38:37','2019-11-08 09:38:37','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'32','2','ERC20','ERC20','protocol_type_list',NULL,'1','2019-11-08 09:38:45','2019-11-08 09:38:45','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'32','3','TRC20','TRC20','protocol_type_list',NULL,'1','2019-11-08 09:39:45','2019-11-08 09:39:45','Tian','Tian',NULL);


-- 币种表
ALTER TABLE `wallet`.`basic_symbol`
ADD COLUMN  `protocol_type` varchar(128) DEFAULT NULL COMMENT '链类型' AFTER `symbol`;
ALTER TABLE `wallet`.`basic_symbol`
ADD COLUMN `is_show` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '是否展示: 1展示(默认),0不展示 ' AFTER `symbol`;


-- 删除原索引
ALTER  TABLE   `wallet`.`basic_symbol`  drop  INDEX  index_symbol;
ALTER  TABLE   `wallet`.`basic_symbol`  drop  INDEX  index_status;
-- 添加唯一索引
ALTER TABLE `wallet`.`basic_symbol`  ADD UNIQUE key UNIQUE_symbol (symbol,protocol_type);



-- 手续费配置表
ALTER TABLE `wallet`.`cfg_currency_charge`
ADD COLUMN  `protocol_type` varchar(128) DEFAULT NULL COMMENT '链类型' AFTER `symbol`;
ALTER TABLE `wallet`.`cfg_currency_charge`
ADD COLUMN `is_show` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '根据币种更新此字段' AFTER `symbol`;
-- 删除原索引
ALTER  TABLE   `wallet`.`cfg_currency_charge`  drop  INDEX  Unique_symbol_exchid;
-- 添加唯一索引
ALTER TABLE `wallet`.`cfg_currency_charge`  ADD UNIQUE key Unique_symbol_exchid (exch_id,basic_symbol_id,protocol_type);



-- 充提币配置表
ALTER TABLE `wallet`.`cfg_dc_recharge_withdraw`
ADD COLUMN  `protocol_type` varchar(128) DEFAULT NULL COMMENT '链类型' AFTER `symbol`;
ALTER TABLE `wallet`.`cfg_dc_recharge_withdraw`
ADD COLUMN `is_show` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '' AFTER `symbol`;

-- 删除原索引
ALTER  TABLE   `wallet`.`cfg_dc_recharge_withdraw`  drop  INDEX  index_symbol;
-- 添加唯一索引
ALTER TABLE `wallet`.`cfg_dc_recharge_withdraw`  ADD UNIQUE key Unique_symbol (symbol,exch_id,system_config,protocol_type);



-- 币种描述信息
ALTER TABLE `wallet`.`cfg_symbol_description`
ADD COLUMN  `protocol_type` varchar(128) DEFAULT NULL COMMENT '链类型' AFTER `symbol`;
ALTER TABLE `wallet`.`cfg_symbol_description`
ADD COLUMN `is_show` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '' AFTER `protocol_type`;
-- 添加唯一索引
ALTER TABLE `wallet`.`cfg_symbol_description`  ADD UNIQUE key Unique_symbol (symbol,exchange_id,protocol_type);
-- 删除symbol_id 列
ALTER TABLE `wallet`.`cfg_symbol_description`  DROP COLUMN symbol_id

--  充值表
ALTER TABLE `wallet`.`front_recharge`
ADD COLUMN  `protocol_type` varchar(128) DEFAULT NULL COMMENT '链类型' AFTER `proxy_code`;
-- 提现表
ALTER TABLE `wallet`.`front_withdraw`
ADD COLUMN  `protocol_type` varchar(128) DEFAULT NULL COMMENT '链类型' AFTER `auto_withdraw`;