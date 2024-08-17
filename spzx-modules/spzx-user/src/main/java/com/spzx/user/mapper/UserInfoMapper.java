package com.spzx.user.mapper;

import java.util.List;
import com.spzx.user.api.domain.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 会员Mapper接口
 *
 * @author atguigu
 * @date 2024-08-06
 */
public interface UserInfoMapper extends BaseMapper<UserInfo>
{

    /**
     * 查询会员列表
     *
     * @param userInfo 会员
     * @return 会员集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

}
