package com.equiphub.equiphub.dto;

import com.equiphub.equiphub.model.enums.EquipmentCategory;
import com.equiphub.equiphub.model.enums.EquipmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EquipmentDTO(
        Long id,
        String name,
        String description,
        String sku,
        BigDecimal dailyRate,
        EquipmentCategory category,
        EquipmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
