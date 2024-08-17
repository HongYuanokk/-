package com.spzx.product.controller;


import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.api.domain.Brand;
import com.spzx.product.service.IBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.spzx.common.core.utils.PageUtils.startPage;

@Tag(name = "品牌接口管理")
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController
{
    @Autowired
    private IBrandService brandService;


   @InnerAuth
   @Operation(summary = "获取全部品牌")
   @GetMapping("getBrandAllList")
   public R<List<Brand>> getBrandAllList(){
       return R.ok(brandService.selectBrandAll());
   }



    /**
     * 查询品牌列表
     */
    @Operation(summary = "查询品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(Brand brand)
    {
        startPage();
        List<Brand> list = brandService.selectBrandList(brand);
        return getDataTable(list);
    }

    /**
     * 获取品牌详细信息
     */
    @Operation(summary = "获取品牌详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
            return success(brandService.selectBrandById(id));
    }

    /**
     * 新增商品
     */
    @Operation(summary = "新增商品")
    @PostMapping
    public AjaxResult add(@RequestBody  @Validated Brand brand) {
        brand.setCreateBy(SecurityUtils.getUsername());
        int i = brandService.insertBrand(brand);
        return toAjax(i);
    }

    /**
     * 修改品牌
     */
    @Operation(summary = "修改品牌")
    @RequiresPermissions("product:brand:edit")
    @PutMapping
    public AjaxResult update(@RequestBody  @Validated Brand brand) {
        brand.setCreateBy(SecurityUtils.getUsername());
        int i = brandService.setUpdateBy(brand);
        return toAjax(i);
    }

    /**
     * 删除品牌
     */
    @Operation(summary = "删除品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(brandService.deleteBrandByIds(ids));
    }

    /**
     * 获取全部品牌
     */

    @Operation(summary = "获取全部品牌")
    @GetMapping("getBrandAll")
    public AjaxResult getBrandAll() {
        return success(brandService.selectBrandAll());
    }

}