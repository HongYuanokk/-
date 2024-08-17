package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.api.domain.SkuPrice;
import com.spzx.product.api.domain.SkuQuery;
import com.spzx.product.api.domain.SkuStockVo;
import com.spzx.product.api.domain.Product;
import com.spzx.product.api.domain.ProductDetails;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.domain.SkuStock;
import com.spzx.product.mapper.*;
import com.spzx.product.service.IProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper,Product> implements IProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ProductSkuMapper productSkuMapper;

    @Autowired
    ProductDetailsMapper productDetailsMapper;

    @Autowired
    SkuStockMapper skuStockMapper;

    @Override
    public List<Product> selectProductList(Product product) {
        return productMapper.selectProductList(product);
    }

    @Override
    public Product selectProductById(Long id) {

        //商品信息
        Product product = productMapper.selectById(id);

        //商品SKU列表
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id));

        //库存查询
        List<Long> skuIdList = productSkuList.stream().map(ProductSku::getProductId).collect(Collectors.toList());

        List<SkuStock> skuStockList = skuStockMapper.selectList(new LambdaQueryWrapper<SkuStock>().in(SkuStock::getSkuId, skuIdList).select(SkuStock::getSkuId, SkuStock::getTotalNum));

        Map<Long, Integer> skuIdToStockNumMap = skuStockList.stream().collect(Collectors.toMap(SkuStock::getSkuId, SkuStock::getTotalNum));

        productSkuList.forEach(item ->{
            item.setStockNum(skuIdToStockNumMap.get(item.getId()));
        });
        product.setProductSkuList(productSkuList);

        //商品详情
        ProductDetails productDetails = productDetailsMapper.selectOne(new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, id));

        product.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));

        return product;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAuditStatus(Long id, Integer audiStatus) {
        Product product = new Product();
        product.setId(id);
        if(audiStatus == 1){
            product.setAuditStatus(1);
            product.setAuditMessage("审批通过");
        }else {
            product.setAuditStatus(-1);
            product.setAuditMessage("审批不通过");
        }
        productMapper.updateById(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        if(status == 1){
            product.setStatus(1);
        }else {
            product.setStatus(-1);
        }
        productMapper.updateById(product);
    }

    /**
     * 新增商品
     * @param product
     * @return
     */
    @Override
    public int insertProduct(Product product) {

        productMapper.insert(product);

        //spu
        List<ProductSku> productSkuList = product.getProductSkuList();
        for (int i =0,size = productSkuList.size();i < size;i++){
            ProductSku productSku = productSkuList.get(i);
            productSku.setSkuCode(product.getId() + "_"  + i);
            productSku.setProductId(product.getId());
            String skuName = product.getName() + "" + productSku.getSkuSpec();
            productSku.setSkuName(skuName);
            productSkuMapper.insert(productSku);

            //添加商品库存
            SkuStock skuStock = new SkuStock();
            skuStock.setSkuId(productSku.getId());
            skuStock.setTotalNum(productSku.getStockNum());
            skuStock.setLockNum(0);
            skuStock.setAvailableNum(productSku.getStockNum());
            skuStock.setSaleNum(0);
            skuStockMapper.insert(skuStock);
        }


        //details
        ProductDetails productDetails = new ProductDetails();

        productDetails.setProductId(product.getId());

        List<String> detailsImageUrlList = product.getDetailsImageUrlList();

        String join = String.join(",", detailsImageUrlList);

        productDetails.setImageUrls(join);

        productDetailsMapper.insert(productDetails);

        return product.getId().intValue();

    }


    /**
     * 修改商品信息
     * @param product 商品
     * @return
     */
    @Override
    public int updateProduct(Product product) {
        //修改商品信息
        baseMapper.updateById(product);

        List<ProductSku> productSkuList = product.getProductSkuList();
        productSkuList.forEach(productSku -> {
            //修改商品SKU信息
            productSkuMapper.updateById(productSku);

            //修改商品库存
            SkuStock skuStock = skuStockMapper.selectOne(new LambdaQueryWrapper<SkuStock>().eq(SkuStock::getSkuId, productSku.getId()));

            skuStock.setTotalNum(productSku.getStockNum());

            int availableNum = skuStock.getTotalNum() - skuStock.getLockNum();

            skuStock.setAvailableNum(availableNum);

        });

        //修改商品详细信息
        ProductDetails productDetails = productDetailsMapper.selectOne(new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, product.getId()));

        productDetails.setImageUrls(String.join(",",productDetails.getImageUrls()));

        productDetailsMapper.updateById(productDetails);

        return 1;
    }



    /**
     * 批量删除商品
     *
     * @param ids 需要删除的商品主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteProductByIds(Long[] ids) {

        productMapper.deleteBatchIds(Arrays.asList(ids));

        //获取sku列表
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().in(ProductSku::getProductId, ids).select(ProductSku::getId));

        List<Long> skuIdList = productSkuList.stream().map(ProductSku::getId).collect(Collectors.toList());

        productSkuMapper.delete(new LambdaQueryWrapper<ProductSku>().in(ProductSku::getProductId,ids));

        skuStockMapper.delete(new LambdaQueryWrapper<SkuStock>().in(SkuStock::getSkuId,skuIdList));

        productDetailsMapper.delete(new LambdaQueryWrapper<ProductDetails>().in(ProductDetails::getProductId,ids));

        return 1;
    }

    @Override
    public List<ProductSku> getTopSale() {
        return productSkuMapper.selectTopSale();
    }


    @Override
    public List<ProductSku> skuList(SkuQuery skuQuery) {
        return productSkuMapper.skuList(skuQuery);
    }

    @Override
    public ProductSku getProductSku(Long skuId) {
        ProductSku productSku = productSkuMapper.selectById(skuId);
        return productSku;
    }

    @Override
    public SkuPrice getSkuPrice(Long skuId) {
        ProductSku productSku = productSkuMapper.selectById(skuId);
        SkuPrice skuPrice = new SkuPrice();
        skuPrice.setSkuId(skuId);
        skuPrice.setMarketPrice(productSku.getMarketPrice());
        skuPrice.setSalePrice(productSku.getSalePrice());
        return skuPrice;
    }

    @Override
    public Product getProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        return product;
    }

    @Override
    public ProductDetails getProductDetails(Long id) {
        QueryWrapper<ProductDetails> productDetailsQueryWrapper = new QueryWrapper<>();
        productDetailsQueryWrapper.eq("product_id",id);
        ProductDetails productDetails = productDetailsMapper.selectOne(productDetailsQueryWrapper);
        return productDetails;
    }

    @Override
    public SkuStockVo getSkuStock(Long skuId) {
        SkuStock skuStock = skuStockMapper.selectOne(new LambdaQueryWrapper<SkuStock>().eq(SkuStock::getSkuId, skuId));
        SkuStockVo stockVo = new SkuStockVo();
        BeanUtils.copyProperties(skuStock,stockVo);
        return stockVo;
    }

    @Override
    public Map<String, Long> getSkuSpecValue(Long id) {
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id).select(ProductSku::getId, ProductSku::getSkuSpec));
        Map<String,Long> skuSpecValueMap = new HashMap<>();
        productSkuList.forEach(item -> {
            skuSpecValueMap.put(item.getSkuSpec(), item.getId());
        });
        return skuSpecValueMap;
    }

    @Override
    public List<SkuPrice> getSkuPriceList(List<Long> skuIdList) {
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().in(ProductSku::getId, skuIdList).select(ProductSku::getId, ProductSku::getSalePrice));
        return productSkuList.stream().map(item ->{
            SkuPrice skuPrice = new SkuPrice();
            skuPrice.setSkuId(item.getId());
            skuPrice.setSalePrice(item.getSalePrice());
            return skuPrice;
        }).collect(Collectors.toList());
    }



}
