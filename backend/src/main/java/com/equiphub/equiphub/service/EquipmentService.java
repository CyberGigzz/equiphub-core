package com.equiphub.equiphub.service;

import com.equiphub.equiphub.dto.EquipmentCreateDTO;
import com.equiphub.equiphub.dto.EquipmentDTO;
import com.equiphub.equiphub.exception.ResourceNotFoundException;
import com.equiphub.equiphub.model.Equipment;
import com.equiphub.equiphub.model.enums.EquipmentStatus;
import com.equiphub.equiphub.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public List<EquipmentDTO> getAllEquipment() {
        return equipmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public EquipmentDTO getEquipmentById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));
        return mapToDTO(equipment);
    }

    public EquipmentDTO createEquipment(EquipmentCreateDTO createDTO) {
        Equipment equipment = new Equipment();
        equipment.setName(createDTO.name());
        equipment.setDescription(createDTO.description());
        equipment.setSku(createDTO.sku());
        equipment.setDailyRate(createDTO.dailyRate());
        equipment.setCategory(createDTO.category());
        // Default status for new equipment
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        Equipment savedEquipment = equipmentRepository.save(equipment);
        return mapToDTO(savedEquipment);
    }

    public EquipmentDTO updateEquipment(Long id, EquipmentCreateDTO updateDTO) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));

        equipment.setName(updateDTO.name());
        equipment.setDescription(updateDTO.description());
        equipment.setSku(updateDTO.sku());
        equipment.setDailyRate(updateDTO.dailyRate());
        equipment.setCategory(updateDTO.category());

        Equipment updatedEquipment = equipmentRepository.save(equipment);
        return mapToDTO(updatedEquipment);
    }

    public void deleteEquipment(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipment not found with id: " + id);
        }
        equipmentRepository.deleteById(id);
    }

    // Manual Mapper
    private EquipmentDTO mapToDTO(Equipment equipment) {
        return new EquipmentDTO(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getSku(),
                equipment.getDailyRate(),
                equipment.getCategory(),
                equipment.getStatus(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt()
        );
    }
}
