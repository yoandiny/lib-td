package mg.yoan.lib.endpoint.rest.controller.health;

import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mg.yoan.lib.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/revenue-by-genre")
    public ResponseEntity<Map<String, BigDecimal>> getRevenueByGenre() {
        return ResponseEntity.ok(reportService.getRevenueByGenre());
    }
}