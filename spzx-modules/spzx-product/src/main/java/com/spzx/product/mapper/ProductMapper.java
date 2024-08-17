package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.api.domain.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    List<Product> selectProductList(Product product);

//    List<ProductSku> getTopSale();
}
