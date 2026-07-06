package mg.yoan.lib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

import mg.yoan.lib.endpoint.rest.controller.health.BookController;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Book;
import mg.yoan.lib.model.dto.BookRequest;
import mg.yoan.lib.service.BookEditionService;
import mg.yoan.lib.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean BookService bookService;
    @MockBean BookEditionService bookEditionService;

    @Test
    void addBook_returns201() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("Les Misérables");
        request.setAuthorId(UUID.randomUUID());

        Book created = Book.builder().id(UUID.randomUUID()).title("Les Misérables").build();
        when(bookService.create(any(BookRequest.class))).thenReturn(created);

        mockMvc
                .perform(
                        post("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getBookById_withUnknownId_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(bookService.getById(id)).thenThrow(new NotFoundException("Book with id " + id + " not found"));

        mockMvc.perform(get("/books/{id}", id)).andExpect(status().isNotFound());
    }
}