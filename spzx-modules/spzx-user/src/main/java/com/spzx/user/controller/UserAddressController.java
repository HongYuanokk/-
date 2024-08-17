package com.spzx.user.controller;

import java.util.List;
import java.util.Arrays;

import com.spzx.common.core.domain.R;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.user.service.IRegionService;
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
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.service.IUserAddressService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.spzx.common.core.web.page.TableDataInfo;

/**
 * 用户地址Controller
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Tag(name = "用户地址接口管理")
@RestController
@RequestMapping("/userAddress")
public class UserAddressController extends BaseController
{
    @Autowired
    private IUserAddressService userAddressService;

    @Autowired
    private IRegionService regionService;



    @InnerAuth
    @GetMapping(value = "/getUserAddress/{id}")
    public R<UserAddress> getUserAddress(@PathVariable("id") Long id)
    {
        return R.ok(userAddressService.getById(id));
    }


    @GetMapping(value = "/treeSelect/{parentCode}")
    public AjaxResult treeSelect(@PathVariable String parentCode) {
        return success(regionService.treeSelect(parentCode));
    }

    /**
     * 查询用户地址列表
     */
    @Operation(summary = "查询用户地址列表")
    @GetMapping("/list")
    public AjaxResult list()
    {
        List<UserAddress> list = userAddressService.selectUserAddressList(new UserAddress());
        return success(list);
    }


    /**
     * 获取用户地址详细信息
     */
    @Operation(summary = "获取用户地址详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(userAddressService.getById(id));
    }

    /**
     * 新增用户地址
     */
    @Operation(summary = "新增用户地址")
    @Log(title = "用户地址", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserAddress userAddress)
    {
        return toAjax(userAddressService.save(userAddress));
    }

    /**
     * 修改用户地址
     */
    @Operation(summary = "修改用户地址")
    @Log(title = "用户地址", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserAddress userAddress)
    {
        return toAjax(userAddressService.updateById(userAddress));
    }

    /**
     * 删除用户地址
     */
    @Operation(summary = "删除用户地址")
    @Log(title = "用户地址", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(userAddressService.removeBatchByIds(Arrays.asList(ids)));
    }
}
