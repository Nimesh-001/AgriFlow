package com.agriflow.backend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptHashGeneratorTest {

    @Test
    void generateAndVerifyAdminHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "AdminPassword@2026";
        String encoded = encoder.encode(rawPassword);
        System.out.println("VERIFIED_ADMIN_HASH: " + encoded);
        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
    }
}
