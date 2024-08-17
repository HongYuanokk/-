package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.domain.Brand;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.mapper.CategoryBrandMapper;
import com.spzx.product.service.ICategoryBrandService;
import com.spzx.product.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper,CategoryBrand> implements ICategoryBrandService {

    @Autowired
    CategoryBrandMapper categoryBrandMapper;

    @Autowired
    ICategoryService categoryService;

    /**
     *查询分页品牌列表
     * @param categoryCategoryBrand
     * @return
     */
    @Override
    public List<CategoryBrand> selectCategoryBrandList(CategoryBrand categoryCategoryBrand) {
        return categoryBrandMapper.selectCategoryBrandList(categoryCategoryBrand);
    }

    @Override
    public CategoryBrand selectCategoryBrandById(Long id) {
        CategoryBrand categoryBrand = this.baseMapper.selectById(id);
        List<Long> categoryIdList = categoryService.getAllCategoryIdList(categoryBrand.getBrandId());
        categoryBrand.setCategoryIdList(categoryIdList);
        return categoryBrand;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertCategoryBrand(CategoryBrand categoryBrand) {
        Long count = categoryBrandMapper.selectCount(new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getBrandId, categoryBrand.getCategoryId()).eq(CategoryBrand::getBrandId, categoryBrand.getBrandId()));
        if (count>0){
            throw new ServiceException("该分类已加该品牌");
        }
        return categoryBrandMapper.insert(categoryBrand);
    }

    @Override
    public int updateCategoryBrand(CategoryBrand categoryBrand) {
        CategoryBrand originalCategoryBrand = this.getById(categoryBrand.getId());
        if(categoryBrand.getCategoryId().longValue() !=originalCategoryBrand.getCategoryId().longValue()||categoryBrand.getBrandId().longValue() != originalCategoryBrand.getBrandId().longValue()){
            categoryBrandMapper.selectCount(new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getBrandId,categoryBrand.getBrandId()).eq(CategoryBrand::getCategoryId,categoryBrand.getCategoryId()));
            if(count()>0){
                throw new ServiceException("该分类已加载该品牌");
            }
        }
        return categoryBrandMapper.updateById(categoryBrand);
    }

    @Override
    public List<Brand> selectBrandListByCategoryId(Long categoryId) {
        return categoryBrandMapper.selectBrandListByCategoryId(categoryId);
    }

    @Override
    public int deleteCategoryBrandByIds(Long[] ids) {
        return baseMapper.deleteById(ids);
    }
}
