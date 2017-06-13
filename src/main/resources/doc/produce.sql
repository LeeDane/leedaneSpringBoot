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
CREATE PROCEDURE `getHostestCirclesProcedure` (in $time DATETIME)
BEGIN
	declare circle_id int(11); -- 自定义变量1
	declare addNumber int(11); -- 这个时间段内新增的成员数
	declare logNumber int(11); -- 这个时间段内访问数
	declare subjectVal VARCHAR(255); -- 日记的标题
  declare totalScore FLOAT; -- 最终的总分
	DECLARE done INT DEFAULT FALSE; -- 自定义控制游标循环变量,默认false 
	#获取符合条件的圈子列表
	DECLARE My_Cursor CURSOR FOR (SELECT c.id  from t_circle c where c.create_time > $time);
	OPEN My_Cursor; -- 打开游标
  myLoop: LOOP -- 开始循环体,myLoop为自定义循环名,结束循环时用到  
    FETCH My_Cursor into circle_id; -- 将游标当前读取行的数据顺序赋予自定义变量12  
    IF done THEN -- 判断是否继续循环  
      LEAVE myLoop; -- 结束循环  
    END IF;  

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
		select addNumber * 0.6 + logNumber* 0.1, totalScore, addNumber, logNumber, subjectVal;

    COMMIT; -- 提交事务  
  END LOOP myLoop; -- 结束自定义循环体  
  CLOSE My_Cursor; -- 关闭游标  

END $$
delimiter