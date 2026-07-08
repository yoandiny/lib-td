package mg.yoan.lib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import mg.yoan.lib.endpoint.rest.controller.health.BookEditionController;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.FormatLabel;
import mg.yoan.lib.model.dto.BookEditionRequest;
import mg.yoan.lib.model.dto.BookEditionSearchCriteria;
import mg.yoan.lib.service.BookEditionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookEditionController.class)
class BookEditionControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean BookEditionService bookEditionService;

  @Test
  void createBookEdition_returns201() throws Exception {
    BookEditionRequest request = new BookEditionRequest();
    request.setIsbn("9780000000001");
    request.setBookId(UUID.randomUUID());
    request.setFormatId(UUID.randomUUID());
    request.setPrice(19.9);

    when(bookEditionService.create(any(BookEditionRequest.class)))
        .thenReturn(BookEdition.builder().id(UUID.randomUUID()).isbn("9780000000001").build());

    mockMvc
        .perform(
            post("/book-editions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void getAllBookEditions_withoutBookId_returns200() throws Exception {
    when(bookEditionService.getAll()).thenReturn(List.of(BookEdition.builder().build()));

    mockMvc.perform(get("/book-editions")).andExpect(status().isOk());
  }

  @Test
  void getAllBookEditions_withBookId_returns200() throws Exception {
    UUID bookId = UUID.randomUUID();
    when(bookEditionService.getAllByBook(bookId)).thenReturn(List.of(BookEdition.builder().build()));

    mockMvc.perform(get("/book-editions").param("bookId", bookId.toString())).andExpect(status().isOk());
  }

  @Test
  void searchBookEditions_returns200() throws Exception {
    when(bookEditionService.search(any(BookEditionSearchCriteria.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(BookEdition.builder().build())));

    mockMvc
        .perform(
            get("/book-editions/search")
                .param("isbn", "978")
                .param("formatLabel", FormatLabel.HARDCOVER.name())
                .param("minPrice", "10")
                .param("maxPrice", "30")
                .param("title", "title")
                .param("genre", "Roman")
                .param("minYear", "1900")
                .param("maxYear", "2024")
                .param("authorName", "Victor")
                .param("page", "1")
                .param("size", "5")
                .param("sortBy", "price")
                .param("sortDir", "desc"))
        .andExpect(status().isOk());
  }

  @Test
  void getBookEditionById_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(bookEditionService.getById(id)).thenReturn(BookEdition.builder().id(id).build());

    mockMvc.perform(get("/book-editions/{id}", id)).andExpect(status().isOk());
  }

  @Test
  void updateBookEdition_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    BookEditionRequest request = new BookEditionRequest();
    request.setPrice(20.0);
    when(bookEditionService.update(eq(id), any(BookEditionRequest.class)))
        .thenReturn(BookEdition.builder().id(id).build());

    mockMvc
        .perform(
            put("/book-editions/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void deleteBookEdition_returns204() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(delete("/book-editions/{id}", id)).andExpect(status().isNoContent());

    verify(bookEditionService).delete(id);
  }
}
