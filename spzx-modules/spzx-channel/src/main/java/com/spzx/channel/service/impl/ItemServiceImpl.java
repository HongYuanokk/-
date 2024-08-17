package com.spzx.channel.service.impl;

import com.alibaba.fastjson.JSON;
import com.spzx.channel.domain.ItemVo;
import com.spzx.channel.service.IItemService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    RemoteProductService remoteProductService;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;


    @Override
    public ItemVo item(Long skuId) {
        long start = System.currentTimeMillis();
        // 调用6个接口，分别查询
        // 封装item
        ItemVo itemVo = new ItemVo();

        // sku 开始
        CompletableFuture<ProductSku> skuTask = CompletableFuture.supplyAsync(new Supplier<ProductSku>() {
            @Override
            public ProductSku get() {
                R<ProductSku> productSkuResult = remoteProductService.getProductSku(skuId,SecurityConstants.INNER);
                ProductSku productSku = productSkuResult.getData();
                itemVo.setProductSku(productSku);
                return productSku;
            }
        }, threadPoolExecutor);


        // procut 接力then
        CompletableFuture productTask = skuTask.thenAcceptAsync(new Consumer<ProductSku>() {
            @Override
            public void accept(ProductSku productSku) {
                R<Product> productResult = remoteProductService.getProduct(productSku.getProductId(), SecurityConstants.INNER);
                Product product = productResult.getData();
                itemVo.setProduct(product);
                itemVo.setSliderUrlList(Arrays.asList(product.getSliderUrls().split(",")));
                String specValue = product.getSpecValue();
                itemVo.setSpecValueList(JSON.parseArray(specValue));
            }
        }, threadPoolExecutor);


        // 详情 接力then
        CompletableFuture detailsTask = skuTask.thenAcceptAsync(new Consumer<ProductSku>() {
            @Override
            public void accept(ProductSku productSku) {
                R<ProductDetails> productDetailsResult = remoteProductService.getProductDetails(productSku.getProductId(), SecurityConstants.INNER);
                ProductDetails productDetails = productDetailsResult.getData();
                itemVo.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));
            }
        }, threadPoolExecutor);


        // 价格 独立
        CompletableFuture priceTask = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                R<SkuPrice> skuPriceResult = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER);
                SkuPrice skuPrice = skuPriceResult.getData();
                itemVo.setSkuPrice(skuPrice);
            }
        }, threadPoolExecutor);


        // 库存stock 独立
        CompletableFuture stockTask = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                R<SkuStockVo> skuStockResult = remoteProductService.getSkuStock(skuId, SecurityConstants.INNER);
                SkuStockVo skuStockVo = skuStockResult.getData();
                itemVo.setSkuStockVo(skuStockVo);
            }
        }, threadPoolExecutor);


        // 当前prodcut下的所有skus，specMap，接力then
        CompletableFuture mapTask = skuTask.thenAcceptAsync(new Consumer<ProductSku>() {
            @Override
            public void accept(ProductSku productSku) {
                R<Map<String, Long>> skuSpecValueResult = remoteProductService.getSkuSpecValue(productSku.getProductId(), SecurityConstants.INNER);
                Map<String, Long> specToSkuIdMap = skuSpecValueResult.getData();
                itemVo.setSkuSpecValueMap(specToSkuIdMap);
            }
        }, threadPoolExecutor);

        // 阻塞主线程
        CompletableFuture.allOf(skuTask,productTask, detailsTask, priceTask, stockTask,mapTask).join();

        long end = System.currentTimeMillis();
        System.out.println("==========================新方法耗时：" + (end - start) + "ms");
        return itemVo;
    }




     @Override
    public ItemVo itembak(Long skuId) {

        long start = System.currentTimeMillis();

        // 调用6个接口，分别查询

        // sku 开始
        R<ProductSku> productSkuResult = remoteProductService.getProductSku(skuId,SecurityConstants.INNER);
        ProductSku productSku = productSkuResult.getData();


        // procut 接力then
        R<Product> productResult = remoteProductService.getProduct(productSku.getProductId(), SecurityConstants.INNER);
        Product product = productResult.getData();

        // 详情 接力then
        R<ProductDetails> productDetailsResult = remoteProductService.getProductDetails(productSku.getProductId(), SecurityConstants.INNER);
        ProductDetails productDetails = productDetailsResult.getData();

        // 价格 独立
        R<SkuPrice> skuPriceResult = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER);
        SkuPrice skuPrice = skuPriceResult.getData();

        // 库存stock 独立
        R<SkuStockVo> skuStockResult = remoteProductService.getSkuStock(skuId, SecurityConstants.INNER);
        SkuStockVo skuStockVo = skuStockResult.getData();

        // 当前prodcut下的所有skus，specMap，接力then
        R<Map<String, Long>> skuSpecValueResult = remoteProductService.getSkuSpecValue(productSku.getProductId(), SecurityConstants.INNER);
        Map<String, Long> specToSkuIdMap = skuSpecValueResult.getData();


        // 封装item
        ItemVo itemVo = new ItemVo();
        itemVo.setProductSku(productSku);
        itemVo.setProduct(product);
        itemVo.setSkuPrice(skuPrice);
        itemVo.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));
        itemVo.setSkuStockVo(skuStockVo);
        itemVo.setSliderUrlList(Arrays.asList(product.getSliderUrls().split(",")));
        itemVo.setSkuSpecValueMap(specToSkuIdMap);
        String specValue = product.getSpecValue();
        itemVo.setSpecValueList(JSON.parseArray(specValue));

        long end = System.currentTimeMillis();

        System.out.println("==========================旧方法耗时：" + (end - start) + "ms");
        return itemVo;
    }
}
