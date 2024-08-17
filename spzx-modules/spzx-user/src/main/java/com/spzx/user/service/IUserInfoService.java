package com.spzx.user.service;

import java.util.List;

import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.api.domain.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员Service接口
 *
 * @author atguigu
 * @date 2024-08-06
 */
public interface IUserInfoService extends IService<UserInfo>
{

    /**
     * 查询用户地址列表
     *
     * @param userId 用户地址
     * @return 用户地址集合
     */
    public List<UserAddress> selectUserAddressList(Long userId);

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

    void register(UserInfo userInfo);

    UserInfo selectUserByUserName(String username);

    Boolean updateUserLogin(UpdateUserLogin updateUserLogin);
}
