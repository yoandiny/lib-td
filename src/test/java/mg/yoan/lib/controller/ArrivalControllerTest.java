package mg.yoan.lib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import mg.yoan.lib.endpoint.rest.controller.health.ArrivalController;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Arrival;
import mg.yoan.lib.model.dto.ArrivalLineRequest;
import mg.yoan.lib.model.dto.ArrivalRequest;
import mg.yoan.lib.service.ArrivalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ArrivalController.class)
class ArrivalControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean ArrivalService arrivalService;

  @Test
  void createArrival_returns201() throws Exception {
    ArrivalLineRequest lineRequest = new ArrivalLineRequest();
    lineRequest.setBookEditionId(UUID.randomUUID());
    lineRequest.setQuantity(10);

    ArrivalRequest request = new ArrivalRequest();
    request.setLines(List.of(lineRequest));

    Arrival created = Arrival.builder().id(UUID.randomUUID()).build();
    when(arrivalService.create(any(ArrivalRequest.class))).thenReturn(created);

    mockMvc
        .perform(
            post("/arrivals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void getArrivalById_withUnknownId_returns404() throws Exception {
    UUID id = UUID.randomUUID();
    when(arrivalService.getById(id))
        .thenThrow(new NotFoundException("Arrival with id " + id + " not found"));

    mockMvc.perform(get("/arrivals/{id}", id)).andExpect(status().isNotFound());
  }
}
