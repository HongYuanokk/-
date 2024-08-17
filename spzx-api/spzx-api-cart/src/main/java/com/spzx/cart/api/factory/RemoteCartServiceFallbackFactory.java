package com.spzx.cart.api.factory;

import com.spzx.cart.api.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.domain.R;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoteCartServiceFallbackFactory implements FallbackFactory<RemoteCartService>{


    @Override
    public RemoteCartService create(Throwable cause) {
        return new RemoteCartService() {
            @Override
            public R<List<CartInfo>> getCartCheckedList(Long userId, String source) {
                return R.fail("获得被选中的购物车失败:" + cause.getMessage());
            }

            @Override
            public R<Boolean> updateCartPrice(Long userId, String source) {
                return R.fail("更新购物车价格失败:" + cause.getMessage());
            }

            @Override
            public R<Boolean> deleteCartCheckedList(Long userId, String source) {
                return R.fail("删除购物车失败:" + cause.getMessage());
            }
        };
    }
}


