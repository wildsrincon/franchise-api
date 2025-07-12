package com.franchise.repository;

import com.franchise.model.Franchise;
import com.franchise.model.Branch;
import com.franchise.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class MongoConnectionTest {

    @Autowired
    private FranchiseRepository franchiseRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Test
    void testMongoConnection() {
        // Verificar que podemos conectarnos
        Mono<String> dbName = mongoTemplate.getMongoDatabase()
                .map(db -> db.getName());

        StepVerifier.create(dbName)
                .expectNextMatches(name -> name.contains("franchise"))
                .verifyComplete();

        System.out.println("✅ Conexión a MongoDB exitosa");
    }

    @Test
    void testCreateAndFindFranchise() {
        // Crear franquicia de prueba
        Franchise franchise = new Franchise("Test Franchise - " + System.currentTimeMillis());

        // Guardar y verificar
        Mono<Franchise> savedFranchise = franchiseRepository.save(franchise)
                .doOnNext(saved -> System.out.println("✅ Franquicia guardada: " + saved.getName()));

        StepVerifier.create(savedFranchise)
                .expectNextMatches(saved -> saved.getId() != null && saved.getName().startsWith("Test Franchise"))
                .verifyComplete();

        System.out.println("✅ Operaciones CRUD funcionando");
    }

    @Test
    void testCompleteWorkflow() {
        // Crear franquicia completa
        Franchise franchise = new Franchise("McDonald's Test");

        // Crear sucursal
        Branch branch = new Branch("Sucursal Centro");
        branch.setId("branch_1");

        // Agregar productos
        Product product1 = new Product("Big Mac", 50);
        product1.setId("prod_1");
        Product product2 = new Product("Papas", 30);
        product2.setId("prod_2");

        branch.addProduct(product1);
        branch.addProduct(product2);
        franchise.addBranch(branch);

        // Guardar, buscar y verificar
        Mono<Franchise> workflow = franchiseRepository.save(franchise)
                .flatMap(saved -> franchiseRepository.findById(saved.getId()))
                .doOnNext(found -> {
                    System.out.println("✅ Franquicia: " + found.getName());
                    System.out.println("📍 Sucursales: " + found.getBranches().size());
                    System.out.println("📦 Total productos: " + found.getTotalProducts());
                });

        StepVerifier.create(workflow)
                .expectNextMatches(found ->
                        found.getName().equals("McDonald's Test") &&
                                found.getBranches().size() == 1 &&
                                found.getTotalProducts() == 2
                )
                .verifyComplete();

        System.out.println("✅ Workflow completo funcionando");
    }
}