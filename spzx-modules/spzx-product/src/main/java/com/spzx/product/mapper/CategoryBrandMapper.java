package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.api.domain.Brand;
import com.spzx.product.domain.CategoryBrand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryBrandMapper extends BaseMapper<CategoryBrand> {
    List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryBrand);

    List<Brand> selectBrandListByCategoryId(@Param("categoryId") Long categoryId);
}
