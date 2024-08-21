package com.spzx.product.controller;


import com.github.pagehelper.PageHelper;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.product.api.domain.*;
import com.spzx.product.service.ICategoryBrandService;
import com.spzx.product.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品Controller
 */
@Tag(name ="商品管理")
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    IProductService productService;

    @Autowired
    ICategoryBrandService categoryBrandService;

    @InnerAuth
    @Operation(summary = "检查与锁定库存")
    @PostMapping("checkAndLock/{orderNo}")
    public R<String> checkAndLock(@PathVariable String tradeNo, @RequestBody List<SkuLockVo> skuLockVoList) {
        try {
            return R.ok(productService.checkAndLock(tradeNo, skuLockVoList));
        } catch (ServiceException e) {
            e.printStackTrace();
            return R.ok(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.ok("库存不足");
        }
    }


    @Operation(summary = "批量获取商品sku最新价格信息")
    @InnerAuth
    @PostMapping(value = "/getSkuPriceList")
    public R<List<SkuPrice>> getSkuPriceList(@RequestBody List<Long> skuList){
        return R.ok(productService.getSkuPriceList(skuList));
    }



    @Operation(summary = "获取商品sku规则详细信息")
    @InnerAuth
    @GetMapping(value = "/getSkuSpecValue/{id}")
    public R<Map<String, Long>> getSkuSpecValue(@PathVariable("id") Long id)
    {
        return R.ok(productService.getSkuSpecValue(id));
    }



    @InnerAuth
    @Operation(summary = "获取商品库存")
    @GetMapping(value = "/getSkuStock/{skuId}")
    public R<SkuStockVo> getSkuStock(@PathVariable("skuId") Long skuId){
        SkuStockVo stockVo = productService.getSkuStock(skuId);
        return R.ok(stockVo);
    }


    @InnerAuth
    @Operation(summary = "获取商品详细信息")
    @GetMapping(value = "/getProductDetails/{id}")
    public R<ProductDetails> getProductDetails(@PathVariable Long id){
        ProductDetails productDetails = productService.getProductDetails(id);
        return R.ok(productDetails);
    }


    @Operation(summary = "获取商品sku最新价格信息")
    @InnerAuth
    @GetMapping(value = "/getSkuPrice/{skuId}")
    public R<SkuPrice> getSkuPrice(@PathVariable("skuId") Long skuId)
    {
        SkuPrice skuPrice = productService.getSkuPrice(skuId);
        return R.ok(skuPrice);
    }


    @Operation(summary = "获取商品")
    @InnerAuth
    @GetMapping(value = "/getProduct/{id}")
    public  R<Product> getProduct(@PathVariable Long id){
        Product product = productService.getProduct(id);
        return R.ok(product);
    }


    @InnerAuth
    @Operation(summary = "获取商品Sku信息")
    @GetMapping("/getProductSku/{skuId}")
    public R<ProductSku> getProductSku(@PathVariable Long skuId){
        ProductSku productSku = productService.getProductSku(skuId);
        return R.ok(productSku);
    }


    @InnerAuth
    @GetMapping("/skuList/{pageNum}/{pageSize}")
    public R<TableDataInfo> skuList(@PathVariable Integer pageNum,@PathVariable Integer pageSize,SkuQuery skuQuery){
        PageHelper.startPage(pageNum,pageSize);
        List<ProductSku> productSkus = productService.skuList(skuQuery);
        return R.ok(getDataTable(productSkus));
    }


    @InnerAuth
    @Operation(summary = "获取销量好的sku")
    @GetMapping("getTopSale")
    public R<List<ProductSku>> getTopSale(){
        return R.ok(productService.getTopSale());
    }

    /**
     * 查询商品列表
     */
    @Operation(summary = "查询商品列表")
    @GetMapping("/list")
    public TableDataInfo list(Product product){
        startPage();
        List<Product> list = productService.selectProductList(product);
        return getDataTable(list);
    }

    /**
     * 根据分类id获取品牌列表
     * @param categoryId
     * @return
     */
    @Operation(summary = "根据分类id获取品牌列表")
    @GetMapping("brandList/{categoryId}")
    public AjaxResult selectBrandListByCategoryId(@PathVariable Long categoryId){
        return success(categoryBrandService.selectBrandListByCategoryId(categoryId));
    }

    /**
     * 获取商品详细信息
     */
    @Operation(summary = "获取商品详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id){
        return success(productService.selectProductById(id));
    }

    /**
     * 商品审核
     */
    @Operation(summary = "商品审核")
    @GetMapping("updateAuditstatus/{id}/{audiStatus}")
    public AjaxResult updateAuditStatus(@PathVariable Long id,@PathVariable Integer audiStatus){
        productService.updateAuditStatus(id,audiStatus);
        return success();
    }

    /**
     * 更新上下架状态
     */
    @Operation(summary = "更新上下架状态")
    @GetMapping("updateStatus/{id}/{status}")
    public AjaxResult updateStatus(@PathVariable Long id,@PathVariable Integer status){
        productService.updateStatus(id,status);
        return success();
    }

    /**
     * 新增商品
     *
     * @param product
     * @return
     */
    @Operation(summary = "新增商品")
    @PostMapping
    public AjaxResult add(@RequestBody Product product){
        return toAjax(productService.insertProduct(product));
    }

    /**
     * 修改商品
     * @param product
     * @return
     */
    @Operation(summary = "修改商品")
    @PutMapping
    public AjaxResult edit(@RequestBody Product product) {
        return toAjax(productService.updateProduct(product));
    }

    /**
     * 删除商品
     *
     * @param ids
     * @return
     */
    @Operation(summary = "删除商品")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(productService.deleteProductByIds(ids));
    }

}
