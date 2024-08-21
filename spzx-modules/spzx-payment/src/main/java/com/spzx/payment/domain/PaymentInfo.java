package com.spzx.payment.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spzx.common.core.annotation.Excel;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 付款信息对象 payment_info
 *
 * @author atguigu
 * @date 2024-08-19
 */
@Data
@Schema(description = "付款信息")
public class PaymentInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户id */
    @Excel(name = "用户id")
    @Schema(description = "用户id")
    private Long userId;

    /** 订单号 */
    @Excel(name = "订单号")
    @Schema(description = "订单号")
    private String orderNo;

    /** 付款方式：1-微信 2-支付宝 */
    @Excel(name = "付款方式：1-微信 2-支付宝")
    @Schema(description = "付款方式：1-微信 2-支付宝")
    private Integer payType;

    /** 交易编号（微信或支付） */
    @Excel(name = "交易编号", readConverterExp = "微=信或支付")
    @Schema(description = "交易编号")
    private String tradeNo;

    /** 支付金额 */
    @Excel(name = "支付金额")
    @Schema(description = "支付金额")
    private BigDecimal amount;

    /** 交易内容 */
    @Excel(name = "交易内容")
    @Schema(description = "交易内容")
    private String content;

    /** 支付状态：0-未支付 1-已支付 -1-关闭 */
    @Excel(name = "支付状态：0-未支付 1-已支付 -1-关闭")
    @Schema(description = "支付状态：0-未支付 1-已支付 -1-关闭")
    private Integer paymentStatus;

    /** 回调时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "回调时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "回调时间")
    private Date callbackTime;

    /** 回调信息 */
    @Excel(name = "回调信息")
    @Schema(description = "回调信息")
    private String callbackContent;

}
