package com.spzx.product.service;

import com.spzx.product.api.domain.Brand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IBrandService {
    List<Brand> selectBrandList(Brand brand);

    Brand selectBrandById(Long id);

    int insertBrand(Brand brand);

    int setUpdateBy(Brand brand);

    int deleteBrandByIds(Long[] ids);

    /**
     * 获取全部品牌
     * @return
     */
    public List<Brand> selectBrandAll();
}
