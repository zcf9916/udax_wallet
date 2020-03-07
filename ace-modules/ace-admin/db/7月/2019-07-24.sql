INSERT INTO `base_param` VALUES ('52', 'withdraw_limit', '1000', '自动提币限额(USDT)', '1', null, null, '2019-04-19 15:47:32', 'Tian');
INSERT INTO `base_param` VALUES ('53', 'withdraw_balance_limit', '1.5', '自动提币不能小于当前余额的倍数', '1', null, null, null, null);

ALTER TABLE `wallet`.`front_withdraw`
ADD COLUMN `auto_withdraw` TINYINT(4) NULL DEFAULT 0 COMMENT '0 审核提币   1自动提币' AFTER `withdraw_fee`;
-- (已执行 2019-7-24)