package com.equiphub.equiphub.controller;

import com.equiphub.equiphub.dto.EquipmentCreateDTO;
import com.equiphub.equiphub.model.enums.EquipmentCategory;
import com.equiphub.equiphub.service.EquipmentService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EquipmentController.class) // Only boots up the web layer, NOT the whole app
class EquipmentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc; // Used to simulate HTTP requests like Postman

    @Autowired
    private ObjectMapper objectMapper; // Converts Java objects to JSON

    @MockitoBean // Creates a fake service for the controller to use
    private EquipmentService equipmentService;

    @Test
    void createEquipment_ShouldReturn400BadRequest_WhenValidationFails() throws Exception {
        // Arrange: Create an invalid request (missing Name and negative DailyRate)
        EquipmentCreateDTO badRequest = new EquipmentCreateDTO(
                "", // Blank name (should fail @NotBlank)
                "Broken drill",
                "DR-001",
                new BigDecimal("-10.00"), // Negative rate (should fail @Positive)
                EquipmentCategory.POWER_TOOLS
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest()) // Expect 400
                .andExpect(jsonPath("$.name").value("Name is required")) // Checking our exact GlobalExceptionHandler output
                .andExpect(jsonPath("$.dailyRate").value("Daily rate must be positive"));
    }
}
