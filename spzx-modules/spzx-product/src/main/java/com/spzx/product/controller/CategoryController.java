package com.spzx.product.controller;

import com.alibaba.excel.EasyExcel;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.product.api.domain.CategoryVo;
import com.spzx.product.domain.Category;
import com.spzx.product.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类接口管理
 */
@Tag(name = "商品分类接口管理")
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {

    @Autowired
    ICategoryService categoryService;

    @InnerAuth
    @GetMapping("/tree")
    public List<CategoryVo> tree() {
        return categoryService.tree();
    }

    /**
     * 获取一级分类列表
     * @return
     */
    @InnerAuth
    @GetMapping(value = "/getOneCategory")
    public R<List<CategoryVo>> getOneCategory(){
        return R.ok(categoryService.getOneCategory());
    }


    @Operation(summary = "获取分类下拉树列表")
    @GetMapping(value = "/treeSelect/{id}")
    public AjaxResult treeSelect(@PathVariable Long id){
        return success(categoryService.treeSelect(id));
    }

    @PostMapping("export")
    @SneakyThrows
    public void  export(HttpServletResponse response){
        List<Category> list = categoryService.list();
        EasyExcel.write(response.getOutputStream(), Category.class).sheet("商品分类").doWrite(list);
    }
}