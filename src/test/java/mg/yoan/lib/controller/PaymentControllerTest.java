package mg.yoan.lib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import mg.yoan.lib.endpoint.rest.controller.health.PaymentController;
import mg.yoan.lib.model.Payment;
import mg.yoan.lib.model.PaymentMethod;
import mg.yoan.lib.model.PaymentStatus;
import mg.yoan.lib.model.dto.PaymentRequest;
import mg.yoan.lib.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean PaymentService paymentService;

  @Test
  void createPayment_returns201() throws Exception {
    PaymentRequest request = new PaymentRequest();
    request.setSaleId(UUID.randomUUID());
    request.setMethod(PaymentMethod.CARD);

    Payment created = Payment.builder().id(UUID.randomUUID()).status(PaymentStatus.PENDING).build();
    when(paymentService.create(any(PaymentRequest.class))).thenReturn(created);

    mockMvc
        .perform(
            post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void processPayment_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(paymentService.process(id))
        .thenReturn(Payment.builder().id(id).status(PaymentStatus.PAID).build());

    mockMvc.perform(put("/payments/{id}/process", id)).andExpect(status().isOk());
  }
}
