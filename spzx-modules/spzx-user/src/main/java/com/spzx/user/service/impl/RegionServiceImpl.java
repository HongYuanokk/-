package com.spzx.user.service.impl;

import java.util.List;
import java.util.Arrays;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spzx.user.mapper.RegionMapper;
import com.spzx.user.domain.Region;
import com.spzx.user.service.IRegionService;

/**
 * 地区信息Service业务层处理
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements IRegionService
{
    @Autowired
    private RegionMapper regionMapper;

    /**
     * 查询地区信息列表
     *
     * @param region 地区信息
     * @return 地区信息
     */
    @Override
    public List<Region> selectRegionList(Region region)
    {
        return regionMapper.selectRegionList(region);
    }

    @Override
    public List<Region> treeSelect(String parentCode) {
        return regionMapper.selectList(new LambdaQueryWrapper<Region>().eq(Region::getParentCode, parentCode));
    }

}
