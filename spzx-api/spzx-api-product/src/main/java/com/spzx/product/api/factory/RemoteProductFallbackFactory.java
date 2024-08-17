package com.spzx.product.api.factory;


import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RemoteProductFallbackFactory implements FallbackFactory<RemoteProductService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteProductFallbackFactory.class);


    @Override
    public RemoteProductService create(Throwable throwable) {
        log.error("商品服务调用失败:{}", throwable.getMessage());
        return new RemoteProductService() {
            @Override
            public R<List<ProductSku>> getTopSale(String source) {
                return R.fail("调用商品服务getTopSale失败:"+throwable.getMessage());
            }

            @Override
            public R<TableDataInfo> skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery, String source) {
                return R.fail("调用商品服务skuList的商品失败:"+throwable.getMessage());
            }

            @Override
            public R<ProductSku> getProductSku(Long skuId, String inner) {
                return R.fail("调用商品服务getProductSku的商品失败:"+throwable.getMessage());
            }

            @Override
            public R<Product> getProduct(Long productId, String inner) {
                return R.fail("调用商品服务getProduct的商品失败:"+throwable.getMessage());
            }

            @Override
            public R<SkuPrice> getSkuPrice(Long skuId, String inner) {
                return R.fail("调用商品服务getSkuPrice的商品失败:"+throwable.getMessage());
            }

            @Override
            public R<ProductDetails> getProductDetails(Long productId, String inner) {
                return R.fail("调用商品服务getProductDetails的商品失败:"+throwable.getMessage());
            }

            @Override
            public R<SkuStockVo> getSkuStock(Long skuId, String inner) {
                return R.fail("调用商品服务getSkuStock的商品失败:"+throwable.getMessage());
            }

            @Override
            public R<Map<String, Long>> getSkuSpecValue(Long productId, String inner) {
                return null;
            }

            @Override
            public R<List<SkuPrice>> getSkuPriceList(List<Long> skuIdList, String source) {
                return R.fail("获取商品sku价格列表失败:" + throwable.getMessage());
            }

//            @Override
//            public R<Map<String, Long>> getSkuSpecValue(Long productId, String inner) {
//                return R.fail("调用商品服务getSkuSpecValue的商品失败:"+throwable.getMessage());
//            }

        };
    }
}