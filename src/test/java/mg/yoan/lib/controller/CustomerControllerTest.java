package mg.yoan.lib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import mg.yoan.lib.endpoint.rest.controller.health.CustomerController;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Customer;
import mg.yoan.lib.model.dto.CustomerRequest;
import mg.yoan.lib.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean CustomerService customerService;

  @Test
  void addCustomer_returns201() throws Exception {
    CustomerRequest request =
        CustomerRequest.builder().fullName("Jean Dupont").email("jean@test.com").build();
    Customer created =
        Customer.builder()
            .id(UUID.randomUUID())
            .fullName("Jean Dupont")
            .email("jean@test.com")
            .build();

    when(customerService.create(any(CustomerRequest.class))).thenReturn(created);

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void getCustomerById_withUnknownId_returns404() throws Exception {
    UUID id = UUID.randomUUID();
    when(customerService.getById(id))
        .thenThrow(new NotFoundException("Customer with id " + id + " not found"));

    mockMvc.perform(get("/customers/{id}", id)).andExpect(status().isNotFound());
  }
}
