package com.adhd.ad_hell;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootTest
@EnableFeignClients(basePackages = "com.adhd.ad_hell.external.notification")
class AdHellApplicationTests {

    @Test
    void contextLoads() {
    }

}
