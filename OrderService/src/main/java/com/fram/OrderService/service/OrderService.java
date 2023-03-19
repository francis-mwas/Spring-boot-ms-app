package com.fram.OrderService.service;

import com.fram.OrderService.model.OrderRequest;
import com.fram.OrderService.model.OrderResponse;

public interface OrderService {



    long placeOrder(OrderRequest orderRequest);
    OrderResponse getOrderDetails(long orderId) ;
}
