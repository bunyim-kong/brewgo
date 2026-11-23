package com.example.back_end.auth;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;

@Profile("demo")
@Configuration
class DemoStaffSetup {
    @Bean
    ApplicationRunner demoAdmin(
            JdbcClient jdbc, PasswordEncoder encoder,
            @Value("${brewgo.demo.admin.email:${BREWGO_DEMO_ADMIN_EMAIL:}}") String email,
            @Value("${brewgo.demo.admin.password:${BREWGO_DEMO_ADMIN_PASSWORD:}}") String password
    ) {
        return args -> {
            if (email.isBlank() && password.isEmpty()) {
                return;
            }
            if (email.isBlank() || password.length() < 12 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new IllegalArgumentException("Set a demo admin email and a password of 12 to 72 UTF-8 bytes");
            }

            jdbc.sql("""
                    INSERT INTO users (name, email, password_hash, role)
                    VALUES ('Demo Admin', :email, :hash, 'ADMIN')
                    ON DUPLICATE KEY UPDATE id = id
                    """)
                    .param("email", StaffAccounts.normalizeEmail(email))
                    .param("hash", encoder.encode(password))
                    .update();
        };
    }
}
