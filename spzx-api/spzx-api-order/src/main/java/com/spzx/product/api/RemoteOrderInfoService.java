package com.spzx.product.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.domain.OrderInfo;
import com.spzx.product.api.factory.RemoteOrderInfoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(contextId = "remoteUserInfoService",value = ServiceNameConstants.ORDER_SERVICE,fallbackFactory = RemoteOrderInfoFallbackFactory.class )
public interface RemoteOrderInfoService {

    @GetMapping("/orderInfo/getByOrderNo/{orderNo}")
    public R<OrderInfo> getByOrderNo(@PathVariable("orderNo") String orderNo, @RequestHeader(SecurityConstants.INNER) String source);

}
