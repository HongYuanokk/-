package com.spzx.product.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.api.domain.*;
import com.spzx.product.domain.SkuStock;
import com.spzx.product.mapper.*;
import com.spzx.product.service.IProductService;
import io.swagger.v3.core.util.Json;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate redisTemplate;

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

    @Transactional
    @Override
    public String checkAndLock(String tradeNo, List<SkuLockVo> skuLockVoList) {
        //检查分布式锁
        // 因为后续需要释放锁或者扣减库存，所以需要将skuLockVoList保存到redis中
        String stockSecuritylockKey = "sku:checkAndLock:" + tradeNo;
        String deleteStockSecuritylockValue = UUID.randomUUID().toString();
        String skuStockDataKey = "sku:lock:data:" + tradeNo;
        Boolean ifCheckLock = stringRedisTemplate.opsForValue().setIfAbsent(stockSecuritylockKey, deleteStockSecuritylockValue, 1, TimeUnit.HOURS);

        try {
            // Assert.isTrue(ifCheckLock, "重复提交订单");
            if (!ifCheckLock) {
                return "重复提交订单";
            }

            // 检查库存，库存中的可用数量是否大于等于购买数量
            // 先获得所有的skuId
            List<Long> skuIds = skuLockVoList.stream().map(SkuLockVo::getSkuId).collect(Collectors.toList());
            // 根据skuIds获得所有的库存信息
            List<SkuStock> skuStocks = skuStockMapper.selectList(new LambdaQueryWrapper<SkuStock>().in(SkuStock::getSkuId, skuIds));
            // 将skuId和可用库存availableNum放到map中
            Map<Long, Integer> stockMap = skuStocks.stream().collect(Collectors.toMap(SkuStock::getSkuId, SkuStock::getAvailableNum));
            // 检查库存
            for (SkuLockVo skuLockVo : skuLockVoList) {
                Integer buyNum = skuLockVo.getSkuNum();// 我要买的数量
                Integer availableNum = stockMap.get(skuLockVo.getSkuId());// 库存中的可用数量
                // Assert.isTrue(buyNum <= availableNum, "库存不足");
                if (buyNum > availableNum) {
                    return "库存不足";
                }
            }

            // 锁定库存，修改锁定库存的值和锁定库存的值
            for (SkuLockVo skuLockVo : skuLockVoList) {
                skuStockMapper.lock(skuLockVo);
            }

            // 将锁定的库存信息暂存到缓存，方便后续的解锁和扣减
            stringRedisTemplate.opsForValue().set(skuStockDataKey, JSON.toJSONString(skuLockVoList), 24, TimeUnit.HOURS);
        } finally {
            // 释放分布式锁
            String delLua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(delLua);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList(stockSecuritylockKey), deleteStockSecuritylockValue);
            // Assert.isTrue(delFlag!=0L,"订单已经提交或者发生异常");
        }
        return "";
    }

    @Transactional
    @Override
    public void unlock(String orderNo) {
        String stockSecurityUnLockKey = "sku:stockUnlock:" + orderNo;
        String deleteStockSecurityUnlockValue = UUID.randomUUID().toString();
        String skuLockDataKey = "sku:lock:data:" + orderNo;

        Boolean ifAbsent = stringRedisTemplate.opsForValue().setIfAbsent(stockSecurityUnLockKey, deleteStockSecurityUnlockValue, 3, TimeUnit.SECONDS);
        Assert.isTrue(ifAbsent,"订单已解锁");

        try {
            String skuLockVosStr = (String) redisTemplate.opsForValue().get(skuLockDataKey);

            List<SkuLockVo> skuLockVos = JSON.parseArray(skuLockVosStr, SkuLockVo.class);

            for (SkuLockVo skuLockVo : skuLockVos) {
                skuStockMapper.unlock(skuLockVo);
            }
        }finally {
            // 释放分布式锁
            String delLua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(delLua);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList(stockSecurityUnLockKey), deleteStockSecurityUnlockValue);
        }

    }

    @Transactional
    @Override
    public void minus(String orderNo) {
        String stockSecurityMinusKey = "sku:stockMinus:" + orderNo;
        String deleteStockSecurityMinusValue = UUID.randomUUID().toString();
        String skuLockDataKey = "sku:lock:data:" +orderNo;

        Boolean ifCheckUnlock = stringRedisTemplate.opsForValue().setIfAbsent(stockSecurityMinusKey, deleteStockSecurityMinusValue, 3, TimeUnit.SECONDS);
        Assert.isTrue(ifCheckUnlock,"订单已扣减库存");

        try{
            String skuLockVosStr = (String) redisTemplate.opsForValue().get(skuLockDataKey);
            List<SkuLockVo> skuStockVos = JSON.parseArray(skuLockVosStr, SkuLockVo.class);
            for (SkuLockVo skuStockVo : skuStockVos) {
                skuStockMapper.minusStock(skuStockVo);
            }
            //释放用户库存数据
            stringRedisTemplate.delete(skuLockDataKey);
        }finally {
            // 释放分布式锁
            String delLua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(delLua);
            redisScript.setResultType(Long.class);
            redisTemplate.execute(redisScript, Arrays.asList(stockSecurityMinusKey), deleteStockSecurityMinusValue);
        }

    }
}
