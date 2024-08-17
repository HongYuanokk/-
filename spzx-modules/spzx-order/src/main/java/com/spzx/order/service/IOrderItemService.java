package com.spzx.order.service;

import java.util.List;
import com.spzx.order.domain.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单项信息Service接口
 *
 * @author atguigu
 * @date 2024-08-06
 */
public interface IOrderItemService extends IService<OrderItem>
{

    /**
     * 查询订单项信息列表
     *
     * @param orderItem 订单项信息
     * @return 订单项信息集合
     */
    public List<OrderItem> selectOrderItemList(OrderItem orderItem);

}
