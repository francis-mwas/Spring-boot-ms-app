package com.fram.OrderService.external.client;


import com.fram.OrderService.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CircuitBreaker(name="external", fallbackMethod = "fallback")
@FeignClient(name="PRODUCT-SERVICE/v1/products")
public interface ProductService {
    @PutMapping("/reduceQuantity/{id}")
   ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity
    );

    //adding default fallback method for circuit breaker
    default  ResponseEntity<Void> fallback(Exception e){
        throw new CustomException("Product Service is not available", "UNAVAILABLE", 500);
    }
}
