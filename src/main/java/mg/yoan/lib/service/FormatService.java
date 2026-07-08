package mg.yoan.lib.service;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Format;
import mg.yoan.lib.model.dto.FormatRequest;
import mg.yoan.lib.repository.FormatRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FormatService {
  private final FormatRepository formatRepository;

  public Format create(FormatRequest request) {
    if (formatRepository.existsByFormatLabel(request.getFormatLabel())) {
      throw new IllegalStateException(
          "Format with label " + request.getFormatLabel() + " already exists");
    }
    Format format = Format.builder().formatLabel(request.getFormatLabel()).build();
    return formatRepository.save(format);
  }

  public List<Format> getAll() {
    return formatRepository.findAll();
  }

  public Format getById(UUID id) {
    return formatRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Format with id " + id + " not found"));
  }

  public Format update(UUID id, FormatRequest request) {
    Format format = getById(id);
    format.setFormatLabel(request.getFormatLabel());
    return formatRepository.save(format);
  }

  public void delete(UUID id) {
    if (!formatRepository.existsById(id)) {
      throw new NotFoundException("Format with id " + id + " not found");
    }
    formatRepository.deleteById(id);
  }
}
