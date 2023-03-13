package com.fam.ProductService.service;

import com.fam.ProductService.entity.Product;
import com.fam.ProductService.model.ProductRequest;
import com.fam.ProductService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
