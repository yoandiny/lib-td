package mg.yoan.lib.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import mg.yoan.lib.endpoint.rest.controller.health.SaleController;
import mg.yoan.lib.model.Sale;
import mg.yoan.lib.model.SaleStatus;
import mg.yoan.lib.service.SaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleController.class)
class SaleControllerTest {

  @Autowired MockMvc mockMvc;
  @MockBean SaleService saleService;

  @Test
  void validateSale_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    Sale validated = Sale.builder().id(id).status(SaleStatus.VALIDATED).build();
    when(saleService.validate(id)).thenReturn(validated);

    mockMvc.perform(put("/sales/{id}/validate", id)).andExpect(status().isOk());
  }

  @Test
  void validateSale_withInsufficientStock_returns409() throws Exception {
    UUID id = UUID.randomUUID();
    when(saleService.validate(id)).thenThrow(new IllegalStateException("Insufficient stock"));

    mockMvc.perform(put("/sales/{id}/validate", id)).andExpect(status().isConflict());
  }

  @Test
  void getSaleById_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(saleService.getById(id)).thenReturn(Sale.builder().id(id).build());

    mockMvc.perform(get("/sales/{id}", id)).andExpect(status().isOk());
  }
}
