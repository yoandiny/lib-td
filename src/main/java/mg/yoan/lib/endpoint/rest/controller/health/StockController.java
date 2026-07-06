package mg.yoan.lib.endpoint.rest.controller.health;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import mg.yoan.lib.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {
  private final StockService stockService;

  @GetMapping("/book-edition/{id}/stock")
  public ResponseEntity<Integer> getStock(@PathVariable UUID id) {
    return ResponseEntity.ok(stockService.getStock(id));
  }
  @GetMapping("/book/{bookId}")
  public ResponseEntity<Map<String, Integer>> getStockByBook(@PathVariable UUID bookId) {
    return ResponseEntity.ok(stockService.getStockByBook(bookId));
  }
}
