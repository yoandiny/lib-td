package mg.yoan.lib.endpoint.rest.controller.health;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.model.Format;
import mg.yoan.lib.model.dto.FormatRequest;
import mg.yoan.lib.service.FormatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/formats")
@AllArgsConstructor
public class FormatController {
  private final FormatService formatService;

  @PostMapping
  public ResponseEntity<Format> createFormat(@RequestBody FormatRequest request) {
    Format created = formatService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<Format>> getAllFormats() {
    return ResponseEntity.ok(formatService.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Format> getFormatById(@PathVariable UUID id) {
    return ResponseEntity.ok(formatService.getById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Format> updateFormat(
      @PathVariable UUID id, @RequestBody FormatRequest request) {
    return ResponseEntity.ok(formatService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFormat(@PathVariable UUID id) {
    formatService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
