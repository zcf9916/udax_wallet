
-- 修改资源sql(已执行)

UPDATE `base_element` SET `method`='PUT' WHERE (`id`='180');
INSERT INTO `base_element` VALUES ('250', 'valuationMode:btn_edit', 'button', 'Edit', '/valuationMode/{*}/mode/defaultSymbol', '105', null, null, 'POST', null, null, null, null, '2019-05-31 09:59:45', 'Tian');
UPDATE `base_element` SET `uri`='/basicSymbol/service' WHERE (`id`='77');
UPDATE `base_element` SET `uri`='/currencyTransfer/{*}' WHERE (`id`='78');
UPDATE `base_element` SET `uri`='/currencyTransfer' WHERE (`id`='79');
UPDATE `base_element` SET `uri`='/currencyTransfer/{*}' WHERE (`id`='80');
UPDATE `base_element` SET `uri`='/currencyTransfer/{*}' WHERE (`id`='81');
UPDATE `base_element` SET `uri`='/hedgeDetail/{*}/adminStatus' WHERE (`id`='166');
INSERT INTO `base_element` VALUES (null, 'configInfo:view', 'uri', 'View', '/blockchain/{*}', '88', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES (null, 'configInfo:view', 'uri', 'View', '/blockchain/{*}', '89', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES (null, 'configInfo:view', 'uri', 'View', '/blockchain/{*}', '90', null, null, 'GET', null, null, null, null, null, null);
INSERT INTO `base_element` VALUES (null, 'configInfo:view', 'uri', 'View', '/blockchain/{*}', '91', null, null, 'GET', null, null, null, null, null, null);


