package com.crell.common.service;

import com.crell.common.model.Cart;

import java.util.List;

/**
 *
 */
public interface CartSer {
    Boolean add(String userId,String businessId);

    List<Cart> getCart(String userId);
}
