package com.spzx.channel.service;

import com.spzx.channel.domain.ItemVo;

public interface IItemService {
    ItemVo item(Long skuId);

    ItemVo itembak(Long skuId);
}
