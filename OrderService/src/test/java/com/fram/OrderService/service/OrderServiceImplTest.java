package com.fram.OrderService.service;


import com.fam.ProductService.model.ProductResponse;
import com.fram.OrderService.entity.Order;
import com.fram.OrderService.exception.CustomException;
import com.fram.OrderService.external.client.PaymentService;
import com.fram.OrderService.external.client.ProductService;
import com.fram.OrderService.external.request.PaymentRequest;
import com.fram.OrderService.external.response.PaymentResponse;
import com.fram.OrderService.model.OrderRequest;
import com.fram.OrderService.model.OrderResponse;
import com.fram.OrderService.model.PaymentMode;
import com.fram.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        OrderResponse orderResp = orderService.getOrderDetails(1);
        //Verification
        Mockito.verify(orderRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(restTemplate, Mockito.times(1)).getForObject(
                "http://PRODUCT-SERVICE/v1/products/"+ order.getProductId(),
                ProductResponse.class);
        Mockito.verify(restTemplate, Mockito.times(1)).getForObject(
                "http://PAYMENT-SERVICE/v1/payments/order/" + order.getId(),
                PaymentResponse.class);
        //Assert
        Assertions.assertNotNull(orderResp);
        Assertions.assertEquals(order.getId(), orderResp.getOrderId());

    }
    @DisplayName("Get Orders - failure scenario")
    @Test
    void test_when_Get_Order_Not_FOUND_then_Not_Found(){
        Mockito.when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        CustomException exception =
                Assertions.assertThrows(CustomException.class,
                        ()-> orderService.getOrderDetails(1));
        Assertions.assertEquals("NOT_FOUND", exception.getErrorCode());
        Assertions.assertEquals(404, exception.getStatus());

        Mockito.verify(orderRepository, Mockito.times(1))
                .findById(anyLong());

    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success(){

        Order order = getMockOrder();
        OrderRequest orderRequest = mockOrderRequest();

        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenReturn(order);
        Mockito.when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        Mockito.when(paymentService.doPayment(Mockito.any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L, HttpStatus.OK));
        long orderId = orderService.placeOrder(orderRequest);
          //Verify
        Mockito.verify(orderRepository, Mockito.times(2))
                .save(Mockito.any());
        Mockito.verify(productService, Mockito.times(1))
                .reduceQuantity(anyLong(), anyLong());
        Mockito.verify(paymentService, Mockito.times(1))
                .doPayment(Mockito.any(PaymentRequest.class));

        Assertions.assertEquals(order.getId(), orderId);

    }

    @DisplayName("Place Order - Payment Failed Scenario")
    @Test
    void test_When_Place_Order_Payment_Fails_then_Order_Placed(){

        Order order = getMockOrder();
        OrderRequest orderRequest = mockOrderRequest();

        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenReturn(order);
        Mockito.when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));

        Mockito.when(paymentService.doPayment(Mockito.any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());
        long orderId = orderService.placeOrder(orderRequest);

        //Verify
        Mockito.verify(orderRepository, Mockito.times(2))
                .save(Mockito.any());
        Mockito.verify(productService, Mockito.times(1))
                .reduceQuantity(anyLong(), anyLong());
        Mockito.verify(paymentService, Mockito.times(1))
                .doPayment(Mockito.any(PaymentRequest.class));

        Assertions.assertEquals(order.getId(), orderId);

    }

    //mocking order request
    private OrderRequest mockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100)
                .build();
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