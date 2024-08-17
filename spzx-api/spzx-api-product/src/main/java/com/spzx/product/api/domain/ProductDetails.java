package com.spzx.product.api.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品详情对象 product_details
 */
@Schema(description = "商品详情")
@Data
public class ProductDetails extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @Schema(description = "商品id")
    private Long productId;

    /**
     * 详情图片地址
     */
    @Schema(description = "详情图片地址")
    private String imageUrls;

}