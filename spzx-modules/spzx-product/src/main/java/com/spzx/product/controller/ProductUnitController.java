package com.spzx.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.service.IProductUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品单位Controller
 */
@Tag(name = "商品单位接口管理")
@RestController
@RequestMapping("/productUnit")
public class ProductUnitController extends BaseController {

    @Autowired
    private IProductUnitService productUnitService;

    /**
     * 获取分页列表
     */
    @Operation(summary = "获取分页列表")
    @GetMapping("/list")
    public TableDataInfo findPage(
        @Parameter(name = "pageNum", description = "当前页码", required = true)
        @RequestParam(value = "pageNum", defaultValue = "0", required = true) Integer pageNum,

        @Parameter(name = "pageSize", description = "每页记录数", required = true)
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,

        @Parameter(name = "driverInfoQuery", description = "查询对象", required = false)
        ProductUnit productUnit) {
        Page<ProductUnit> pageParam = new Page<>(pageNum, pageSize);
        IPage<ProductUnit> iPage = productUnitService.selectProductUnitPage(pageParam, productUnit);
        return getDataTable(iPage);
    }

    /**
     * 获取商品单位详细信息
     */
    @Operation(summary = "获取商品详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productUnitService.selectProductUnitById(id));
    }


    /**
     *新增商品单位
     */
    @Operation(summary = "新增商品单位")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated ProductUnit productUnit) {
        productUnit.setCreateBy(SecurityUtils.getUsername());
        return toAjax(productUnitService.insertProductUnit(productUnit));
    }


    /**
     *修改商品
     */
    @Operation(summary = "修改商品")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated ProductUnit productUnit) {
        productUnit.setCreateBy(SecurityUtils.getUsername());
        return toAjax(productUnitService.updateProductUnit(productUnit));
    }

    /**
     * 删除商品
     */
    @Operation(summary = "删除商品")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(productUnitService.deleteProductUntiByIds(ids));
    }

    /**
     * 获取全部单位
     */
    @Operation(summary = "获取全部单位")
    @GetMapping("getUnitAll")
    public AjaxResult selectProductUnitAll() {
        return success(productUnitService.list());
    }

}