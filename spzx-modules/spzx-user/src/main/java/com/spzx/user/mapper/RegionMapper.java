package com.spzx.user.mapper;

import java.util.List;
import com.spzx.user.domain.Region;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 地区信息Mapper接口
 *
 * @author atguigu
 * @date 2024-08-06
 */
public interface RegionMapper extends BaseMapper<Region>
{

    /**
     * 查询地区信息列表
     *
     * @param region 地区信息
     * @return 地区信息集合
     */
    public List<Region> selectRegionList(Region region);

}
