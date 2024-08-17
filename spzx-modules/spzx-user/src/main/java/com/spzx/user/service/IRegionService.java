package com.spzx.user.service;

import java.util.List;
import com.spzx.user.domain.Region;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 地区信息Service接口
 *
 * @author atguigu
 * @date 2024-08-06
 */
public interface IRegionService extends IService<Region>
{

    /**
     * 查询地区信息列表
     *
     * @param region 地区信息
     * @return 地区信息集合
     */
    public List<Region> selectRegionList(Region region);

    List<Region> treeSelect(String parentCode);
}
