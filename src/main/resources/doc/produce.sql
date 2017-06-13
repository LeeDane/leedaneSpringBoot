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
	select count(id) into arealy from t_circle_member cm where cm.create_user_id = $createUserId and `status` = $status;

	#set returnValue = 0;
	select returnValue - arealy as number;
END $$
delimiter