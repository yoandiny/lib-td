package mg.yoan.lib.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;
import mg.yoan.lib.endpoint.rest.controller.health.StockController;
import mg.yoan.lib.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockController.class)
class StockControllerTest {

  @Autowired MockMvc mockMvc;

  @MockBean StockService stockService;

  @Test
  void getStock_returns200WithStock() throws Exception {
    UUID id = UUID.randomUUID();
    when(stockService.getStock(id)).thenReturn(7);

    mockMvc
        .perform(get("/stock/book-edition/{id}/stock", id))
        .andExpect(status().isOk())
        .andExpect(content().string("7"));
  }

  @Test
  void getStock_withUnknownId_stillReturns200WithZero() throws Exception {
    UUID id = UUID.randomUUID();
    when(stockService.getStock(id)).thenReturn(0);

    mockMvc
        .perform(get("/stock/book-edition/{id}/stock", id))
        .andExpect(status().isOk())
        .andExpect(content().string("0"));
  }
}
