/*为博客增加是否推荐的列*/
alter table t_blog add COLUMN recommend BOOLEAN DEFAULT false;

/*为签到表添加唯一性约束*/
alter table t_sign_in add constraint sign_in_unique UNIQUE(create_user_id, create_date);

/*为聊天背景与用户关系表添加唯一性约束*/
alter table t_chat_bg_user add constraint chat_bg_user_unique UNIQUE(create_user_id, chat_bg_table_id);

/*为用户表添加用户账号唯一性*/
alter table t_user add constraint user_account_unique UNIQUE(account);

/*为用户表添加用户手机号码唯一性*/
alter table t_user add constraint user_phone_unique UNIQUE(mobile_phone);

/*为文章表添加source和origin_link唯一约束*/
ALTER TABLE t_blog ADD UNIQUE blog_source_origin_link(source, origin_link);

/*为访客表的tableName, tableId以及创建时间添加唯一性约束*/
alter table t_visitor add constraint visitor_all_unique UNIQUE(table_name, table_id, create_time);

/*修改博客表，添加是否加入es的标记字段, 0:false表示未加入*/
alter table t_blog add COLUMN `es_index` bit(1) DEFAULT b'0';

/*修改用户表，添加是否加入es的标记字段, 0:false表示未加入*/
alter table t_user add COLUMN `es_index` bit(1) DEFAULT b'0';

/*修改心情表，添加是否加入es的标记字段, 0:false表示未加入*/
alter table t_mood add COLUMN `es_index` bit(1) DEFAULT b'0';

/*修改日志表，添加是否加入es的标记字段, 0:false表示未加入*/
alter table t_operate_log add COLUMN `es_index` bit(1) DEFAULT b'0';

/*修改日志表，添加位置信息字段*/
alter table t_operate_log add COLUMN `location` varchar(100) DEFAULT NULL;