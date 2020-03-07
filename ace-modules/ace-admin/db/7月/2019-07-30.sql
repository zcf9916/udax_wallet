INSERT INTO `base_element` VALUES (null, 'withdrawAddress:btn_del', 'button', 'Delete', '/blockchain/{*}', '89', null, null, 'DELETE', null, null, null, null, null, null);

-- 备注: 执行SQL语句后及时修改 邮件账号[294816099@qq.com] 和 关联交易所信息
-- 每个交易所必须添加一条数据
INSERT INTO `base_email_auditor` VALUES
 (null, 8, 'xxxx', 'withdrawAuditError', '294816099@qq.com', '区块链提币异常通知', '管理员您好,用户%s 的提币请求区块链处理异常,请到[用户提现管理]重新审核,谢谢。', '[UDX] 白标 用户提币区块链处理异常,通知管理员重新审核', '2019-7-23 16:16:17', 'TangHK', '2019-7-30 18:06:44', 'admin');