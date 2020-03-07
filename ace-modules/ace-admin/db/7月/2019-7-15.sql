-- 用户利润分成比例配置表
ALTER TABLE `wallet`.`h_bonus_config`
ADD COLUMN `exch_id` bigint(11) NOT NULL AFTER `profit_rate`;

-- 节点配置表
ALTER TABLE `wallet`.`h_node_award`
ADD COLUMN `exch_id` bigint(11) NOT NULL AFTER `child_invest`;
ALTER TABLE `wallet`.`h_node_award`
ADD COLUMN `symbol` varchar(30) NOT NULL AFTER `exch_id`;
-- 更新币种
UPDATE `h_node_award` SET `symbol`='UD'



-- 参数配置表
ALTER TABLE `wallet`.`h_param`
ADD COLUMN `exch_id` bigint(11) NOT NULL AFTER `ud_value`;

-- 申购等级表
ALTER TABLE `wallet`.`h_purchase_level`
ADD COLUMN `exch_id` bigint(11) NOT NULL AFTER `name`;

ALTER TABLE `wallet`.`h_purchase_level`
ADD COLUMN  `symbol` varchar(10) NOT NULL AFTER `exch_id`;
UPDATE `h_purchase_level` SET `symbol`='UD'

-- 分润详情表
ALTER TABLE `wallet`.`h_commission_detail`
ADD COLUMN  `symbol` varchar(10) NOT NULL AFTER `receive_user_id`;
UPDATE `h_commission_detail` SET `symbol`='UD'

-- 每日收益流水表
ALTER TABLE `wallet`.`h_daily_profit_detail`
ADD COLUMN  `symbol` varchar(10) NOT NULL AFTER `level_id`;
UPDATE `h_daily_profit_detail` SET `symbol`='UD'

-- 备注信息  需修改线上已存在的数据
-- 删除利润分成表中索引
ALTER TABLE wallet.h_bonus_config DROP INDEX level_Unique;
-- (已执行)