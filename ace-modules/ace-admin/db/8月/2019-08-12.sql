
-- 2019-8-12 商家表
ALTER TABLE `wallet`.`merchant`
ADD COLUMN `settle_time` int(10) DEFAULT '0' COMMENT '商家待结资产(结算时间)' AFTER `recharge_callback`;

-- 2019-8-12 交易所表
ALTER TABLE `wallet`.`white_exch_info`
ADD COLUMN `settle_time` int(10) DEFAULT '0' COMMENT '商家待结资产(结算时间)' AFTER `group_id`;

ALTER TABLE `wallet`.`mch_trade_detail`
ADD COLUMN `settle_status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '0 未结算   1 已结算到可用余额' AFTER `notify_url`;


ALTER TABLE `wallet`.`mch_trade_detail`
ADD COLUMN `settle_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '结算时间';


ALTER TABLE `wallet`.`transfer_order`
ADD COLUMN `type` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '0 普通转账   1  红包转账' AFTER `user_name`;


CREATE TABLE `red_packet_send` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `receive_user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '个人红包对手方id',
  `group_id` varchar(20) NOT NULL DEFAULT '' COMMENT '群组号',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `symbol` varchar(20) NOT NULL COMMENT '代币',
  `type` tinyint(4) NOT NULL COMMENT '红包类型 0. 普通红包  1. 随机红包',
  `send_type` tinyint(4) NOT NULL COMMENT '0 个人红包   1 群红包',
  `num` int(10) NOT NULL DEFAULT '1' COMMENT '红包个数',
  `current_num` int(11) NOT NULL DEFAULT '0' COMMENT '红包已抢个数',
  `current_amout` decimal(16,8) NOT NULL DEFAULT '0.00000000' COMMENT '红包已抢金额',
  `total_amount` decimal(16,8) NOT NULL COMMENT '总金额',
  `return_amount` decimal(16,8) NOT NULL DEFAULT '0.00000000' COMMENT '返还总金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '利润结算状态: 0-已生成,1-已抢完  2 退款中  3.全部退还  4.部分退还',
  `create_time` datetime NOT NULL COMMENT '记录生成时间',
  `remark` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_orderno` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='发红包订单表'

CREATE TABLE `red_packet_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '发红包用户id',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `symbol` varchar(20) NOT NULL COMMENT '代币',
  `receive_user_id` bigint(20) NOT NULL COMMENT '收红包用户id',
  `type` tinyint(4) NOT NULL COMMENT '红包类型 0. 普通红包  1. 随机红包',
  `amount` decimal(16,8) NOT NULL COMMENT '抢到的红包金额',
  `total_amount` decimal(16,8) NOT NULL COMMENT '总金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '金额结算状态: 0-待入账,1-已入账',
  `create_time` datetime NOT NULL COMMENT '记录生成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_orderno` (`order_no`,`receive_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='红包明细记录表(拆红包)'
