package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.*;

import java.util.List;
import java.util.Map;

public interface IProductService extends IService<Product> {
    List<Product> selectProductList(Product product);

    Product selectProductById(Long id);

    void updateAuditStatus(Long id, Integer audiStatus);

    void updateStatus(Long id, Integer status);

    int insertProduct(Product product);

    int updateProduct(Product product);

    int deleteProductByIds(Long[] ids);

    List<ProductSku> getTopSale();

    List<ProductSku> skuList(SkuQuery skuQuery);

    ProductSku getProductSku(Long skuId);

    SkuPrice getSkuPrice(Long skuId);

    Product getProduct(Long productId);

    ProductDetails getProductDetails(Long productId);

    SkuStockVo getSkuStock(Long skuId);

    Map<String, Long> getSkuSpecValue(Long id);

    List<SkuPrice> getSkuPriceList(List<Long> skuList);

    String checkAndLock(String tradeNo, List<SkuLockVo> skuLockVoList);

    void unlock(String orderNo);

    void minus(String orderNo);
}
