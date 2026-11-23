package com.example.back_end.auth;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public final class StaffPrincipal extends User {
    private final long id;
    private final Long branchId;
    private final String name;
    private final Role role;

    public enum Role { ADMIN, CASHIER, BARISTA }

    StaffPrincipal(long id, Long branchId, String name, String email, String passwordHash, Role role, boolean enabled) {
        super(email, passwordHash, enabled, true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
        this.id = id;
        this.branchId = branchId;
        this.name = name;
        this.role = role;
    }

    public long id() {
        return id;
    }

    public Long branchId() {
        return branchId;
    }

    public String name() {
        return name;
    }

    public Role role() {
        return role;
    }
}
