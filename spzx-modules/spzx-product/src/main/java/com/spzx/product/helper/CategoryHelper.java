package com.spzx.product.helper;

import com.spzx.product.api.domain.CategoryVo;

import java.util.ArrayList;
import java.util.List;

public class CategoryHelper {



    /**
     * 使用递归方法建分类树
     * @param
     * @return
     */
    public static List< CategoryVo> buildTree(List<CategoryVo> categoryVos) {
        List<CategoryVo> tree = new ArrayList<>();

        //做树
        for (CategoryVo categoryVo : categoryVos) {
            Long parentId = categoryVo.getParentId();
            if(parentId.longValue() == 0){//一级分类
                tree.add(findChilren(categoryVo,categoryVos));
            }
        }
        return tree;
    }


    private static CategoryVo findChilren(CategoryVo categoryVo, List<CategoryVo> categoryVos) {
        //帮助categoryVo找儿子
        for (CategoryVo child : categoryVos) {      //选取categoryVos列表中的每一个元素，并将其赋值给child变量
            if(child.getParentId().longValue() == categoryVo.getId().longValue()){
                    categoryVo.getChildren().add(findChilren(child,categoryVos));
            }
        }
        return categoryVo;
    }


}
