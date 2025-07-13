package com.franchise.controller;

import com.franchise.dto.ProductTopStockDTO;
import com.franchise.dto.UpdateNameDTO;
import com.franchise.dto.UpdateStockDTO;
import com.franchise.dto.ApiResponseDTO;
import com.franchise.model.Branch;
import com.franchise.model.Franchise;
import com.franchise.model.Product;
import com.franchise.service.FranchiseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/franchises")
@CrossOrigin(origins = "*") // Para desarrollo - en producción especificar dominios
public class FranchiseController {

    @Autowired
    private FranchiseService franchiseService;

    // ==================== ENDPOINTS DE FRANQUICIA ====================

    @PostMapping
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> createFranchise(
            @Valid @RequestBody Franchise franchise) {
        return franchiseService.createFranchise(franchise)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponseDTO.success("Franchise created successfully", saved)))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to create franchise")));
    }

    @GetMapping
    public Flux<Franchise> getAllFranchises() {
        return franchiseService.getAllFranchises();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> getFranchiseById(@PathVariable String id) {
        return franchiseService.getFranchiseById(id)
                .map(franchise -> ResponseEntity.ok(
                        ApiResponseDTO.success("Franchise found", franchise)))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> getFranchiseByName(@PathVariable String name) {
        return franchiseService.getFranchiseByName(name)
                .map(franchise -> ResponseEntity.ok(
                        ApiResponseDTO.success("Franchise found", franchise)))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/name")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> updateFranchiseName(
            @PathVariable String id,
            @Valid @RequestBody UpdateNameDTO updateNameDTO) {
        return franchiseService.updateFranchiseName(id, updateNameDTO)
                .map(updated -> ResponseEntity.ok(
                        ApiResponseDTO.success("Franchise name updated successfully", updated)))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to update franchise name")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponseDTO<String>>> deleteFranchise(@PathVariable String id) {
        return franchiseService.deleteFranchise(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponseDTO.success("Franchise deleted successfully", "Franchise with id " + id + " deleted"))))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to delete franchise")));
    }

    @GetMapping("/count")
    public Mono<ResponseEntity<ApiResponseDTO<Long>>> getFranchiseCount() {
        return franchiseService.countFranchises()
                .map(count -> ResponseEntity.ok(
                        ApiResponseDTO.success("Franchise count retrieved", count)));
    }

    @GetMapping("/{id}/stats")
    public Mono<ResponseEntity<ApiResponseDTO<FranchiseService.FranchiseStatsDTO>>> getFranchiseStats(
            @PathVariable String id) {
        return franchiseService.getFranchiseStats(id)
                .map(stats -> ResponseEntity.ok(
                        ApiResponseDTO.success("Franchise statistics retrieved", stats)))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    // ==================== ENDPOINTS DE SUCURSAL ====================

    @PostMapping("/{franchiseId}/branches")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> addBranch(
            @PathVariable String franchiseId,
            @Valid @RequestBody Branch branch) {
        return franchiseService.addBranch(franchiseId, branch)
                .map(updated -> ResponseEntity.ok(
                        ApiResponseDTO.success("Branch added successfully", updated)))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to add branch")));
    }

    @PutMapping("/{franchiseId}/branches/{branchId}/name")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> updateBranchName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody UpdateNameDTO updateNameDTO) {
        return franchiseService.updateBranchName(franchiseId, branchId, updateNameDTO)
                .map(updated -> ResponseEntity.ok(
                        ApiResponseDTO.success("Branch name updated successfully", updated)))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to update branch name")));
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> deleteBranch(
            @PathVariable String franchiseId,
            @PathVariable String branchId) {
        return franchiseService.deleteBranch(franchiseId, branchId)
                .map(updated -> ResponseEntity.ok(
                        ApiResponseDTO.success("Branch deleted successfully", updated)))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to delete branch")));
    }

    // ==================== ENDPOINTS DE PRODUCTO ====================

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> addProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody Product product) {
        return franchiseService.addProduct(franchiseId, branchId, product)
                .map(updated -> ResponseEntity.ok(
                        ApiResponseDTO.success("Product added successfully", updated)))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to add product")));
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> removeProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId) {
        return franchiseService.removeProduct(franchiseId, branchId, productId)
                .map(updated -> ResponseEntity.ok(
                        ApiResponseDTO.success("Product removed successfully", updated)))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to remove product")));
    }

    @PutMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> updateProductStock(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateStockDTO updateStockDTO) {
        return franchiseService.updateProductStock(franchiseId, branchId, productId, updateStockDTO)
                .map(updated -> ResponseEntity.ok(
                        ApiResponseDTO.success("Product stock updated successfully", updated)))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to update product stock")));
    }

    @PutMapping("/{franchiseId}/branches/{branchId}/products/{productId}/name")
    public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> updateProductName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateNameDTO updateNameDTO) {
        return franchiseService.updateProductName(franchiseId, branchId, productId, updateNameDTO)
                .map(updated -> ResponseEntity.ok(
                        ApiResponseDTO.success("Product name updated successfully", updated)))
                .onErrorReturn(ResponseEntity.badRequest()
                        .body(ApiResponseDTO.error("Failed to update product name")));
    }

    // ==================== ENDPOINTS DE REPORTES ====================

    @GetMapping("/{franchiseId}/top-stock-products")
    public Mono<ResponseEntity<ApiResponseDTO<List<ProductTopStockDTO>>>> getTopStockProducts(
            @PathVariable String franchiseId) {
        return franchiseService.getTopStockProductsByFranchise(franchiseId)
                .map(products -> ResponseEntity.ok(
                        ApiResponseDTO.success("Top stock products retrieved", products)))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    // ==================== ENDPOINTS AUXILIARES ====================

    @GetMapping("/exists/{name}")
    public Mono<ResponseEntity<ApiResponseDTO<Boolean>>> checkFranchiseExists(@PathVariable String name) {
        return franchiseService.existsByName(name)
                .map(exists -> ResponseEntity.ok(
                        ApiResponseDTO.success("Existence check completed", exists)));
    }

    @DeleteMapping("/all")
    public Mono<ResponseEntity<ApiResponseDTO<String>>> deleteAllFranchises() {
        return franchiseService.deleteAll()
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponseDTO.success("All franchises deleted", "Database cleared successfully"))));
    }

    // Endpoint para operaciones en lote (batch)
    @PostMapping("/batch")
    public Flux<Franchise> createMultipleFranchises(@RequestBody List<Franchise> franchises) {
        return Flux.fromIterable(franchises)
                .flatMap(franchiseService::createFranchise)
                .onErrorContinue((error, franchise) -> {
                    System.err.println("Failed to create franchise: " + franchise + ", Error: " + error.getMessage());
                });
    }

    // Endpoint para búsqueda con filtros
    @GetMapping("/search")
    public Flux<Franchise> searchFranchises(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int minBranches,
            @RequestParam(required = false, defaultValue = "0") int minProducts) {

        return franchiseService.getAllFranchises()
                .filter(franchise -> {
                    boolean nameMatch = name == null || franchise.getName().toLowerCase().contains(name.toLowerCase());
                    boolean branchMatch = franchise.getBranches().size() >= minBranches;
                    boolean productMatch = franchise.getTotalProducts() >= minProducts;
                    return nameMatch && branchMatch && productMatch;
                });
    }
}

