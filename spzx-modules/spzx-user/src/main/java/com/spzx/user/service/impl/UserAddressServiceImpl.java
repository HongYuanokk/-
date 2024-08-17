package com.spzx.user.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spzx.user.mapper.UserAddressMapper;
import com.spzx.user.api.domain.UserAddress;
import com.spzx.user.service.IUserAddressService;

/**
 * 用户地址Service业务层处理
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService
{
    @Autowired
    private UserAddressMapper userAddressMapper;

    /**
     * 查询用户地址列表
     *
     * @param userAddress 用户地址
     * @return 用户地址
     */
    @Override
    public List<UserAddress> selectUserAddressList(UserAddress userAddress)
    {
        return userAddressMapper.selectUserAddressList(userAddress);
    }

}
