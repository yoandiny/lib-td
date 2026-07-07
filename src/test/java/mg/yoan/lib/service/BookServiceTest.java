package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Author;
import mg.yoan.lib.model.Book;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.dto.BookRequest;
import mg.yoan.lib.repository.AuthorRepository;
import mg.yoan.lib.repository.BookEditionRepository;
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
  @Mock private BookEditionRepository bookEditionRepository;

  @InjectMocks private BookService bookService;

  @Test
  void shouldCreateBook() {
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

    Book result = bookService.create(request);

    assertNotNull(result);
    assertEquals("Spring Boot", result.getTitle());
    assertEquals(authorId01, result.getId());

    verify(authorRepository).findById(authorId01);
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void create_withUnknownAuthor_throwsNotFoundException() {
    UUID authorId = UUID.randomUUID();
    BookRequest request = new BookRequest();
    request.setAuthorId(authorId);
    when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookService.create(request));
  }

  @Test
  void getAll_returnsAllBooks() {
    Book book1 = Book.builder().id(UUID.randomUUID()).title("Book 1").build();
    Book book2 = Book.builder().id(UUID.randomUUID()).title("Book 2").build();
    when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

    assertThat(bookService.getAll()).hasSize(2);
  }

  @Test
  void getById_withExistingId_returnsBook() {
    UUID id = UUID.randomUUID();
    Book book = Book.builder().id(id).title("Les Misérables").build();
    when(bookRepository.findById(id)).thenReturn(Optional.of(book));

    assertThat(bookService.getById(id)).isEqualTo(book);
  }

  @Test
  void getById_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(bookRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookService.getById(id));
  }

  @Test
  void update_withExistingBookAndAuthor_updatesAndReturnsBook() {
    UUID bookId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    Book existing = Book.builder().id(bookId).title("Old title").build();
    Author author = Author.builder().id(authorId).lastName("Hugo").firstName("Victor").build();

    BookRequest request = new BookRequest();
    request.setTitle("Les Misérables");
    request.setAuthorId(authorId);
    request.setIsbn("9782070409228");
    request.setGenre("Roman");
    request.setPublishYear(1862);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(existing));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

    Book result = bookService.update(bookId, request);

    assertThat(result.getTitle()).isEqualTo("Les Misérables");
    assertThat(result.getAuthor()).isEqualTo(author);
  }

  @Test
  void update_withUnknownBook_throwsNotFoundException() {
    UUID bookId = UUID.randomUUID();
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookService.update(bookId, new BookRequest()));
  }

  @Test
  void delete_withExistingId_deletesBook() {
    UUID id = UUID.randomUUID();
    when(bookRepository.existsById(id)).thenReturn(true);

    bookService.delete(id);

    verify(bookRepository).deleteById(id);
  }

  @Test
  void delete_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(bookRepository.existsById(id)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> bookService.delete(id));
  }

  @Test
  void getEditionOfBook_withMatchingEdition_returnsEdition() {
    UUID bookId = UUID.randomUUID();
    UUID editionId = UUID.randomUUID();
    Book book = Book.builder().id(bookId).build();
    BookEdition edition = BookEdition.builder().id(editionId).book(book).build();

    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(bookEditionRepository.findById(editionId)).thenReturn(Optional.of(edition));

    assertThat(bookService.getEditionOfBook(bookId, editionId)).isEqualTo(edition);
  }

  @Test
  void getEditionOfBook_withUnknownBook_throwsNotFoundException() {
    UUID bookId = UUID.randomUUID();
    UUID editionId = UUID.randomUUID();
    when(bookRepository.existsById(bookId)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> bookService.getEditionOfBook(bookId, editionId));
  }

  @Test
  void getEditionOfBook_withEditionBelongingToAnotherBook_throwsNotFoundException() {
    UUID bookId = UUID.randomUUID();
    UUID otherBookId = UUID.randomUUID();
    UUID editionId = UUID.randomUUID();
    Book otherBook = Book.builder().id(otherBookId).build();
    BookEdition edition = BookEdition.builder().id(editionId).book(otherBook).build();

    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(bookEditionRepository.findById(editionId)).thenReturn(Optional.of(edition));

    assertThrows(NotFoundException.class, () -> bookService.getEditionOfBook(bookId, editionId));
  }
}
