package com.spzx.user.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.user.service.ISmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Tag(name = "短信接口")
@RestController
@RequestMapping("/sms")
public class SmsController extends BaseController {

    @Autowired
    private ISmsService smsService;

    @Operation(summary = "获取图片验证码")
    @GetMapping(value = "sendCode/{phone}")
    public AjaxResult sendCode(@Parameter(name = "phone", description = "手机", required = true) @PathVariable String phone) {
        smsService.send(phone);
        return success();
    }

}