package com.spzx.user.service.impl;



import com.spzx.user.controller.HttpUtils;
import com.spzx.user.service.ISmsService;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements ISmsService {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void send(String phone) {
        String code = new DecimalFormat("0000").format(new Random().nextInt(10000));
        redisTemplate.opsForValue().set("phone:code:"+phone , code, 5, TimeUnit.MINUTES);
        Map<String, String> param = new HashMap<>();
        param.put("code", code);
        param.put("phone",phone);
        sendMessage(param);
    }

    private void sendMessage(Map<String, String> param) {
        // 发送短信
        String host = "https://zwp.market.alicloudapi.com";
        String path = "/sms/sendv2";
        String method = "GET";
        String appcode = "6e125298cf0e49a3805302957b2fef4f";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", param.get("phone"));
        querys.put("content", "【智能云】您的验证码是"+param.get("code")+"。如非本人操作，请忽略本短信");


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
