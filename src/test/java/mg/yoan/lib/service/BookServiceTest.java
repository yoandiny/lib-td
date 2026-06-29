package mg.yoan.lib.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import mg.yoan.lib.model.Author;
import mg.yoan.lib.model.Book;
import mg.yoan.lib.model.dto.BookRequest;
import mg.yoan.lib.repository.AuthorRepository;
import mg.yoan.lib.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
  @Mock private BookRepository bookRepository;

  @Mock private AuthorRepository authorRepository;

  @InjectMocks private BookService bookService;

  @Test
  void shouldCreateBook() {

    // given
    UUID authorId01 = UUID.randomUUID();

    BookRequest request = new BookRequest();
    request.setTitle("Spring Boot");
    request.setIsbn("123");
    request.setPublishYear(2025);
    request.setGenre("Tech");
    request.setAuthorId(authorId01);

    Author author = new Author();
    author.setId(authorId01);
    author.setFirstName("Benson");
    author.setLastName("Boom");

    Book saved =
        Book.builder()
            .id(authorId01)
            .title("Spring Boot")
            .author(author)
            .isbn("123")
            .genre("Tech")
            .publicationYear(2025)
            .build();

    when(authorRepository.findById(authorId01)).thenReturn(Optional.of(author));

    when(bookRepository.save(any(Book.class))).thenReturn(saved);

    // when
    Book result = bookService.create(request);

    // then
    assertNotNull(result);
    assertEquals("Spring Boot", result.getTitle());
    assertEquals(authorId01, result.getId());

    verify(authorRepository).findById(authorId01);
    verify(bookRepository).save(any(Book.class));
  }
}
