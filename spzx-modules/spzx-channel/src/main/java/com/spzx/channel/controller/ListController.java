package com.spzx.channel.controller;


import com.spzx.channel.service.IListService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.SkuQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/list")
public class ListController extends BaseController {

    @Autowired
    private IListService listService;

    @GetMapping(value = "/skuList/{pageNum}/{pageSize}")
    public TableDataInfo skuList(@PathVariable Integer pageNum,@PathVariable Integer pageSize,SkuQuery skuQuery){
        TableDataInfo tableDataInfo = listService.skuList(pageNum,pageSize,skuQuery);
        return tableDataInfo;
    }

}
