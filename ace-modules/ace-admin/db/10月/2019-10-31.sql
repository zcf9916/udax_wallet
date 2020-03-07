insert into `base_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values('29','钉钉通知URL','noticeManager','1','2019-10-31 17:08:27','2019-10-31 17:08:27','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'29','1','https://oapi.dingtalk.com/robot/send?access_token=cebf348da8f511331c833ca3eef945d6fa57d81d12bd3f6b1e4b00a768dcccef','1','noticeManager','zh','1','2019-10-31 17:09:09','2019-10-31 17:09:09','Tian','Tian','https://oapi.dingtalk.com/robot/send?access_token=cebf348da8f511331c833ca3eef945d6fa57d81d12bd3f6b1e4b00a768dcccef');



insert into `base_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values('30','用户充值到账(内容)','RechargeCoin','1','2019-10-31 17:58:09','2019-10-31 17:59:53','Tian','Tian',NULL);
insert into `base_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values('31','用户充值到账(邮件标题)','RechargeCoin_title','1','2019-10-31 18:00:28','2019-10-31 18:00:28','Tian','Tian',NULL);

-- 充值到账通知 内容考取的交易所的
-- 邮件标题需要翻译

insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'30','1','尊敬的%s您好，你充值的%s%s已到账','1','RechargeCoin','zh','1','2019-10-31 17:58:35','2019-10-31 17:58:35','Tian','Tian','尊敬的%s你好，你充值的%s%s已到账');
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'30','1','Salam %s Halo, isi ulang Anda %s %s telah ditambahkan','1','RechargeCoin','en','1','2019-10-31 17:59:00','2019-10-31 17:59:00','Tian','Tian','Salam %s Halo, isi ulang Anda %s %s telah ditambahkan');
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'30','1','존경하는%s안녕하세요.  당신의%s%s충전이 완료되었습니다','1','RechargeCoin','ko','1','2019-10-31 17:59:17','2019-10-31 17:59:17','Tian','Tian','존경하는%s안녕하세요.  당신의%s%s충전이 완료되었습니다');
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null ,'31','1','充值到账通知','1','RechargeCoin_title','zh','1','2019-10-31 18:05:37','2019-10-31 18:05:43','Tian','Tian','充值到账通知');
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null ,'31','1','充值到账通知','1','RechargeCoin_title','en','1','2019-10-31 18:05:52','2019-10-31 18:05:52','Tian','Tian',NULL);
insert into `base_dict_data` (`id`, `dict_id`, `sort`, `dict_label`, `dict_value`, `dict_type`, `language_type`, `status`, `crt_time`, `upd_time`, `upd_name`, `crt_name`, `remark`) values(null,'31','1','充值到账通知','1','RechargeCoin_title','ko','1','2019-10-31 18:05:59','2019-10-31 18:05:59','Tian','Tian',NULL);

--  后台登录失效时间
INSERT INTO `base_param` (`id`, `param_key`, `param_value`, `remark`, `status`, `crt_time`, `crt_name`, `upd_time`, `upd_name`) VALUES(NULL,'admin_token','3600','后台token失效时间','1','2019-11-01 17:59:57','Tian','2019-11-01 18:06:09','Tian');


-- 交易对是否允许交易 按钮权限
INSERT INTO `base_element` VALUES (NULL, 'transferExch:isOpen', 'button', 'updateIsOpen', '/transferExch/{*}', '84', NULL, NULL, 'POST', NULL, NULL, NULL, NULL, '2019-05-31 09:59:45', 'Tian');


ALTER TABLE `wallet`.`transfer_exch`
ADD COLUMN `is_open` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '是否允许交易:0禁用 1启用(默认)' AFTER `min_trans_amount`;
