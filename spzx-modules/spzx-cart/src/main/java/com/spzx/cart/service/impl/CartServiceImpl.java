package com.spzx.cart.service.impl;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.controller.CartController;
import com.spzx.cart.service.ICartService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.spzx.cart.service.impl.CartKeyUtil.getCartKey;

@Service
@Slf4j
public class CartServiceImpl implements ICartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RemoteProductService remoteProductService;

//    @Autowired
//    private

    @Override
    public void addToCart(Long skuId, Integer num) {

        //初始化
        Long userId = SecurityUtils.getUserId();
        String cartKey = getCartKey(userId);
        BoundHashOperations<String, String, CartInfo>  boundHashOperations = redisTemplate.boundHashOps(cartKey);

        //判断是添加还是更新
        CartInfo cartInfoCache = boundHashOperations.get(skuId + "");

        if (null !=cartInfoCache){
            //更新数量
            cartInfoCache.setSkuNum(cartInfoCache.getSkuNum() + num);
        }else{
            // 通过skuId，查询product商品sku信息
            ProductSku productSku = remoteProductService.getProductSku(skuId,SecurityConstants.INNER).getData();

            //封装cartInfo
            cartInfoCache = new CartInfo();
            cartInfoCache.setSkuId(skuId);
            cartInfoCache.setSkuNum(num);
            cartInfoCache.setSkuName(productSku.getSkuName());
            cartInfoCache.setThumbImg(productSku.getThumbImg());
            cartInfoCache.setIsChecked(1);
            cartInfoCache.setUserId(userId);


            //查询实时价格，因为productSku是缓存数据
            SkuPrice price = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER).getData();
            cartInfoCache.setCartPrice(price.getSalePrice());
            cartInfoCache.setSkuPrice(price.getMarketPrice());
        }

        //存入redis
        boundHashOperations.put(skuId + "",cartInfoCache);

    }

    @Override
    public List<CartInfo> getCartList() {

        //获取当前用户登录的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);

        List<CartInfo> cartInfoList = boundHashOperations.values();

        List<Long> skuIds = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        List<SkuPrice> skuPrices = remoteProductService.getSkuPriceList(skuIds, SecurityConstants.INNER).getData();

        Map<Long, SkuPrice> skuPriceMap = skuPrices.stream().collect(Collectors.toMap(skuPrice -> skuPrice.getSkuId(), skuPrice -> skuPrice));
        // 封装实时价格信息
        for (CartInfo cartInfo : cartInfoList) {
            cartInfo.setCartPrice(skuPriceMap.get(cartInfo.getSkuId()).getSalePrice());
            cartInfo.setSkuPrice(skuPriceMap.get(cartInfo.getSkuId()).getMarketPrice());
        }

        return cartInfoList;
    }

    @Override
    public void deleteCart(Long skuId) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        //获取缓存对象
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);

        boundHashOperations.delete(skuId.toString());
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked) {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);

        if(boundHashOperations.hasKey(skuId.toString())){
            CartInfo cartInfo = boundHashOperations.get(skuId.toString());
            //cartInfo 写入缓存
            cartInfo.setIsChecked(isChecked);
            //更新缓存
            boundHashOperations.put(skuId.toString(),cartInfo);
        }
    }

    @Override
    public void allCheckCart(Integer isChecked) {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);

        List<CartInfo> cartInfoList = boundHashOperations.values();

        cartInfoList.forEach(item ->{
            CartInfo cartInfo = boundHashOperations.get(item.getSkuId().toString());
            cartInfo.setIsChecked(isChecked);

            //更新缓存
            boundHashOperations.put(item.getSkuId().toString(),cartInfo);
        });
    }

    @Override
    public void clearCart() {
        Long userId = SecurityContextHolder.getUserId();
        String cartKey = getCartKey(userId);

        redisTemplate.delete(cartKey);

    }

    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        //获取当前用户的id
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = boundHashOperations.values();

        //过滤掉没选中的
        cartInfoList.stream().filter(item->item.getIsChecked() == 1).collect(Collectors.toList());
        return cartInfoList;
    }

    @Override
    public Boolean updateCartPrice(Long userId) {
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOperations.values();

        if (!CollectionUtils.isEmpty(cartInfoList)){
            for (CartInfo cartInfo : cartInfoList) {
                if (cartInfo.getIsChecked().intValue()==1){
                    SkuPrice skuPrice = remoteProductService.getSkuPrice(cartInfo.getSkuId(), SecurityConstants.INNER).getData();
                    cartInfo.setCartPrice(skuPrice.getSalePrice());
                    cartInfo.setSkuPrice(skuPrice.getSalePrice());
                    hashOperations.put(cartInfo.getSkuId().toString(),cartInfo);
                }
            }
        }

        return true;
    }

    @Override
    public Boolean deleteCartCheckedList(Long userId) {
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = boundHashOperations.values();

        if (!CollectionUtils.isEmpty(cartInfoList)){
            for (CartInfo cartInfo : cartInfoList) {
                //获取选中的商品
                if (cartInfo.getIsChecked().intValue()==1){
                    boundHashOperations.delete(cartInfo.getSkuId().toString());
                }
            }
        }
        return true;
    }

}
