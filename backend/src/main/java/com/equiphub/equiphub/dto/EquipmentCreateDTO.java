package com.equiphub.equiphub.dto;

import com.equiphub.equiphub.model.enums.EquipmentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record EquipmentCreateDTO(
        @NotBlank(message = "Name is required") 
        String name,
        
        String description,
        
        @NotBlank(message = "SKU is required") 
        String sku,
        
        @NotNull(message = "Daily rate is required")
        @Positive(message = "Daily rate must be positive") 
        BigDecimal dailyRate,
        
        @NotNull(message = "Category is required") 
        EquipmentCategory category
) {}
