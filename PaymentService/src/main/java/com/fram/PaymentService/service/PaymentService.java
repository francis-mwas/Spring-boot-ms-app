package com.fram.PaymentService.service;

import com.fram.PaymentService.model.PaymentRequest;
import com.fram.PaymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
