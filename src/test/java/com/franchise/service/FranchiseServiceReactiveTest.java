package com.franchise.service;

import com.franchise.dto.UpdateNameDTO;
import com.franchise.dto.UpdateStockDTO;
import com.franchise.dto.ProductTopStockDTO;
import com.franchise.model.Branch;
import com.franchise.model.Franchise;
import com.franchise.model.Product;
import com.franchise.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest
class FranchiseServiceReactiveTest {

    @Autowired
    private FranchiseService franchiseService;

    @Autowired
    private FranchiseRepository franchiseRepository;

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        franchiseRepository.deleteAll().block();
    }

    @Test
    void testCreateFranchise() {
        // Create a new franchise
        Franchise franchise = new Franchise("Test McDonald's Reactive");

        // Save using reactive service
        Mono<Franchise> savedFranchise = franchiseService.createFranchise(franchise);

        // Verify using StepVerifier
        StepVerifier.create(savedFranchise)
                .expectNextMatches(saved -> {
                    System.out.println("✅ Franquicia creada reactivamente: " + saved.getName());
                    return saved.getId() != null && saved.getName().equals("Test McDonald's Reactive");
                })
                .verifyComplete();
    }

    @Test
    void testCreateDuplicateFranchiseShouldFail() {
        String franchiseName = "Duplicate Test";
        Franchise franchise1 = new Franchise(franchiseName);
        Franchise franchise2 = new Franchise(franchiseName);

        // Create first franchise
        Mono<Franchise> workflow = franchiseService.createFranchise(franchise1)
                .then(franchiseService.createFranchise(franchise2)); // Debería fallar

        StepVerifier.create(workflow)
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().contains("already exists")
                )
                .verify();

        System.out.println("✅ Validación de nombres duplicados funciona");
    }

    @Test
    void testCompleteWorkflowReactive() {
        // Create a franchise
        Franchise franchise = new Franchise("KFC Reactive Test");

        Mono<List<ProductTopStockDTO>> completeWorkflow = franchiseService.createFranchise(franchise)
                .flatMap(savedFranchise -> {
                    // Add first branch
                    Branch branch1 = new Branch("KFC Centro");
                    return franchiseService.addBranch(savedFranchise.getId(), branch1);
                })
                .flatMap(franchiseWithBranch1 -> {
                    String branch1Id = franchiseWithBranch1.getBranches().get(0).getId();

                    // Add products to the first branch
                    Product product1 = new Product("Pollo Original", 45);
                    return franchiseService.addProduct(franchiseWithBranch1.getId(), branch1Id, product1)
                            .flatMap(f -> {
                                Product product2 = new Product("Papas Caseras", 30);
                                return franchiseService.addProduct(f.getId(), branch1Id, product2);
                            });
                })
                .flatMap(franchiseWithProducts -> {
                    // Add second branch
                    Branch branch2 = new Branch("KFC Norte");
                    return franchiseService.addBranch(franchiseWithProducts.getId(), branch2);
                })
                .flatMap(franchiseWithBranch2 -> {
                    String branch2Id = franchiseWithBranch2.getBranches().get(1).getId();

                    // Add products to the second branch
                    Product product3 = new Product("Ensalada de Col", 60); // Mayor stock
                    return franchiseService.addProduct(franchiseWithBranch2.getId(), branch2Id, product3);
                })
                .flatMap(finalFranchise -> {
                    // Generate report of top stock products
                    return franchiseService.getTopStockProductsByFranchise(finalFranchise.getId());
                });

        StepVerifier.create(completeWorkflow)
                .expectNextMatches(topProducts -> {
                    System.out.println("✅ Workflow reactivo completo:");
                    System.out.println("📊 Productos con mayor stock por sucursal:");

                    topProducts.forEach(product ->
                            System.out.println("  - " + product.getProductName() +
                                    " en " + product.getBranchName() +
                                    ": " + product.getStock() + " unidades")
                    );

                    return topProducts.size() == 2 &&
                            topProducts.stream().anyMatch(p -> p.getProductName().equals("Pollo Original") && p.getStock() == 45) &&
                            topProducts.stream().anyMatch(p -> p.getProductName().equals("Ensalada de Col") && p.getStock() == 60);
                })
                .verifyComplete();
    }

    @Test
    void testUpdateOperationsReactive() {
        // Create structure for reactive updates
        Franchise franchise = new Franchise("Subway Reactive");

        Mono<Franchise> updateWorkflow = franchiseService.createFranchise(franchise)
                .flatMap(saved -> {
                    Branch branch = new Branch("Subway Plaza");
                    return franchiseService.addBranch(saved.getId(), branch);
                })
                .flatMap(withBranch -> {
                    String branchId = withBranch.getBranches().get(0).getId();
                    Product product = new Product("Italian BMT", 25);
                    return franchiseService.addProduct(withBranch.getId(), branchId, product);
                })
                .flatMap(withProduct -> {
                    // Update Franchise Name
                    UpdateNameDTO nameUpdate = new UpdateNameDTO("Subway Renovado Reactivo");
                    return franchiseService.updateFranchiseName(withProduct.getId(), nameUpdate);
                })
                .flatMap(franchiseUpdated -> {
                    String branchId = franchiseUpdated.getBranches().get(0).getId();
                    // Update Branch Name
                    UpdateNameDTO branchUpdate = new UpdateNameDTO("Subway Plaza Renovado");
                    return franchiseService.updateBranchName(franchiseUpdated.getId(), branchId, branchUpdate);
                })
                .flatMap(branchUpdated -> {
                    String branchId = branchUpdated.getBranches().get(0).getId();
                    String productId = branchUpdated.getBranches().get(0).getProducts().get(0).getId();
                    // Update stock
                    UpdateStockDTO stockUpdate = new UpdateStockDTO(50);
                    return franchiseService.updateProductStock(branchUpdated.getId(), branchId, productId, stockUpdate);
                });

        StepVerifier.create(updateWorkflow)
                .expectNextMatches(result -> {
                    System.out.println("✅ Todas las actualizaciones reactivas completadas:");
                    System.out.println("  - Franquicia: " + result.getName());
                    System.out.println("  - Sucursal: " + result.getBranches().get(0).getName());
                    System.out.println("  - Producto: " + result.getBranches().get(0).getProducts().get(0).getName());
                    System.out.println("  - Stock: " + result.getBranches().get(0).getProducts().get(0).getStock());

                    return result.getName().equals("Subway Renovado Reactivo") &&
                            result.getBranches().get(0).getName().equals("Subway Plaza Renovado") &&
                            result.getBranches().get(0).getProducts().get(0).getStock() == 50;
                })
                .verifyComplete();
    }

    @Test
    void testFranchiseStats() {
        // Create a franchise and add branches and products to test stats
        Franchise franchise = new Franchise("Stats Test");

        Mono<FranchiseService.FranchiseStatsDTO> statsWorkflow = franchiseService.createFranchise(franchise)
                .flatMap(saved -> {
                    // Add Multiple Branches and Products
                    Branch branch1 = new Branch("Sucursal 1");
                    return franchiseService.addBranch(saved.getId(), branch1);
                })
                .flatMap(f -> {
                    String branchId = f.getBranches().get(0).getId();
                    Product p1 = new Product("Producto 1", 100);
                    return franchiseService.addProduct(f.getId(), branchId, p1);
                })
                .flatMap(f -> {
                    String branchId = f.getBranches().get(0).getId();
                    Product p2 = new Product("Producto 2", 150);
                    return franchiseService.addProduct(f.getId(), branchId, p2);
                })
                .flatMap(f -> {
                    Branch branch2 = new Branch("Sucursal 2");
                    return franchiseService.addBranch(f.getId(), branch2);
                })
                .flatMap(f -> {
                    String branch2Id = f.getBranches().get(1).getId();
                    Product p3 = new Product("Producto 3", 200);
                    return franchiseService.addProduct(f.getId(), branch2Id, p3);
                })
                .flatMap(finalFranchise -> {
                    return franchiseService.getFranchiseStats(finalFranchise.getId());
                });

        StepVerifier.create(statsWorkflow)
                .expectNextMatches(stats -> {
                    System.out.println("✅ Estadísticas reactivas:");
                    System.out.println("  - Franquicia: " + stats.getFranchiseName());
                    System.out.println("  - Total sucursales: " + stats.getTotalBranches());
                    System.out.println("  - Total productos: " + stats.getTotalProducts());
                    System.out.println("  - Stock total: " + stats.getTotalStock());

                    return stats.getTotalBranches() == 2 &&
                            stats.getTotalProducts() == 3 &&
                            stats.getTotalStock() == 450; // 100 + 150 + 200
                })
                .verifyComplete();
    }

    @Test
    void testReactivePerformance() {
        // Create a performance test for creating multiple franchises in parallel
        Mono<Long> performanceTest = Mono.fromCallable(() -> System.currentTimeMillis())
                .flatMap(startTime -> {
                    // Create 5 Franchises in Paralel
                    return Mono.when(
                            franchiseService.createFranchise(new Franchise("Perf Test 1")),
                            franchiseService.createFranchise(new Franchise("Perf Test 2")),
                            franchiseService.createFranchise(new Franchise("Perf Test 3")),
                            franchiseService.createFranchise(new Franchise("Perf Test 4")),
                            franchiseService.createFranchise(new Franchise("Perf Test 5"))
                    ).then(Mono.fromCallable(() -> System.currentTimeMillis() - startTime));
                });

        StepVerifier.create(performanceTest)
                .expectNextMatches(duration -> {
                    System.out.println("✅ Test de performance reactiva completado en: " + duration + "ms");
                    return duration < 5000;
                })
                .verifyComplete();

        // Verify count of franchises
        StepVerifier.create(franchiseService.countFranchises())
                .expectNext(5L)
                .verifyComplete();
    }
}
