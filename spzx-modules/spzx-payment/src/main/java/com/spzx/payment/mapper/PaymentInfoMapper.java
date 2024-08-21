package com.spzx.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.payment.domain.PaymentInfo;

import java.util.List;

/**
 * 付款信息Mapper接口
 *
 * @author atguigu
 * @date 2024-08-19
 */

public interface PaymentInfoMapper extends BaseMapper<PaymentInfo>
{

    /**
     * 查询付款信息列表
     *
     * @param paymentInfo 付款信息
     * @return 付款信息集合
     */
    public List<PaymentInfo> selectPaymentInfoList(PaymentInfo paymentInfo);

}
