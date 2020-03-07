 ALTER TABLE `dc_asset_account_log` DROP INDEX `unique_trans`;
 
 ALTER TABLE `dc_asset_account_log` ADD UNIQUE (trans_no,type,user_id,symbol);

ALTER TABLE `mch_trade_detail` DROP symbol;

ALTER TABLE `wallet`.`mch_trade_detail`
ADD COLUMN `symbol` varchar(128) NULL DEFAULT null  AFTER `amount`;

ALTER TABLE `mch_trade_detail` DROP amount;

ALTER TABLE `wallet`.`mch_trade_detail`
ADD COLUMN `amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '订单代币数量'  AFTER `refund_amount`;

CREATE TABLE `mch_pay_token` (
  `detail_id` bigint(11) NOT NULL,
  `symbol` varchar(10) DEFAULT NULL,
  `amount` decimal(16,8) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

