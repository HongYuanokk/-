package com.spzx.product.service.impl;

import com.spzx.product.api.domain.Brand;
import com.spzx.product.mapper.BrandMapper;
import com.spzx.product.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    BrandMapper brandMapper;

    @Override
    public List<Brand> selectBrandList(Brand brand) {
        List<Brand> brands = brandMapper.selectBrandList(brand);
        return brands ;
    }

    @Override
    public Brand selectBrandById(Long id) {
        Brand brand =brandMapper.selectBrandById(id);
        return brand;
    }

    @Override
    public int insertBrand(Brand brand) {
        int i = brandMapper.insertBrand(brand);
        return i;
    }

    @Override
    public int setUpdateBy(Brand brand) {
        int i = brandMapper.updateBrand(brand);
        return i;
    }

    @Override
    public int deleteBrandByIds(Long[] ids) {
        int i = brandMapper.deleteBrandById(ids);
        return i;
    }

    @Override
    public List<Brand> selectBrandAll() {
        return brandMapper.selectBrandList(null);
    }
}
