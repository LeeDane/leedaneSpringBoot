
-- ----------------------------
-- Table structure for t_financial
-- ----------------------------
DROP TABLE IF EXISTS `t_financial`;
CREATE TABLE `t_financial` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `local_id` int(11) COMMENT '客户端本地存储的ID',
  `imei` varchar(20) COMMENT '客户端唯一标记imei码',
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `addition_time` datetime NOT NULL   '很重要，添加时间，用于今后的统计时间，必须',
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `model` int(11) DEFAULT '0' COMMENT '模块，1:收入；2：支出',
  `money` float NOT NULL DEFAULT '0.00' COMMENT '相关金额',
  `one_level` varchar(20) DEFAULT NULL COMMENT '一级分类',
  `two_level` varchar(20) DEFAULT NULL COMMENT '二级分类',
  `has_img` bit(1) DEFAULT b'0' COMMENT '是否有图片',
  `path` longtext DEFAULT NULL COMMENT '',
  `latitude` double COMMENT '纬度',
  `location` varchar(255) DEFAULT NULL  COMMENT '位置的展示信息',
  `longitude` double COMMENT '经度',
  `financial_desc` longtext COMMENT '备注信息',
  `add_day` varchar(10) NOT NULL COMMENT '取addition_time前面的10为，格式如2016-09-01',
  PRIMARY KEY (`id`),
  KEY `financial_create_user_id` (`create_user_id`),
  KEY `financial_modify_user_id` (`modify_user_id`),
  CONSTRAINT `FK_financial_create_user_id` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_financial_modify_user_id` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*添加imei码和localId、add_day的唯一性约束，避免用户多次提交*/
alter table t_financial add constraint imei_local_id_unique UNIQUE(imei, local_id, add_day);

/* 在插入数据之前设置add_day列的值*/
create trigger financial_add_day_trigger before INSERT on t_financial
for EACH ROW
BEGIN
 SET NEW.add_day = date_format(NEW.addition_time,'%Y-%m-%d');
end;


CREATE TABLE `t_mood` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `comment_number` int(11) DEFAULT '0',
  `content` longtext NOT NULL,
  `froms` varchar(255) DEFAULT NULL,
  `has_img` bit(1) DEFAULT b'0',
  `is_publish_now` bit(1) DEFAULT b'0',
  `read_number` int(11) DEFAULT '0',
  `share_number` int(11) DEFAULT '0',
  `is_solr_index` bit(1) DEFAULT b'0',
  `str1` varchar(255) DEFAULT NULL,
  `str2` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `transmit_number` int(11) DEFAULT '0',
  `uuid` varchar(255) DEFAULT NULL,
  `zan_number` int(11) DEFAULT '0',
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `latitude` double NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `longitude` double NOT NULL,
  `can_comment` bit(1) DEFAULT b'1',
  `can_transmit` bit(1) DEFAULT b'1' COMMENT '是否能转发',
  PRIMARY KEY (`id`),
  KEY `FK_2787lae1f3u8spdpchjjpagol` (`create_user_id`),
  KEY `FK_3dk4sagctkrb4cm2g0lx8r4sd` (`modify_user_id`),
  CONSTRAINT `FK_2787lae1f3u8spdpchjjpagol` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_3dk4sagctkrb4cm2g0lx8r4sd` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


<!-- 创建记账位置表 -->
CREATE TABLE `t_financial_location` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  `modify_time` datetime DEFAULT NULL,
  `location` varchar(255) NOT NULL COMMENT '位置描述',
  `location_desc` varchar(255) DEFAULT NULL COMMENT '位置描述信息',
  `create_user_id` int(11) NOT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_financial_location_create_user` (`create_user_id`),
  KEY `FK_financial_location_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_financial_location_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_financial_location_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

<!-- 创建文章表 -->
DROP TABLE IF EXISTS `t_blog`;
CREATE TABLE `t_blog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `comment_number` int(11) DEFAULT '0',
  `content` longtext NOT NULL,
  `digest` varchar(255) DEFAULT NULL,
  `froms` varchar(255) DEFAULT NULL,
  `has_img` bit(1) DEFAULT b'0',
  `img_url` longtext,
  `is_index` bit(1) DEFAULT b'0',
  `origin_link` longtext,
  `is_publish_now` bit(1) DEFAULT b'0',
  `is_read` bit(1) DEFAULT b'0',
  `read_number` int(11) DEFAULT '0',
  `share_number` int(11) DEFAULT '0',
  `is_solr_index` bit(1) DEFAULT b'0',
  `source` varchar(255) DEFAULT NULL,
  `str1` varchar(255) DEFAULT NULL,
  `str2` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `transmit_number` int(11) DEFAULT '0',
  `uuid` varchar(255) DEFAULT NULL,
  `zan_number` int(11) DEFAULT '0',
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `can_comment` bit(1) DEFAULT b'1',
  `can_transmit` bit(1) DEFAULT b'1',
  `is_recommend` bit(1) DEFAULT b'0' COMMENT '是否推荐，默认是false',
  `category` varchar(10) DEFAULT NULL COMMENT '分类',
  PRIMARY KEY (`id`),
  KEY `FK_82ogubia30gvvfbwa76x41ogj` (`create_user_id`),
  KEY `FK_26d1pwob9u3l1qujbkmfjqf3o` (`modify_user_id`),
  CONSTRAINT `FK_26d1pwob9u3l1qujbkmfjqf3o` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_82ogubia30gvvfbwa76x41ogj` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

<!-- 创建一级分类表 -->
DROP TABLE IF EXISTS `t_financial_one_category`;
CREATE TABLE `t_financial_one_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  
  `category_value` varchar(8) NOT NULL COMMENT '展示的大类名称',
  `icon_name` varchar(8) COMMENT '显示的图标名称',
  `model` int(1) NOT NULL DEFAULT '1' COMMENT '1表示收入, 2表示支出',
  `budget` float(11) DEFAULT '0.00' COMMENT ' 一级分类的预算',
  `category_order` int(2) NOT NULL DEFAULT '1' COMMENT '排序的位置',
  `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否是默认的分类',
  `is_system` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否是系统默认的(禁止删除，修改)',

  PRIMARY KEY (`id`),
  KEY `FK_financial_one_category_create_user` (`create_user_id`),
  KEY `FK_financial_one_category_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_financial_one_category_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_financial_one_category_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

<!-- 创建二级分类表 -->
DROP TABLE IF EXISTS `t_financial_two_category`;
CREATE TABLE `t_financial_two_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  
  `one_level_id` int(11) DEFAULT NULL COMMENT '一级分类的ID',
  `category_value` varchar(8) NOT NULL COMMENT '展示的大类名称',
  `icon_name` varchar(8) COMMENT '显示的图标名称',
  `budget` float(11) DEFAULT '0.00' COMMENT ' 一级分类的预算',
  `category_order` int(2) NOT NULL DEFAULT '1' COMMENT '排序的位置',
  `is_default` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否是默认的分类',
  `is_system` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否是系统默认的(禁止删除，修改)',

  PRIMARY KEY (`id`),
  KEY `FK_financial_two_category_create_user` (`create_user_id`),
  KEY `FK_financial_two_category_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_financial_two_category_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_financial_two_category_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_chat
-- ----------------------------
DROP TABLE IF EXISTS `t_chat`;
CREATE TABLE `t_chat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `to_user_id` int(11) DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_user_name` varchar(255) DEFAULT NULL COMMENT '创建人的名称',
  `modify_user_id` int(11) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `is_read` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FK_4l4d6iq0ei1lu3omi8ftc63vf` (`create_user_id`),
  KEY `FK_3x07xyf2j7g1eq8d7u1gm5w4b` (`modify_user_id`),
  CONSTRAINT `FK_3x07xyf2j7g1eq8d7u1gm5w4b` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_4l4d6iq0ei1lu3omi8ftc63vf` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user_token
-- ----------------------------
DROP TABLE IF EXISTS `t_user_token`;
CREATE TABLE `t_user_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `token` varchar(255) NOT NULL COMMENT 'token码',
  `overdue` datetime NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`),
  KEY `FK_user_token_create_user` (`create_user_id`),
  KEY `FK_user_token_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_user_token_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_user_token_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
-- 创建索引
ALTER TABLE `t_user_token` ADD INDEX t_user_token_index_name ( `token`, `overdue`)

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `role_desc` varchar(255) COMMENT '角色描述信息',
  `role_name` varchar(255) COMMENT '角色信息',
  `role_code` varchar(255) COMMENT '角色编码，唯一',
  `role_order` int(4) COMMENT '角色排序',
  PRIMARY KEY (`id`),
  KEY `FK_role_create_user` (`create_user_id`),
  KEY `FK_role_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_role_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_role_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE t_role ADD UNIQUE KEY(role_code);

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  KEY `FK_user_role_create_user` (`create_user_id`),
  KEY `FK_user_role_modify_user` (`modify_user_id`),
  KEY `FK_user_role_role_id` (`role_id`),
  KEY `FK_user_role_user_id` (`user_id`),
  CONSTRAINT `FK_user_role_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_user_role_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`),
  CONSTRAINT `FK_user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
-- 创建索引
ALTER TABLE `t_user_role` ADD INDEX t_user_Role_index_name ( `role_id`, `user_id`)

-- ----------------------------
-- Table structure for t_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `permission_desc` varchar(255) COMMENT '权限描述信息',
  `permission_name` varchar(255) COMMENT '权限信息',
  `permission_code` varchar(255) COMMENT '权限编码，唯一',
  `permission_order` int(4) COMMENT '权限排序',
  PRIMARY KEY (`id`),
  KEY `FK_permission_create_user` (`create_user_id`),
  KEY `FK_permission_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_permission_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_permission_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE t_permission ADD UNIQUE KEY(permission_code);

-- ----------------------------
-- Table structure for t_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `permission_id` int(11) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  KEY `FK_role_permission_create_user` (`create_user_id`),
  KEY `FK_role_permission_modify_user` (`modify_user_id`),
  KEY `FK_role_permission_role_id` (`role_id`),
  KEY `FK_role_permission_permission_id` (`permission_id`),
  CONSTRAINT `FK_role_permissione_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_role_permission_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_role_permission_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`),
  CONSTRAINT `FK_role_permission_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `t_permission` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
-- 创建索引
ALTER TABLE `t_role_permission` ADD INDEX t_role_permission_index_name ( `permission_id`, `role_id`)

-- ----------------------------
-- Table structure for t_link_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_link_manage`;
CREATE TABLE `t_link_manage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `link` varchar(255) NOT NULL COMMENT '链接',
  `alias` varchar(255) NOT NULL COMMENT '链接的别名',
  `role` bit(1) DEFAULT b'1' COMMENT '类型：true表示roleId不能为空，false表示permissionId不能为空',
  `order_` int(4) NOT NULL DEFAULT 1 COMMENT '排序',
  `all_` bit(1) DEFAULT b'1' COMMENT '是否是全部都符合，true是全部都符合，false是任意一个符合，默认是true',
  PRIMARY KEY (`id`),
  KEY `FK_link_manage_create_user` (`create_user_id`),
  KEY `FK_link_manage_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_link_manage_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_link_manage_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
-- 创建索引
ALTER TABLE t_link_manage ADD UNIQUE KEY(alias);

-- ----------------------------
-- Table structure for t_link_permission_or_role
-- ----------------------------
DROP TABLE IF EXISTS `t_link_role_or_permission`;
CREATE TABLE `t_link_role_or_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `role_id` int(11) COMMENT '角色ID',
  `permission_id` int(11) COMMENT '权限ID',
  `link_id` int(11) NOT NULL COMMENT '链接ID',
  `role` bit(1) DEFAULT b'1' NOT NULL COMMENT '是否是角色权限，默认是true，需要填充role_id,否则需要填充permission_id',
  PRIMARY KEY (`id`),
  KEY `FK_link_role_or_permission_create_user` (`create_user_id`),
  KEY `FK_link_role_or_permission_modify_user` (`modify_user_id`),
  KEY `FK_link_role_or_permission_role_id` (`role_id`),
  KEY `FK_link_role_or_permission_permission_id` (`permission_id`),
  KEY `FK_link_role_or_permission_link_id` (`link_id`),
  CONSTRAINT `FK_link_role_or_permission_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_link_role_or_permission_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_link_role_or_permission_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`),
  CONSTRAINT `FK_link_role_or_permission_link_id` FOREIGN KEY (`link_id`) REFERENCES `t_link_manage` (`id`),
  CONSTRAINT `FK_link_role_or_permission_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `t_permission` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
-- 创建索引
ALTER TABLE `t_link_role_or_permission` ADD INDEX t_link_role_or_permission_index_name ( `permission_id`, `role_id`, `link_id`)
