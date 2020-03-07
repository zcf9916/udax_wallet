
-- (已执行)
INSERT INTO `base_dict_type` VALUES (24, 'UD社区申购等级配置', 'UD_LEVEL ', 1, '2019-7-18 10:08:45', '2019-7-18 10:08:45', 'Tian', 'Tian', NULL);


INSERT INTO `base_dict_data` VALUES (null, '24', 1, '1', '1', 'UD_LEVEL ', NULL, 1, '2019-7-18 10:09:26', '2019-7-18 10:09:26', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '24', 2, '2', '2', 'UD_LEVEL ', NULL, 1, '2019-7-18 10:09:39', '2019-7-18 10:09:39', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '24', 3, '3', '3', 'UD_LEVEL ', NULL, 1, '2019-7-18 10:09:52', '2019-7-18 10:09:52', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '24', 4, '4', '4', 'UD_LEVEL ', NULL, 1, '2019-7-18 10:10:04', '2019-7-18 10:10:04', 'Tian', 'Tian', NULL);
INSERT INTO `base_dict_data` VALUES (null, '24', 5, '5', '5', 'UD_LEVEL ', NULL, 1, '2019-7-18 10:10:17', '2019-7-18 10:10:17', 'Tian', 'Tian', NULL);


--  申购表
ALTER TABLE `wallet`.`h_purchase_level`
ADD COLUMN    `level` int(2) DEFAULT NULL COMMENT '对应字典UD_LEVEL 申购等级' AFTER `wait_time`;

--  申购表
ALTER TABLE `wallet`.`h_user_info`
ADD COLUMN    `user_level` int DEFAULT '0' COMMENT '用户申购等级' ;