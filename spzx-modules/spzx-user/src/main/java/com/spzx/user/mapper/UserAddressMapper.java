package com.spzx.user.mapper;

import java.util.List;
import com.spzx.user.api.domain.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户地址Mapper接口
 *
 * @author atguigu
 * @date 2024-08-06
 */
public interface UserAddressMapper extends BaseMapper<UserAddress>
{

    /**
     * 查询用户地址列表
     *
     * @param userAddress 用户地址
     * @return 用户地址集合
     */
    public List<UserAddress> selectUserAddressList(UserAddress userAddress);

}
