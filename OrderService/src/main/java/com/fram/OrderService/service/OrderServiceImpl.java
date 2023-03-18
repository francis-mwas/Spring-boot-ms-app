package com.fram.OrderService.service;


import com.fram.OrderService.entity.Order;
import com.fram.OrderService.external.client.PaymentService;
import com.fram.OrderService.external.client.ProductService;
import com.fram.OrderService.external.request.PaymentRequest;
import com.fram.OrderService.model.OrderRequest;
import com.fram.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentService  paymentService;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        //Order entity -> Save the data with status Order Created
        //Product Service - Block Products(Reduce the Quantity)
        //Call payment service tocomplete the payments ->success: COMPLETE, Else
        //CANCELLED
        log.info("Placing Order Request: {}", orderRequest);

        //Call the product service to reduce the quantity
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
       log.info("Creating Order with status CREATED");
        Order order = Order.builder()
        .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                        .productId(orderRequest.getProductId())
                                .orderDate(Instant.now())
                                        .quantity(orderRequest.getQuantity())
                                                .
                build();
        order = orderRepository.save(order);
        log.info("Calling Payment service to complete the payments");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();
    String orderStatus = null;
    try {
        paymentService.doPayment(paymentRequest);
        log.info("Payment processing is done. Changing the order status to SUCCESS");
        orderStatus="PLACED";
    }catch (Exception e){
        log.error("Error occurred in payment. Changing order status to SUCCESS failed");
        orderStatus = "PAYMENT_FAILED";
    }

    order.setOrderStatus(orderStatus);
    orderRepository.save(order);
        log.info("Order Placed successfully with orderId: {}", order.getId());
        return order.getId();

    }


}
