package com.learn.concurrency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kevin
 */
@RestController
@SpringBootApplication
public class ConcurrencyApplication  {

    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(){
        return "Hello world";
    }
}