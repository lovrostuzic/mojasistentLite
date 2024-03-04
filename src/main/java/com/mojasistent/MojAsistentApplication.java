package com.mojasistent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@ComponentScan(basePackages = "com.mojasistent")
@EnableScheduling
@CrossOrigin
public class MojAsistentApplication {
    public static void main(String[] args) {
        SpringApplication.run(MojAsistentApplication.class, args);
        System.out.println("Startup successful!");
        System.out.println("Go to: ");
        System.out.println("http://localhost:8080/");
    }
}
