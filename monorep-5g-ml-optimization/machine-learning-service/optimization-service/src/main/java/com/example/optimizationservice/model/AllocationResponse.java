package com.example.optimizationservice.model;
import java.util.List;

public class AllocationResponse {
    private List<Allocation> allocations;

    public List<Allocation> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<Allocation> allocations) {
        this.allocations = allocations;
    }
}
