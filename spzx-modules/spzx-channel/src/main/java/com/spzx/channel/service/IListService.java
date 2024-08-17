package com.spzx.channel.service;

import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuQuery;

import java.util.List;

public interface IListService {

    TableDataInfo skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery);
}
