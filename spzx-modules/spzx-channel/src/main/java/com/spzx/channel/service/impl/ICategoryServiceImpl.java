package com.spzx.channel.service.impl;

import com.spzx.channel.service.ICategoryService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.domain.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ICategoryServiceImpl implements ICategoryService {

    @Autowired
    private RemoteCategoryService remoteCategoryService;

    @Override
    public List<CategoryVo> tree() {
        List<CategoryVo> categoryVoList = new ArrayList<>();
        categoryVoList = remoteCategoryService.tree(SecurityConstants.INNER);
        return categoryVoList;
    }
}
