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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


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
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get Order By Order Id: {}", orderId);
        Order order
                = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for the Id:" + orderId,
                        "NOT_FOUND",
                        404));

        log.info("Invoking Product service, to get the product details by Id"+ order.getProductId());
        ProductResponse productResponse = restTemplate.getForObject("http://PRODUCT-SERVICE/v1/products/"+ order.getProductId(),
                ProductResponse.class);
        assert productResponse != null;
        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails
                .builder()
                        .productName(productResponse.getProductName())
                                .productId(productResponse.getProductId())
                                        .build();

        log.info("Getting payment details from payment service");
        PaymentResponse paymentResponse
                = restTemplate.getForObject(
                "http://PAYMENT-SERVICE/v1/payments/order/" + order.getId(),
                PaymentResponse.class
        );


//        private long paymentId;
//        private long amount;
//        private String referenceNumber;
//        private PaymentMode paymentMode;

        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails
                .builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentMode(paymentResponse.getPaymentMode())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentStatus(paymentResponse.getStatus())
                .build();

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
    }

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
