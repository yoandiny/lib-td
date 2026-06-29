package mg.yoan.lib.endpoint.rest.controller.health;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.model.Arrival;
import mg.yoan.lib.model.dto.ArrivalRequest;
import mg.yoan.lib.service.ArrivalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/arrivals")
@AllArgsConstructor
public class ArrivalController {
  private final ArrivalService arrivalService;

  @PostMapping
  public ResponseEntity<Arrival> createArrival(@RequestBody ArrivalRequest request) {
    var created = arrivalService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<Arrival>> getAllArrivals() {
    return ResponseEntity.ok(arrivalService.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Arrival> getArrivalById(@PathVariable UUID id) {
    return ResponseEntity.ok(arrivalService.getById(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteArrival(@PathVariable UUID id) {
    arrivalService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
