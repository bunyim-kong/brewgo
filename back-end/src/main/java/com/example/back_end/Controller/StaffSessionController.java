package com.example.back_end.Controller;

import com.example.back_end.auth.StaffPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class StaffSessionController {
    @GetMapping("/me")
    ResponseEntity<StaffSession> me(@AuthenticationPrincipal StaffPrincipal staff, CsrfToken token) {
        var response = ResponseEntity.status(staff == null ? 401 : 200)
                .header(token.getHeaderName(), token.getToken());

        return staff == null
                ? response.build()
                : response.body(new StaffSession(staff.id(), staff.name(), staff.role(), staff.branchId()));
    }

    record StaffSession(long id, String name, StaffPrincipal.Role role, Long branchId) {}
}
