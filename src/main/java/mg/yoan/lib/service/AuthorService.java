package mg.yoan.lib.service;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Author;
import mg.yoan.lib.model.dto.AuthorRequest;
import mg.yoan.lib.repository.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorService {
  private final AuthorRepository authorRepository;

  public Author create(AuthorRequest authorRequest) {
    var author =
        Author.builder()
            .lastName(authorRequest.getLastName())
            .firstName(authorRequest.getFirstName())
            .build();
    return authorRepository.save(author);
  }

  public List<Author> getAll() {
    return authorRepository.findAll();
  }

  public Author getById(UUID id) {
    return authorRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
  }

  public Author update(UUID id, AuthorRequest authorRequest) {
    var author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
    author.setLastName(authorRequest.getLastName());
    author.setFirstName(authorRequest.getFirstName());
    return authorRepository.save(author);
  }

  public void delete(UUID id) {
    if (!authorRepository.existsById(id)) {
      throw new NotFoundException("Author with id " + id + " not found");
    }
    authorRepository.deleteById(id);
  }
}
