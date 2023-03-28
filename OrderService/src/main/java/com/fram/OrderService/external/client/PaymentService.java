package com.fram.OrderService.external.client;


import com.fram.OrderService.exception.CustomException;
import com.fram.OrderService.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/*
 * This interface will help call
 * payment service using feign
 *
 */
@CircuitBreaker(name="external", fallbackMethod = "fallback")
@FeignClient(name="PAYMENT-SERVICE/v1/payments")
public interface PaymentService {
    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

//adding default fallback method for circuit breaker
default void fallback(Exception e){
    throw new CustomException("Payment Service is not available", "UNAVAILABLE", 500);
}
}
