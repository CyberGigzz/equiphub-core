package com.equiphub.equiphub.controller;

import com.equiphub.equiphub.dto.EquipmentCreateDTO;
import com.equiphub.equiphub.dto.EquipmentDTO;
import com.equiphub.equiphub.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDTO> getEquipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @PostMapping
    public ResponseEntity<EquipmentDTO> createEquipment(@Valid @RequestBody EquipmentCreateDTO createDTO) {
        EquipmentDTO createdEquipment = equipmentService.createEquipment(createDTO);
        return new ResponseEntity<>(createdEquipment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentDTO> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentCreateDTO updateDTO) {
        return ResponseEntity.ok(equipmentService.updateEquipment(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}

