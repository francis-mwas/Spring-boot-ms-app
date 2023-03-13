package com.fam.ProductService.exception;


import lombok.Data;

@Data
public class ProductServiceCustomExcepion extends RuntimeException{
    private String errorCode;

    public ProductServiceCustomExcepion(String message, String errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
