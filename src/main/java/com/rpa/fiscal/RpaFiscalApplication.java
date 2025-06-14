package com.rpa.fiscal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class RpaFiscalApplication {
    public static void main(String[] args) {
        SpringApplication.run(RpaFiscalApplication.class, args);
    }
}
