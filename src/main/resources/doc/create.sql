
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

-- ----------------------------
-- Table structure for t_visitor
-- ----------------------------
DROP TABLE IF EXISTS `t_visitor`;
CREATE TABLE `t_visitor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `froms` varchar(255) COMMENT '来源',
  `user_id` int(11) NOT NULL COMMENT '访问的用户ID',
  `table_name` varchar(255) NOT NULL COMMENT '对应业务表的名称',
  `table_id` int(11) NOT NULL COMMENT '对应业务表的ID',
  PRIMARY KEY (`id`),
  KEY `FK_visitor_create_user` (`create_user_id`),
  KEY `FK_visitor_modify_user` (`modify_user_id`),
  KEY `FK_visitor_user_id` (`user_id`),
  CONSTRAINT `FK_visitor_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_visitor_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_visitor_user_id` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_material
-- ----------------------------
DROP TABLE IF EXISTS `t_material`;
CREATE TABLE `t_material` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `path` varchar(255) COMMENT '本地路径',
  `qiniu_path` varchar(255) COMMENT '七牛路径',
  `width` int(11) NOT NULL DEFAULT '0' COMMENT '宽度',
  `height` int(11) NOT NULL DEFAULT '0' COMMENT '高度',
  `length` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件大小',
  `material_desc` varchar(255) COMMENT '描述信息',
  `material_type` varchar(5) COMMENT '类型， 文件/图像',
  PRIMARY KEY (`id`),
  KEY `FK_material_create_user` (`create_user_id`),
  KEY `FK_material_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_material_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_material_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_job_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_job_manage`;
CREATE TABLE `t_job_manage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `job_name` varchar(25) NOT NULL COMMENT '任务名称',
  `job_group` varchar(25) NOT NULL COMMENT '任务分组',
  `expression` varchar(255) NOT NULL COMMENT '任务运行时间表达式 ',
  `class_name` varchar(25) NOT NULL COMMENT '任务实体类名称 ',
  `job_desc` varchar(255) COMMENT '任务描述 ',
  `job_order` int(5) NOT NULL DEFAULT '0' COMMENT '排序顺序，默认是0' ,
  `job_params` varchar(255) COMMENT '任务的参数，支持表达式',
  PRIMARY KEY (`id`),
  KEY `FK_job_manage_create_user` (`create_user_id`),
  KEY `FK_job_manage_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_job_manage_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_job_manage_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_job_manage add constraint job_manage_unique UNIQUE(job_name, job_group);


-- ----------------------------
-- Table structure for t_circle
-- ----------------------------
DROP TABLE IF EXISTS `t_circle`;
CREATE TABLE `t_circle` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `name` varchar(25) NOT NULL COMMENT '圈子名称',
  `circle_desc` varchar(25) COMMENT '圈子描述信息',
  `circle_path` varchar(255) COMMENT '圈子的图像地址',
  `circle_score` int(11) NOT　NULL DEFAULT 0 COMMENT '一定时间的评分，目前计划是通过定时任务去计算评分',
  `circle_recommend` bit(1) DEFAULT b'0' COMMENT '是否推荐',
  PRIMARY KEY (`id`),
  KEY `FK_circle_main_create_user` (`create_user_id`),
  KEY `FK_circle_main_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_circle_main_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_main_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_circle add constraint circle_main_unique UNIQUE(name);

-- ----------------------------
-- Table structure for t_circle_member
-- ----------------------------
DROP TABLE IF EXISTS `t_circle_member`;
CREATE TABLE `t_circle_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `member_id` int(11) DEFAULT NULL COMMENT '成员id，一般情况下跟create_user_id一致',
  `circle_id` int(11) DEFAULT NULL COMMENT '圈子id',
  `role_type` int(2) NOT NULL DEFAULT 0 COMMENT '权限的类型，为1是创建者，2是管理者，0是普通',
  `member_score` int(11) NOT　NULL DEFAULT 0 COMMENT '一定时间的评分，目前计划是通过定时任务去计算评分',
  `member_recommend` bit(1) DEFAULT b'0' COMMENT '是否推荐',
  PRIMARY KEY (`id`),
  KEY `FK_circle_member_create_user` (`create_user_id`),
  KEY `FK_circle_member_modify_user` (`modify_user_id`),
  KEY `FK_circle_member_member_id` (`member_id`),
  KEY `FK_circle_member_circle_id` (`circle_id`),
  CONSTRAINT `FK_circle_member_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_member_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_member_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_member_circle_id` FOREIGN KEY (`circle_id`) REFERENCES `t_circle` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_circle_member add constraint circle_member_unique UNIQUE(member_id, circle_id);

-- ----------------------------
-- Table structure for t_circle_create_limit
-- ----------------------------
DROP TABLE IF EXISTS `t_circle_create_limit`;
CREATE TABLE `t_circle_create_limit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `number` int(11) DEFAULT 0 COMMENT '能创建的最多数量',
  `left_score` int(11) DEFAULT 0 COMMENT '左边范围，大于等于',
  `right_score` int(11) DEFAULT 0 COMMENT '右边范围，小于',
  `limit_desc` varchar(255) COMMENT '描述信息',
  PRIMARY KEY (`id`),
  KEY `FK_circle_create_limit_create_user` (`create_user_id`),
  KEY `FK_circle_create_limit_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_circle_create_limit_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_create_limit_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_circle_setting
-- ----------------------------
DROP TABLE IF EXISTS `t_circle_setting`;
CREATE TABLE `t_circle_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `circle_id` int(11) DEFAULT NULL COMMENT '圈子id',
  `add_member` bit(1) DEFAULT b'1' NOT NULL COMMENT '是否可以添加成员',
  `welcome_member`varchar(255) DEFAULT NULL COMMENT '欢迎成员的信息(支持el表达式)，为空将不发送',
  `question_title`varchar(255) DEFAULT NULL COMMENT '问题的标题',
  `question_answer`varchar(255) DEFAULT NULL COMMENT '问题的答案',
  PRIMARY KEY (`id`),
  KEY `FK_circle_setting_create_user` (`create_user_id`),
  KEY `FK_circle_setting_modify_user` (`modify_user_id`),
  KEY `FK_circle_setting_circle_id` (`circle_id`),
  CONSTRAINT `FK_circle_setting_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_setting_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_setting_circle_id` FOREIGN KEY (`circle_id`) REFERENCES `t_circle` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_circle_setting add constraint circle_setting_id_unique UNIQUE(circle_id);

-- ----------------------------
-- Table structure for t_clock_in
-- ----------------------------
DROP TABLE IF EXISTS `t_clock_in`;
CREATE TABLE `t_clock_in` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `circle_id` int(11) NOT NULL COMMENT '外键关联圈子的id',
  `continuous` int(11) NOT NULL COMMENT '连续打卡的天数',
  `pid` int(11) DEFAULT NULL COMMENT '上次记录的id',
  `create_date` varchar(10) DEFAULT NULL COMMENT '创建日期(用于和create_user_id做联合约束记录的唯一性)',
  `froms` varchar(25) COMMENT '打卡方式',
  PRIMARY KEY (`id`),
  UNIQUE KEY `clock_in_unique` (`create_user_id`,`create_date`),
  KEY `FK_clock_in_create_user` (`create_user_id`),
  KEY `FK_clock_in_modify_user` (`modify_user_id`),
  KEY `FK_clock_in_circle_id` (`circle_id`),
  CONSTRAINT `FK_clock_in_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_in_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_in_circle_id` FOREIGN KEY (`circle_id`) REFERENCES `t_circle` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_circle_contribution
-- ----------------------------
DROP TABLE IF EXISTS `t_circle_contribution`;
CREATE TABLE `t_circle_contribution` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `score_desc` varchar(255) DEFAULT NULL,
  `score` int(11) NOT NULL COMMENT '当前的获得的贡献值(非总贡献值)',
  `circle_id` int(11) NOT NULL COMMENT '外键关联圈子的id',
  `total_score` int(11) DEFAULT NULL COMMENT '历史的总贡献值(冗余字段)',
  PRIMARY KEY (`id`),
  KEY `FK_circle_contribution_create_user` (`create_user_id`),
  KEY `FK_circle_contribution_modify_user` (`modify_user_id`),
  KEY `FK_circle_contribution_circle_id` (`circle_id`),
  CONSTRAINT `FK_circle_contribution_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_contribution_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_contribution_circle_id` FOREIGN KEY (`circle_id`) REFERENCES `t_circle` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_circle_post
-- ----------------------------
DROP TABLE IF EXISTS `t_circle_post`;
CREATE TABLE `t_circle_post` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `title` varchar(255) NOT NULL COMMENT '帖子的标题',
  `content` varchar(255) NOT NULL COMMENT '帖子的内容',
  `tag` varchar(50) COMMENT '帖子的标签',
  `has_img` bit(1) DEFAULT b'0' NOT NULL COMMENT '是否包含图片，冗余字段',
  `imgs` text  COMMENT '帖子的图片链接，多个用;隔开',
  `circle_id` int(11) NOT NULL COMMENT '外键关联圈子的id',
  PRIMARY KEY (`id`),
  KEY `FK_circle_post_create_user` (`create_user_id`),
  KEY `FK_circle_post_modify_user` (`modify_user_id`),
  KEY `FK_circle_post_circle_id` (`circle_id`),
  CONSTRAINT `FK_circle_post_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_post_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_post_circle_id` FOREIGN KEY (`circle_id`) REFERENCES `t_circle` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;