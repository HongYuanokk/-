package com.spzx.product.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.domain.CategoryVo;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoteCategoryServiceFallbackFactory implements FallbackFactory<RemoteCategoryService> {
    @Override
    public RemoteCategoryService create(Throwable cause) {

        return new RemoteCategoryService(){

            @Override
            public R<List<CategoryVo>> getOneCategory(String inner) {
                System.out.println("RemoteCategoryService,getOneCategory接口异常：" + cause.getMessage() );
                return R.fail(cause.getMessage());
            }

            @Override
            public List<CategoryVo> tree(String inner) {
                System.out.println("RemoteCategoryService,tree接口异常：" + cause.getMessage() );
                return null;
            }
        };
    }
}
