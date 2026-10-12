package com.agriflow.backend.service;

import com.agriflow.backend.dto.HealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HealthService {

    private static final Logger log = LoggerFactory.getLogger(HealthService.class);

    private final JdbcTemplate jdbcTemplate;

    public HealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public HealthResponse checkHealth() {
        Map<String, Object> dbDetails = new LinkedHashMap<>();
        String overallStatus = "UP";

        try {
            // Explicit SQL check 1: Ping connectivity
            Integer ping = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (ping == null || ping != 1) {
                throw new IllegalStateException("Database ping query failed");
            }

            // Explicit SQL check 2: Active schema name
            String currentDb = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);

            // Explicit SQL check 3: Table count in active database
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()",
                Integer.class
            );

            // Metadata check: Database engine and version
            String engineInfo = jdbcTemplate.execute((ConnectionCallback<String>) con -> {
                var meta = con.getMetaData();
                return meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion();
            });

            dbDetails.put("status", "UP");
            dbDetails.put("databaseName", currentDb != null ? currentDb : "unknown");
            dbDetails.put("tableCount", tableCount != null ? tableCount : 0);
            dbDetails.put("databaseProduct", engineInfo != null ? engineInfo : "unknown");

        } catch (Exception ex) {
            log.error("Database health check failed: {}", ex.getMessage());
            overallStatus = "DOWN";
            dbDetails.put("status", "DOWN");
            dbDetails.put("error", "Database connection unavailable");
        }

        return new HealthResponse(
            overallStatus,
            "AgriFlow Backend",
            Instant.now(),
            dbDetails
        );
    }
}
