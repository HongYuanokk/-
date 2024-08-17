package com.spzx.cart.service.impl;

public class CartKeyUtil {

    public static String getCartKey(Long userId) {
        //定义key user:userId:cart
        return "user:cart:" + userId;
    }

}
