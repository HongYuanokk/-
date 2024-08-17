package com.spzx.user.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 会员对象 user_info
 *
 * @author spzx
 * @date 2024-01-12
 */
@Data
public class UpdateUserLogin
{

    private Long userId;

    /** 最后一次登录ip */
    private String lastLoginIp;

    /** 最后一次登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastLoginTime;


}