package com.spzx.auth.service;

import com.spzx.auth.form.LoginBody;
import com.spzx.auth.form.RegisterBody;
import com.spzx.common.core.constant.Constants;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.utils.ip.IpUtils;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.system.api.model.LoginUser;
import com.spzx.user.api.RemoteUserInfoService;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class H5LoginService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    SysRecordLogService recordLogService;

    @Autowired
    RemoteUserInfoService remoteUserInfoService;

    @Autowired
    SysPasswordService sysPasswordService;

    public void register(RegisterBody registerBody) {

        // 参数
        String password = registerBody.getPassword();// 未经过加密的明文
        String username = registerBody.getUsername();
        String registerCode = registerBody.getCode();// 用户手机验证码
        String nickName = registerBody.getNickName();

        // 参数校验
        Assert.isTrue(StringUtils.hasText(password)&&StringUtils.hasText(username)&&StringUtils.hasText(registerCode)&&StringUtils.hasText(nickName), "用户或密码或验证码或昵称不能为空");

        // 校验验证码
        String cacheCode = stringRedisTemplate.opsForValue().get("phone:code:" + username);
        Assert.isTrue(registerCode.equals(cacheCode), "验证码不正确");
        stringRedisTemplate.delete("phone:code:" + username);//删除验证码

        // 密码加密
        String encryptPassword = SecurityUtils.encryptPassword(password);

        // 注册用户
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPhone(username);// 手机号作为用户名
        userInfo.setNickName(nickName);
        userInfo.setPassword(encryptPassword);
        R<?> registerResult = remoteUserInfoService.register(userInfo, SecurityConstants.INNER);

        // 记录日志
        int code = registerResult.getCode();
        Assert.isTrue(code == 200, "注册失败");
        recordLogService.recordLogininfor(username, Constants.REGISTER, "注册成功");
    }

    public LoginUser login(LoginBody form) {
        String password = form.getPassword();
        String username = form.getUsername();
        // 参数校验
        Assert.isTrue(StringUtils.hasText(password)&&StringUtils.hasText(username), "用户或密码不能为空");

        // 查询用户，远程接口
        UserInfo userInfo = remoteUserInfoService.getUserInfo(username, SecurityConstants.INNER).getData();
        Assert.notNull(userInfo,"用户不存在");

        // 校验密码
        // 用户输入的密码
        String encryptPassword = SecurityUtils.encryptPassword(password);
        // 数据库中的user
        LoginUser loginUser = new LoginUser();
        loginUser.setUserid(userInfo.getId());
        loginUser.setUsername(userInfo.getUsername());
        loginUser.setPassword(userInfo.getPassword());
        loginUser.setStatus(userInfo.getStatus()+"");
        sysPasswordService.validate(loginUser,password);// 校验时候传明文

        // 更新用户，远程接口
        UpdateUserLogin updateUserLogin = new UpdateUserLogin();
        updateUserLogin.setUserId(userInfo.getId());
        updateUserLogin.setLastLoginTime(new Date());
        updateUserLogin.setLastLoginIp(IpUtils.getIpAddr());
        R<?> updateResult = remoteUserInfoService.updateUserLogin(updateUserLogin, SecurityConstants.INNER);

        return loginUser;
    }
}
