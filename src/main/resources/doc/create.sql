
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
  `content` longtext NOT NULL,
  `froms` varchar(255) DEFAULT NULL,
  `has_img` bit(1) DEFAULT b'0',
  `is_publish_now` bit(1) DEFAULT b'0',
  `is_solr_index` bit(1) DEFAULT b'0',
  `str1` varchar(255) DEFAULT NULL,
  `str2` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `latitude` double NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `longitude` double NOT NULL,
  `can_comment` bit(1) DEFAULT b'1' COMMENT '是否能评论',
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
  `content` longtext NOT NULL,
  `digest` varchar(255) DEFAULT NULL,
  `froms` varchar(255) DEFAULT NULL,
  `has_img` bit(1) DEFAULT b'0',
  `img_url` longtext,
  `is_index` bit(1) DEFAULT b'0',
  `origin_link` longtext,
  `is_publish_now` bit(1) DEFAULT b'0',
  `is_solr_index` bit(1) DEFAULT b'0',
  `source` varchar(255) DEFAULT NULL,
  `str1` varchar(255) DEFAULT NULL,
  `str2` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
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
  `table_name` varchar(255) NOT NULL COMMENT '对应业务表的名称',
  `table_id` int(11) NOT NULL COMMENT '对应业务表的ID',
  PRIMARY KEY (`id`),
  KEY `FK_visitor_create_user` (`create_user_id`),
  KEY `FK_visitor_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_visitor_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_visitor_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
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
  `job_params` text COMMENT '任务的参数，支持表达式',
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
  `announce`varchar(255) DEFAULT NULL COMMENT '公告',
  `check_post` bit(1) DEFAULT b'0' NOT NULL COMMENT '是否需要审核圈子的帖子,默认不需要审核',
  `background_color`varchar(20) NOT NULL DEFAULT '#f5f5f5' COMMENT '背景颜色',
  `limit_number` int(5) NOT NULL DEFAULT 1 COMMENT '限制成员的总数',
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
  `pid` int(11) NOT NULL DEFAULT 0 COMMENT '父圈子的id(转发、引用时候才不为0),关联自身',
  `title` varchar(255) NOT NULL COMMENT '帖子的标题',
  `content` text NOT NULL COMMENT '帖子的内容',
  `digest` varchar(255) NOT NULL COMMENT '帖子的摘要',
  `tag` varchar(50) COMMENT '帖子的标签',
  `has_img` bit(1) DEFAULT b'0' NOT NULL COMMENT '是否包含图片，冗余字段',
  `imgs` text  COMMENT '帖子的图片链接，多个用;隔开',
  `circle_id` int(11) NOT NULL COMMENT '外键关联圈子的id',
  `can_comment` bit(1) DEFAULT b'1' NOT NULL COMMENT '是否能评论，默认能评论',
  `can_transmit` bit(1) DEFAULT b'1' NOT NULL COMMENT '是否能转发，默认能转发',
  `post_score` int(11) NOT　NULL DEFAULT 0 COMMENT '一定时间的评分，目前计划是通过定时任务去计算评分',
  `post_recommend` bit(1) DEFAULT b'0' COMMENT '是否推荐',
  PRIMARY KEY (`id`),
  KEY `FK_circle_post_create_user` (`create_user_id`),
  KEY `FK_circle_post_modify_user` (`modify_user_id`),
  KEY `FK_circle_post_circle_id` (`circle_id`),
  CONSTRAINT `FK_circle_post_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_post_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_circle_post_circle_id` FOREIGN KEY (`circle_id`) REFERENCES `t_circle` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_category
-- ----------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `text` varchar(20) NOT NULL COMMENT '分类名称',
  `pid` int(11) NOT NULL DEFAULT 0 COMMENT '上级的id',
	`is_system` bit(1) DEFAULT b'0' NOT NULL COMMENT '是否是系统级别的',
  PRIMARY KEY (`id`),
  KEY `FK_category_create_user` (`create_user_id`),
  KEY `FK_category_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_category_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_category_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_category add constraint category_create_user_id_pid_text_unique UNIQUE(create_user_id, pid, text);


-- ----------------------------
-- Table structure for t_mall_product
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_product`;
CREATE TABLE `t_mall_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `p_code` varchar(20) COMMENT '商品编号',
  `title` varchar(255) NOT NULL COMMENT '商品的标题',
  `subtitle` varchar(255) NOT NULL COMMENT '商品的副标题',
  `digest` varchar(255) NOT NULL COMMENT '商品的摘要信息',
  `detail` text NOT NULL COMMENT '商品的详情,用于展示的，已经通过mardown4j进行格式化过的。',
  `detail_source` text NOT NULL COMMENT '最原始的用户输入编辑器的商品的详情,还没有通过mardown4j进行格式化过的。',
  `platform` varchar(20) NOT NULL COMMENT '商品来源的平台(通过枚举赋值)',
  `price` float NOT NULL DEFAULT '0.00' COMMENT '商品现价',
  `old_price` float NOT NULL DEFAULT '0.00' COMMENT '商品原价',
  `cash_back_ratio` float NOT NULL DEFAULT '0.00' COMMENT '商品总的返现比率(百分比)',
  `cash_back` float NOT NULL DEFAULT '0.00' COMMENT '商品返现的价钱',
  `shop_id` int(11) NOT NULL COMMENT '商店的ID',
  `link` text COMMENT '商品的链接',
  `is_new` bit(1) DEFAULT b'1' NOT NULL COMMENT '商品是否是最新的价格',
  `category_id` int(11) NOT NULL COMMENT '商品分类ID',
  `main_img_links` varchar(1000) NOT NULL COMMENT '商品的主图片链接，多个用;隔开',
  PRIMARY KEY (`id`),
  KEY `FK_mall_product_create_user` (`create_user_id`),
  KEY `FK_mall_product_modify_user` (`modify_user_id`),
  KEY `FK_mall_product_category` (`category_id`),
  KEY `FK_mall_shop__name` (`shop_id`),
  CONSTRAINT `FK_mall_product_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_product_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_product_category` FOREIGN KEY (`category_id`) REFERENCES `t_category` (`id`),
  CONSTRAINT `FK_mall_shop_name` FOREIGN KEY (`shop_id`) REFERENCES `t_mall_shop` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_mall_big_event
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_big_event`;
CREATE TABLE `t_mall_big_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `product_id` int(11) NOT NULL COMMENT '商品ID',
  `text` varchar(255) NOT NULL COMMENT '事件描述的文本信息',
  `can_comment` bit(1) DEFAULT b'1' COMMENT '是否能评论',
  `can_transmit` bit(1) DEFAULT b'1' COMMENT '是否能转发',
  PRIMARY KEY (`id`),
  KEY `FK_mallp_big_event_create_user` (`create_user_id`),
  KEY `FK_mall_big_event_modify_user` (`modify_user_id`),
  KEY `FK_mall_big_event_product` (`product_id`),
  CONSTRAINT `FK_mall_big_event_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_big_event_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_big_event_product` FOREIGN KEY (`product_id`) REFERENCES `t_mall_product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_mall_statistics
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_statistics`;
CREATE TABLE `t_mall_statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `product_id` int(11) NOT NULL COMMENT '商品ID',
  `statistics_date` date NOT NULL COMMENT '统计的日期',
  `statistics_text` varchar(50) DEFAULT null COMMENT '统计展示的文本',
  `comment_total` int(11) NOT NULL COMMENT '统计的评论总数',
  `wish_total` int(11) NOT NULL COMMENT '统计的心愿单总数',
  `visitor_total` int(11) NOT NULL COMMENT '统计的访问总数',
  `buy_total` int(11) NOT NULL COMMENT '统计的购买总数',
  PRIMARY KEY (`id`),
  KEY `FK_mall_statistics_create_user` (`create_user_id`),
  KEY `FK_mall_statistics_modify_user` (`modify_user_id`),
  KEY `FK_mall_statistics_product` (`product_id`),
  CONSTRAINT `FK_mall_statistics_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_statistics_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_statistics_product` FOREIGN KEY (`product_id`) REFERENCES `t_mall_product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE `t_mall_statistics` ADD INDEX t_mall_statistics_index_date ( `statistics_date`);
ALTER TABLE t_mall_statistics ADD CONSTRAINT mall_statistics_time_product_id_unique UNIQUE(statistics_date, product_id);

-- ----------------------------
-- Table structure for t_mall_wish
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_wish`;
CREATE TABLE `t_mall_wish` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `product_id` int(11) NOT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  KEY `FK_mall_wish_create_user` (`create_user_id`),
  KEY `FK_mall_wish_modify_user` (`modify_user_id`),
  KEY `FK_mall_wish_product` (`product_id`),
  CONSTRAINT `FK_mall_wish_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_wish_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_wish_product` FOREIGN KEY (`product_id`) REFERENCES `t_mall_product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_mall_wish add constraint mall_wish_create_user_product_id_unique UNIQUE(create_user_id, product_id);

-- ----------------------------
-- Table structure for t_mall_shop
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_shop`;
CREATE TABLE `t_mall_shop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `validation` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否验证， 默认还没有验证',
  `is_official` bit(1) NOT NULL DEFAULT b'0' COMMENT '商店的是否是官方的, 默认不是',
  `detail` text NOT NULL COMMENT '商店的描述信息',
  `validation_detail` text NOT NULL COMMENT '商店的审核信息信息',
  `shop_name` varchar(50) NOT NULL COMMENT '商店的名称',
  `link` text NOT NULL COMMENT '商店的链接',
  `img` text NOT NULL COMMENT '商店的图片',
  PRIMARY KEY (`id`),
  KEY `FK_mall_shop_create_user` (`create_user_id`),
  KEY `FK_mall_shop_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_mall_shop_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_shop_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_mall_shop add constraint mall_shop_name_unique UNIQUE(shop_name);

-- ----------------------------
-- Table structure for t_mall_order
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_order`;
CREATE TABLE `t_mall_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `order_code` varchar(50) NOT NULL COMMENT '订单编号',
  `product_code` varchar(50) NOT NULL COMMENT '商品的编号的唯一ID',
  `title` varchar(50) COMMENT '描述的标题，可以为空，用于展示用的',
  `referrer` varchar(50) COMMENT '推荐人，可以为空',
  `platform` varchar(50) COMMENT '平台',
  `order_date` varchar(20) NOT NULL COMMENT '下订单的日期',
  `price` float NOT NULL DEFAULT '0.00' COMMENT '商品现价',
  `cash_back_ratio` float NOT NULL DEFAULT '0.00' COMMENT '商品总的返现比率(百分比)',
  `cash_back` float NOT NULL DEFAULT '0.00' COMMENT '商品返现的价钱',
  PRIMARY KEY (`id`),
  KEY `FK_mall_order_create_user` (`create_user_id`),
  KEY `FK_mall_order_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_mall_order_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_order_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_mall_order add constraint mall_order_code_product_unique UNIQUE(order_code, product_code);

-- ----------------------------
-- Table structure for t_mall_home_carousel
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_home_carousel`;
CREATE TABLE `t_mall_home_carousel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `product_id` int(11) NOT NULL COMMENT '商品ID',
  `carousel_order` int(4) NOT NULL DEFAULT 1 COMMENT '轮播的排序',
  PRIMARY KEY (`id`),
  KEY `FK_mall_home_carousel_create_user` (`create_user_id`),
  KEY `FK_mall_home_carousel_modify_user` (`modify_user_id`),
  KEY `FK_mall_home_carousel_product` (`product_id`),
  CONSTRAINT `FK_mall_home_carousel_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_home_carousel_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_home_carousel_product` FOREIGN KEY (`product_id`) REFERENCES `t_mall_product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_mall_home_item
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_home_item`;
CREATE TABLE `t_mall_home_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `category_id` int(11) NOT NULL COMMENT '分类ID',
  `children` varchar(255) COMMENT '子分类列表，json字符串',
  `category_order` int(4) NOT NULL DEFAULT 1 COMMENT '分类展示的排序',
  `number` int(3) NOT NULL DEFAULT 1 COMMENT '展示的数量',
  PRIMARY KEY (`id`),
  KEY `FK_mall_home_item_create_user` (`create_user_id`),
  KEY `FK_mall_home_item_modify_user` (`modify_user_id`),
  KEY `FK_mall_home_item_category` (`category_id`),
  CONSTRAINT `FK_mall_home_item_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_home_item_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_home_item_category` FOREIGN KEY (`category_id`) REFERENCES `t_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_mall_home_item add constraint mall_home_item_id_unique UNIQUE(category_id);


-- ----------------------------
-- Table structure for t_mall_home_item_product
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_home_item_product`;
CREATE TABLE `t_mall_home_item_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `item_id` int(11) NOT NULL COMMENT '分类ID',
  `product_id` int(11) NOT NULL COMMENT '商品ID',
  `product_order` int(4) NOT NULL DEFAULT 1 COMMENT '商品展示的排序',
  PRIMARY KEY (`id`),
  KEY `FK_mall_home_item_product_create_user` (`create_user_id`),
  KEY `FK_mall_home_item_product_modify_user` (`modify_user_id`),
  KEY `FK_mall_home_item_product_bean` (`product_id`),
  KEY `FK_mall_home_item_product_item` (`item_id`),
  CONSTRAINT `FK_mall_home_item_product_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_home_item_product_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_home_item_product_bean` FOREIGN KEY (`product_id`) REFERENCES `t_mall_product` (`id`),
  CONSTRAINT `FK_mall_home_item_product_item` FOREIGN KEY (`item_id`) REFERENCES `t_mall_home_item` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_mall_home_item_product add constraint mall_home_item_product_category_id_unique UNIQUE(product_id, item_id);


-- ----------------------------
-- Table structure for t_mall_home_shop
-- ----------------------------
DROP TABLE IF EXISTS `t_mall_home_shop`;
CREATE TABLE `t_mall_home_shop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `shop_id` int(11) NOT NULL COMMENT '店铺ID',
  `shop_order` int(4) NOT NULL DEFAULT 1 COMMENT '店铺的排序',
  PRIMARY KEY (`id`),
  KEY `FK_mall_home_shop_create_user` (`create_user_id`),
  KEY `FK_mall_home_shop_modify_user` (`modify_user_id`),
  KEY `FK_mall_home_shop_shop` (`shop_id`),
  CONSTRAINT `FK_mall_home_shop_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_home_shop_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_mall_home_shop_product` FOREIGN KEY (`shop_id`) REFERENCES `t_mall_shop` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_gallery
-- ----------------------------
DROP TABLE IF EXISTS `t_gallery`;
CREATE TABLE `t_gallery` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `gallery_desc` varchar(255) DEFAULT NULL,
  `height` int(11) NOT NULL,
  `length` bigint(20) NOT NULL,
  `path` varchar(255) NOT NULL,
  `width` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_rqkbmgjk89nmwkx0rj7cxfq92` (`create_user_id`),
  KEY `FK_p3k7ar9k260jv7c7sves4sgax` (`modify_user_id`),
  CONSTRAINT `FK_p3k7ar9k260jv7c7sves4sgax` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_rqkbmgjk89nmwkx0rj7cxfq92` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=259 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_baby
-- ----------------------------
DROP TABLE IF EXISTS `t_baby`;
CREATE TABLE `t_baby` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(2) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `born` bit(1) DEFAULT b'0' COMMENT '宝宝的类型(0:备孕中， 1：已出生)',
  `name` varchar(8) NOT NULL COMMENT '宝宝的姓名',
  `nickname` varchar(8) DEFAULT NULL COMMENT '宝宝的昵称',
  `gregorian_birthDay` datetime DEFAULT NULL COMMENT '宝宝的公历生日',
  `lunar_birthDay` datetime DEFAULT NULL COMMENT '宝宝的农历生日',
  `head_pic` varchar(255) DEFAULT NULL COMMENT '宝宝的头像图片地址',
  `sex` varchar(10) NOT NULL DEFAULT '未知' COMMENT '宝宝的性别',
  `personalized_signature` varchar(255) DEFAULT NULL COMMENT '宝宝的个性签名',
  `healthy_state` int(2) NOT NULL DEFAULT 0 COMMENT '宝宝的健康状态',
  `sorting` int(2) NOT NULL DEFAULT 1 COMMENT '宝宝的排列序号',
  `introduction` varchar(255) DEFAULT NULL COMMENT '宝宝的简介',
  `pregnancy_date` datetime DEFAULT NULL COMMENT '宝宝的怀孕时间',
  `pre_production` datetime DEFAULT NULL COMMENT '宝宝的预产期',
  PRIMARY KEY (`id`),
  KEY `FK_baby_create_user` (`create_user_id`),
  KEY `FK_baby_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_baby_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_baby_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_baby add constraint t_baby_unique UNIQUE(create_user_id, name);

-- ----------------------------
-- Table structure for t_baby_life
-- ----------------------------
DROP TABLE IF EXISTS `t_baby_life`;
CREATE TABLE `t_baby_life` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(2) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `baby_id` int(11) DEFAULT NULL COMMENT '宝宝的ID',
  `life_type` int(1) NOT NULL DEFAULT 1 COMMENT '类型(枚举， 1：吃喝、2：睡觉、3：洗刷 4：生病)',
  `occur_date` date NOT NULL COMMENT '发生日期',
  `occur_time` datetime NOT NULL COMMENT '发生时间',
  `reaction` int(1) NOT NULL DEFAULT 1 COMMENT '宝宝反应情况, 1:积极配合, 2:一般配合, 3:不怎么配合, 4:不配合',
  `baby_desc` text COMMENT '宝宝的描述信息',
  `occur_place` varchar(255) COMMENT '发生的位置',
  `eat_type` int(1) NOT NULL DEFAULT 1 COMMENT '喂养类型(1: 亲喂养, 2:瓶喂养，3:碗喂养)',
  `left_capacity` float NOT NULL DEFAULT '0.00' COMMENT '亲喂养左侧容量',
  `right_capacity` float NOT NULL DEFAULT '0.00' COMMENT '亲喂养右侧容量',
  `capacity` float NOT NULL DEFAULT '0.00' COMMENT '总的容量',
  `temperature` float NOT NULL DEFAULT '0.00' COMMENT '总的温度',
  `brand` varchar(50) COMMENT '品牌',
  `wake_up_time` datetime DEFAULT NULL COMMENT '起床时间',
  `wash_end_time` datetime DEFAULT NULL COMMENT '洗刷结束时间',
  PRIMARY KEY (`id`),
  KEY `FK_baby_life_create_user` (`create_user_id`),
  KEY `FK_baby_life_modify_user` (`modify_user_id`),
  KEY `FK_baby_baby_id` (`baby_id`),
  CONSTRAINT `FK_baby_life_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_baby_life_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_baby_baby_id` FOREIGN KEY (`baby_id`) REFERENCES `t_baby` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE t_baby_life ADD INDEX index_baby_life_occur_date(occur_date);


-- ----------------------------
-- Table structure for t_stock
-- ----------------------------
DROP TABLE IF EXISTS `t_stock`;
CREATE TABLE `t_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `name` varchar(10) NOT NULL COMMENT '股票的名称',
  `code` varchar(10) NOT NULL COMMENT '股票编码',
  PRIMARY KEY (`id`),
  KEY `FK_stock_create_user` (`create_user_id`),
  KEY `FK_stock_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_stock_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_stock_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_stock_buy
-- ----------------------------
DROP TABLE IF EXISTS `t_stock_buy`;
CREATE TABLE `t_stock_buy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `stock_id` int(11) NOT NULL COMMENT '股票的id',
  `price` float NOT NULL DEFAULT '0.00' COMMENT '买入价格',
  `number` int(8) NOT NULL DEFAULT 1 COMMENT '买入数量',
  `sell_out` bit(1) DEFAULT b'0' COMMENT '是否卖光了(0:没有， 1：已卖光)',
  PRIMARY KEY (`id`),
  KEY `FK_stock_buy_create_user` (`create_user_id`),
  KEY `FK_stock_buy_modify_user` (`modify_user_id`),
  KEY `FK_stock_buy_id` (`stock_id`),
  CONSTRAINT `FK_stock_buy_create_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_stock_buy_modify_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_stock_buy_id` FOREIGN KEY (`stock_id`) REFERENCES `t_stock` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_stock_sell
-- ----------------------------
DROP TABLE IF EXISTS `t_stock_sell`;
CREATE TABLE `t_stock_sell` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `stock_buy_id` int(11) NOT NULL COMMENT '股票买入的id',
  `price` float NOT NULL DEFAULT '0.00' COMMENT '卖出价格',
  `number` int(8) NOT NULL DEFAULT 1 COMMENT '卖出数量',
  `residue_number` int(8) NOT NULL DEFAULT 1 COMMENT '卖出剩余数量(为0表示已经卖完)',
  PRIMARY KEY (`id`),
  KEY `FK_stock_sell_create_user` (`create_user_id`),
  KEY `FK_stock_sell_modify_user` (`modify_user_id`),
  KEY `FK_stock_sell_id` (`stock_buy_id`),
  CONSTRAINT `FK_stock_sell_create_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_stock_sell_modify_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_stock_sell_id` FOREIGN KEY (`stock_buy_id`) REFERENCES `t_stock_buy` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_clock
-- ----------------------------
DROP TABLE IF EXISTS `t_clock`;
CREATE TABLE `t_clock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `title` varchar(30) NOT NULL COMMENT '任务的标题，必须字段',
  `clock_describe` varchar(255) NOT NULL COMMENT '任务的描述，必须字段(为空将以标题代替)',
  `icon` varchar(255) NOT NULL DEFAULT 'http://pic.onlyloveu.top/default_no_pic20170613.jpg' COMMENT '任务的图标，必须字段(为空将以显示默认的无图照片)',
  `share` bit(1) DEFAULT b'0' COMMENT '任务的类型(false:私有默认是false， true：共享)',
  `apply_end_date` date DEFAULT NULL COMMENT '当是共享模式的情况下，最迟的报名日期，可以为空，为空表示不限制报名日期',
  `take_part_number` int(4) DEFAULT 0 COMMENT '当是共享模式的情况下，最大参与人数，必须大于1小于当前用户等级的最大数',
  `reward_score` int(4) DEFAULT 0 COMMENT '当是共享模式的情况下，参与的用户在结束之后，不成功参与的用户将被扣除的积分',
  `start_date` date NOT NULL COMMENT '任务开始时间（不能为空）',
  `end_date` date DEFAULT NULL COMMENT '任务结束时间（可以为空, 为空表示无穷）',
  `clock_in_type` int(1) DEFAULT 1 COMMENT '任务的打卡类型(1：普通打卡， 2：图片打卡 3：位置打卡，4：计步打卡)',
  `choose_img` bit(1) DEFAULT b'0' COMMENT '任务的打卡是否可以选择图片(false:不可以默认是false， true：可以)',
  `clock_repeat` varchar(20) NOT NULL COMMENT '重复规则，如1,2,3,4,5,6,7，表示每周周日是1',
  `clock_start_time` time NOT NULL COMMENT '任务打卡开始时间（可以为空, 为空表示不限定打卡开始时间）',
  `clock_end_time` time NOT NULL COMMENT '任务打卡结束时间（可以为空, 为空表示不限定打卡时间）',
  `category_id` int(11) DEFAULT 0 COMMENT '分类的ID，目前主要用于系统的任务',
  `parent_id` int(11) DEFAULT 0 COMMENT '父任务的ID，一般只作用于用户行为的任务，不作用系统任务',
  `must_step` int(6) DEFAULT 0 COMMENT '任务的计步打卡，当clock_in_type为计步打卡的时候，用户计步打卡最低的要求',
  `share_id` varchar(40) DEFAULT NULL COMMENT '共享任务的ID，只要是共享的任务，系统自动分配共享ID',
  `total_day` int(3) DEFAULT -1 COMMENT '需要打卡的总天数,如果无穷，默认是-1',
  `auto_add` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否成员自动加入，默认是false，表示共享的任务，其他人员可以不通过创建者自动加入',
  `auto_out` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否允许成员自动退出，默认是true，表示共享的任务，其他人员可以自动退出',
  `see_each_other` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否允许成员在动态中看大家的信息，默认是true，表示共享的任务，其他人员可以看到彼此的信息',
  `must_check_clock_in` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否成员打卡需要审核，默认是false，表示共享的任务，其他成员打卡不需要审核',
  PRIMARY KEY (`id`),
  KEY `FK_clock_create_user` (`create_user_id`),
  KEY `FK_clock_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_clock_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_clock_member
-- ----------------------------
DROP TABLE IF EXISTS `t_clock_member`;
CREATE TABLE `t_clock_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `clock_id` int(11) NOT NULL COMMENT '任务提醒的id',
  `member_id` int(11) NOT NULL COMMENT '成员的id',
  `remind` varchar(30) DEFAULT NULL COMMENT '任务的提醒时间',
  `notification` bit(1) DEFAULT b'1' NOT NULL COMMENT '任务的是否接受其他成员打卡的消息,默认是开启的',
  PRIMARY KEY (`id`),
  KEY `FK_clock_member_create_user` (`create_user_id`),
  KEY `FK_clock_member_modify_user` (`modify_user_id`),
  KEY `FK_clock_member_clock` (`clock_id`),
  KEY `FK_clock_member_id` (`member_id`),
  CONSTRAINT `FK_clock_member_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_member_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_member_clock` FOREIGN KEY (`clock_id`) REFERENCES `t_clock` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_clock_member_id` FOREIGN KEY (`member_id`) REFERENCES `t_user` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_clock_member add constraint index_t_clock_member_id UNIQUE(clock_id, member_id);
-- ----------------------------
-- Table structure for t_clock_in
-- ----------------------------
DROP TABLE IF EXISTS `t_clock_in`;
CREATE TABLE `t_clock_in` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `froms` varchar(30) COMMENT '任务的方式',
  `clock_id` int(11) NOT NULL COMMENT '任务的ID，必须字段',
  `clock_date` date NOT NULL COMMENT '打卡的日期',
  `img` varchar(255) DEFAULT NULL COMMENT '打卡的图片',
  `location` varchar(255) DEFAULT NULL COMMENT '任务的打卡位置，当任务type为3的时候是必须字段',
  `step` int(6) DEFAULT 0 COMMENT '任务的计步数，当任务type为4的时候是必须字段',
  PRIMARY KEY (`id`),
  KEY `FK_clock_in_create_user` (`create_user_id`),
  KEY `FK_clock_in_modify_user` (`modify_user_id`),
  KEY `FK_clock_in_clock` (`clock_id`),
  CONSTRAINT `FK_clock_in_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_in_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_in_clock` FOREIGN KEY (`clock_id`) REFERENCES `t_clock` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_clock_in add constraint index_t_clock_in_date UNIQUE(create_user_id, clock_date, clock_id);

-- ----------------------------
-- Table structure for t_clock_system
-- ----------------------------
DROP TABLE IF EXISTS `t_clock_system`;
CREATE TABLE `t_clock_system` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `title` varchar(30) NOT NULL COMMENT '任务的标题，必须字段',
  `system_category` varchar(10) NOT NULL COMMENT '任务的分类字段',
  `system_describe` varchar(255) NOT NULL COMMENT '任务的描述，必须字段(为空将以标题代替)',
  `icon` varchar(255) NOT NULL DEFAULT 'http://pic.onlyloveu.top/default_no_pic20170613.jpg' COMMENT '任务的图标，必须字段(为空将以显示默认的无图照片)',
  `share` bit(1) DEFAULT b'0' COMMENT '任务的类型(false:私有默认是false， true：共享)',
  `start_date` date NOT NULL COMMENT '任务开始时间（不能为空）',
  `end_date` date DEFAULT NULL COMMENT '任务结束时间（可以为空, 为空表示无穷）',
  `must_img` bit(1) DEFAULT b'0' COMMENT '任务的打卡是否需要图片(false:不需要默认是false， true：必须)',
  `clock_repeat` varchar(20) NOT NULL COMMENT '重复规则，如1,2,3,4,5,6,7，表示每周周日是1',
  `clock_start_time` time NOT NULL COMMENT '任务打卡开始时间（可以为空, 为空表示不限定打卡开始时间）',
  `clock_end_time` time NOT NULL COMMENT '任务打卡结束时间（可以为空, 为空表示不限定打卡时间）',
  PRIMARY KEY (`id`),
  KEY `FK_clock_system_create_user` (`create_user_id`),
  KEY `FK_clock_system_modify_user` (`modify_user_id`),
  CONSTRAINT `FK_clock_system_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_system_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_clock_deal
-- ----------------------------
DROP TABLE IF EXISTS `t_clock_deal`;
CREATE TABLE `t_clock_deal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `clock_id` int(11) NOT NULL COMMENT '任务提醒的id',
  `member_id` int(11) NOT NULL COMMENT '成员的id',
  `new_status` int(3) DEFAULT -10 COMMENT '处理后的最新状态',
  PRIMARY KEY (`id`),
  KEY `FK_clock_deal_create_user` (`create_user_id`),
  KEY `FK_clock_deal_modify_user` (`modify_user_id`),
  KEY `FK_clock_deal_clock` (`clock_id`),
  KEY `FK_clock_deal_id` (`member_id`),
  CONSTRAINT `FK_clock_deal_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_deal_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_deal_clock` FOREIGN KEY (`clock_id`) REFERENCES `t_clock` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_clock_deal_id` FOREIGN KEY (`member_id`) REFERENCES `t_user` (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_clock_deal add constraint index_t_clock_deal_id UNIQUE(create_user_id, clock_id, member_id);

-- ----------------------------
-- Table structure for t_clock_score
-- ----------------------------
DROP TABLE IF EXISTS `t_clock_score`;
CREATE TABLE `t_clock_score` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `clock_id` int(11) NOT NULL COMMENT '任务提醒的id',
  `score` int(11) NOT NULL DEFAULT 0 COMMENT '当前的获得的积分(非总积分)',
  `total_score` int(11) NOT NULL DEFAULT 0 COMMENT '历史的总积分(冗余字段)',
  `business_type` int(2) NOT NULL DEFAULT 0 COMMENT '相关联的任务业务类型',
  `score_desc` varchar(255) DEFAULT NULL COMMENT '描述 信息',
  `score_date` date NOT NULL COMMENT '记录积分的日期',
  PRIMARY KEY (`id`),
  KEY `FK_clock_score_create_user` (`create_user_id`),
  KEY `FK_clock_score_modify_user` (`modify_user_id`),
  KEY `FK_clock_score_clock` (`clock_id`),
  CONSTRAINT `FK_clock_score_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_score_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_score_clock` FOREIGN KEY (`clock_id`) REFERENCES `t_clock` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
alter table t_clock_score add constraint index_t_clock_score_id UNIQUE(create_user_id, clock_id, business_type, create_time);

-- ----------------------------
-- Table structure for t_clock_dynamic
-- ----------------------------
DROP TABLE IF EXISTS `t_clock_dynamic`;
CREATE TABLE `t_clock_dynamic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `clock_id` int(11) NOT NULL COMMENT '任务提醒的id',
  `message_type` int(3) NOT NULL DEFAULT -1 COMMENT '标记的消息类型，如任务打卡、其他等',
  `dynamic_desc` varchar(255) NOT NULL COMMENT '任务动态的描述信息',
  `publicity` bit(1) DEFAULT b'0' COMMENT '标记该动态的等级,是否是公开的，默认是false',
  PRIMARY KEY (`id`),
  KEY `FK_clock_dynamic_create_user` (`create_user_id`),
  KEY `FK_clock_dynamic_modify_user` (`modify_user_id`),
  KEY `FK_clock_dynamic_clock` (`clock_id`),
  CONSTRAINT `FK_clock_dynamic_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_dynamic_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_dynamic_clock` FOREIGN KEY (`clock_id`) REFERENCES `t_clock` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_clock_in_image
-- ----------------------------
DROP TABLE IF EXISTS `t_clock_in_resources`;
CREATE TABLE `t_clock_in_resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `clock_in_id` int(11) NOT NULL COMMENT '任务打卡的id',
  `resource` varchar(255) NOT NULL COMMENT '打卡的资源信息',
  `resource_type` varchar(255) NOT NULL COMMENT '打卡的资源类型',
  `main` bit(1) DEFAULT b'0' NOT NULL COMMENT '是否是主图(如果任务是图片打卡任务，main标记的图片是无法删除的，一个任务只能有一张图片)',
  PRIMARY KEY (`id`),
  KEY `FK_clock_in_resources_create_user` (`create_user_id`),
  KEY `FK_clock_in_resources_modify_user` (`modify_user_id`),
  KEY `FK_clock_in_resources_clock_in` (`clock_in_id`),
  CONSTRAINT `FK_clock_in_resources_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_in_resources_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_clock_in_resources_clock_in` FOREIGN KEY (`clock_in_id`) REFERENCES `t_clock_in` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_event
-- ----------------------------
DROP TABLE IF EXISTS `t_event`;
CREATE TABLE `t_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'markdown语法的html数据',
  `source` longtext NOT NULL COMMENT 'markdown语法的源数据',
  `str1` varchar(255) DEFAULT NULL,
  `str2` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
   KEY `FK_event_create_user` (`create_user_id`),
   KEY `FK_event_modify_user` (`modify_user_id`),
   CONSTRAINT `FK_event_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`),
   CONSTRAINT `FK_event_modify_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_read
-- ----------------------------
DROP TABLE IF EXISTS `t_read`;
CREATE TABLE `t_read` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `froms` varchar(255) DEFAULT NULL,
  `table_id` int(11) NOT NULL,
  `table_name` varchar(15) NOT NULL,
  `create_user_id` int(11) DEFAULT NULL,
  `modify_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_read_create_user` (`create_user_id`),
  KEY `FK_modify_create_user` (`modify_user_id`),
  CONSTRAINT `FK_modify_create_user` FOREIGN KEY (`modify_user_id`) REFERENCES `t_user` (`id`),
  CONSTRAINT `FK_read_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
