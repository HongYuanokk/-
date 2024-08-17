package com.spzx.user.api.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.spzx.common.core.annotation.Excel;
import com.spzx.common.core.web.domain.BaseEntity;

/**
 * 会员对象 user_info
 *
 * @author atguigu
 * @date 2024-08-06
 */
@Data
@Schema(description = "会员")
public class UserInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户名 */
    @Excel(name = "用户名")
    @Schema(description = "用户名")
    private String username;

    /** 密码 */
    @Excel(name = "密码")
    @Schema(description = "密码")
    private String password;

    /** 昵称 */
    @Excel(name = "昵称")
    @Schema(description = "昵称")
    private String nickName;

    /** 电话号码 */
    @Excel(name = "电话号码")
    @Schema(description = "电话号码")
    private String phone;

    /** 头像 */
    @Excel(name = "头像")
    @Schema(description = "头像")
    private String avatar;

    /** 性别 */
    @Excel(name = "性别")
    @Schema(description = "性别")
    private Integer sex;

    /** 备注 */
    @Excel(name = "备注")
    @Schema(description = "备注")
    private String memo;

    /** 微信open id */
    @Excel(name = "微信open id")
    @Schema(description = "微信open id")
    private String openId;

    /** 微信开放平台unionID */
    @Excel(name = "微信开放平台unionID")
    @Schema(description = "微信开放平台unionID")
    private String unionId;

    /** 最后一次登录ip */
    @Excel(name = "最后一次登录ip")
    @Schema(description = "最后一次登录ip")
    private String lastLoginIp;

    /** 最后一次登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后一次登录时间", width = 30, dateFormat = "yyyy-MM-dd")
    @Schema(description = "最后一次登录时间")
    private Date lastLoginTime;

    /** 状态：1为正常，0为禁止 */
    @Excel(name = "状态：1为正常，0为禁止")
    @Schema(description = "状态：1为正常，0为禁止")
    private Integer status;

}
