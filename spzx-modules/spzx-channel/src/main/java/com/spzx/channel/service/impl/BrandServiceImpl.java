package com.spzx.channel.service.impl;

import com.spzx.channel.service.IBrandService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteBrandService;
import com.spzx.product.api.domain.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    RemoteBrandService remoteBrandService;

    @Override
    public List<Brand> getBrandAll() {
        R<List<Brand>> brandAllList = remoteBrandService.getBrandAllList(SecurityConstants.INNER);
        return brandAllList.getData();
    }
}
