package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Format;
import mg.yoan.lib.model.FormatLabel;
import mg.yoan.lib.model.dto.FormatRequest;
import mg.yoan.lib.repository.FormatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FormatServiceTest {

  @Mock FormatRepository formatRepository;
  @InjectMocks FormatService formatService;

  @Test
  void create_savesAndReturnsFormat() {
    FormatRequest request = new FormatRequest();
    request.setFormatLabel(FormatLabel.HARDCOVER);

    Format saved =
        Format.builder().id(UUID.randomUUID()).formatLabel(FormatLabel.HARDCOVER).build();
    when(formatRepository.save(any(Format.class))).thenReturn(saved);

    assertThat(formatService.create(request).getFormatLabel()).isEqualTo(FormatLabel.HARDCOVER);
  }

  @Test
  void getAll_returnsAllFormats() {
    when(formatRepository.findAll())
        .thenReturn(List.of(Format.builder().id(UUID.randomUUID()).build()));

    assertThat(formatService.getAll()).hasSize(1);
  }

  @Test
  void getById_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(formatRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> formatService.getById(id));
  }

  @Test
  void update_withExistingId_updatesFormat() {
    UUID id = UUID.randomUUID();
    Format existing = Format.builder().id(id).formatLabel(FormatLabel.POCKET).build();
    FormatRequest request = new FormatRequest();
    request.setFormatLabel(FormatLabel.SOFTCOVER);

    when(formatRepository.findById(id)).thenReturn(Optional.of(existing));
    when(formatRepository.save(any(Format.class))).thenAnswer(inv -> inv.getArgument(0));

    assertThat(formatService.update(id, request).getFormatLabel()).isEqualTo(FormatLabel.SOFTCOVER);
  }

  @Test
  void delete_withExistingId_deletesFormat() {
    UUID id = UUID.randomUUID();
    when(formatRepository.existsById(id)).thenReturn(true);

    formatService.delete(id);

    verify(formatRepository).deleteById(id);
  }

  @Test
  void delete_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(formatRepository.existsById(id)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> formatService.delete(id));
  }

  @Test
  void create_withExistingLabel_throwsIllegalStateException() {
    FormatRequest request = new FormatRequest();
    request.setFormatLabel(FormatLabel.HARDCOVER);

    when(formatRepository.existsByFormatLabel(FormatLabel.HARDCOVER)).thenReturn(true);

    assertThrows(IllegalStateException.class, () -> formatService.create(request));
    verify(formatRepository, never()).save(any());
  }
}
