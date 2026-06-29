package mg.yoan.lib.endpoint.rest.controller.health;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.FormatLabel;
import mg.yoan.lib.model.dto.BookEditionRequest;
import mg.yoan.lib.model.dto.BookEditionSearchCriteria;
import mg.yoan.lib.service.BookEditionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book-editions")
@AllArgsConstructor
public class BookEditionController {
  private final BookEditionService bookEditionService;

  @PostMapping
  public ResponseEntity<BookEdition> createBookEdition(@RequestBody BookEditionRequest request) {
    BookEdition created = bookEditionService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<BookEdition>> getAllBookEditions(
      @RequestParam(required = false) UUID bookId) {
    if (bookId != null) {
      return ResponseEntity.ok(bookEditionService.getAllByBook(bookId));
    }
    return ResponseEntity.ok(bookEditionService.getAll());
  }

  @GetMapping("/search")
  public ResponseEntity<Page<BookEdition>> searchBookEditions(
      @RequestParam(required = false) String isbn,
      @RequestParam(required = false) FormatLabel formatLabel,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String genre,
      @RequestParam(required = false) Integer minYear,
      @RequestParam(required = false) Integer maxYear,
      @RequestParam(required = false) String authorName,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {

    BookEditionSearchCriteria criteria = new BookEditionSearchCriteria();
    criteria.setIsbn(isbn);
    criteria.setFormatLabel(formatLabel);
    criteria.setMinPrice(minPrice);
    criteria.setMaxPrice(maxPrice);
    criteria.setTitle(title);
    criteria.setGenre(genre);
    criteria.setMinYear(minYear);
    criteria.setMaxYear(maxYear);
    criteria.setAuthorName(authorName);

    Sort.Direction direction =
        "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    return ResponseEntity.ok(bookEditionService.search(criteria, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookEdition> getBookEditionById(@PathVariable UUID id) {
    return ResponseEntity.ok(bookEditionService.getById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookEdition> updateBookEdition(
      @PathVariable UUID id, @RequestBody BookEditionRequest request) {
    return ResponseEntity.ok(bookEditionService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBookEdition(@PathVariable UUID id) {
    bookEditionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
