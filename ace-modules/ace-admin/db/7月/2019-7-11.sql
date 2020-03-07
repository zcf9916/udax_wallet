 ALTER TABLE `wallet`.`h_commission_relation`
  ADD  UNIQUE INDEX `user_receive_unique` (`user_id`, `receive_user_id`);

  -- 2019-7-11
  INSERT INTO `base_element` VALUES ('223', 'udUserManager:btn_edit_node', 'button', 'Edit', '/userInfo/{*}', '96', null, null, 'PUT', null, null, null, null, null, null);
  INSERT INTO `base_element` VALUES ('224', 'udUserManager:btn_edit_unlock', 'button', 'Edit', '/userInfo/{*}', '96', null, null, 'PUT', null, null, null, null, null, null);

  -- 2019-7-11
ALTER TABLE `wallet`.`h_user_info`
ADD COLUMN `add_node_amount` DECIMAL(16,8) NOT NULL DEFAULT 0 COMMENT '额外增加的投资额  用于节点奖的等级变更' AFTER `add_amount`;
  -- 2019-7-11
ALTER TABLE `wallet`.`h_unlock_detail`
ADD COLUMN `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '激活类型:默认0 后台激活:1 ' AFTER `create_time`;

-- (已执行)