package com.spzx.user.controller;

import java.util.List;
import java.util.Arrays;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.user.domain.Region;
import com.spzx.user.service.IRegionService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.spzx.common.core.web.page.TableDataInfo;

/**
 * 地区信息Controller
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Tag(name = "地区信息接口管理")
@RestController
@RequestMapping("/region")
public class RegionController extends BaseController
{
    @Autowired
    private IRegionService regionService;

    @GetMapping(value = "/treeSelect/{parentCode}")
    public AjaxResult treeSelect(@PathVariable String parentCode){
        return success(regionService.treeSelect(parentCode));
    }


    /**
     * 查询地区信息列表
     */
    @Operation(summary = "查询地区信息列表")
    @RequiresPermissions("user:region:list")
    @GetMapping("/list")
    public TableDataInfo list(Region region)
    {
        startPage();
        List<Region> list = regionService.selectRegionList(region);
        return getDataTable(list);
    }

    /**
     * 导出地区信息列表
     */
    @Operation(summary = "导出地区信息列表")
    @RequiresPermissions("user:region:export")
    @Log(title = "地区信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Region region)
    {
        List<Region> list = regionService.selectRegionList(region);
        ExcelUtil<Region> util = new ExcelUtil<Region>(Region.class);
        util.exportExcel(response, list, "地区信息数据");
    }

    /**
     * 获取地区信息详细信息
     */
    @Operation(summary = "获取地区信息详细信息")
    @RequiresPermissions("user:region:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(regionService.getById(id));
    }

    /**
     * 新增地区信息
     */
    @Operation(summary = "新增地区信息")
    @RequiresPermissions("user:region:add")
    @Log(title = "地区信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Region region)
    {
        return toAjax(regionService.save(region));
    }

    /**
     * 修改地区信息
     */
    @Operation(summary = "修改地区信息")
    @RequiresPermissions("user:region:edit")
    @Log(title = "地区信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Region region)
    {
        return toAjax(regionService.updateById(region));
    }

    /**
     * 删除地区信息
     */
    @Operation(summary = "删除地区信息")
    @RequiresPermissions("user:region:remove")
    @Log(title = "地区信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(regionService.removeBatchByIds(Arrays.asList(ids)));
    }
}
