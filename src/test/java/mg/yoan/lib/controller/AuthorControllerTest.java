package mg.yoan.lib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

import mg.yoan.lib.endpoint.rest.controller.health.AuthorController;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Author;
import mg.yoan.lib.model.dto.AuthorRequest;
import mg.yoan.lib.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthorService authorService;

    @Test
    void createAuthor_returns201() throws Exception {
        AuthorRequest request = new AuthorRequest();
        request.setLastName("Hugo");
        request.setFirstName("Victor");

        Author created =
                Author.builder().id(UUID.randomUUID()).lastName("Hugo").firstName("Victor").build();
        when(authorService.create(any(AuthorRequest.class))).thenReturn(created);

        mockMvc
                .perform(
                        post("/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lastName").value("Hugo"));
    }

    @Test
    void getAuthorById_withUnknownId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(authorService.getById(id))
                .thenThrow(new NotFoundException("Author with id " + id + " not found"));

        mockMvc.perform(get("/authors/{id}", id)).andExpect(status().isNotFound());
    }
}