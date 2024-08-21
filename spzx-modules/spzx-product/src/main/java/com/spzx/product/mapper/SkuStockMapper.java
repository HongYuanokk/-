package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.api.domain.SkuLockVo;
import com.spzx.product.domain.SkuStock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkuStockMapper extends BaseMapper<SkuStock> {
    void lock(SkuLockVo skuLockVo);

    void unlock(SkuLockVo skuLockVo);

    void minusStock(SkuLockVo skuStockVo);
}
