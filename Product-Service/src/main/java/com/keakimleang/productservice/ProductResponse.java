package com.keakimleang.productservice;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description,
                              String skuCode, BigDecimal price) {
}
