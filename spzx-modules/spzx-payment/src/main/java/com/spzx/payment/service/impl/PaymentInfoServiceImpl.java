package com.spzx.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.payment.domain.PaymentInfo;
import com.spzx.payment.mapper.PaymentInfoMapper;
import com.spzx.payment.service.IPaymentInfoService;
import com.spzx.product.api.RemoteOrderInfoService;
import com.spzx.product.api.domain.OrderInfo;
import com.spzx.product.api.domain.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 付款信息Service业务层处理
 *
 * @author atguigu
 * @date 2024-08-19
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements IPaymentInfoService
{
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
     private  RemoteOrderInfoService remoteOrderInfoService;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 查询付款信息列表
     *
     * @param paymentInfo 付款信息
     * @return 付款信息
     */
    @Override
    public List<PaymentInfo> selectPaymentInfoList(PaymentInfo paymentInfo)
    {
        return paymentInfoMapper.selectPaymentInfoList(paymentInfo);
    }

    @Override
    public PaymentInfo savePaymentInfo(String orderNo) {
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        if(null == paymentInfo){
            R<OrderInfo> orderInfoResult = remoteOrderInfoService.getByOrderNo(orderNo, SecurityConstants.INNER);
            if (R.FAIL == orderInfoResult.getCode()) {
                throw new ServiceException(orderInfoResult.getMsg());
            }
            OrderInfo orderInfo= orderInfoResult.getData();

            paymentInfo = new PaymentInfo();
            paymentInfo.setUserId(orderInfo.getUserId());
            String content = "";
            for (OrderItem orderItem : orderInfo.getOrderItemList()) {
                content += orderItem.getSkuName();
            }
            paymentInfo.setContent(content);
            paymentInfo.setAmount(orderInfo.getTotalAmount());
            paymentInfo.setOrderNo(orderNo);
            paymentInfo.setPaymentStatus(0);
            paymentInfoMapper.insert(paymentInfo);
        }
        return paymentInfo;
    }

    @Override
    public void updatePaymentStatus(Map<String, String> paramMap, int i) {
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo,paramMap.get("out_trade_no")));
        if (paymentInfo.getPaymentStatus() == 1){
            return;
        }

        //更新支付信息
        paymentInfo.setPayType(i);
        paymentInfo.setPaymentStatus(1);
        paymentInfo.setTradeNo(paramMap.get("trade_no"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(JSON.toJSONString(paramMap));
        paymentInfoMapper.updateById(paymentInfo);

        //基于MQ通知订单系统，修改订单状态
        rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT_PAY,MqConst.ROUTING_PAYMENT_PAY,paymentInfo.getOrderNo());
    }

}
