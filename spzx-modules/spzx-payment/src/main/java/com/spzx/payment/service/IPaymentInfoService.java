package com.spzx.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.payment.domain.PaymentInfo;

import java.util.List;
import java.util.Map;

/**
 * 付款信息Service接口
 *
 * @author atguigu
 * @date 2024-08-19
 */
public interface IPaymentInfoService extends IService<PaymentInfo>
{

    /**
     * 查询付款信息列表
     *
     * @param paymentInfo 付款信息
     * @return 付款信息集合
     */
    public List<PaymentInfo> selectPaymentInfoList(PaymentInfo paymentInfo);

    PaymentInfo savePaymentInfo(String orderNo);

    void updatePaymentStatus(Map<String, String> paramMap, int i);
}
