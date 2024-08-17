package com.spzx.order.service;

import java.util.List;

import com.spzx.order.domain.OrderForm;
import com.spzx.order.domain.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.order.domain.TradeVo;

/**
 * 订单Service接口
 *
 * @author atguigu
 * @date 2024-08-06
 */
public interface IOrderInfoService extends IService<OrderInfo>
{

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单集合
     */
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    TradeVo orderTradeData();

    Long submitOrder(OrderForm orderForm);

    TradeVo buy(Long skuId);




}
