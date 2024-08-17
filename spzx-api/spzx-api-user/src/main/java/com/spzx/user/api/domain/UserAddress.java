package com.spzx.user.api.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.spzx.common.core.annotation.Excel;
import com.spzx.common.core.web.domain.BaseEntity;

/**
 * 用户地址对象 user_address
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Data
@Schema(description = "用户地址")
public class UserAddress extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Excel(name = "用户ID")
    @Schema(description = "用户ID")
    private Long userId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @Schema(description = "${comment}")
    private String name;

    /** 电话 */
    @Excel(name = "电话")
    @Schema(description = "电话")
    private String phone;

    /** 标签名称 */
    @Excel(name = "标签名称")
    @Schema(description = "标签名称")
    private String tagName;

    /** 省code */
    @Excel(name = "省code")
    @Schema(description = "省code")
    private String provinceCode;

    /** 市code */
    @Excel(name = "市code")
    @Schema(description = "市code")
    private String cityCode;

    /** 区code */
    @Excel(name = "区code")
    @Schema(description = "区code")
    private String districtCode;

    /** 详细地址 */
    @Excel(name = "详细地址")
    @Schema(description = "详细地址")
    private String address;

    /** 完整地址 */
    @Excel(name = "完整地址")
    @Schema(description = "完整地址")
    private String fullAddress;

    /** 是否默认地址（0：否 1：是） */
    @Excel(name = "是否默认地址", readConverterExp = "0=：否,1=：是")
    @Schema(description = "是否默认地址")
    private Long isDefault;

}
