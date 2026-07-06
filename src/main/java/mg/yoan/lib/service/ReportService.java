package mg.yoan.lib.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mg.yoan.lib.model.dto.GenreRevenue;
import mg.yoan.lib.repository.SaleLineRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final SaleLineRepository saleLineRepository;

  public Map<String, BigDecimal> getRevenueByGenre() {
    return saleLineRepository.sumRevenueGroupedByGenre().stream()
        .collect(
            Collectors.toMap(
                GenreRevenue::getGenre,
                GenreRevenue::getRevenue,
                BigDecimal::add,
                LinkedHashMap::new));
  }
}
