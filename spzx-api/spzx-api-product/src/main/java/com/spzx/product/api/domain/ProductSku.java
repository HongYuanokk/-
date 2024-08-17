package com.spzx.product.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品sku对象 product_sku
 */
@Schema(description = "商品sku")
@Data
public class ProductSku extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品编号")
    private String skuCode;

    @Schema(description = "sku名称")
    private String skuName;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "缩略图路径")
    private String thumbImg;

    @Schema(description = "售价")
    private BigDecimal salePrice;

    @Schema(description = "市场价")
    private BigDecimal marketPrice;

    @Schema(description = "成本价")
    private BigDecimal costPrice;

    @Schema(description = "sku规格信息json")
    private String skuSpec;

    @Schema(description = "重量")
    private BigDecimal weight;

    @Schema(description = "体积")
    private BigDecimal volume;

    @Schema(description = "线上状态：0-初始值，1-上架，-1-自主下架")
    private Integer status;

    // 扩展的属性
    @Schema(description = "sku库存")
    @TableField(exist = false)
    private Integer stockNum;

    @Schema(description = "sku销量")
    @TableField(exist = false)
    private Integer saleNum;

}