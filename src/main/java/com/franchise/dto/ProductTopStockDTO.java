package com.franchise.dto;

public class ProductTopStockDTO {
    private String productName;
    private String branchName;
    private Integer stock;

    // Constructors
    public ProductTopStockDTO() {}

    public ProductTopStockDTO(String productName, String branchName, Integer stock) {
        this.productName = productName;
        this.branchName = branchName;
        this.stock = stock;
    }

    // Getters y Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "ProductTopStockDTO{" +
                "productName='" + productName + '\'' +
                ", branchName='" + branchName + '\'' +
                ", stock=" + stock +
                '}';
    }
}

