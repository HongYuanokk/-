package com.spzx.product.api.domain;


import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 品牌对象 brand
 *
 */
@Data
@Schema(description = "品牌")
public class Brand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Schema(description = "品牌名称")
    @NotBlank(message = "品牌不能为空")
    private String name;

    @Schema(description = "品牌图标")
    @NotBlank(message = "品牌图标不能为空")
    private String logo;

}