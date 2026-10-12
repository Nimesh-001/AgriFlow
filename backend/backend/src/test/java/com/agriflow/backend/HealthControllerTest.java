package com.agriflow.backend;

import com.agriflow.backend.controller.HealthController;
import com.agriflow.backend.dto.HealthResponse;
import com.agriflow.backend.service.HealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class HealthControllerTest {

    @Autowired
    private HealthController healthController;

    @Autowired
    private HealthService healthService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(healthController).build();
    }

    @Test
    void testHealthServiceDirectly() {
        HealthResponse response = healthService.checkHealth();
        assertThat(response.status()).isEqualTo("UP");
        assertThat(response.application()).isEqualTo("AgriFlow Backend");
        assertThat(response.database().get("status")).isEqualTo("UP");
        assertThat(response.database().get("databaseName")).isEqualTo("agriflow_db");
        assertThat((Integer) response.database().get("tableCount")).isEqualTo(44);
    }

    @Test
    void testHealthEndpointReturnsUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("AgriFlow Backend"))
                .andExpect(jsonPath("$.database.status").value("UP"))
                .andExpect(jsonPath("$.database.databaseName").value("agriflow_db"))
                .andExpect(jsonPath("$.database.tableCount").value(44));
    }
}
