package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.ProductSpec;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IProductSpecService extends IService<ProductSpec> {
    List<ProductSpec> selectProductSpecList(ProductSpec productSpec);

    ProductSpec selectProductById(Long id);

    List<ProductSpec> selectProductSpecListByCategoryId(Long categoryId);
}
