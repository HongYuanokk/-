package com.spzx.product.api;


import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.domain.Brand;
import com.spzx.product.api.factory.RemoteBrandFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;


@FeignClient(contextId = "remoteBrandService",value = ServiceNameConstants.PRODUCT_SERVICE,fallbackFactory = RemoteBrandFallbackFactory.class)
public interface RemoteBrandService {

    @GetMapping("/brand/getBrandAllList")
    public R<List<Brand>> getBrandAllList(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
 }
