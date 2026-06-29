package mg.yoan.lib.service;

import java.util.List;
import java.util.UUID;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Book;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.Format;
import mg.yoan.lib.model.dto.BookEditionRequest;
import mg.yoan.lib.model.dto.BookEditionSearchCriteria;
import mg.yoan.lib.repository.BookEditionRepository;
import mg.yoan.lib.repository.BookRepository;
import mg.yoan.lib.repository.FormatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookEditionService {

  private final BookEditionRepository bookEditionRepository;
  private final BookRepository bookRepository;
  private final FormatRepository formatRepository;

  public BookEditionService(
      BookEditionRepository bookEditionRepository,
      BookRepository bookRepository,
      FormatRepository formatRepository) {
    this.bookEditionRepository = bookEditionRepository;
    this.bookRepository = bookRepository;
    this.formatRepository = formatRepository;
  }

  public BookEdition create(BookEditionRequest request) {
    Book book = findBook(request.getBookId());
    Format format = findFormat(request.getFormatId());

    BookEdition bookEdition =
        BookEdition.builder()
            .isbn(request.getIsbn())
            .book(book)
            .format(format)
            .price(request.getPrice())
            .build();

    return bookEditionRepository.save(bookEdition);
  }

  public List<BookEdition> getAll() {
    return bookEditionRepository.findAll();
  }

  public Page<BookEdition> search(BookEditionSearchCriteria criteria, Pageable pageable) {
    throw new UnsupportedOperationException("TODO: still need to verify this one line per line");
    // Specification<BookEdition> specification = BookEditionSpecification.fromCriteria(criteria);
    // return bookEditionRepository.findAll(specification, pageable);
  }

  public List<BookEdition> getAllByBook(UUID bookId) {
    if (!bookRepository.existsById(bookId)) {
      throw new NotFoundException("Book with id " + bookId + " not found");
    }
    return bookEditionRepository.findAllByBookId(bookId);
  }

  public BookEdition getById(UUID id) {
    return bookEditionRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("BookEdition with id " + id + " not found"));
  }

  public BookEdition update(UUID id, BookEditionRequest request) {
    BookEdition bookEdition = getById(id);

    bookEdition.setIsbn(request.getIsbn());
    bookEdition.setBook(findBook(request.getBookId()));
    bookEdition.setFormat(findFormat(request.getFormatId()));
    bookEdition.setPrice(request.getPrice());

    return bookEditionRepository.save(bookEdition);
  }

  public void delete(UUID id) {
    if (!bookEditionRepository.existsById(id)) {
      throw new NotFoundException("BookEdition with id " + id + " not found");
    }
    bookEditionRepository.deleteById(id);
  }

  private Book findBook(UUID bookId) {
    return bookRepository
        .findById(bookId)
        .orElseThrow(() -> new NotFoundException("Book with id " + bookId + " not found"));
  }

  private Format findFormat(UUID formatId) {
    if (formatId == null) {
      return null;
    }
    return formatRepository
        .findById(formatId)
        .orElseThrow(() -> new NotFoundException("Format with id " + formatId + " not found"));
  }
}
