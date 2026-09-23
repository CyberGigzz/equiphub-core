package com.equiphub.equiphub.repository;

import com.equiphub.equiphub.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}

