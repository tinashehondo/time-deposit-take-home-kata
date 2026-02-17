package org.ikigaidigital;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class TimeDepositIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.defer-datasource-initialization", () -> "true");
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllTimeDeposits_withCorrectStructureAndValues() throws Exception {
        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                // Verify the basic plan deposit (id=1) regardless of ordering
                .andExpect(jsonPath("$[?(@.id == 1)].planType").value(hasItem("basic")))
                .andExpect(jsonPath("$[?(@.id == 1)].balance").value(hasItem(10000.0)))
                .andExpect(jsonPath("$[?(@.id == 1)].days").value(hasItem(45)))
                // There should be exactly one withdrawal for this deposit
                .andExpect(jsonPath("$[?(@.id == 1)].withdrawals[0]", hasSize(1)))
                // And that withdrawal should have the expected amount and date
                .andExpect(jsonPath("$..withdrawals[?(@.amount == 500.00)].amount").value(hasItem(500.00)))
                .andExpect(jsonPath("$..withdrawals[?(@.amount == 500.00)].date").value(hasItem("2024-01-15")));
    }

    @Test
    void shouldGetAllTimeDeposits_depositWithNoWithdrawals() throws Exception {
        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                // Deposit with id=4 has no withdrawals
                .andExpect(jsonPath("$[?(@.id == 4)].withdrawals[0]", hasSize(0)));
    }

    @Test
    void shouldUpdateBalances_andVerifyInterestApplied() throws Exception {
        mockMvc.perform(put("/api/time-deposits/update-balances"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                // Basic plan deposit (id=1) should have interest applied
                .andExpect(jsonPath("$[?(@.id == 1)].balance").value(hasItem(closeTo(10008.33, 0.01))));
    }

    @Test
    void shouldNotApplyInterest_forDepositUnder30Days() throws Exception {
        mockMvc.perform(put("/api/time-deposits/update-balances"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                // Basic plan deposit under 30 days (id=4) should not have interest applied
                .andExpect(jsonPath("$[?(@.id == 4)].balance").value(hasItem(3000.0)));
    }

    @Test
    void shouldNotApplyStudentInterest_after365Days() throws Exception {
        mockMvc.perform(put("/api/time-deposits/update-balances"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                // Student plan deposit over 365 days (id=5) should not have interest applied
                .andExpect(jsonPath("$[?(@.id == 5)].balance").value(hasItem(8000.0)));
    }

    @Test
    void shouldApplyStudentInterest_between31And365Days() throws Exception {
        mockMvc.perform(put("/api/time-deposits/update-balances"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                // Deposit with id=2 is the student plan with 90 days and 5000.00 balance
                .andExpect(jsonPath("$[?(@.id == 2)].planType").value(hasItem("student")))
                .andExpect(jsonPath("$[?(@.id == 2)].balance").value(hasItem(closeTo(5012.5, 0.01))));
    }

    @Test
    void shouldApplyPremiumInterest_after45Days() throws Exception {
        mockMvc.perform(put("/api/time-deposits/update-balances"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                // Premium plan deposit after 45 days (id=3) should have interest applied
                .andExpect(jsonPath("$[?(@.id == 3)].balance").value(hasItem(closeTo(20083.33, 0.01))));
    }

    @Test
    void shouldApplyCorrectInterestForAllDeposits() throws Exception {
        mockMvc.perform(put("/api/time-deposits/update-balances"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/time-deposits"))
                .andExpect(status().isOk())
                // basic (id=1): 10000.00 + 8.33
                .andExpect(jsonPath("$[?(@.id == 1)].balance").value(hasItem(closeTo(10008.33, 0.01))))
                // student (id=2): 5000.00 + 12.50
                .andExpect(jsonPath("$[?(@.id == 2)].balance").value(hasItem(closeTo(5012.50, 0.01))))
                // premium (id=3): 20000.00 + 83.33
                .andExpect(jsonPath("$[?(@.id == 3)].balance").value(hasItem(closeTo(20083.33, 0.01))))
                // basic under threshold (id=4): unchanged
                .andExpect(jsonPath("$[?(@.id == 4)].balance").value(hasItem(closeTo(3000.00, 0.01))))
                // student over 365 days (id=5): unchanged
                .andExpect(jsonPath("$[?(@.id == 5)].balance").value(hasItem(closeTo(8000.00, 0.01))))
                // premium (id=6): 15000.00 + 62.50
                .andExpect(jsonPath("$[?(@.id == 6)].balance").value(hasItem(closeTo(15062.50, 0.01))));
    }
}
