package com.spzx.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.order.domain.*;
import com.spzx.order.mapper.OrderItemMapper;
import com.spzx.order.mapper.OrderLogMapper;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.user.api.RemoteUserAddressService;
import com.spzx.user.api.domain.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import com.spzx.order.mapper.OrderInfoMapper;
import com.spzx.order.service.IOrderInfoService;
import org.springframework.util.Assert;

/**
 * 订单Service业务层处理
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService
{
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private RemoteCartService remoteCartService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RemoteProductService remoteProductService;

    @Autowired
    private RemoteUserAddressService remoteUserAddressService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;


    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单
     */


    @Override
    public TradeVo orderTradeData() {
        Long userId = SecurityContextHolder.getUserId();

        TradeVo tradeVo = new TradeVo();

        //调用购物车获得被选中的购物车选项
        List<CartInfo> cartInfoList = remoteCartService.getCartCheckedList(userId, SecurityConstants.INNER).getData();
        //Assert.notEmpty(cartInfoList,"购物车为空");


        //将购物车集合项转换为订单详情集合项
        List<OrderItem> orderItemList = cartInfoList.stream().map(cartInfo -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setSkuPrice(cartInfo.getSkuPrice());
            orderItem.setThumbImg(cartInfo.getThumbImg());
            return orderItem;
        }).collect(Collectors.toList());

        //总金额
        BigDecimal sum = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            sum = sum.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        }


        //封装
        tradeVo.setOrderItemList(orderItemList);
        tradeVo.setTotalAmount(sum);
        tradeVo.setTradeNo(generateTradeNo(SecurityContextHolder.getUserId()));//交易单号

        return tradeVo;
    }

    @Override
    public Long submitOrder(OrderForm orderForm) {

        Long userId = SecurityUtils.getUserId();
        String tradeNo = orderForm.getTradeNo();


        //校验码校验
        Assert.hasText(tradeNo,"订单已提交或者发生异常");
        String userTradeKey = "user:tradeNo:" + userId;


        //用lua脚本解锁
        String delLua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(delLua);
        redisScript.setResultType(Long.class);
        Long delFlag = (Long)redisTemplate.execute(redisScript, Arrays.asList(userTradeKey), tradeNo);
        io.jsonwebtoken.lang.Assert.isTrue(delFlag!=0L,"订单已经提交或者发生异常");


        //获得用户收货地址
        UserAddress userAddress = remoteUserAddressService.getUserAddress(userId, SecurityConstants.INNER).getData();
        Assert.notNull(userAddress,"用户地址获取失败");

        //封装订单和订单详情
        Long orderId = saveOrder(orderForm);
        Assert.notNull(orderId,"订单保存失败");


        //删除购物车数据
        remoteCartService.deleteCartCheckedList(userId,SecurityConstants.INNER);

        return orderId;
    }

    private Long saveOrder(OrderForm orderForm) {

        // 获得用户收获地址
        Long userAddressId = orderForm.getUserAddressId();
        UserAddress userAddress = remoteUserAddressService.getUserAddress(userAddressId, SecurityConstants.INNER).getData();
        io.jsonwebtoken.lang.Assert.notNull(userAddress,"用户地址获取失败");

        //保存订单生成主键
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderForm.getTradeNo());//订单号
        BigDecimal sum = new BigDecimal("0");
        for (OrderItem orderItem : orderForm.getOrderItemList()) {
            sum=sum.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        }
        orderInfo.setTotalAmount(sum);
        orderInfo.setOriginalTotalAmount(sum);
        orderInfo.setUserId(SecurityUtils.getUserId());
        orderInfo.setNickName(SecurityUtils.getUsername());
        orderInfo.setRemark(orderForm.getRemark());
        orderInfo.setReceiverName(userAddress.getName());
        orderInfo.setReceiverPhone(userAddress.getPhone());
        orderInfo.setReceiverTagName(userAddress.getTagName());
        orderInfo.setReceiverProvince(userAddress.getProvinceCode());
        orderInfo.setReceiverCity(userAddress.getCityCode());
        orderInfo.setReceiverDistrict(userAddress.getDistrictCode());
        orderInfo.setReceiverAddress(userAddress.getFullAddress());
        orderInfo.setCouponAmount(new BigDecimal(0));
        orderInfo.setFeightFee(orderForm.getFeightFee());

        orderInfo.setOrderStatus(0);
        baseMapper.insert(orderInfo);

        Long id = orderInfo.getId();
        io.jsonwebtoken.lang.Assert.notNull(id,"订单保存失败");

        //根据主键保存订单详情
        for (OrderItem orderItem : orderForm.getOrderItemList()) {
                orderItem.setOrderId(id);
                orderItemMapper.insert(orderItem);
        }

        //根据主键保存订单日志

        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderInfo.getId());
        orderLog.setProcessStatus(0L);
        orderLog.setNote("提交订单");
        orderLogMapper.insert(orderLog);

        return id;

    }


    /**
     * 立即购买
     * @param skuId
     * @return
     */
    @Override
    public TradeVo buy(Long skuId) {
        ProductSku productSku = remoteProductService.getProductSku(skuId,SecurityConstants.INNER).getData();
        ArrayList<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(skuId);
        orderItem.setSkuName(productSku.getSkuName());
        orderItem.setSkuNum(1);
        orderItem.setSkuPrice(productSku.getSalePrice());
        orderItem.setThumbImg(productSku.getThumbImg());
        orderItemList.add(orderItem);

        //订单总金额
        BigDecimal totalAmount = productSku.getSalePrice();

        //渲染订单确认页面-生成用户流水号
        String tradeNo = this.generateTradeNo(SecurityUtils.getUserId());

        TradeVo tradeVo = new TradeVo();
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        tradeVo.setTradeNo(tradeNo);
        return tradeVo;
    }


    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo)
    {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    private String generateTradeNo(Long userId) {
        String userTradeKey ="user:tradeNo" +userId;
        String tradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set(userTradeKey,tradeNo,5, TimeUnit.MINUTES);
        return tradeNo;
    }

}
