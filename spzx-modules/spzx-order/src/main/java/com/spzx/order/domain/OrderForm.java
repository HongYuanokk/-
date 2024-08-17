package com.spzx.order.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderForm {

    @Schema(description = "用户流水号")
    private String tradeNo;

    //送货地址id
    @Schema(description = "送货地址id")
    private Long userAddressId;

    //运费
    @Schema(description = "运费")
    private BigDecimal feightFee;
    
    //备注
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "结算商品列表")
    private List<OrderItem> orderItemList;
}