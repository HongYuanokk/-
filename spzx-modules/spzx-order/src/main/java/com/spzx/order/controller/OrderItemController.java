package com.spzx.order.controller;

import java.util.List;
import java.util.Arrays;

import com.github.pagehelper.PageHelper;
import com.spzx.order.domain.OrderInfo;
import com.spzx.order.domain.OrderItem;
import com.spzx.order.service.IOrderInfoService;
import com.spzx.order.service.IOrderItemService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.spzx.common.core.web.page.TableDataInfo;

/**
 * 订单项信息Controller
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Tag(name = "订单项信息接口管理")
@RestController
@RequestMapping("/orderItem")
public class OrderItemController extends BaseController
{
    @Autowired
    private IOrderItemService orderItemService;

    @Autowired
    private IOrderInfoService orderInfoService;

    @Operation(summary = "获取用户订单分页列表")
    @GetMapping("/userOrderInfoList/{pageNum}/{pageSize}")
    public TableDataInfo list(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestParam(required = false, defaultValue = "") OrderInfo orderInfo) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderInfo> list = orderInfoService.selectOrderInfoList(orderInfo);
        return getDataTable(list);
    }


    /**
     * 查询订单项信息列表
     */
    @Operation(summary = "查询订单项信息列表")
    @RequiresPermissions("user:orderItem:list")
    @GetMapping("/list")
    public TableDataInfo list(OrderItem orderItem)
    {
        startPage();
        List<OrderItem> list = orderItemService.selectOrderItemList(orderItem);
        return getDataTable(list);
    }

    /**
     * 导出订单项信息列表
     */
    @Operation(summary = "导出订单项信息列表")
    @RequiresPermissions("user:orderItem:export")
    @Log(title = "订单项信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OrderItem orderItem)
    {
        List<OrderItem> list = orderItemService.selectOrderItemList(orderItem);
        ExcelUtil<OrderItem> util = new ExcelUtil<OrderItem>(OrderItem.class);
        util.exportExcel(response, list, "订单项信息数据");
    }

    /**
     * 获取订单项信息详细信息
     */
    @Operation(summary = "获取订单项信息详细信息")
    @RequiresPermissions("user:orderItem:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(orderItemService.getById(id));
    }

    /**
     * 新增订单项信息
     */
    @Operation(summary = "新增订单项信息")
    @RequiresPermissions("user:orderItem:add")
    @Log(title = "订单项信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OrderItem orderItem)
    {
        return toAjax(orderItemService.save(orderItem));
    }

    /**
     * 修改订单项信息
     */
    @Operation(summary = "修改订单项信息")
    @RequiresPermissions("user:orderItem:edit")
    @Log(title = "订单项信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OrderItem orderItem)
    {
        return toAjax(orderItemService.updateById(orderItem));
    }

    /**
     * 删除订单项信息
     */
    @Operation(summary = "删除订单项信息")
    @RequiresPermissions("user:orderItem:remove")
    @Log(title = "订单项信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(orderItemService.removeBatchByIds(Arrays.asList(ids)));
    }
}
