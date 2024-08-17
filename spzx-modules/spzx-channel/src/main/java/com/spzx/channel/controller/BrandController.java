package com.spzx.channel.controller;


import com.spzx.channel.service.IBrandService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.product.api.domain.Brand;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "品牌管理")
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {

    @Autowired
    private IBrandService brandService;


    @GetMapping("/getBrandAll")
    public AjaxResult selectBrandAll(){
            List<Brand> getBrandAllList = brandService.getBrandAll();
        return success(getBrandAllList);
    }
}
