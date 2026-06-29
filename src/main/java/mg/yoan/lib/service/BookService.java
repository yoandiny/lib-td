package mg.yoan.lib.service;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Book;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.dto.BookRequest;
import mg.yoan.lib.repository.AuthorRepository;
import mg.yoan.lib.repository.BookEditionRepository;
import mg.yoan.lib.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookService {
  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;
  private final BookEditionRepository bookEditionRepository;

  public Book create(BookRequest bookRequest) {
    var author =
        authorRepository
            .findById(bookRequest.getAuthorId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Author with id " + bookRequest.getAuthorId() + " not found"));
    var book =
        Book.builder()
            .title(bookRequest.getTitle())
            .author(author)
            .isbn(bookRequest.getIsbn())
            .publicationYear(bookRequest.getPublishYear())
            .genre(bookRequest.getGenre())
            .build();

    return bookRepository.save(book);
  }

  public List<Book> getAll() {
    return bookRepository.findAll();
  }

  public Book getById(UUID id) {
    return bookRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));
  }

  public Book update(UUID id, BookRequest bookRequest) {
    var book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));

    var author =
        authorRepository
            .findById(bookRequest.getAuthorId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Author with id " + bookRequest.getAuthorId() + " not found"));

    book.setTitle(bookRequest.getTitle());
    book.setIsbn(bookRequest.getIsbn());
    book.setGenre(bookRequest.getGenre());
    book.setAuthor(author);
    book.setPublicationYear(bookRequest.getPublishYear());

    return bookRepository.save(book);
  }

  public void delete(UUID id) {
    if (!bookRepository.existsById(id)) {
      throw new NotFoundException("Book with id " + id + " not found");
    }
    bookRepository.deleteById(id);
  }

  public BookEdition getEditionOfBook(UUID bookId, UUID editionId) {
    if (!bookRepository.existsById(bookId)) {
      throw new NotFoundException("Book with id " + bookId + " not found");
    }

    var edition =
        bookEditionRepository
            .findById(editionId)
            .orElseThrow(
                () -> new NotFoundException("BookEdition with id " + editionId + " not found"));

    if (!edition.getBook().getId().equals(bookId)) {
      throw new NotFoundException(
          "BookEdition with id " + editionId + " does not belong to Book with id " + bookId);
    }

    return edition;
  }
}
