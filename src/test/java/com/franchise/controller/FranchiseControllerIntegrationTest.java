package com.franchise.controller;

import com.franchise.model.Branch;
import com.franchise.model.Franchise;
import com.franchise.model.Product;
import com.franchise.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class FranchiseControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private FranchiseRepository franchiseRepository;

    @BeforeEach
    void setUp() {
        franchiseRepository.deleteAll().block();
    }

    @Test
    void testCreateFranchise() {
        Franchise franchise = new Franchise("McDonald's Test");

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.name").isEqualTo("McDonald's Test")
                .jsonPath("$.data.id").exists();

        System.out.println("✅ Franquicia creada exitosamente via API");
    }

    @Test
    void testGetAllFranchises() {
        // Crear franquicias de prueba directamente en el repositorio
        Franchise franchise1 = new Franchise("KFC Test");
        Franchise franchise2 = new Franchise("Subway Test");

        franchiseRepository.save(franchise1).block();
        franchiseRepository.save(franchise2).block();

        webTestClient.get()
                .uri("/api/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Franchise.class)
                .hasSize(2);

        System.out.println("✅ Listado de franquicias funcionando");
    }

    @Test
    void testGetFranchiseById() {
        Franchise franchise = new Franchise("Burger King Test");
        Franchise saved = franchiseRepository.save(franchise).block();

        webTestClient.get()
                .uri("/api/franchises/{id}", saved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.name").isEqualTo("Burger King Test")
                .jsonPath("$.data.id").isEqualTo(saved.getId());

        System.out.println("✅ Búsqueda por ID funcionando");
    }

    @Test
    void testWorkflowStepByStep() {
        // 1. Crear franquicia usando repositorio (más directo para testing)
        Franchise franchise = new Franchise("Complete Test Franchise");
        Franchise savedFranchise = franchiseRepository.save(franchise).block();
        String franchiseId = savedFranchise.getId();

        // 2. Agregar sucursal via API
        Branch branch = new Branch("Test Branch");

        webTestClient.post()
                .uri("/api/franchises/{franchiseId}/branches", franchiseId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(branch)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.branches[0].name").isEqualTo("Test Branch")
                .jsonPath("$.data.branches[0].id").exists();

        // 3. Obtener ID de la sucursal creada
        Franchise updatedFranchise = franchiseRepository.findById(franchiseId).block();
        String branchId = updatedFranchise.getBranches().get(0).getId();

        // 4. Agregar producto via API
        Product product = new Product("Test Product", 25);

        webTestClient.post()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/products",
                        franchiseId, branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.branches[0].products[0].name").isEqualTo("Test Product")
                .jsonPath("$.data.branches[0].products[0].stock").isEqualTo(25);

        System.out.println("✅ Workflow paso a paso completado exitosamente");
    }

    @Test
    void testUpdateOperations() {
        // Crear estructura inicial
        Franchise franchise = new Franchise("Update Test Franchise");
        Franchise saved = franchiseRepository.save(franchise).block();

        Branch branch = new Branch("Original Branch");
        branch.setId("test-branch-id");
        saved.addBranch(branch);

        Product product = new Product("Original Product", 10);
        product.setId("test-product-id");
        branch.addProduct(product);

        Franchise updated = franchiseRepository.save(saved).block();

        // Test 1: Actualizar nombre de franquicia
        webTestClient.put()
                .uri("/api/franchises/{id}/name", updated.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Updated Franchise Name\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.name").isEqualTo("Updated Franchise Name");

        // Test 2: Actualizar nombre de sucursal
        webTestClient.put()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/name",
                        updated.getId(), "test-branch-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Updated Branch Name\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.branches[0].name").isEqualTo("Updated Branch Name");

        // Test 3: Actualizar stock de producto
        webTestClient.put()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock",
                        updated.getId(), "test-branch-id", "test-product-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\": 50}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.branches[0].products[0].stock").isEqualTo(50);

        System.out.println("✅ Operaciones de actualización funcionando correctamente");
    }

    @Test
    void testTopStockReport() {
        // Crear estructura de datos para el reporte usando repositorio
        Franchise franchise = new Franchise("Report Test Franchise");

        // Sucursal 1 con 2 productos
        Branch branch1 = new Branch("Branch 1");
        branch1.setId("branch1");
        Product product1 = new Product("Product A", 100); // Mayor stock en branch1
        product1.setId("prod1");
        Product product2 = new Product("Product B", 50);
        product2.setId("prod2");
        branch1.addProduct(product1);
        branch1.addProduct(product2);

        // Sucursal 2 con 1 producto
        Branch branch2 = new Branch("Branch 2");
        branch2.setId("branch2");
        Product product3 = new Product("Product C", 75); // Único producto en branch2
        product3.setId("prod3");
        branch2.addProduct(product3);

        franchise.addBranch(branch1);
        franchise.addBranch(branch2);

        Franchise saved = franchiseRepository.save(franchise).block();

        // Probar endpoint de reporte
        webTestClient.get()
                .uri("/api/franchises/{franchiseId}/top-stock-products", saved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[?(@.branchName == 'Branch 1')].productName").isEqualTo("Product A")
                .jsonPath("$.data[?(@.branchName == 'Branch 1')].stock").isEqualTo(100)
                .jsonPath("$.data[?(@.branchName == 'Branch 2')].productName").isEqualTo("Product C")
                .jsonPath("$.data[?(@.branchName == 'Branch 2')].stock").isEqualTo(75);

        System.out.println("✅ Reporte de productos con mayor stock funcionando correctamente");
    }

    @Test
    void testValidationErrors() {
        // Test 1: Franquicia con nombre vacío
        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"\"}")
                .exchange()
                .expectStatus().isBadRequest();

        // Test 2: Crear franquicia válida para probar validaciones de productos
        Franchise validFranchise = new Franchise("Valid Franchise");
        Branch validBranch = new Branch("Valid Branch");
        validBranch.setId("validBranch");
        validFranchise.addBranch(validBranch);

        Franchise saved = franchiseRepository.save(validFranchise).block();

        // Test 3: Producto con stock negativo
        webTestClient.post()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/products",
                        saved.getId(), "validBranch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"Invalid Product\", \"stock\": -10}")
                .exchange()
                .expectStatus().isBadRequest();

        // Test 4: Producto sin nombre
        webTestClient.post()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/products",
                        saved.getId(), "validBranch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\": \"\", \"stock\": 10}")
                .exchange()
                .expectStatus().isBadRequest();

        System.out.println("✅ Validaciones funcionando correctamente");
    }

    @Test
    void testSearchAndFilterEndpoints() {
        // Crear franquicias con diferentes características
        Franchise franchise1 = new Franchise("Search Test Alpha");
        Branch branch1 = new Branch("Alpha Branch");
        branch1.setId("alpha-branch");
        Product product1 = new Product("Alpha Product", 10);
        product1.setId("alpha-product");
        branch1.addProduct(product1);
        franchise1.addBranch(branch1);

        Franchise franchise2 = new Franchise("Search Test Beta");
        Branch branch2a = new Branch("Beta Branch A");
        Branch branch2b = new Branch("Beta Branch B");
        branch2a.setId("beta-branch-a");
        branch2b.setId("beta-branch-b");

        Product product2a = new Product("Beta Product A", 20);
        Product product2b = new Product("Beta Product B", 30);
        Product product2c = new Product("Beta Product C", 40);
        product2a.setId("beta-product-a");
        product2b.setId("beta-product-b");
        product2c.setId("beta-product-c");

        branch2a.addProduct(product2a);
        branch2a.addProduct(product2b);
        branch2b.addProduct(product2c);
        franchise2.addBranch(branch2a);
        franchise2.addBranch(branch2b);

        franchiseRepository.save(franchise1).block();
        franchiseRepository.save(franchise2).block();

        // Test 1: Buscar por nombre
        webTestClient.get()
                .uri("/api/franchises/search?name=Alpha")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Franchise.class)
                .hasSize(1);

        // Test 2: Buscar por mínimo de sucursales
        webTestClient.get()
                .uri("/api/franchises/search?minBranches=2")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Franchise.class)
                .hasSize(1); // Solo franchise2 tiene 2 sucursales

        // Test 3: Buscar por mínimo de productos
        webTestClient.get()
                .uri("/api/franchises/search?minProducts=2")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Franchise.class)
                .hasSize(1); // Solo franchise2 tiene 3 productos

        System.out.println("✅ Endpoints de búsqueda y filtrado funcionando");
    }

    @Test
    void testDeleteOperations() {
        // Crear estructura para probar eliminaciones
        Franchise franchise = new Franchise("Delete Test Franchise");
        Branch branch = new Branch("Delete Test Branch");
        branch.setId("delete-branch");
        Product product = new Product("Delete Test Product", 25);
        product.setId("delete-product");

        branch.addProduct(product);
        franchise.addBranch(branch);

        Franchise saved = franchiseRepository.save(franchise).block();

        // Test 1: Eliminar producto
        webTestClient.delete()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}",
                        saved.getId(), "delete-branch", "delete-product")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.branches[0].products").isEmpty();

        // Test 2: Eliminar sucursal
        webTestClient.delete()
                .uri("/api/franchises/{franchiseId}/branches/{branchId}",
                        saved.getId(), "delete-branch")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.branches").isEmpty();

        // Test 3: Eliminar franquicia
        webTestClient.delete()
                .uri("/api/franchises/{id}", saved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);

        // Verificar que la franquicia fue eliminada
        webTestClient.get()
                .uri("/api/franchises/{id}", saved.getId())
                .exchange()
                .expectStatus().isNotFound();

        System.out.println("✅ Operaciones de eliminación funcionando correctamente");
    }

    @Test
    void testErrorHandling() {
        // Test 1: Buscar franquicia inexistente
        webTestClient.get()
                .uri("/api/franchises/nonexistent-id")
                .exchange()
                .expectStatus().isNotFound();

        // Test 2: Agregar sucursal a franquicia inexistente
        Branch branch = new Branch("Test Branch");

        webTestClient.post()
                .uri("/api/franchises/nonexistent-id/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(branch)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);

        // Test 3: Crear franquicia duplicada
        Franchise franchise = new Franchise("Duplicate Test");
        franchiseRepository.save(franchise).block();

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);

        System.out.println("✅ Manejo de errores funcionando correctamente");
    }
}
