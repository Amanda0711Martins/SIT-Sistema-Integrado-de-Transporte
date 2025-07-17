package com.logistics.financial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.logistics.financial.client")
@EnableJpaAuditing
public class FinancialServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinancialServiceApplication.class, args);
    }
}