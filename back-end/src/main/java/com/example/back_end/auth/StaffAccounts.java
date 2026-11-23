package com.example.back_end.auth;

import java.util.Locale;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
class StaffAccounts implements UserDetailsService {
    private final JdbcClient jdbc;

    StaffAccounts(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return jdbc.sql("""
                SELECT id, branch_id, name, email, password_hash, role, enabled
                FROM users WHERE email = :email
                """)
                .param("email", normalizeEmail(username))
                .query((row, index) -> new StaffPrincipal(
                        row.getLong("id"), row.getObject("branch_id", Long.class), row.getString("name"),
                        row.getString("email"), row.getString("password_hash"),
                        StaffPrincipal.Role.valueOf(row.getString("role")), row.getBoolean("enabled")))
                .optional()
                .orElseThrow(() -> new UsernameNotFoundException("Staff account not found"));
    }

    static String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
