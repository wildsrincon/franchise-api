package com.franchise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class FranchiseApiApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Franchise API...");
        SpringApplication.run(FranchiseApiApplication.class, args);
    }
}

@RestController
class TestController {

    @GetMapping("/")
    public String home() {
        return "¡Franchise API funcionando!";
    }

    @GetMapping("/test")
    public String test() {
        return "Test exitoso - " + new java.util.Date();
    }
}
