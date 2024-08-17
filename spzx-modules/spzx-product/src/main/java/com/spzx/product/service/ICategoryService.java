package com.spzx.product.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.api.domain.CategoryVo;
import com.spzx.product.domain.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ICategoryService extends IService<Category> {
    /**
     * 获取分类下拉树列表
     * @param
     * @return
     */
    List<Category> treeSelect(Long parentId);

    List<Long> getAllCategoryIdList(Long categoryId);


    List<CategoryVo> getOneCategory();

    List<CategoryVo> tree();
}
