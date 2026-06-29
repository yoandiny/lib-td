package mg.yoan.lib.endpoint.rest.controller.health;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.model.Payment;
import mg.yoan.lib.model.dto.PaymentRequest;
import mg.yoan.lib.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(request));
  }

  @GetMapping
  public ResponseEntity<List<Payment>> getAllPayments() {
    return ResponseEntity.ok(paymentService.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Payment> getPaymentById(@PathVariable UUID id) {
    return ResponseEntity.ok(paymentService.getById(id));
  }

  @GetMapping("/sale/{saleId}")
  public ResponseEntity<Payment> getPaymentBySaleId(@PathVariable UUID saleId) {
    return ResponseEntity.ok(paymentService.getBySaleId(saleId));
  }

  @PutMapping("/{id}/process")
  public ResponseEntity<Payment> processPayment(@PathVariable UUID id) {
    return ResponseEntity.ok(paymentService.process(id));
  }

  @PutMapping("/{id}/fail")
  public ResponseEntity<Payment> failPayment(@PathVariable UUID id) {
    return ResponseEntity.ok(paymentService.failPayment(id));
  }
}
