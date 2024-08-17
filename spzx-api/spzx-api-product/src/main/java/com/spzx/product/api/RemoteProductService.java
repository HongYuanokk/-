package com.spzx.product.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.domain.*;


import com.spzx.product.api.factory.RemoteProductFallbackFactory;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteProductService",value = ServiceNameConstants.PRODUCT_SERVICE,fallbackFactory = RemoteProductFallbackFactory.class)
public interface RemoteProductService {

    @GetMapping("/product/getTopSale")
    public R<List<ProductSku>> getTopSale(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    @GetMapping("/product/skuList/{pageNum}/{pageSize}")
    R<TableDataInfo> skuList(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize, @SpringQueryMap SkuQuery skuQuery, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    @GetMapping("/product/getProductSku/{skuId}")
    R<ProductSku> getProductSku(@PathVariable ("skuId")Long skuId, @RequestHeader(SecurityConstants.FROM_SOURCE) String inner);

    @GetMapping(value = "/product/getProduct/{id}")
    R<Product> getProduct(@PathVariable ("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String inner);

    @GetMapping(value = "/product/getSkuPrice/{skuId}")
    R<SkuPrice> getSkuPrice(@PathVariable ("skuId") Long skuId, @RequestHeader(SecurityConstants.FROM_SOURCE) String inner);

    @GetMapping(value = "/product/getProductDetails/{id}")
    R<ProductDetails> getProductDetails(@PathVariable ("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String inner);

    @GetMapping(value = "/product/getSkuStock/{skuId}")
    R<SkuStockVo> getSkuStock(@PathVariable ("skuId") Long skuId, @RequestHeader(SecurityConstants.FROM_SOURCE) String inner);

    @GetMapping(value = "/product/getSkuSpecValue/{id}")
    R<Map<String, Long>> getSkuSpecValue(@PathVariable ("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String inner);

    @PostMapping(value = "/product/getSkuPriceList")
    public R<List<SkuPrice>> getSkuPriceList(@RequestBody List<Long> skuIdList, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
