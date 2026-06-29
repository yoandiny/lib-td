package mg.yoan.lib.endpoint.rest.controller.health;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.model.Book;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.dto.BookRequest;
import mg.yoan.lib.service.BookEditionService;
import mg.yoan.lib.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
public class BookController {
  private final BookService bookService;
  private final BookEditionService bookEditionService;

  @PostMapping
  public ResponseEntity<Book> addBook(@RequestBody BookRequest bookRequest) {
    var created = bookService.create(bookRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<Book>> getAllBooks() {
    return ResponseEntity.ok(bookService.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Book> getBookById(@PathVariable UUID id) {
    return ResponseEntity.ok(bookService.getById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Book> updateBook(
      @PathVariable UUID id, @RequestBody BookRequest bookRequest) {
    return ResponseEntity.ok(bookService.update(id, bookRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Book> deleteBook(@PathVariable UUID id) {
    bookService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/editions")
  public ResponseEntity<List<BookEdition>> getEditionsByBook(@PathVariable UUID id) {
    return ResponseEntity.ok(bookEditionService.getAllByBook(id));
  }

  @GetMapping("/{id}/editions/{editionId}")
  public ResponseEntity<BookEdition> getEditionOfBook(
      @PathVariable UUID id, @PathVariable UUID editionId) {
    return ResponseEntity.ok(bookService.getEditionOfBook(id, editionId));
  }
}
