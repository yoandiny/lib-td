package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Author;
import mg.yoan.lib.model.dto.AuthorRequest;
import mg.yoan.lib.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

  @Mock AuthorRepository authorRepository;
  @InjectMocks AuthorService authorService;

  @Test
  void create_savesAndReturnsAuthor() {
    AuthorRequest request = new AuthorRequest();
    request.setLastName("Hugo");
    request.setFirstName("Victor");

    Author saved =
        Author.builder().id(UUID.randomUUID()).lastName("Hugo").firstName("Victor").build();
    when(authorRepository.save(any(Author.class))).thenReturn(saved);

    Author result = authorService.create(request);

    assertThat(result.getLastName()).isEqualTo("Hugo");
    assertThat(result.getFirstName()).isEqualTo("Victor");
  }

  @Test
  void getAll_returnsAllAuthors() {
    Author author1 =
        Author.builder().id(UUID.randomUUID()).lastName("Hugo").firstName("Victor").build();
    Author author2 =
        Author.builder().id(UUID.randomUUID()).lastName("Rowling").firstName("J.K.").build();
    when(authorRepository.findAll()).thenReturn(List.of(author1, author2));

    assertThat(authorService.getAll()).hasSize(2).containsExactly(author1, author2);
  }

  @Test
  void getById_withExistingId_returnsAuthor() {
    UUID id = UUID.randomUUID();
    Author author = Author.builder().id(id).lastName("Hugo").firstName("Victor").build();
    when(authorRepository.findById(id)).thenReturn(Optional.of(author));

    assertThat(authorService.getById(id)).isEqualTo(author);
  }

  @Test
  void getById_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> authorService.getById(id));
  }

  @Test
  void update_withExistingId_updatesAndReturnsAuthor() {
    UUID id = UUID.randomUUID();
    Author existing = Author.builder().id(id).lastName("Old").firstName("Name").build();
    AuthorRequest request = new AuthorRequest();
    request.setLastName("Hugo");
    request.setFirstName("Victor");

    when(authorRepository.findById(id)).thenReturn(Optional.of(existing));
    when(authorRepository.save(any(Author.class))).thenAnswer(inv -> inv.getArgument(0));

    Author result = authorService.update(id, request);

    assertThat(result.getLastName()).isEqualTo("Hugo");
    assertThat(result.getFirstName()).isEqualTo("Victor");
  }

  @Test
  void update_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> authorService.update(id, new AuthorRequest()));
  }

  @Test
  void delete_withExistingId_deletesAuthor() {
    UUID id = UUID.randomUUID();
    when(authorRepository.existsById(id)).thenReturn(true);

    authorService.delete(id);

    verify(authorRepository).deleteById(id);
  }

  @Test
  void delete_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(authorRepository.existsById(id)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> authorService.delete(id));
    verify(authorRepository, never()).deleteById(any());
  }
}
