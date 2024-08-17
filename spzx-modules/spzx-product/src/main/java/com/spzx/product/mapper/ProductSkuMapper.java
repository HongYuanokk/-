package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {
    List<ProductSku> selectTopSale();

    List<ProductSku> skuList(SkuQuery skuQuery);
}
