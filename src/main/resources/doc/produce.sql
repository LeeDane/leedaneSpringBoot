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
drop PROCEDURE if EXISTS `getHostestCirclesProcedure` ;
-- 用户打卡记录 * 0.3 + 用户净新增记录(新增-退出) * 0.6 + 任务完成总数 * 0.4 + 打开的次数 * 0.01 + 帖子热门积分* 0.02 -被举报总数 * 0.8
delimiter $$
CREATE PROCEDURE `getHostestCirclesProcedure` (in $time DATETIME, in $limit INT)
BEGIN
	DECLARE circle_id INT(11); -- 自定义变量1
	DECLARE addNumber INT(11); -- 这个时间段内新增的成员数
	DECLARE logNumber INT(11); -- 这个时间段内访问数
	DECLARE postScore INT(11);  -- 这个时间段内的帖子积分(通过帖子热门算法去计算)
	DECLARE subjectVal VARCHAR(255); -- 日记的标题
    DECLARE totalScore FLOAT; -- 最终的总分
	DECLARE DONE BOOLEAN DEFAULT 0; #定义结束标识  
	
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
			set totalScore = 0; -- 设置总分为0
			-- 获取这个时间段内新增的成员数
			SELECT count(cm.id) into addNumber from t_circle_member cm where cm.circle_id = circle_id and cm.create_time > $time;
		
			-- 获取圈子在这段时间内被访问的次数
			select count(id) into logNumber from t_visitor v where v.table_name='t_circle' and v.table_id = circle_id and v.create_time > $time;

			-- 获取圈子在这段时间内的帖子的积分
			select sum(p.post_score) into postScore from t_circle_post p where p.circle_id = circle_id;

			set totalScore = ifNull(addNumber, 0) * 0.6 + ifNull(logNumber, 0)* 0.01 + ifNull(postScore, 0) * 0.02;
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
drop PROCEDURE if EXISTS `getHostestCircleMembersProcedure` ;
-- 打卡积分 * 0.6 + 任务积分 * 0.3 
delimiter $$
CREATE PROCEDURE `getHostestCircleMembersProcedure` (in $circleId INT, in $limit INT)
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
			set totalScore = contributionNumber * 0.6;
			update t_circle_member cm set cm.member_score = totalScore where cm.member_id = member_id and cm.circle_id = $circleId;

		FETCH cursor_circle_members into member_id; -- 将游标当前读取行的数据顺序赋予自定义变量
		UNTIL DONE
		END REPEAT ;
  CLOSE cursor_circle_members; -- 关闭游标
END $$
delimiter