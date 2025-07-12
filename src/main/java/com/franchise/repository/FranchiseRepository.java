package com.franchise.repository;

import com.franchise.model.Franchise;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FranchiseRepository extends ReactiveMongoRepository<Franchise, String> {

    // Search by name
    Mono<Franchise> findByName(String name);

    // Check if a franchise exists by name
    Mono<Boolean> existsByName(String name);

    // Delete by name
    Mono<Void> deleteByName(String name);
}
