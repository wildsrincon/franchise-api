package com.franchise.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

public class Product {
    @Id
    private String id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    // Constructores
    public Product() {}

    public Product(String name, Integer stock) {
        this.name = name;
        this.stock = stock;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "', stock=" + stock + "}";
    }
}
