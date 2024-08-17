package com.spzx.product.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.domain.CategoryVo;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.factory.RemoteCategoryServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(contextId = "remoteCategoryService",value = ServiceNameConstants.PRODUCT_SERVICE,fallbackFactory = RemoteCategoryServiceFallbackFactory.class)
@Service
public interface RemoteCategoryService {

    @GetMapping(value = "/category/getOneCategory")
    public R<List<CategoryVo>> getOneCategory(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping(value = "/category/tree")
    List<CategoryVo> tree(@RequestHeader(SecurityConstants.FROM_SOURCE) String inner);
}
