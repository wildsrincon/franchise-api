package com.franchise.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "franchises")
public class Franchise {
    @Id
    private String id;

    @NotBlank(message = "Franchise name is required")
    @Indexed(unique = true)
    private String name;

    @Valid
    private List<Branch> branches = new ArrayList<>();

    // Constructors
    public Franchise() {}

    public Franchise(String name) {
        this.name = name;
        this.branches = new ArrayList<>();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Branch> getBranches() { return branches; }
    public void setBranches(List<Branch> branches) {
        this.branches = branches != null ? branches : new ArrayList<>();
    }

    // Methods
    public void addBranch(Branch branch) {
        if (this.branches == null) {
            this.branches = new ArrayList<>();
        }
        this.branches.add(branch);
    }

    public boolean removeBranch(String branchId) {
        if (this.branches == null) return false;
        return this.branches.removeIf(b -> b.getId() != null && b.getId().equals(branchId));
    }

    public Branch findBranchById(String branchId) {
        if (this.branches == null) return null;
        return this.branches.stream()
                .filter(b -> b.getId() != null && b.getId().equals(branchId))
                .findFirst()
                .orElse(null);
    }

    public int getTotalProducts() {
        if (this.branches == null) return 0;
        return this.branches.stream()
                .mapToInt(branch -> branch.getProducts() != null ? branch.getProducts().size() : 0)
                .sum();
    }

    @Override
    public String toString() {
        return "Franchise{id='" + id + "', name='" + name + "', branches=" +
                (branches != null ? branches.size() : 0) + " branches}";
    }
}

