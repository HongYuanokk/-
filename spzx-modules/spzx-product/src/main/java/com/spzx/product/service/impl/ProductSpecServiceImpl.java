package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.mapper.ProductSpecMapper;
import com.spzx.product.service.ICategoryService;
import com.spzx.product.service.IProductSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper,ProductSpec> implements IProductSpecService {

    @Autowired
    ProductSpecMapper productSpecMapper;

    @Autowired
    ICategoryService categoryService;

    @Override
    public List<ProductSpec> selectProductSpecList(ProductSpec productSpec) {
        return productSpecMapper.selectProductSpectList(productSpec);
    }

    @Override
    public ProductSpec selectProductById(Long id) {
        ProductSpec productSpec = productSpecMapper.selectById(id);
        List<Long> categoryIdList = categoryService.getAllCategoryIdList(productSpec.getCategoryId()); // 2. 获取与 ProductSpec 关联的类别 ID 列表
        productSpec.setCategoryIdList(categoryIdList); // 3. 设置 ProductSpec 对象的 categoryIdList 属性
        return productSpec; // 4. 返回 ProductSpec 对象
    }

    @Override
    public List<ProductSpec> selectProductSpecListByCategoryId(Long categoryId) {
        return productSpecMapper.selectList(new LambdaQueryWrapper<ProductSpec>().eq(ProductSpec::getCategoryId,categoryId));
    }
}
