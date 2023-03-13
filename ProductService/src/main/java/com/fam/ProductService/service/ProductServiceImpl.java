package com.fam.ProductService.service;


import com.fam.ProductService.entity.Product;
import com.fam.ProductService.exception.ProductServiceCustomExcepion;
import com.fam.ProductService.model.ProductRequest;
import com.fam.ProductService.model.ProductResponse;
import com.fam.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product...");
        Product product = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);

        log.info("Product Created"+product);
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get product by Id: {}", productId);
        Product product =
                productRepository.findById(productId)
                        .orElseThrow(()-> new ProductServiceCustomExcepion(
                                "Product with the supplied id does not exist", "PRODUCT_NOT_FOUND"));
//        convert to product response
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        return productResponse;

    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity for {} for id: {}", quantity, productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomExcepion("Product with given id not found",
                        "PRODUCT_NOT_FOUND"));
        if(product.getQuantity() < quantity){
            throw new ProductServiceCustomExcepion("The supplied product has insufficient quantity",
                    "INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product Quantity updated successfully");
    }


}
