package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Arrival;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.dto.ArrivalLineRequest;
import mg.yoan.lib.model.dto.ArrivalRequest;
import mg.yoan.lib.repository.ArrivalRepository;
import mg.yoan.lib.repository.BookEditionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArrivalServiceTest {

  @Mock ArrivalRepository arrivalRepository;
  @Mock BookEditionRepository bookEditionRepository;
  @InjectMocks ArrivalService arrivalService;

  @Test
  void create_withValidLines_createsArrival() {
    UUID editionId = UUID.randomUUID();
    BookEdition edition = BookEdition.builder().id(editionId).build();

    ArrivalLineRequest lineRequest = new ArrivalLineRequest();
    lineRequest.setBookEditionId(editionId);
    lineRequest.setQuantity(50);

    ArrivalRequest request = new ArrivalRequest();
    request.setArrivedAt(Instant.now());
    request.setLines(List.of(lineRequest));

    when(bookEditionRepository.findById(editionId)).thenReturn(Optional.of(edition));
    when(arrivalRepository.save(any(Arrival.class))).thenAnswer(inv -> inv.getArgument(0));

    Arrival result = arrivalService.create(request);

    assertThat(result.getLines()).hasSize(1);
    assertThat(result.getLines().getFirst().getQuantity()).isEqualTo(50);
  }

  @Test
  void create_withNullLines_throwsIllegalArgumentException() {
    ArrivalRequest request = new ArrivalRequest();
    request.setLines(null);

    assertThrows(IllegalArgumentException.class, () -> arrivalService.create(request));
  }

  @Test
  void create_withEmptyLines_throwsIllegalArgumentException() {
    ArrivalRequest request = new ArrivalRequest();
    request.setLines(List.of());

    assertThrows(IllegalArgumentException.class, () -> arrivalService.create(request));
  }

  @Test
  void create_withInvalidQuantity_throwsIllegalArgumentException() {
    ArrivalLineRequest lineRequest = new ArrivalLineRequest();
    lineRequest.setBookEditionId(UUID.randomUUID());
    lineRequest.setQuantity(0);

    ArrivalRequest request = new ArrivalRequest();
    request.setLines(List.of(lineRequest));

    assertThrows(IllegalArgumentException.class, () -> arrivalService.create(request));
  }

  @Test
  void create_withUnknownBookEdition_throwsNotFoundException() {
    UUID editionId = UUID.randomUUID();
    ArrivalLineRequest lineRequest = new ArrivalLineRequest();
    lineRequest.setBookEditionId(editionId);
    lineRequest.setQuantity(10);

    ArrivalRequest request = new ArrivalRequest();
    request.setLines(List.of(lineRequest));

    when(bookEditionRepository.findById(editionId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> arrivalService.create(request));
  }

  @Test
  void getAll_returnsAllArrivals() {
    when(arrivalRepository.findAll())
        .thenReturn(List.of(Arrival.builder().id(UUID.randomUUID()).build()));

    assertThat(arrivalService.getAll()).hasSize(1);
  }

  @Test
  void getById_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(arrivalRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> arrivalService.getById(id));
  }

  @Test
  void delete_withExistingId_deletesArrival() {
    UUID id = UUID.randomUUID();
    when(arrivalRepository.existsById(id)).thenReturn(true);

    arrivalService.delete(id);

    verify(arrivalRepository).deleteById(id);
  }

  @Test
  void delete_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(arrivalRepository.existsById(id)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> arrivalService.delete(id));
  }
}
