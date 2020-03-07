--邮件审核增加交易所(已执行)
ALTER TABLE `wallet`.`base_email_auditor`   
  ADD COLUMN `white_exch_id` BIGINT(11) NOT NULL COMMENT '交易所id' AFTER `id`;
   
ALTER TABLE `wallet`.`base_email_auditor`   
  CHANGE `email_account` `email_account` VARCHAR(256) CHARSET utf8 COLLATE utf8_general_ci NULL COMMENT '审核人员邮箱账号';
  
insert into `base_email_auditor` (`white_exch_id`, `auditor_name`, `auditor_role`, `email_account`, `email_title`, `email_content`, `remark_`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) 
values('6','xxxx','UserAuditRemind','294816099@qq.com,294816099@qq.com','身份认证审核通知','管理员您好,用户%s已经发起身份认证申请,请及时审核,谢谢。',NULL,'2019-04-19 18:05:57','Tang','2019-07-23 16:13:27','TangHK');
insert into `base_email_auditor` (`white_exch_id`, `auditor_name`, `auditor_role`, `email_account`, `email_title`, `email_content`, `remark_`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) 
values('6','xxxx','WithdrawAuditRemind','294816099@qq.com,294816099@qq.com','提币审核通知','管理员您好,用户%s已经发起提币申请,请及时审核,谢谢。',NULL,'2019-07-23 14:51:57','Tang','2019-07-23 16:13:33','TangHK');
insert into `base_email_auditor` (`white_exch_id`, `auditor_name`, `auditor_role`, `email_account`, `email_title`, `email_content`, `remark_`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) 
values('6','xxxx','Merchant_Review','294816099@qq.com','商户认证通知','管理员您好,用户%s已经发起商户认证申请,请及时审核,谢谢。',NULL,'2019-07-23 16:16:17','TangHK','2019-07-23 16:16:17','TangHK');
