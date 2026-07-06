package mg.yoan.lib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

import mg.yoan.lib.endpoint.rest.controller.health.FormatController;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Format;
import mg.yoan.lib.model.FormatLabel;
import mg.yoan.lib.model.dto.FormatRequest;
import mg.yoan.lib.service.FormatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FormatController.class)
class FormatControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean FormatService formatService;

    @Test
    void createFormat_returns201() throws Exception {
        FormatRequest request = new FormatRequest();
        request.setFormatLabel(FormatLabel.HARDCOVER);

        Format created = Format.builder().id(UUID.randomUUID()).formatLabel(FormatLabel.HARDCOVER).build();
        when(formatService.create(any(FormatRequest.class))).thenReturn(created);

        mockMvc
                .perform(
                        post("/formats")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getFormatById_withUnknownId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(formatService.getById(id))
                .thenThrow(new NotFoundException("Format with id " + id + " not found"));

        mockMvc.perform(get("/formats/{id}", id)).andExpect(status().isNotFound());
    }
}