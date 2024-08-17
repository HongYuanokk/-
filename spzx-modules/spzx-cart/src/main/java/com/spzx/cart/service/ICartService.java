package com.spzx.cart.service;

import com.spzx.cart.api.domain.CartInfo;

import java.util.List;

public interface ICartService {
    void addToCart(Long skuId, Integer skuNum);

    List<CartInfo> getCartList();

    void deleteCart(Long skuId);

    void checkCart(Long skuId, Integer isChecked);

    void allCheckCart(Integer isChecked);

    void clearCart();

    List<CartInfo> getCartCheckedList(Long userId);

    Boolean updateCartPrice(Long userId);

    Boolean deleteCartCheckedList(Long userId);
}
