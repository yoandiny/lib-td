package mg.yoan.lib.endpoint.rest.controller.health;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.model.Author;
import mg.yoan.lib.model.dto.AuthorRequest;
import mg.yoan.lib.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@AllArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<Author> addAuthor(@RequestBody AuthorRequest authorRequest) {
        var created = authorService.create(authorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable UUID id) {
        return ResponseEntity.ok(authorService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(
            @PathVariable UUID id, @RequestBody AuthorRequest authorRequest) {
        return ResponseEntity.ok(authorService.update(id, authorRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Author> deleteAuthor(@PathVariable UUID id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}