package com.franchise.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Branch {
    @Id
    private String id;

    @NotBlank(message = "Branch name is required")
    private String name;

    @Valid
    private List<Product> products = new ArrayList<>();

    // Constructors
    public Branch() {}

    public Branch(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    // Methods 
    public void addProduct(Product product) {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        this.products.add(product);
    }

    public boolean removeProduct(String productId) {
        if (this.products == null) return false;
        return this.products.removeIf(p -> p.getId() != null && p.getId().equals(productId));
    }

    public Product findProductById(String productId) {
        if (this.products == null) return null;
        return this.products.stream()
                .filter(p -> p.getId() != null && p.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "Branch{id='" + id + "', name='" + name + "', products=" +
                (products != null ? products.size() : 0) + " products}";
    }
}
