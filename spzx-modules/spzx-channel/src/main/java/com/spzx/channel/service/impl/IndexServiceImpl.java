package com.spzx.channel.service.impl;

import com.spzx.channel.service.IIndexService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.CategoryVo;
import com.spzx.product.api.domain.ProductSku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class IndexServiceImpl implements IIndexService {
    @Autowired
    private RemoteCategoryService remoteCategoryService;

    @Autowired
    private RemoteProductService remoteProductService;



    @Override
    public Map<String, Object> getIndexData() {

        //查询一级分类
        R<List<CategoryVo>> categoryR = remoteCategoryService.getOneCategory(SecurityConstants.INNER);
        if (R.FAIL == categoryR.getCode()) {
            throw new ServiceException(categoryR.getMsg());
        }

        //查看商品列表
        R<List<ProductSku>> productSkuR = remoteProductService.getTopSale(SecurityConstants.INNER);
        if (R.FAIL == productSkuR.getCode()) {
            throw new ServiceException(productSkuR.getMsg());
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("categoryList",categoryR.getData());
        map.put("productSkuList",productSkuR.getData());


        return map;
    }
}
