package mg.yoan.lib.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import mg.yoan.lib.repository.ArrivalLineRepository;
import mg.yoan.lib.repository.SaleLineRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {
  private final ArrivalLineRepository arrivalLineRepository;
  private final SaleLineRepository saleLineRepository;

  public int getStock(UUID bookEditionId) {
    int arrived = arrivalLineRepository.sumQuantityByBookEditionId(bookEditionId);
    int sold = saleLineRepository.sumQuantityByBookEditionId(bookEditionId);
    return arrived - sold;
  }
}
