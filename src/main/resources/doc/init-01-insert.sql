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

/*圈子限制数量初始化*/
INSERT INTO `t_circle_create_limit` VALUES ('1', '1', '2017-06-11 17:09:06', null, '1', null, '5', '0', '500', '小于500分，只能创建5个圈子');
INSERT INTO `t_circle_create_limit` VALUES ('2', '1', '2017-06-11 17:09:53', null, '1', null, '10', '500', '1000', '大于500积分小于1000积分，只能创建10个圈子');
INSERT INTO `t_circle_create_limit` VALUES ('3', '1', '2017-06-11 17:10:46', null, '1', null, '20', '1000', '2000', '大于1000积分小于2000积分，只能创建20个圈子');
INSERT INTO `t_circle_create_limit` VALUES ('4', '1', '2017-06-11 17:11:25', null, '1', null, '40', '2000', '-1', '大于2000，能创建40个圈子');

/*发布新版本app的执行sql语句*/
INSERT INTO t_file_path (STATUS, CREATE_TIME, MODIFY_TIME, PIC_ORDER, PATH, QINIU_PATH, PIC_SIZE, TABLE_NAME, TABLE_UUID, IS_UPLOAD_QINIU, CREATE_USER_ID, MODIFY_USER_ID, HEIGHT, WIDTH, LENGHT, FILE_DESC, FILE_VERSION) 
VALUES(1, CURRENT_DATE(), CURRENT_DATE(), 0, '1_leedane8342cd456ew4t07-1ae03wee95a6_20170803113016_app-release.apk', 'http://7xnv8i.com1.z0.glb.clouddn.com/1_leedane8342cd456ew4t07-1ae03wee95a6_20170803113016_app-release.apk'
, 'source', 'app_version', 'leedaneba1cc32e-da82-4aa4-b4f0-d43fffd868994', 1, 1, 1, 0, 0, 23520364, '1.支持账号、密码、手机等重要信息的加密传输 2、记账新增今日栏 3、解决记账的地址bug 4、解决记账列表偶尔没有数据和数据没有缓冲的问题', '1.0.5');
