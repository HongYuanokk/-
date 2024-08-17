package com.spzx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.utils.bean.BeanUtils;
import com.spzx.product.api.domain.CategoryVo;
import com.spzx.product.domain.Category;
import com.spzx.product.helper.CategoryHelper;
import com.spzx.product.mapper.CategoryMapper;
import com.spzx.product.service.ICategoryService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Override
    public List<Category> treeSelect(Long parentId) {

        List<Category> categoryList = baseMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getParentId, parentId));

        for (Category category : categoryList) {
            QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
            categoryQueryWrapper.eq("parent_id", category.getId());
            Long count = baseMapper.selectCount(categoryQueryWrapper);
            category.setHasChildren(count > 0);
        }

        return categoryList;
    }

    @Override
    public List<Long> getAllCategoryIdList(Long category3Id) {
        List<Long> list = new ArrayList<>();

        ArrayList<Category> car = new ArrayList<>();
        List<Category> categoryList = getParentCategory(category3Id, car);

        for (Category category : categoryList) {
            list.add(category.getId());
        }
        return list;


    }



    /**
     * 获取一级分类
     * 从数据库中查询出所有父ID为0的类别，并将查询结果转换为CategoryVo对象的列表
     * @return
     */
    @Override
    public List<CategoryVo> getOneCategory() {
        List<Category> categoryList = baseMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getParentId, 0));
        List<CategoryVo> categoryVoList = categoryList.stream().map(category -> {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(category,categoryVo);
            return categoryVo;
        }).collect(Collectors.toList());
        return categoryVoList;
    }


    @Override
    public List<CategoryVo> tree() {
        List<Category> categoryList = baseMapper.selectList(null);
        List<CategoryVo> categoryVos = categoryList.stream().map(category -> {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(category,categoryVo);
            return categoryVo;
        }).collect(Collectors.toList());

        //将平行的分类数据处理成tree树装菜单
        List<CategoryVo> tree = CategoryHelper.buildTree(categoryVos);
        return tree;
    }


    private List<Category> getParentCategory(Long categoryId, ArrayList<Category> car) {
        if (categoryId > 0) {
            QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
            categoryQueryWrapper.eq("id", categoryId);
            Category category = baseMapper.selectOne(categoryQueryWrapper);
            car.add(category);
            getParentCategory(category.getParentId(), car);
        }
        return car;
    }
}
