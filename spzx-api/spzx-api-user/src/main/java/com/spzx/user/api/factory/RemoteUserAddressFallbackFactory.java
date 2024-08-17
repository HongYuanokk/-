package com.spzx.user.api.factory;


import com.spzx.common.core.domain.R;
import com.spzx.user.api.RemoteUserAddressService;
import com.spzx.user.api.domain.UserAddress;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteUserAddressFallbackFactory implements FallbackFactory<RemoteUserAddressService> {
    @Override
    public RemoteUserAddressService create(Throwable cause) {
        return new RemoteUserAddressService() {
            @Override
            public R<UserAddress> getUserAddress(Long id, String source) {
                return R.fail("获取用户地址失败："+cause.getMessage());
            }
        };
    }
}
