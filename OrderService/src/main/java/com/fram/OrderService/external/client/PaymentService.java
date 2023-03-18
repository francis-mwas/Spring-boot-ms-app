package com.fram.OrderService.external.client;


import com.fram.OrderService.external.request.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/*
 * This interface will help call
 * payment service using feign
 *
 */

@FeignClient(name="PAYMENT-SERVICE/payment")
public interface PaymentService {

    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);
}
