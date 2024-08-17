package com.spzx.user.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.user.api.RemoteUserInfoService;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 *
 * @author spzx
 */
@Component
public class RemoteUserInfoFallbackFactory implements FallbackFactory<RemoteUserInfoService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteUserInfoFallbackFactory.class);

    @Override
    public RemoteUserInfoService create(Throwable throwable)
    {
        log.error("消费者用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserInfoService()
        {

            @Override
            public R<UserInfo> getUserInfo(String username, String source) {
                log.error("根据用户名获取会员信息失败:{}", throwable.getMessage());
                return R.fail(throwable.getMessage());
            }

            @Override
            public R<Boolean> updateUserLogin(UpdateUserLogin updateUserLogin, String source) {
                log.error("更新会员登录信息失败:{}", throwable.getMessage());
                return R.fail(throwable.getMessage());
            }

            @Override
            public R<Boolean> register(UserInfo userInfo, String source) {
                log.error("注册用户失败:{}", throwable.getMessage());
                return R.fail(throwable.getMessage());
            }

        };
    }
}
