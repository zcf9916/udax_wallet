ALTER TABLE `wallet`.`cfg_dc_recharge_withdraw`
ADD COLUMN  `min_transfer_amount` decimal(20,8) DEFAULT '0.00000000' COMMENT '用户之间转账最小数量限制' AFTER `max_withdraw_amount`;