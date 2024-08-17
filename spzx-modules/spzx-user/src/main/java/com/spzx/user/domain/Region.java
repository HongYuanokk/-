package com.spzx.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.spzx.common.core.annotation.Excel;
import com.spzx.common.core.web.domain.BaseEntity;

/**
 * 地区信息对象 region
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Data
@Schema(description = "地区信息")
public class Region extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 地区编码 */
    @Excel(name = "地区编码")
    @Schema(description = "地区编码")
    private String code;

    /** 上级地区code */
    @Excel(name = "上级地区code")
    @Schema(description = "上级地区code")
    private String parentCode;

    /** 地区名称 */
    @Excel(name = "地区名称")
    @Schema(description = "地区名称")
    private String name;

    /** 地区级别：1-省、自治区、直辖市 2-地级市、地区、自治州、盟 3-市辖区、县级市、县 */
    @Excel(name = "地区级别：1-省、自治区、直辖市 2-地级市、地区、自治州、盟 3-市辖区、县级市、县")
    @Schema(description = "地区级别：1-省、自治区、直辖市 2-地级市、地区、自治州、盟 3-市辖区、县级市、县")
    private Long level;

}
