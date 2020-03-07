-- 禁用功能资源
INSERT INTO `base_element` VALUES (null, 'userInfo:btn_freeze', 'button', 'freeze', '/userInfo/{*}', '53', NULL, NULL, 'PUT', NULL, NULL, '2019-7-29 13:52:49', 'Tian', '2019-7-29 13:52:49', 'Tian');



-- 功能列表

INSERT INTO `base_dict_type` VALUES (25, '用户功能冻结列表', 'User_Features_freeze', 1, '2019-7-29 14:24:36', '2019-7-29 14:24:36', 'Tian', 'Tian', NULL);

INSERT INTO `base_dict_data` VALUES (null, '25', 1, '币币转换', '1', 'User_Features_freeze', 'zh', 1, '2019-7-29 14:25:45', '2019-7-30 16:01:02', 'Tang', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 1, '币币转换', '1', 'User_Features_freeze', 'en', 1, '2019-7-29 14:26:10', '2019-7-29 14:26:10', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 1, '币币转换', '1', 'User_Features_freeze', 'ko', 1, '2019-7-29 14:27:12', '2019-7-30 16:01:11', 'Tang', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 2, '用户与用户转账', '2', 'User_Features_freeze', 'zh', 1, '2019-7-29 14:27:39', '2019-7-29 14:28:16', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 2, '用户与用户转账', '2', 'User_Features_freeze', 'en', 1, '2019-7-29 14:28:04', '2019-7-29 14:28:04', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 2, '用户与用户转账', '2', 'User_Features_freeze', 'ko', 1, '2019-7-29 14:28:32', '2019-7-29 14:28:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 3, '提币', '3', 'User_Features_freeze', 'zh', 1, '2019-7-29 14:28:58', '2019-7-29 14:28:58', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 3, '提币', '3', 'User_Features_freeze', 'en', 1, '2019-7-29 14:29:14', '2019-7-29 14:29:14', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 3, '提币', '3', 'User_Features_freeze', 'ko', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 4, '充币', '4', 'User_Features_freeze', 'zh', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 4, '充币', '4', 'User_Features_freeze', 'en', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 4, '充币', '4', 'User_Features_freeze', 'ko', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 5, 'UD社区方案申购', '5', 'User_Features_freeze', 'zh', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 5, 'UD社区方案申购', '5', 'User_Features_freeze', 'en', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 5, 'UD社区方案申购', '5', 'User_Features_freeze', 'ko', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 6, '跟单交易', '6', 'User_Features_freeze', 'zh', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 6, '跟单交易', '6', 'User_Features_freeze', 'en', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '25', 6, '跟单交易', '6', 'User_Features_freeze', 'ko', 1, '2019-7-29 14:29:32', '2019-7-29 14:29:32', 'Tian', 'Tian', NULL);



-- 表结构

CREATE TABLE `front_freeze_info` (
  `id` bigint(12) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(12) NOT NULL,
  `freeze_type` tinyint(4) NOT NULL COMMENT '冻结功能 1币币交易,2-用户转账,3-提币,4-充币,5-社区排单申购,6-跟单交易',
  `enable` tinyint(4) DEFAULT NULL COMMENT '0-冻结(禁用),1-启用',
  `crt_name` varchar(255) DEFAULT NULL,
  `crt_time` datetime DEFAULT NULL,
  `upd_time` datetime DEFAULT NULL,
  `upd_name` varchar(255) DEFAULT NULL,
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_Freeze` (`user_id`,`freeze_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户操作功能冻结表'


