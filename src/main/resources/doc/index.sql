-- 给通知表创建索引，主要是解决读取未读通知SQL全表查询的问题
CREATE INDEX t_notification_to_user on t_notification(to_user_id);