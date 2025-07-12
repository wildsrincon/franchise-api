package com.franchise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableReactiveMongoRepositories
@RestController
public class FranchiseApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FranchiseApiApplication.class, args);
    }

    @GetMapping("/")
    public Mono<String> home() {
        return Mono.just("✅ Franchise API Reactiva con MongoDB funcionando!");
    }

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("🚀 Servidor Reactivo - WebFlux y MongoDB Reactive activos");
    }
}

