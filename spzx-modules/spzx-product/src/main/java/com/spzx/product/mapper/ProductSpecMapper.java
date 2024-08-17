package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.ProductSpec;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductSpecMapper extends BaseMapper<ProductSpec> {

    List<ProductSpec> selectProductSpectList(ProductSpec productSpec);
}
