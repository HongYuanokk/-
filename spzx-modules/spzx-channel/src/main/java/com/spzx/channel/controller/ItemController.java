package com.spzx.channel.controller;

import com.spzx.channel.domain.ItemVo;
import com.spzx.channel.service.IItemService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "商品详情接口")
@RestController
@RequestMapping("/item")
public class ItemController extends BaseController {

    @Autowired
    private IItemService iItemService;


    @Operation(summary = "商品详情")
    @GetMapping("/{skuId}")
    public AjaxResult item(@PathVariable Long skuId){
        ItemVo itemVo = iItemService.item(skuId);
        return success(itemVo);
    }
}
