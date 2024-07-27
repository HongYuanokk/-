package com.spzx.gateway.service;

import java.io.IOException;
import com.spzx.common.core.exception.CaptchaException;
import com.spzx.common.core.web.domain.AjaxResult;

/**
 * 验证码处理
 *
 * @author spzx
 */
public interface ValidateCodeService
{
    /**
     * 生成验证码
     */
    public AjaxResult createCaptcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCaptcha(String key, String value) throws CaptchaException;
}
