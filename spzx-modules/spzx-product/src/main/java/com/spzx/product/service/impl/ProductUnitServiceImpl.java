package com.spzx.product.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.mapper.ProductUnitMapper;
import com.spzx.product.service.IProductUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper, ProductUnit> implements IProductUnitService {

    @Autowired
    ProductUnitMapper productUnitMapper;

    /**
     * 查询商品分页列表
     * @param pageParam
     * @param productUnit
     * @return
     */
    @Override
    public IPage<ProductUnit> selectProductUnitPage(Page<ProductUnit> pageParam, ProductUnit productUnit) {
        return productUnitMapper.selectProductUnitPage(pageParam,productUnit);
    }

    /**
     *查询商品单位
     * @param id
     * @return
     */
    @Override
    public ProductUnit selectProductUnitById(Long id) {
        return this.getById(id);
    }

    @Override
    public int insertProductUnit(ProductUnit productUnit) {
        return this.save(productUnit) ? 1 : 0;
    }

    @Override
    public int updateProductUnit(ProductUnit productUnit) {
        return this.updateById(productUnit) ? 1 : 0;
    }

    @Override
    public int deleteProductUntiByIds(Long[] ids) {
        return this.removeBatchByIds(Arrays.asList(ids)) ? 1:0;
    }




}
