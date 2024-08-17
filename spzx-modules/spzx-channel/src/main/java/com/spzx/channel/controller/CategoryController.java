package com.spzx.channel.controller;

import com.spzx.channel.service.ICategoryService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.product.api.domain.CategoryVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 商品分类Controller
 *
 * @author ruoyi
 * @date 2024-01-08
 */
@Tag(name = "分类管理")
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController{

    @Autowired
    private ICategoryService categoryService;

    @GetMapping(value = "/tree")
    public AjaxResult tree(){
        List<CategoryVo> categoryVoList = categoryService.tree();
        return success(categoryVoList);
    }
}
