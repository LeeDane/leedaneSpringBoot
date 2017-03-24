/*选项T_OPTION初始化语句*/
insert into t_option set STATUS = 1, option_key ='page-size', option_value=5, version = 1.0, create_user_id = 1, create_time = now(), option_desc = '默认的分页中每页的大小';
insert into t_option set STATUS = 1, option_key ='login-error-limit-number', option_value=5, version = 1.0, create_user_id = 1, create_time = now(), option_desc ='登录错误的限制次数';
insert into t_option set STATUS = 1, option_key ='maintenance-period', option_value='23PM-8AM', version = 1.0, create_user_id = 1, create_time = now(), option_desc ='系统维护时间段(23时到8时)';
insert into t_option set STATUS = 1, option_key ='admin-id', option_value= 1 , version = 1.0, create_user_id = 1, create_time = now(), option_desc ='默认得管理员的ID';
insert into t_option set STATUS = 1, option_key ='first-sign-in', option_value= 300 , version = 1.0, create_user_id = 1, create_time = now(), option_desc ='第一次签到获取的积分';
insert into t_option set STATUS = 1, option_key ='leedane-robot-id', option_value= 7 , version = 1.0, create_user_id = 1, create_time = now(), option_desc ='leedane机器人的ID';


/*好友关系表初始化语句*/
insert into t_friend(id,from_user_id,to_user_id,create_user_id,create_time,status,from_user_remark,to_user_remark) VALUES(1,1,2,1,now(),1,'Lee友','leedane友');
/*查找该用户的所有好友id,备注名称*/
select to_user_id id, (case when to_user_remark = '' || to_user_remark = null then (select u.account from t_user u where  u.id = to_user_id and u.status = 1) else to_user_remark end ) remark from t_friend where from_user_id =1 and status = 1
UNION 
select from_user_id id, (case when from_user_remark = '' || from_user_remark = null then (select u.account from t_user u where  u.id = from_user_id and u.status = 1) else from_user_remark end ) remark from t_friend where to_user_id = 1 and status = 1;

/*查找该用户的所有好友的全部信息*/
select * from t_user 
where id in (
		select to_user_id id from t_friend where from_user_id =1 and status = 1
		UNION 
		select from_user_id id from t_friend where to_user_id = 1 and status = 1
);
