package com.franchise.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.franchise.dto.ProductTopStockDTO;
import com.franchise.dto.UpdateNameDTO;
import com.franchise.dto.UpdateStockDTO;
import com.franchise.model.Branch;
import com.franchise.model.Franchise;
import com.franchise.model.Product;
import com.franchise.repository.FranchiseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FranchiseService {

    @Autowired
    private FranchiseRepository franchiseRepository;

    // ==================== OPERACIONES DE FRANQUICIA ====================

    public Mono<Franchise> createFranchise(Franchise franchise) {
        // Asignar IDs a sucursales y productos si no tienen
        if (franchise.getBranches() != null) {
            for (Branch branch : franchise.getBranches()) {
                if (branch.getId() == null || branch.getId().isEmpty()) {
                    branch.setId(UUID.randomUUID().toString());
                }
                if (branch.getProducts() != null) {
                    for (Product product : branch.getProducts()) {
                        if (product.getId() == null || product.getId().isEmpty()) {
                            product.setId(UUID.randomUUID().toString());
                        }
                    }
                }
            }
        }
        return franchiseRepository.existsByName(franchise.getName())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Franchise with this name already exists"));
                    }
                    return franchiseRepository.save(franchise);
                });
    }

    public Flux<Franchise> getAllFranchises() {
        return franchiseRepository.findAll();
    }

    public Mono<Franchise> getFranchiseById(String id) {
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + id)));
    }

    public Mono<Franchise> getFranchiseByName(String name) {
        return franchiseRepository.findByName(name)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with name: " + name)));
    }

    public Mono<Franchise> updateFranchiseName(String id, UpdateNameDTO updateNameDTO) {
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + id)))
                .flatMap(franchise -> {
                    // Verificar que no existe otra franquicia con el mismo nombre
                    if (franchise.getName().equals(updateNameDTO.getName())) {
                        // Si es el mismo nombre, no hacer verificación adicional
                        franchise.setName(updateNameDTO.getName());
                        return franchiseRepository.save(franchise);
                    }

                    return franchiseRepository.existsByName(updateNameDTO.getName())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(
                                            new IllegalArgumentException("Franchise with this name already exists"));
                                }
                                franchise.setName(updateNameDTO.getName());
                                return franchiseRepository.save(franchise);
                            });
                });
    }

    public Mono<Void> deleteFranchise(String id) {
        return franchiseRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Franchise not found with id: " + id));
                    }
                    return franchiseRepository.deleteById(id);
                });
    }

    // ==================== OPERACIONES DE SUCURSAL ====================

    public Mono<Franchise> addBranch(String franchiseId, Branch branch) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    // Generar ID único para la sucursal
                    branch.setId(UUID.randomUUID().toString());

                    // Si la lista de productos viene como null, inicializarla
                    if (branch.getProducts() == null) {
                        branch.setProducts(new ArrayList<>());
                    } else {
                        // Asignar IDs a todos los productos que no tengan ID
                        for (Product product : branch.getProducts()) {
                            if (product.getId() == null || product.getId().isEmpty()) {
                                product.setId(UUID.randomUUID().toString());
                            }
                        }
                    }

                    franchise.addBranch(branch);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> updateBranchName(String franchiseId, String branchId, UpdateNameDTO updateNameDTO) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId);
                    if (branch == null) {
                        return Mono.error(new IllegalArgumentException("Branch not found with id: " + branchId));
                    }

                    branch.setName(updateNameDTO.getName());
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> deleteBranch(String franchiseId, String branchId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    boolean removed = franchise.removeBranch(branchId);
                    if (!removed) {
                        return Mono.error(new IllegalArgumentException("Branch not found with id: " + branchId));
                    }
                    return franchiseRepository.save(franchise);
                });
    }

    // ==================== OPERACIONES DE PRODUCTO ====================

    public Mono<Franchise> addProduct(String franchiseId, String branchId, Product product) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId);
                    if (branch == null) {
                        return Mono.error(new IllegalArgumentException("Branch not found with id: " + branchId));
                    }
    
                    // Generar ID único para el producto si no tiene
                    if (product.getId() == null || product.getId().isEmpty()) {
                        product.setId(UUID.randomUUID().toString());
                    }
                    
                    branch.addProduct(product);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId);
                    if (branch == null) {
                        return Mono.error(new IllegalArgumentException("Branch not found with id: " + branchId));
                    }

                    boolean removed = branch.removeProduct(productId);
                    if (!removed) {
                        return Mono.error(new IllegalArgumentException("Product not found with id: " + productId));
                    }

                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId,
            UpdateStockDTO updateStockDTO) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId);
                    if (branch == null) {
                        return Mono.error(new IllegalArgumentException("Branch not found with id: " + branchId));
                    }

                    Product product = branch.findProductById(productId);
                    if (product == null) {
                        return Mono.error(new IllegalArgumentException("Product not found with id: " + productId));
                    }

                    product.setStock(updateStockDTO.getStock());
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> updateProductName(String franchiseId, String branchId, String productId,
            UpdateNameDTO updateNameDTO) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.findBranchById(branchId);
                    if (branch == null) {
                        return Mono.error(new IllegalArgumentException("Branch not found with id: " + branchId));
                    }

                    Product product = branch.findProductById(productId);
                    if (product == null) {
                        return Mono.error(new IllegalArgumentException("Product not found with id: " + productId));
                    }

                    product.setName(updateNameDTO.getName());
                    return franchiseRepository.save(franchise);
                });
    }

    // ==================== REPORTES ====================

    public Mono<List<ProductTopStockDTO>> getTopStockProductsByFranchise(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + franchiseId)))
                .map(franchise -> {
                    List<ProductTopStockDTO> topProducts = new ArrayList<>();

                    for (Branch branch : franchise.getBranches()) {
                        if (branch.getProducts() != null && !branch.getProducts().isEmpty()) {
                            Product topProduct = branch.getProducts().stream()
                                    .max(Comparator.comparing(Product::getStock))
                                    .orElse(null);

                            if (topProduct != null) {
                                topProducts.add(new ProductTopStockDTO(
                                        topProduct.getName(),
                                        branch.getName(),
                                        topProduct.getStock()));
                            }
                        }
                    }

                    return topProducts;
                });
    }

    // ==================== MÉTODOS AUXILIARES ====================

    public Mono<Long> countFranchises() {
        return franchiseRepository.count();
    }

    public Mono<Boolean> existsById(String id) {
        return franchiseRepository.existsById(id);
    }

    public Mono<Boolean> existsByName(String name) {
        return franchiseRepository.existsByName(name);
    }

    public Mono<Void> deleteByName(String name) {
        return franchiseRepository.deleteByName(name);
    }

    public Mono<Void> deleteAll() {
        return franchiseRepository.deleteAll();
    }

    // Método para obtener estadísticas de una franquicia
    public Mono<FranchiseStatsDTO> getFranchiseStats(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franchise not found with id: " + franchiseId)))
                .map(franchise -> {
                    int totalBranches = franchise.getBranches().size();
                    int totalProducts = franchise.getTotalProducts();
                    int totalStock = franchise.getBranches().stream()
                            .flatMap(branch -> branch.getProducts().stream())
                            .mapToInt(Product::getStock)
                            .sum();

                    return new FranchiseStatsDTO(
                            franchise.getName(),
                            totalBranches,
                            totalProducts,
                            totalStock);
                });
    }

    // Clase interna para estadísticas
    public static class FranchiseStatsDTO {
        private String franchiseName;
        private int totalBranches;
        private int totalProducts;
        private int totalStock;

        public FranchiseStatsDTO(String franchiseName, int totalBranches, int totalProducts, int totalStock) {
            this.franchiseName = franchiseName;
            this.totalBranches = totalBranches;
            this.totalProducts = totalProducts;
            this.totalStock = totalStock;
        }

        // Getters
        public String getFranchiseName() {
            return franchiseName;
        }

        public int getTotalBranches() {
            return totalBranches;
        }

        public int getTotalProducts() {
            return totalProducts;
        }

        public int getTotalStock() {
            return totalStock;
        }
    }
}
