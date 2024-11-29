package com.example.optimizationservice.controller;

import com.example.optimizationservice.service.OptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OptimizationController {
    @Autowired
    private OptimizationService optimizationService;

    @GetMapping("/optimize")
    public String optimize() {
        optimizationService.optimizeNetwork();
        return "Optimization triggered";
    }
}
