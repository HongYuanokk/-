package com.spzx.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.order.domain.*;
import com.spzx.order.mapper.OrderInfoMapper;
import com.spzx.order.mapper.OrderItemMapper;
import com.spzx.order.mapper.OrderLogMapper;
import com.spzx.order.service.IOrderInfoService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuLockVo;
import com.spzx.product.api.domain.SkuPrice;
import com.spzx.user.api.RemoteUserAddressService;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.product.api.domain.OrderInfo;
import com.spzx.product.api.domain.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private RabbitService rabbitService;


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


        //交易码校验
        Assert.hasText(tradeNo,"订单已提交或者发生异常");
        String userTradeKey = "user:tradeNo:" + userId;


        //脚本解锁用lua
        String delLua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(delLua);
        redisScript.setResultType(Long.class);
        Long delFlag = (Long)redisTemplate.execute(redisScript, Arrays.asList(userTradeKey), tradeNo);
        Assert.isTrue(delFlag!=0,"订单已经提交或者发生异常");


        //获得用户收货地址
        UserAddress userAddress = remoteUserAddressService.getUserAddress(userId, SecurityConstants.INNER).getData();
        Assert.notNull(userAddress,"用户地址获取失败");

        // 提交的订单是购物车中被选中的数据，如果商家在用户已经添加购物车之后改变了商品价格
        // 那么提交订单时的价格和商家的最新价格不一致，所以要在提交订单时检验实时价格数据
        List<Long> skuIds= orderForm.getOrderItemList().stream().map(OrderItem::getSkuId).collect(Collectors.toList());
        List<SkuPrice> skuPrices = remoteProductService.getSkuPriceList(skuIds, SecurityConstants.INNER).getData();
        Map<Long, BigDecimal> priceMap = skuPrices.stream().collect(Collectors.toMap(SkuPrice::getSkuId, SkuPrice::getSalePrice));
        for (OrderItem orderItem : orderForm.getOrderItemList()) {
            BigDecimal skuPrice = orderItem.getSkuPrice();
            Long skuId = orderItem.getSkuId();
            BigDecimal currentSalePrice = priceMap.get(skuId);// 实时价格
            int i = skuPrice.compareTo(currentSalePrice);
            if(i!=0){
                // 更新购物车价格信息
                remoteCartService.updateCartPrice(userId, SecurityConstants.INNER);
            }
            io.jsonwebtoken.lang.Assert.isTrue(i==0,"价格发生变化");
        }

        // 检查和锁定库存
        // 参数，购买的skuId，购买数量，分布式锁
        List<SkuLockVo> skuLockVoList = new ArrayList<>();
        for (OrderItem orderItem : orderForm.getOrderItemList()) {
            Long skuId = orderItem.getSkuId();
            Integer skuNum = orderItem.getSkuNum();
            SkuLockVo skuLockVo = new SkuLockVo();
            skuLockVo.setSkuId(skuId);
            skuLockVo.setSkuNum(skuNum);
            skuLockVoList.add(skuLockVo);
        }
        String errMsg = remoteProductService.checkAndLock(orderForm.getTradeNo(), skuLockVoList, SecurityConstants.INNER).getData();// stock:lock:12313
        Assert.isTrue(!StringUtils.hasText(errMsg),errMsg);



        //封装订单和订单详情
        Long orderId = saveOrder(orderForm);
        Assert.notNull(orderId,"订单保存失败");


        //删除购物车数据
        remoteCartService.deleteCartCheckedList(userId,SecurityConstants.INNER);

        //6 发送延迟消息，取消订单
        rabbitService.sendDealyMessage(MqConst.EXCHANGE_CANCEL_ORDER, MqConst.ROUTING_CANCEL_ORDER, String.valueOf(orderId), MqConst.CANCEL_ORDER_DELAY_TIME);

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


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processCloseOrder(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if(null != orderInfo && orderInfo.getOrderStatus().intValue() == 0) {
            orderInfo.setOrderStatus(-1);
            orderInfo.setCancelTime(new Date());
            orderInfo.setCancelReason("未支付自动取消");
            orderInfoMapper.updateById(orderInfo);

            //记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setProcessStatus(-1L);
            orderLog.setNote("系统取消订单");
            orderLogMapper.insert(orderLog);
        }
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if(null != orderInfo && orderInfo.getOrderStatus().intValue() == 0) {
            orderInfo.setOrderStatus(-1);
            orderInfo.setCancelTime(new Date());
            orderInfo.setCancelReason("用户取消订单");
            orderInfoMapper.updateById(orderInfo);
            //记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderId(orderInfo.getId());
            orderLog.setProcessStatus(-1L);
            orderLog.setNote("用户取消订单");
            orderLogMapper.insert(orderLog);
            //发送MQ消息通知商品系统解锁库存
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT, MqConst.QUEUE_UNLOCK, orderInfo.getOrderNo());
        }
    }

    @Override
    public OrderInfo getByOrderNo(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo));
        List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderInfo.getId()));
        orderInfo.setOrderItemList(orderItemList);
        return orderInfo;
    }


    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo)
    {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    private String generateTradeNo(Long userId) {
        String userTradeKey ="user:tradeNo:" +userId;
        String tradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set(userTradeKey,tradeNo,5, TimeUnit.MINUTES);
        return tradeNo;
    }

}
