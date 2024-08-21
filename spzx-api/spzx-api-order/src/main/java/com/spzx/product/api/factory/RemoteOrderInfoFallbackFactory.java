package com.spzx.product.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteOrderInfoService;
import com.spzx.product.api.domain.OrderInfo;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteOrderInfoFallbackFactory implements FallbackFactory<RemoteOrderInfoService> {

    @Override
    public RemoteOrderInfoService create(Throwable cause) {
        return new RemoteOrderInfoService() {
            @Override
            public R<OrderInfo> getByOrderNo(String orderNo, String source) {
                return R.fail("getByOrderNo降级:"+cause.getMessage());
            }
        };
    }
}
