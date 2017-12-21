-- 判断用户可以创建的朋友圈数量
drop PROCEDURE if EXISTS `getCircleCreateLimitProcedure` ;
delimiter $$
CREATE PROCEDURE `getCircleCreateLimitProcedure` (IN $createUserId INT, IN $status INT)
BEGIN
	DECLARE score INT DEFAULT 0;
  DECLARE returnValue INT DEFAULT 0;
	DECLARE arealy INT DEFAULT 0;
	select sum(t.score) into  score from t_score t where create_user_id = $createUserId and `status` = $status;
	
	-- 获取该用户最多可以创建的圈子数量
	SELECT number into returnValue  from t_circle_create_limit ccl where score >= left_score and
		(case when right_score = -1 then 
			(select 1=1 )
			else 
			(select score < right_score ) 
			end
		) limit 1;

	-- 获取该用户已经创建的圈子数量
	select count(c.id) into arealy from t_circle c where c.create_user_id = $createUserId and c.`status` = $status;

	-- set returnValue = 0;
	select returnValue - arealy as number;
END $$
delimiter

-- 计算热门圈子的积分
drop PROCEDURE if EXISTS `calculateHostestCirclesProcedure` ;
-- 用户打卡记录 * 0.3 + 用户净新增记录(新增-退出) * 0.6 + 任务完成总数 * 0.4 + 打开的次数 * 0.01 + 帖子热门积分* 0.3 + 帖子数 * 0.5 -被举报总数 * 0.8
delimiter $$
CREATE PROCEDURE `calculateHostestCirclesProcedure` (in $time DATETIME, in $limit INT)
BEGIN
	DECLARE circle_id INT(11); -- 自定义变量1
	DECLARE addNumber INT(11); -- 这个时间段内新增的成员数
	DECLARE logNumber INT(11); -- 这个时间段内访问数
	DECLARE postNumber INT(11); -- 这个时间段内帖子数
	DECLARE postScore INT(11);  -- 这个时间段内的帖子积分(通过帖子热门算法去计算)
	DECLARE subjectVal VARCHAR(255); -- 日记的标题
    DECLARE totalScore FLOAT; -- 最终的总分
	DECLARE DONE BOOLEAN DEFAULT 0; -- 定义结束标识  
	
	-- 获取符合条件的圈子列表(最近时间段内有过用户访问记录的圈子)
	DECLARE cursor_circles CURSOR FOR (
									select v.table_id
									from t_visitor v 
									where v.status=1 and v.table_name='t_circle'
									and not exists (
										select 1 from t_visitor v1 
										where v1.status=1 and v1.table_name='t_circle' and v.table_id = v1.table_id
										and v1.create_time > v.create_time) 
									and v.create_time >= $time
									ORDER BY v.create_time desc, v.id desc limit $limit);

	
	-- 定义游标的结束--当遍历完成时，将DONE设置为1  
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET DONE = 1;  

	OPEN cursor_circles; -- 打开游标
		
			FETCH cursor_circles into circle_id; -- 将游标当前读取行的数据顺序赋予自定义变量12 
			REPEAT 
			set addNumber = 0; -- 这个时间段内新增的成员数
			set logNumber = 0; -- 这个时间段内访问数
			set postNumber = 0; -- 这个时间段内帖子数
			set totalScore = 0; -- 设置总分为0
			-- 获取这个时间段内新增的成员数
			SELECT count(cm.id) into addNumber from t_circle_member cm where cm.circle_id = circle_id and cm.create_time > $time;
		
			-- 获取圈子在这段时间内被访问的次数
			select count(id) into logNumber from t_visitor v where v.table_name='t_circle' and v.table_id = circle_id and v.create_time > $time;

			-- 获取圈子在这段时间内的帖子的积分
			select sum(p.post_score) into postScore from t_circle_post p where p.circle_id = circle_id;

			-- 获取圈子在这段时间内的帖子次数
			select count(id) into postNumber from t_circle_post p where p.circle_id = circle_id and p.create_time > $time;

			set totalScore = ifNull(addNumber, 0) * 0.6 + ifNull(logNumber, 0)* 0.01 + ifNull(postScore, 0) * 0.3 + ifNull(postNumber, 0) * 0.5;
			update t_circle c set c.circle_score = totalScore where c.id = circle_id;
			-- select addNumber * 0.6 + logNumber* 0.1, totalScore, addNumber, logNumber, ifNull(postScore, 0);
		FETCH cursor_circles into circle_id; -- 将游标当前读取行的数据顺序赋予自定义变量12 
		UNTIL DONE
		END REPEAT ;
  CLOSE cursor_circles; -- 关闭游标 

  -- select * from t_circle where create_time > $time order by circle_score desc limit 0, $limit;
END $$
delimiter

-- 获取该圈子的总贡献值和我的贡献值
drop PROCEDURE if EXISTS `getCircleContributeProcedure` ;
delimiter $$
CREATE PROCEDURE `getCircleContributeProcedure` (in $circleId INT, in $userId INT)
BEGIN
	DECLARE myContribute INT(11); -- 自定义变量该圈子内我的贡献值
	DECLARE allContribute INT(11); -- 自定义变量该圈子的贡献值

  select sum(cc.score) into allContribute from t_circle_contribution cc where cc.circle_id = $circleId;
  select cc.total_score into myContribute from t_circle_contribution cc where cc.circle_id = $circleId and cc.create_user_id = $userId order by id desc limit 1;
	select myContribute my, allContribute entire;
END $$
delimiter

-- 计算圈子的热门成员积分
drop PROCEDURE if EXISTS `calculateHostestCircleMembersProcedure` ;
-- 打卡积分 * 0.6 + 任务积分 * 0.3 
delimiter $$
CREATE PROCEDURE `calculateHostestCircleMembersProcedure` (in $circleId INT)
BEGIN
	DECLARE member_id INT(11); -- 自定义变量1
	DECLARE contributionNumber INT(11); -- 这个圈子该成员的贡献值
  DECLARE totalScore FLOAT; -- 最终的总分
	DECLARE DONE BOOLEAN DEFAULT 0; -- 定义结束标识  
	
	-- 获取符合条件的圈子成员列表
	DECLARE cursor_circle_members CURSOR FOR (SELECT cm.member_id  from t_circle_member cm where cm.circle_id = $circleId);
	
	-- 定义游标的结束--当遍历完成时，将DONE设置为1  
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET DONE = 1;  

	OPEN cursor_circle_members; -- 打开游标
		
			FETCH cursor_circle_members into member_id; -- 将游标当前读取行的数据顺序赋予自定义变量
			REPEAT 
			set contributionNumber = 0; -- 这个圈子该成员的贡献值
			set totalScore = 0; -- 设置总分为0
			-- 获取这个时间段内新增的成员数
			SELECT sum(cc.total_score) into contributionNumber from t_circle_contribution cc where cc.circle_id = $circleId and cc.create_user_id = member_id ORDER BY cc.id desc LIMIT 1;
			set totalScore = ifNull(contributionNumber, 0) * 0.6;
			update t_circle_member cm set cm.member_score = totalScore where cm.member_id = member_id and cm.circle_id = $circleId;

		FETCH cursor_circle_members into member_id; -- 将游标当前读取行的数据顺序赋予自定义变量
		UNTIL DONE
		END REPEAT ;
  CLOSE cursor_circle_members; -- 关闭游标
END $$
delimiter

-- 计算热门帖子积分
drop PROCEDURE if EXISTS `calculateHostestPostsProcedure` ;
-- 一定时间内的所有的帖子： 访问数 * 0.01 + 点赞 * 0.3 + 评论数 * 0.6 + 转发数 * 0.7 + 打赏数 * 1.6 
delimiter $$
CREATE PROCEDURE `calculateHostestPostsProcedure` (in $time DATETIME, in $limit INT)
BEGIN
	DECLARE post_id INT(11); -- 自定义变量1
  DECLARE logNumber INT(11); -- 这个时间段内访问数
	DECLARE zanNumber INT(11); -- 这个时间段内点赞总数
	DECLARE commentNumber INT(11); -- 这个时间段内评论数
	DECLARE transmitNumber INT(11); -- 这个时间段内转发数
  DECLARE totalScore FLOAT; -- 最终的总分
	DECLARE DONE BOOLEAN DEFAULT 0; -- 定义结束标识  
	
	-- 获取符合条件的圈子成员列表
	DECLARE cursor_circle_posts CURSOR FOR (
		select v.table_id
		from t_visitor v 
		where v.status= 1 and v.table_name='t_circle_post'
		and not exists (
			select 1 from t_visitor v1 
			where v1.status= 1 and v1.table_name='t_circle_post' and v.table_id = v1.table_id
			and v1.create_time > v.create_time) 
		and v.create_time >= $time
		ORDER BY v.create_time desc, v.id desc limit $limit
		);
	
	-- 定义游标的结束--当遍历完成时，将DONE设置为1  
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET DONE = 1;  

	OPEN cursor_circle_posts; -- 打开游标
		
			FETCH cursor_circle_posts into post_id; -- 将游标当前读取行的数据顺序赋予自定义变量
			REPEAT 
			set logNumber = 0; -- 这个时间段内访问数
			set zanNumber = 0; -- 这个帖子点赞总数为0
			set commentNumber = 0; -- 这个帖子评论总数为0
			set transmitNumber = 0; -- 这个帖子转发总数为0
			set totalScore = 0; -- 设置总分为0
			-- 获取圈子在这段时间内被访问的次数
			select count(id) into logNumber from t_visitor v where v.table_name='t_circle_post' and v.table_id = post_id and v.create_time > $time;

			-- 获取这个时间段内的点赞数
			SELECT count(z.id) into zanNumber from t_zan z where z.table_id = post_id and z.table_name = 't_circle_post' and z.create_time > $time;

			-- 获取这个时间段内的评论数
			SELECT count(c.id) into commentNumber from t_comment c where c.table_id = post_id and c.table_name = 't_circle_post' and c.create_time > $time;

			-- 获取这个时间段内的转发数
			SELECT count(t.id) into transmitNumber from t_transmit t where t.table_id = post_id and t.table_name = 't_circle_post' and t.create_time > $time;
			
			set totalScore = ifNull(logNumber, 0) *0.01 + ifNull(zanNumber, 0) * 0.3 + ifNull(commentNumber, 0) * 0.6 + ifNull(transmitNumber, 0) * 0.7;

			update t_circle_post cp set cp.post_score = totalScore where cp.id = post_id;
			-- select totalScore, post_id;

		FETCH cursor_circle_posts into post_id; -- 将游标当前读取行的数据顺序赋予自定义变量
		UNTIL DONE
		END REPEAT ;
		CLOSE cursor_circle_posts; -- 关闭游标
END $$
delimiter

-- 获取热门圈子存储过程
drop PROCEDURE if EXISTS `getHostestCirclesProcedure`;
delimiter $$
CREATE PROCEDURE `getHostestCirclesProcedure` (in $time DATETIME, in $limit INT, in $status INT)
BEGIN	
  	drop table if exists tmp_circle;
  	-- 创建临时表保存结果集变量
	CREATE TEMPORARY TABLE tmp_circle(
		`tmp_circle_id` int(11) NOT NULL
	);

	-- 查询到结果集保存到临时表中
	insert into tmp_circle (tmp_circle_id) (select v.table_id
	from t_visitor v 
	where v.status= 1 and v.table_name='t_circle'
	and not exists (
		select 1 from t_visitor v1 
		where v1.status= 1 and v1.table_name='t_circle' and v.table_id = v1.table_id
		and v1.create_time > v.create_time) 
	and v.create_time >= $time
	ORDER BY v.create_time desc, v.id desc);
	
	select c.id, c.name name, c.circle_desc, c.circle_path, c.status, date_format(c.create_time,'%Y-%m-%d %H:%i:%s') create_time 
	from t_circle c inner join tmp_circle tc on c.id = tc.tmp_circle_id 
	where `status` = $status order by c.circle_score desc, c.id desc limit $limit;
	-- 删除临时表
	drop table if exists tmp_circle;
END $$
delimiter

-- 获取分类以及全部父类的存储过程
drop PROCEDURE if EXISTS `getParentCategory`;
delimiter $$
CREATE PROCEDURE `getParentCategory` (in $pid INT)  
BEGIN   
DECLARE pid INT default 0; 
DECLARE categorys varchar(1000) default concat($pid);

WHILE $pid > 0  do   
    SELECT c.pid into pid FROM t_category c WHERE c.id = $pid;   
    IF pid > 0 THEN   
        SET categorys = concat(categorys, ',' , pid);   
        SET $pid = pid;   
    ELSE   
        SET $pid = pid;   
    END IF;   
END WHILE;   
select categorys;  
END $$
delimiter