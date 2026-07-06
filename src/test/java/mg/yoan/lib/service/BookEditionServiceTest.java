package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Book;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.Format;
import mg.yoan.lib.model.dto.BookEditionRequest;
import mg.yoan.lib.repository.BookEditionRepository;
import mg.yoan.lib.repository.BookRepository;
import mg.yoan.lib.repository.FormatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookEditionServiceTest {

    @Mock BookEditionRepository bookEditionRepository;
    @Mock BookRepository bookRepository;
    @Mock FormatRepository formatRepository;

    @InjectMocks BookEditionService bookEditionService;

    @Test
    void create_withValidBookAndFormat_returnsBookEdition() {
        UUID bookId = UUID.randomUUID();
        UUID formatId = UUID.randomUUID();
        Book book = Book.builder().id(bookId).build();
        Format format = Format.builder().id(formatId).build();

        BookEditionRequest request = new BookEditionRequest();
        request.setBookId(bookId);
        request.setFormatId(formatId);
        request.setIsbn("9782070409229");
        request.setPrice(19.90);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(formatRepository.findById(formatId)).thenReturn(Optional.of(format));
        when(bookEditionRepository.save(any(BookEdition.class))).thenAnswer(inv -> inv.getArgument(0));

        BookEdition result = bookEditionService.create(request);

        assertThat(result.getBook()).isEqualTo(book);
        assertThat(result.getFormat()).isEqualTo(format);
    }

    @Test
    void create_withoutFormatId_createsEditionWithNullFormat() {
        UUID bookId = UUID.randomUUID();
        Book book = Book.builder().id(bookId).build();

        BookEditionRequest request = new BookEditionRequest();
        request.setBookId(bookId);
        request.setFormatId(null);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookEditionRepository.save(any(BookEdition.class))).thenAnswer(inv -> inv.getArgument(0));

        BookEdition result = bookEditionService.create(request);

        assertThat(result.getFormat()).isNull();
    }

    @Test
    void create_withUnknownBook_throwsNotFoundException() {
        UUID bookId = UUID.randomUUID();
        BookEditionRequest request = new BookEditionRequest();
        request.setBookId(bookId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookEditionService.create(request));
    }

    @Test
    void create_withUnknownFormat_throwsNotFoundException() {
        UUID bookId = UUID.randomUUID();
        UUID formatId = UUID.randomUUID();
        Book book = Book.builder().id(bookId).build();

        BookEditionRequest request = new BookEditionRequest();
        request.setBookId(bookId);
        request.setFormatId(formatId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(formatRepository.findById(formatId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookEditionService.create(request));
    }

    @Test
    void getAll_returnsAllEditions() {
        when(bookEditionRepository.findAll())
                .thenReturn(List.of(BookEdition.builder().id(UUID.randomUUID()).build()));

        assertThat(bookEditionService.getAll()).hasSize(1);
    }

    @Test
    void getAllByBook_withExistingBook_returnsEditions() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookEditionRepository.findAllByBookId(bookId))
                .thenReturn(List.of(BookEdition.builder().id(UUID.randomUUID()).build()));

        assertThat(bookEditionService.getAllByBook(bookId)).hasSize(1);
    }

    @Test
    void getAllByBook_withUnknownBook_throwsNotFoundException() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.existsById(bookId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookEditionService.getAllByBook(bookId));
    }

    @Test
    void getById_withUnknownId_throwsNotFoundException() {
        UUID id = UUID.randomUUID();
        when(bookEditionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookEditionService.getById(id));
    }

    @Test
    void update_withValidData_updatesEdition() {
        UUID id = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID formatId = UUID.randomUUID();
        BookEdition existing = BookEdition.builder().id(id).build();
        Book book = Book.builder().id(bookId).build();
        Format format = Format.builder().id(formatId).build();

        BookEditionRequest request = new BookEditionRequest();
        request.setBookId(bookId);
        request.setFormatId(formatId);
        request.setIsbn("new-isbn");
        request.setPrice(25.0);

        when(bookEditionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(formatRepository.findById(formatId)).thenReturn(Optional.of(format));
        when(bookEditionRepository.save(any(BookEdition.class))).thenAnswer(inv -> inv.getArgument(0));

        BookEdition result = bookEditionService.update(id, request);

        assertThat(result.getIsbn()).isEqualTo("new-isbn");
        assertThat(result.getPrice()).isEqualTo(25.0);
    }

    @Test
    void delete_withExistingId_deletesEdition() {
        UUID id = UUID.randomUUID();
        when(bookEditionRepository.existsById(id)).thenReturn(true);

        bookEditionService.delete(id);

        verify(bookEditionRepository).deleteById(id);
    }

    @Test
    void delete_withUnknownId_throwsNotFoundException() {
        UUID id = UUID.randomUUID();
        when(bookEditionRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookEditionService.delete(id));
    }

    @Test
    void search_isNotYetImplemented() {
        assertThrows(
                UnsupportedOperationException.class, () -> bookEditionService.search(null, null));
    }
}