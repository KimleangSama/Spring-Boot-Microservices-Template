package com.keakimleang.apigateway;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @RequestMapping("/fallback")
    public String fallback() {
        return "Service is currently unavailable. Please try again later.";
    }
}
