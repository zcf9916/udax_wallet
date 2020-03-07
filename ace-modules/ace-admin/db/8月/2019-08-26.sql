ALTER TABLE `wallet`.`front_recharge`
ADD COLUMN  `proxy_code` varchar(50) DEFAULT NULL COMMENT '白标标识' AFTER `fee_symbol`;

