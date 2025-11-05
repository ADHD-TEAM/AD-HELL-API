package com.ohgiraffers.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Chap03GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(Chap03GatewayApplication.class, args);
    }

}
