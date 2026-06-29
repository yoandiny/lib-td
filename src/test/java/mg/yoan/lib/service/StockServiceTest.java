package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;
import mg.yoan.lib.repository.ArrivalLineRepository;
import mg.yoan.lib.repository.SaleLineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

  @Mock ArrivalLineRepository arrivalLineRepository;

  @Mock SaleLineRepository saleLineRepository;

  @InjectMocks StockService stockService;

  @Test
  void getStock_returnsArrivedMinusSold() {
    UUID id = UUID.randomUUID();
    when(arrivalLineRepository.sumQuantityByBookEditionId(id)).thenReturn(10);
    when(saleLineRepository.sumQuantityByBookEditionId(id)).thenReturn(3);

    assertThat(stockService.getStock(id)).isEqualTo(7);
  }

  @Test
  void getStock_withNoArrivalsNorSales_returnsZero() {
    UUID id = UUID.randomUUID();
    when(arrivalLineRepository.sumQuantityByBookEditionId(id)).thenReturn(0);
    when(saleLineRepository.sumQuantityByBookEditionId(id)).thenReturn(0);

    assertThat(stockService.getStock(id)).isEqualTo(0);
  }

  @Test
  void getStock_canBeNegative_ifDataInconsistent() {
    UUID id = UUID.randomUUID();
    when(arrivalLineRepository.sumQuantityByBookEditionId(id)).thenReturn(2);
    when(saleLineRepository.sumQuantityByBookEditionId(id)).thenReturn(5);

    assertThat(stockService.getStock(id)).isEqualTo(-3);
  }
}
