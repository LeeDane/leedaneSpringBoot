/* 在插入数据之前设置add_day列的值*/
create trigger financial_add_day_trigger before INSERT on t_financial
for EACH ROW
BEGIN
 SET NEW.add_day = date_format(NEW.addition_time,'%Y-%m-%d');
end;


/* 在插入订单详情数据后寻找关联的订单信息的值*/
DROP TRIGGER IF EXISTS t_mall_order_detail_after_insert_trigger;
create trigger t_mall_order_detail_after_trigger after INSERT on t_mall_order_detail
for EACH ROW
BEGIN
	UPDATE t_mall_order o set o.order_detail_id = NEW.ID, o.order_detail_status = NEW.status where o.product_code = NEW.order_product_id and o.order_code = NEW.order_code and o.platform = NEW.platform;
end;

/* 在更新订单详情数据后寻找关联的订单信息的值*/
DROP TRIGGER IF EXISTS t_mall_order_detail_after_update_trigger;
create trigger t_mall_order_detail_after_update_trigger after UPDATE on t_mall_order_detail
for EACH ROW
BEGIN
	UPDATE t_mall_order o set o.order_detail_id = NEW.ID, o.order_detail_status = NEW.status where o.product_code = NEW.order_product_id and o.order_code = NEW.order_code and o.platform = NEW.platform;
end;


-- /* 在插入订单数据前寻找订单详情关联的信息的值*/
DROP TRIGGER IF EXISTS t_mall_order_before_insert_trigger;
create trigger t_mall_order_before_insert_trigger before INSERT on t_mall_order
for EACH ROW
BEGIN
    -- 查找订单详情的表，判断是否已经存在记录
    DECLARE did BIGINT;
    DECLARE dstatus INT; -- 订单状态
    select d.id, d.status into did, dstatus from t_mall_order_detail d where NEW.product_code = d.order_product_id and NEW.order_code = d.order_code and NEW.platform = d.platform;
    if did > 0 THEN
        SET NEW.order_detail_id = did, NEW.order_detail_status = dstatus;
    END IF;

end;

-- /* 在更新订单数据前寻找订单详情关联的信息的值*/
DROP TRIGGER IF EXISTS t_mall_order_before_update_trigger;
create trigger t_mall_order_before_update_trigger before UPDATE on t_mall_order
for EACH ROW
BEGIN
    -- 查找订单详情的表，判断是否已经存在记录
    DECLARE did BIGINT;
    DECLARE dstatus INT; -- 订单状态
    select d.id, d.status into did, dstatus from t_mall_order_detail d where NEW.product_code = d.order_product_id and NEW.order_code = d.order_code and NEW.platform = d.platform;
    if did > 0 THEN
        SET NEW.order_detail_id = did, NEW.order_detail_status = dstatus;
    END IF;

end;
