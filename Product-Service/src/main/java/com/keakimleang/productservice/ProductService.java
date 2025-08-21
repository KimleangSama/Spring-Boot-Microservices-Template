package com.keakimleang.productservice;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private List<Product> products;

    @PostConstruct
    public void init() {
        products = List.of(
                new Product("1", "Product 1", "Description for product 1", "SKU001", BigDecimal.valueOf(100.00)),
                new Product("2", "Product 2", "Description for product 2", "SKU002", BigDecimal.valueOf(200.00)),
                new Product("3", "Product 3", "Description for product 3", "SKU003", BigDecimal.valueOf(300.00)),
                new Product("4", "Product 4", "Description for product 4", "SKU004", BigDecimal.valueOf(400.00)),
                new Product("5", "Product 5", "Description for product 5", "SKU005", BigDecimal.valueOf(500.00))
        );
    }

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .id(productRequest.id())
                .name(productRequest.name())
                .description(productRequest.description())
                .skuCode(productRequest.skuCode())
                .price(productRequest.price())
                .build();
        products.add(product);
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSkuCode(),
                product.getPrice()
        );
    }

    public List<ProductResponse> getAllProducts() {
        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getSkuCode(),
                        product.getPrice()))
                .toList();
    }
}
