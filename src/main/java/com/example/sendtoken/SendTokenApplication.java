package com.example.sendtoken;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootApplication
public class SendTokenApplication {

    public static void main(String[] args) {
        SpringApplication.run(SendTokenApplication.class, args);

    }

}
