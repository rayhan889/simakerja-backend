package com.rynrama.simakerjabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimakerjaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimakerjaBackendApplication.class, args);
    }

}
