package com.spzx.user.controller;

import java.util.List;
import java.util.Arrays;


import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.utils.poi.ExcelUtil;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserAddress;


import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.domain.UserInfoVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
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
import com.spzx.user.service.IUserInfoService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.spzx.common.core.web.page.TableDataInfo;

/**
 * 会员Controller
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Tag(name = "会员接口管理")
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends BaseController
{
    @Autowired
    private IUserInfoService userInfoService;

    @Operation(summary = "获取当前登录用户信息")
    @InnerAuth
    @GetMapping("/getLoginUserInfo")
    public AjaxResult getLoginUserInfo(HttpServletRequest request){
        Long userId = SecurityContextHolder.getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo,userInfoVo);
        return success(userInfo);
    }


    @Operation(summary = "根据用户名获取用户信息")
    @InnerAuth
    @GetMapping("/info/{username}")
    public R<UserInfo> getUserInfo(@PathVariable("username") String username)
    {
        UserInfo userInfo = userInfoService.selectUserByUserName(username);
        return R.ok(userInfo);
    }

    @Operation(summary = "更新用户登录信息")
    @InnerAuth
    @PutMapping("/updateUserLogin")
    public R<Boolean> updateUserLogin(@RequestBody UpdateUserLogin updateUserLogin)
    {
        return R.ok(userInfoService.updateUserLogin(updateUserLogin));
    }


    @InnerAuth
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody UserInfo userInfo)
    {
        userInfoService.register(userInfo);
        return R.ok();
    }

    /**
     * 查询会员列表
     */
    @Operation(summary = "查询会员列表")
    //@RequiresPermissions("user:userInfo:list")
    @GetMapping("/list")
    public TableDataInfo list(UserInfo userInfo)
    {
        startPage();
        List<UserInfo> list = userInfoService.selectUserInfoList(userInfo);
        return getDataTable(list);
    }

    @Operation(summary = "获取用户地址")
    @RequiresPermissions("user:info:query")
    @GetMapping("getUserAddress/{userId}")
    public AjaxResult getUserAddress(@PathVariable Long userId) {
        List<UserAddress> userAddressList = userInfoService.selectUserAddressList(userId);
        return success(userAddressList);
    }


    /**
     * 导出会员列表
     */
    @Operation(summary = "导出会员列表")
    //@RequiresPermissions("user:userInfo:export")
    @Log(title = "会员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserInfo userInfo)
    {
        List<UserInfo> list = userInfoService.selectUserInfoList(userInfo);
        ExcelUtil<UserInfo> util = new ExcelUtil<UserInfo>(UserInfo.class);
        util.exportExcel(response, list, "会员数据");
    }

    /**
     * 获取会员详细信息
     */
    @Operation(summary = "获取会员详细信息")
//    @RequiresPermissions("user:userInfo:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(userInfoService.getById(id));
    }

    /**
     * 新增会员
     */
    @Operation(summary = "新增会员")
//    @RequiresPermissions("user:userInfo:add")
    @Log(title = "会员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserInfo userInfo)
    {
        return toAjax(userInfoService.save(userInfo));
    }

    /**
     * 修改会员
     */
    @Operation(summary = "修改会员")
//    @RequiresPermissions("user:userInfo:edit")
    @Log(title = "会员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserInfo userInfo)
    {
        return toAjax(userInfoService.updateById(userInfo));
    }

    /**
     * 删除会员
     */
    @Operation(summary = "删除会员")
//    @RequiresPermissions("user:userInfo:remove")
    @Log(title = "会员", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(userInfoService.removeBatchByIds(Arrays.asList(ids)));
    }
}
