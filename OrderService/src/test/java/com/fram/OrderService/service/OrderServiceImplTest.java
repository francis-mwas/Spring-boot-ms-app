package com.fram.OrderService.service;


import com.fam.ProductService.model.ProductResponse;
import com.fram.OrderService.entity.Order;
import com.fram.OrderService.external.client.PaymentService;
import com.fram.OrderService.external.client.ProductService;
import com.fram.OrderService.external.response.PaymentResponse;
import com.fram.OrderService.model.PaymentMode;
import com.fram.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get order - Success testv")
    @Test
    void test_When_Order_Fetch_Success(){
        Order order = getMockOrder();
        //Mocking
        Mockito.when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        Mockito.when(restTemplate.getForObject("http://PRODUCT-SERVICE/v1/products/"+ order.getProductId(),
                ProductResponse.class)).thenReturn(getMockProductResponse());
        Mockito.when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/v1/payments/order/" + order.getId(),
                PaymentResponse.class
        )).thenReturn(getMockpaymentResponse());
        //Actual method call
        orderService.getOrderDetails(1);
        //Verification
        //Assert
    }

    private PaymentResponse getMockpaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                         .productId(2)
                                 .productName("Samsung")
                                         .price(100)
                                                 .build();
    }

    private Order getMockOrder() {
        return Order.builder()
        .orderStatus("PLACED")
                .orderDate(Instant.now())
                        .id(1)
                                .amount(100)
                                        .quantity(200)
                                                .productId(2)
                                                        .
                build();
    }
}