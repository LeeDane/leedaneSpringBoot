--为表增加账号和密码的约束
alter table t_user add constraint t_user_unique UNIQUE(account,password);

--创建唯一索引
create unique index UK_user_account on t_user (account);

--建表后添加约束
alter table t_user add constraint uk_user_account unique (account);