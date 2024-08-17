package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.product.domain.ProductUnit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductUnitMapper extends BaseMapper<ProductUnit> {

    IPage<ProductUnit> selectProductUnitPage(Page<ProductUnit> pageParam, @Param("query") ProductUnit productUnit);
}
