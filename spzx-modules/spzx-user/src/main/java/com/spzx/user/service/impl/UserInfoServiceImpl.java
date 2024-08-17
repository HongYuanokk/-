package com.spzx.user.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spzx.user.mapper.UserInfoMapper;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.service.IUserInfoService;
import org.springframework.util.Assert;

/**
 * 会员Service业务层处理
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService
{
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;


    /**
     * 查询用户地址列表
     *
     * @param userId 用户地址
     * @return 用户地址
     */
    @Override
    public List<UserAddress> selectUserAddressList(Long userId)
    {
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId));
    }

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员
     */
    @Override
    public List<UserInfo> selectUserInfoList(UserInfo userInfo)
    {
        return userInfoMapper.selectUserInfoList(userInfo);
    }

    @Override
    public void register(UserInfo userInfo) {
        long count = userInfoMapper.selectCount(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, userInfo.getUsername()));
        Assert.isTrue(count<=0, "用户名已存在");
        userInfo.setStatus(1);
        userInfo.setSex(0);
        userInfo.setAvatar("http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoj0hHXhgJNOTSOFsS4uZs8x1ConecaVOB8eIl115xmJZcT4oCicvia7wMEufibKtTLqiaJeanU2Lpg3w/132");
        userInfoMapper.insert(userInfo);
    }

    @Override
    public UserInfo selectUserByUserName(String username) {
        return userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, username));
    }

    @Override
    public Boolean updateUserLogin(UpdateUserLogin updateUserLogin) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(updateUserLogin.getUserId());
        userInfo.setLastLoginIp(updateUserLogin.getLastLoginIp());
        userInfo.setLastLoginTime(updateUserLogin.getLastLoginTime());
        userInfoMapper.updateById(userInfo);
        return true;
    }
}
