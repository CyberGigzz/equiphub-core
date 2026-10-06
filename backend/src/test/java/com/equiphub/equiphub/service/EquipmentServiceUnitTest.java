package com.equiphub.equiphub.service;

import com.equiphub.equiphub.dto.EquipmentCreateDTO;
import com.equiphub.equiphub.dto.EquipmentDTO;
import com.equiphub.equiphub.exception.ResourceNotFoundException;
import com.equiphub.equiphub.model.Equipment;
import com.equiphub.equiphub.model.enums.EquipmentCategory;
import com.equiphub.equiphub.model.enums.EquipmentStatus;
import com.equiphub.equiphub.repository.EquipmentRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceUnitTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    // ==========================================
    // HELPER METHODS (Keeps tests clean & short)
    // ==========================================
    private Equipment createDummyEquipment(Long id, String name) {
        Equipment eq = new Equipment();
        eq.setId(id);
        eq.setName(name);
        eq.setDescription("Description for " + name);
        eq.setSku("SKU-" + id);
        eq.setDailyRate(new BigDecimal("50.00"));
        eq.setCategory(EquipmentCategory.HEAVY);
        eq.setStatus(EquipmentStatus.AVAILABLE);
        return eq;
    }

    private EquipmentCreateDTO createDummyRequestDTO(String name) {
        return new EquipmentCreateDTO(
                name,
                "New description",
                "NEW-SKU",
                new BigDecimal("100.00"),
                EquipmentCategory.POWER_TOOLS
        );
    }

    // ==========================================
    // TESTS ORGANIZED BY METHOD
    // ==========================================

    @Nested
    class GetAllEquipment {
        @Test
        void shouldReturnListOfDtos() {
            // Arrange
            when(equipmentRepository.findAll()).thenReturn(List.of(
                    createDummyEquipment(1L, "Drill"),
                    createDummyEquipment(2L, "Crane")
            ));

            // Act
            List<EquipmentDTO> result = equipmentService.getAllEquipment();

            // Assert 
            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Drill");
            assertThat(result.get(1).name()).isEqualTo("Crane");
        }

        @Test
        void shouldReturnEmptyListWhenDatabaseIsEmpty() {
            when(equipmentRepository.findAll()).thenReturn(List.of());

            List<EquipmentDTO> result = equipmentService.getAllEquipment();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetEquipmentById {
        @Test
        void shouldReturnDtoWhenFound() {
            when(equipmentRepository.findById(1L)).thenReturn(Optional.of(createDummyEquipment(1L, "Drill")));

            EquipmentDTO result = equipmentService.getEquipmentById(1L);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Drill");
        }

        @Test
        void shouldThrowExceptionWhenNotFound() {
            when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> equipmentService.getEquipmentById(99L));
        }
    }

    @Nested
    class CreateEquipment {
        @Test
        void shouldSaveAndReturnDtoWithAvailableStatus() {
            EquipmentCreateDTO request = createDummyRequestDTO("Bulldozer");
            Equipment savedEntity = createDummyEquipment(1L, "Bulldozer");

            when(equipmentRepository.save(any(Equipment.class))).thenReturn(savedEntity);

            EquipmentDTO result = equipmentService.createEquipment(request);

            // What the service returned: only savedEntity has an id, so this proves
            // the response is mapped from what save() returned, not the unsaved object
            assertThat(result.id()).isEqualTo(1L);

            // What the service built and passed to save()
            ArgumentCaptor<Equipment> captor = ArgumentCaptor.forClass(Equipment.class);
            verify(equipmentRepository).save(captor.capture());
            Equipment capturedEntity = captor.getValue();

            assertThat(capturedEntity.getId()).isNull(); // the database assigns ids
            assertThat(capturedEntity.getStatus()).isEqualTo(EquipmentStatus.AVAILABLE);
            assertThat(capturedEntity.getName()).isEqualTo(request.name());
            assertThat(capturedEntity.getDescription()).isEqualTo(request.description());
            assertThat(capturedEntity.getSku()).isEqualTo(request.sku());
            assertThat(capturedEntity.getDailyRate()).isEqualTo(request.dailyRate());
            assertThat(capturedEntity.getCategory()).isEqualTo(request.category());
        }
    }

    @Nested
    class UpdateEquipment {
        @Test
        void shouldUpdateAndReturnDtoWhenFound() {
            Equipment existingEntity = createDummyEquipment(1L, "Old Drill");
            EquipmentCreateDTO updateRequest = createDummyRequestDTO("New Super Drill");
            
            when(equipmentRepository.findById(1L)).thenReturn(Optional.of(existingEntity));
            when(equipmentRepository.save(any(Equipment.class))).thenAnswer(i -> i.getArgument(0)); // Returns whatever was saved

            EquipmentDTO result = equipmentService.updateEquipment(1L, updateRequest);

            assertThat(result.name()).isEqualTo("New Super Drill");
            verify(equipmentRepository).save(existingEntity); // Prove it actually called save
        }

        @Test
        void shouldThrowExceptionWhenNotFound() {
            EquipmentCreateDTO updateRequest = createDummyRequestDTO("New Drill");
            when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> equipmentService.updateEquipment(99L, updateRequest));
            verify(equipmentRepository, never()).save(any()); // Prove it completely aborted and didn't save!
        }
    }

    @Nested
    class DeleteEquipment {
        @Test
        void shouldDeleteWhenFound() {
            when(equipmentRepository.existsById(1L)).thenReturn(true);

            equipmentService.deleteEquipment(1L);

            verify(equipmentRepository).deleteById(1L); // Prove it sent the delete command
        }

        @Test
        void shouldThrowExceptionWhenNotFound() {
            when(equipmentRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> equipmentService.deleteEquipment(99L));
            verify(equipmentRepository, never()).deleteById(any()); // Prove it aborted the deletion
        }
    }
}
