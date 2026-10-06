package com.equiphub.equiphub.service;

import com.equiphub.equiphub.dto.EquipmentCreateDTO;
import com.equiphub.equiphub.dto.EquipmentDTO;
import com.equiphub.equiphub.model.enums.EquipmentCategory;
import com.equiphub.equiphub.model.enums.EquipmentStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests against a real Postgres. Only covers what a mocked repository can't:
 * generated ids, JPA lifecycle hooks, column mapping and database constraints.
 * Service logic itself is covered in EquipmentServiceUnitTest.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional // Rolls back after each test, so every test starts with an empty table
class EquipmentServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EntityManager entityManager;

    private EquipmentCreateDTO createRequest(String sku) {
        return new EquipmentCreateDTO(
                "Bulldozer",
                "Heavy duty yellow bulldozer",
                sku,
                new BigDecimal("499.99"),
                EquipmentCategory.HEAVY
        );
    }

    @Test
    void createShouldGenerateIdAndTimestamps() {
        EquipmentDTO created = equipmentService.createEquipment(createRequest("BULL-1000"));

        assertThat(created.id()).isNotNull();
        assertThat(created.createdAt()).isNotNull(); // set by @PrePersist
        assertThat(created.updatedAt()).isNotNull();
    }

    @Test
    void createdEquipmentShouldBeReadBackFromDatabaseUnchanged() {
        EquipmentDTO created = equipmentService.createEquipment(createRequest("BULL-1000"));

        // Write pending changes to the DB and empty Hibernate's cache,
        // so the read below runs a real SELECT instead of returning the cached object
        entityManager.flush();
        entityManager.clear();

        EquipmentDTO found = equipmentService.getEquipmentById(created.id());

        // Postgres stores timestamps with lower precision than Java, so they're compared separately
        assertThat(found)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(created);
        assertThat(found.status()).isEqualTo(EquipmentStatus.AVAILABLE);
        assertThat(found.createdAt()).isNotNull();
    }

    @Test
    void createShouldFailWhenSkuAlreadyExists() {
        equipmentService.createEquipment(createRequest("BULL-1000"));

        assertThatThrownBy(() -> equipmentService.createEquipment(createRequest("BULL-1000")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
