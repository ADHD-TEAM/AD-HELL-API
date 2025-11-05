package com.adhd.ad_hell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.adhd.ad_hell.external.notification")
public class AdHellApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdHellApplication.class, args);
	}

}
