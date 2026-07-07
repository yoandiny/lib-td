package mg.yoan.lib.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;
import mg.yoan.lib.endpoint.rest.controller.health.ReportController;
import mg.yoan.lib.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

  @Autowired MockMvc mockMvc;
  @MockBean ReportService reportService;

  @Test
  void getRevenueByGenre_returns200WithMap() throws Exception {
    when(reportService.getRevenueByGenre()).thenReturn(Map.of("Roman", new BigDecimal("39.80")));

    mockMvc
        .perform(get("/reports/revenue-by-genre"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.Roman").value(39.80));
  }
}
