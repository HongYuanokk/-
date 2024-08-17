package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.Brand;
import com.spzx.product.domain.CategoryBrand;

import java.util.List;

public interface ICategoryBrandService extends IService<CategoryBrand> {
    List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand);

    CategoryBrand selectCategoryBrandById(Long id);

    int insertCategoryBrand(CategoryBrand categoryBrand);

    int updateCategoryBrand(CategoryBrand categoryBrand);

    List<Brand> selectBrandListByCategoryId(Long categoryId);

    int deleteCategoryBrandByIds(Long[] ids);
}
