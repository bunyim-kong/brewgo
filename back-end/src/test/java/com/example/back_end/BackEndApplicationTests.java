package com.example.back_end;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ActiveProfiles("demo")
@SpringBootTest(properties = {
        "brewgo.demo.admin.email=admin@example.test",
        "brewgo.demo.admin.password=demo-test-password",
        "server.servlet.session.cookie.secure=false"
})
@AutoConfigureMockMvc
class BackEndApplicationTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JdbcClient jdbc;
    @Autowired
    private PasswordEncoder passwords;

    @Test
    void readsDemoCatalogFromFreshlyMigratedMysql() throws Exception {
        mvc.perform(get("/api/v1/branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("coffee-bar"));
        mvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Coffee"));
        mvc.perform(get("/api/v1/products").param("q", "iced latte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].basePrice").value("2.50"));
        mvc.perform(get("/api/v1/products").param("size", "101"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/products").param("branchId", "999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void readsProductDetailsBySlugAndId() throws Exception {
        mvc.perform(get("/api/v1/products/iced-latte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Iced Latte"))
                .andExpect(jsonPath("$.allergens").value("Milk"))
                .andExpect(jsonPath("$.basePrice").value("2.50"));
        long productId = jdbc.sql("SELECT id FROM products WHERE slug = 'iced-latte'").query(Long.class).single();
        mvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("iced-latte"));
        mvc.perform(get("/api/v1/products/unknown-product"))
                .andExpect(status().isNotFound());
    }

    @Test
    void signsInWithCsrfAndInvalidatesTheSessionOnLogout() throws Exception {
        mvc.perform(post("/api/v1/auth/login")).andExpect(status().isForbidden());

        var csrf = mvc.perform(get("/api/v1/auth/me")).andExpect(status().isUnauthorized()).andReturn();
        var staffSession = login(csrf, "ADMIN@example.test", "demo-test-password");

        mvc.perform(get("/api/v1/auth/me").session(staffSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(post("/api/v1/auth/logout").session(staffSession).header("X-CSRF-TOKEN", tokenFrom(csrf)))
                .andExpect(status().isForbidden());

        var newCsrf = mvc.perform(get("/api/v1/auth/me").session(staffSession))
                .andExpect(status().isOk())
                .andReturn();
        mvc.perform(post("/api/v1/auth/logout").session(staffSession).header("X-CSRF-TOKEN", tokenFrom(newCsrf)))
                .andExpect(status().isNoContent());
        assertTrue(staffSession.isInvalid());
    }

    @Test
    void baristaCannotAccessAdminEndpoints() throws Exception {
        jdbc.sql("""
                INSERT INTO users (branch_id, name, email, password_hash, role)
                SELECT id, 'Test Barista', 'barista@example.test', :hash, 'BARISTA'
                FROM branches WHERE slug = 'coffee-bar'
                ON DUPLICATE KEY UPDATE password_hash = :hash
                """)
                .param("hash", passwords.encode("barista-test-password"))
                .update();

        var csrf = mvc.perform(get("/api/v1/auth/me")).andExpect(status().isUnauthorized()).andReturn();
        var staffSession = login(csrf, "barista@example.test", "barista-test-password");
        mvc.perform(get("/api/v1/auth/me").session(staffSession)).andExpect(jsonPath("$.role").value("BARISTA"));
        mvc.perform(get("/api/v1/admin/products").session(staffSession)).andExpect(status().isForbidden());
    }

    private MockHttpSession login(MvcResult csrf, String email, String password) throws Exception {
        var session = (MockHttpSession) csrf.getRequest().getSession(false);
        var login = mvc.perform(post("/api/v1/auth/login")
                        .session(session)
                        .header("X-CSRF-TOKEN", tokenFrom(csrf))
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isNoContent())
                .andReturn();
        return (MockHttpSession) login.getRequest().getSession(false);
    }

    private String tokenFrom(MvcResult result) {
        return result.getResponse().getHeader("X-CSRF-TOKEN");
    }
}
