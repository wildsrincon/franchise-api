package com.franchise.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateStockDTO {
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    // Constructors
    public UpdateStockDTO() {}

    public UpdateStockDTO(Integer stock) {
        this.stock = stock;
    }

    // Getters y Setters
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "UpdateStockDTO{stock=" + stock + "}";
    }
}
