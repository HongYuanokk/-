package com.spzx.user.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.api.factory.RemoteUserAddressFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(contextId = "remoteUserAddressService",value = ServiceNameConstants.USER_SERVICE,fallbackFactory = RemoteUserAddressFallbackFactory.class)
public interface RemoteUserAddressService {
    @GetMapping(value = "/userAddress/getUserAddress/{id}")
    public R<UserAddress> getUserAddress(@PathVariable("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE)String source);
}
