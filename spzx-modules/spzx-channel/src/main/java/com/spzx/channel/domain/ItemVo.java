package com.spzx.channel.domain;

import com.alibaba.fastjson.JSONArray;
import com.spzx.product.api.domain.Product;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuPrice;
import com.spzx.product.api.domain.SkuStockVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.util.List;
import java.util.Map;

@Data
@Schema(description = "商品详情对象")
public class ItemVo {

   @Schema(description = "商品sku信息")
   private ProductSku productSku;

   @Schema(description = "商品信息")
   private Product product;

   @Schema(description = "最新价格信息")
   private SkuPrice skuPrice;

   @Schema(description = "商品轮播图列表")
   private List<String> sliderUrlList;

   @Schema(description = "商品详情图片列表")
   private List<String> detailsImageUrlList;

   @Schema(description = "商品规格信息")
   private JSONArray specValueList;

   @Schema(description = "商品库存信息")
   private SkuStockVo skuStockVo;

   @Schema(description = "商品规格对应商品skuId信息")
   private Map<String,Long> skuSpecValueMap;

}