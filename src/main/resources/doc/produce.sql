-- 判断用户可以创建的朋友圈数量
drop PROCEDURE if EXISTS `getCircleCreateLimitProcedure` ;
delimiter $$
CREATE PROCEDURE `getCircleCreateLimitProcedure` (IN $createUserId INT, IN $status INT)
BEGIN
	DECLARE score INT DEFAULT 0;
  DECLARE returnValue INT DEFAULT 0;
	DECLARE arealy INT DEFAULT 0;
	select sum(t.score) into  score from t_score t where create_user_id = $createUserId and `status` = $status;
	
	#获取该用户最多可以创建的圈子数量
	SELECT number into returnValue  from t_circle_create_limit ccl where score >= left_score and
		(case when right_score = -1 then 
			(select 1=1 )
			else 
			(select score < right_score ) 
			end
		) limit 1;

	#获取该用户已经创建的圈子数量
	select count(c.id) into arealy from t_circle c where c.create_user_id = $createUserId and c.`status` = $status;

	#set returnValue = 0;
	select returnValue - arealy as number;
END $$
delimiter


-- 计算圈子的积分
drop PROCEDURE if EXISTS `getHostestCirclesProcedure` ;
-- 用户打卡记录 * 0.3 + 用户净新增记录(新增-退出) * 0.6 + 任务完成总数 * 0.4 + 打开的次数 * 0.1 
delimiter $$
CREATE PROCEDURE `getHostestCirclesProcedure` (in $time DATETIME, in $limit INT)
BEGIN
	DECLARE circle_id INT(11); -- 自定义变量1
	DECLARE addNumber INT(11); -- 这个时间段内新增的成员数
	DECLARE logNumber INT(11); -- 这个时间段内访问数
	DECLARE subjectVal VARCHAR(255); -- 日记的标题
  DECLARE totalScore FLOAT; -- 最终的总分
	DECLARE DONE BOOLEAN DEFAULT 0; #定义结束标识  
	
	-- 获取符合条件的圈子列表
	DECLARE cursor_circles CURSOR FOR (SELECT c.id  from t_circle c where c.create_time > $time);

	
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
		
			set subjectVal = concat('访问圈子', circle_id);
			-- 获取圈子在这段时间内被访问的次数
			select count(id) into logNumber from t_operate_log ol where ol.`subject` = subjectVal;

			set totalScore = addNumber * 0.6 + logNumber* 0.1;
			update t_circle c set c.circle_score = totalScore where c.id = circle_id;
			 -- select addNumber * 0.6 + logNumber* 0.1, totalScore, addNumber, logNumber, subjectVal;

		FETCH cursor_circles into circle_id; -- 将游标当前读取行的数据顺序赋予自定义变量12 
		UNTIL DONE
		END REPEAT ;
  CLOSE cursor_circles; -- 关闭游标 

  select * from t_circle order by circle_score desc limit 0, $limit;
END $$
delimiter