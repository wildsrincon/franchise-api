package com.franchise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.franchise.dto.ApiResponseDTO;
import com.franchise.dto.ProductTopStockDTO;
import com.franchise.dto.UpdateNameDTO;
import com.franchise.dto.UpdateStockDTO;
import com.franchise.model.Branch;
import com.franchise.model.Franchise;
import com.franchise.model.Product;
import com.franchise.service.FranchiseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franchises")
@CrossOrigin(origins = "*")
@Tag(name = "Franchises", description = "👥 API para gestión de franquicias")
public class FranchiseController {

        @Autowired
        private FranchiseService franchiseService;

        // ==================== ENDPOINTS DE FRANQUICIA ====================

        @PostMapping
        @Operation(summary = "✨ Crear nueva franquicia", description = "Crea una nueva franquicia en el sistema. El nombre debe ser único.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Franquicia creada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos o franquicia ya existe")
        })
        public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> createFranchise(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la franquicia a crear") @Valid @RequestBody Franchise franchise) {
                return franchiseService.createFranchise(franchise)
                                .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                                                .body(ApiResponseDTO.success("Franchise created successfully", saved)))
                                .onErrorReturn(ResponseEntity.badRequest()
                                                .body(ApiResponseDTO.error("Failed to create franchise")));
        }

        @GetMapping
        @Operation(summary = "📋 Listar todas las franquicias", description = "Obtiene una lista de todas las franquicias registradas en el sistema")
        public Flux<Franchise> getAllFranchises() {
                return franchiseService.getAllFranchises();
        }

        @GetMapping("/{id}")
        @Operation(summary = "🔍 Buscar franquicia por ID", description = "Obtiene los detalles de una franquicia específica usando su ID único")
        public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> getFranchiseById(@PathVariable String id) {
                return franchiseService.getFranchiseById(id)
                                .map(franchise -> ResponseEntity.ok(
                                                ApiResponseDTO.success("Franchise found", franchise)))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @GetMapping("/name/{name}")
        @Operation(summary = "🏷️ Buscar franquicia por nombre", description = "Obtiene los detalles de una franquicia usando su nombre exacto")
        public Mono<ResponseEntity<ApiResponseDTO<Franchise>>> getFranchiseByName(@PathVariable String name) {
                return franchiseService.getFranchiseByName(name)
                                .map(franchise -> ResponseEntity.ok(
                                                ApiResponseDTO.success("Franchise found", franchise)))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}/name")
        @Operation(summary = "✏️ Actualizar nombre de franquicia", description = "Modifica el nombre de una franquicia existente")
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
        @Operation(summary = "🗑️ Eliminar franquicia", description = "Elimina permanentemente una franquicia y todas sus sucursales y productos")
        public Mono<ResponseEntity<ApiResponseDTO<String>>> deleteFranchise(@PathVariable String id) {
                return franchiseService.deleteFranchise(id)
                                .then(Mono.just(ResponseEntity.ok(
                                                ApiResponseDTO.success("Franchise deleted successfully",
                                                                "Franchise with id " + id + " deleted"))))
                                .onErrorReturn(ResponseEntity.badRequest()
                                                .body(ApiResponseDTO.error("Failed to delete franchise")));
        }

        @GetMapping("/{id}/stats")
        @Operation(summary = "🏢 Ver estadistica de franquicia", description = "Ver estadistica de la franquicia por ID")
        public Mono<ResponseEntity<ApiResponseDTO<FranchiseService.FranchiseStatsDTO>>> getFranchiseStats(
                        @PathVariable String id) {
                return franchiseService.getFranchiseStats(id)
                                .map(stats -> ResponseEntity.ok(
                                                ApiResponseDTO.success("Franchise statistics retrieved", stats)))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        // ==================== ENDPOINTS DE SUCURSAL ====================

        @PostMapping("/{franchiseId}/branches")
        @Operation(summary = "🏢 Agregar sucursal")
        @Tag(name = "Branches")
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
        @Operation(summary = "🏢 Actualizar nombre sucursal")
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
        @Operation(summary = "🗑️ Eliminar sucursal de una franquicia", description = "Elimina permanentemente una sucursal de una franquicia")
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
        @Operation(summary = "📦 Agregar producto")
        @Tag(name = "Products")
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
        @Operation(summary = "🗑️ Eliminar producto de una sucursal", description = "Elimina permanentemente un producto de una sucursal")
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
        @Operation(summary = "📦 Actualizar stock de producto")
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
        @Operation(summary = "📦 Actualizar nombre de un producto")
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
        @Operation(summary = "📦 Ver productos con mas stock por sucursal de una franquicia")
        @Tag(name = "Top Stock Product")
        public Mono<ResponseEntity<ApiResponseDTO<List<ProductTopStockDTO>>>> getTopStockProducts(
                        @PathVariable String franchiseId) {
                return franchiseService.getTopStockProductsByFranchise(franchiseId)
                                .map(products -> ResponseEntity.ok(
                                                ApiResponseDTO.success("Top stock products retrieved", products)))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        // Endpoint para búsqueda con filtros
        @GetMapping("/search")
        @Operation(summary = "📦 Busquedas con filtros")
        @Tag(name = "Search")
        public Flux<Franchise> searchFranchises(
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false, defaultValue = "0") int minBranches,
                        @RequestParam(required = false, defaultValue = "0") int minProducts) {

                return franchiseService.getAllFranchises()
                                .filter(franchise -> {
                                        boolean nameMatch = name == null || franchise.getName().toLowerCase()
                                                        .contains(name.toLowerCase());
                                        boolean branchMatch = franchise.getBranches().size() >= minBranches;
                                        boolean productMatch = franchise.getTotalProducts() >= minProducts;
                                        return nameMatch && branchMatch && productMatch;
                                });
        }
}
