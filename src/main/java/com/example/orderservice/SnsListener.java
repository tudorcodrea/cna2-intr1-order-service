package com.example.orderservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-updates")
public class SnsListener {

    private static final Logger logger = LoggerFactory.getLogger(SnsListener.class);

    @PostMapping
    public void handleProductEvent(@RequestBody Product product) {
        logger.info("Received product event: {}", product);
        // Here you would typically process the event, e.g., create an order
    }
}
