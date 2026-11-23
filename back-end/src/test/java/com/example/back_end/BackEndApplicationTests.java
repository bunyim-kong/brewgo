package com.example.back_end;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ActiveProfiles("demo")
@SpringBootTest(properties = {
        "BREWGO_DEMO_ADMIN_EMAIL=admin@example.test",
        "BREWGO_DEMO_ADMIN_PASSWORD=demo-test-password",
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
        mvc.perform(get("/api/branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("coffee-bar"));
        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Coffee"));
        mvc.perform(get("/api/products").param("q", "iced latte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].basePrice").value("2.50"));
        mvc.perform(get("/api/products").param("size", "101"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/products").param("branchId", "999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void readsProductDetailsBySlugAndId() throws Exception {
        mvc.perform(get("/api/products/iced-latte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Iced Latte"))
                .andExpect(jsonPath("$.allergens").value("Milk"))
                .andExpect(jsonPath("$.basePrice").value("2.50"));
        long productId = jdbc.sql("SELECT id FROM products WHERE slug = 'iced-latte'").query(Long.class).single();
        mvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("iced-latte"));
        mvc.perform(get("/api/products/unknown-product"))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminCreatesUpdatesAndDisablesProducts() throws Exception {
        mvc.perform(get("/api/admin/products")).andExpect(status().isUnauthorized());
        var csrf = mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized()).andReturn();
        var session = login(csrf, "admin@example.test", "demo-test-password");
        String token = tokenFrom(mvc.perform(get("/api/auth/me").session(session)).andReturn());
        String adminPath = "/api/admin/products";

        mvc.perform(post(adminPath).session(session).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mvc.perform(post(adminPath).session(session).header("X-CSRF-TOKEN", token)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());

        long categoryId = jdbc.sql("SELECT id FROM categories WHERE slug = 'coffee'").query(Long.class).single();
        String slug = "vanilla-cold-brew-" + UUID.randomUUID();
        String product = """
                {"categoryId":%d,"name":"Vanilla Cold Brew","slug":"%s","basePrice":"3.00","allergens":"Milk"}
                """.formatted(categoryId, slug);
        var created = mvc.perform(post(adminPath).session(session).header("X-CSRF-TOKEN", token)
                        .contentType(MediaType.APPLICATION_JSON).content(product))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andReturn();
        String productPath = created.getResponse().getHeader("Location");

        mvc.perform(post(adminPath).session(session).header("X-CSRF-TOKEN", token)
                        .contentType(MediaType.APPLICATION_JSON).content(product))
                .andExpect(status().isConflict());
        mvc.perform(get("/api/products/{slug}", slug)).andExpect(status().isOk());
        mvc.perform(get(adminPath).session(session).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1));

        mvc.perform(patch(productPath).session(session).header("X-CSRF-TOKEN", token)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"basePrice":"3.75","allergens":""}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice").value("3.75"))
                .andExpect(jsonPath("$.name").value("Vanilla Cold Brew"))
                .andExpect(jsonPath("$.allergens").isEmpty());

        long auditEntries = jdbc.sql("""
                SELECT COUNT(*) FROM product_price_history h
                JOIN products p ON p.id = h.product_id
                JOIN users u ON u.id = h.actor_user_id
                WHERE p.slug = :slug AND u.email = 'admin@example.test'
                    AND h.previous_base_price = 3.00 AND h.new_base_price = 3.75
                """)
                .param("slug", slug).query(Long.class).single();
        assertEquals(1, auditEntries);

        mvc.perform(delete(productPath).session(session).header("X-CSRF-TOKEN", token))
                .andExpect(status().isNoContent());
        mvc.perform(delete(productPath).session(session).header("X-CSRF-TOKEN", token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/products/{slug}", slug)).andExpect(status().isNotFound());
        mvc.perform(get("/api/products").param("q", "Vanilla Cold Brew"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0));
        mvc.perform(get(productPath).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void signsInWithCsrfAndInvalidatesTheSessionOnLogout() throws Exception {
        mvc.perform(post("/api/auth/login")).andExpect(status().isForbidden());

        var csrf = mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized()).andReturn();
        var staffSession = login(csrf, "ADMIN@example.test", "demo-test-password");

        mvc.perform(get("/api/auth/me").session(staffSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(post("/api/auth/logout").session(staffSession).header("X-CSRF-TOKEN", tokenFrom(csrf)))
                .andExpect(status().isForbidden());

        var newCsrf = mvc.perform(get("/api/auth/me").session(staffSession))
                .andExpect(status().isOk())
                .andReturn();
        mvc.perform(post("/api/auth/logout").session(staffSession).header("X-CSRF-TOKEN", tokenFrom(newCsrf)))
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

        var csrf = mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized()).andReturn();
        var staffSession = login(csrf, "barista@example.test", "barista-test-password");
        mvc.perform(get("/api/auth/me").session(staffSession)).andExpect(jsonPath("$.role").value("BARISTA"));
        mvc.perform(get("/api/admin/products").session(staffSession)).andExpect(status().isForbidden());
        String token = tokenFrom(mvc.perform(get("/api/auth/me").session(staffSession)).andReturn());
        mvc.perform(post("/api/admin/products").session(staffSession).header("X-CSRF-TOKEN", token)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    private MockHttpSession login(MvcResult csrf, String email, String password) throws Exception {
        var session = (MockHttpSession) csrf.getRequest().getSession(false);
        var login = mvc.perform(post("/api/auth/login")
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
