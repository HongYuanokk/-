package com.spzx.product.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
