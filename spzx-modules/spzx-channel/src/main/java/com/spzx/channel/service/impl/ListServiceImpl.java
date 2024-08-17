package com.spzx.channel.service.impl;

import com.spzx.channel.service.IListService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListServiceImpl implements IListService {

    @Autowired
    private RemoteProductService remoteProductService;


    @Override
    public TableDataInfo skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery) {
        R<TableDataInfo> rTable =  remoteProductService.skuList(pageNum,pageSize,skuQuery,SecurityConstants.INNER);
        return rTable.getData();
    }
}
