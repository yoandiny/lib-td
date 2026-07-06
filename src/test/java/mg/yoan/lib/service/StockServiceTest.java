package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.Format;
import mg.yoan.lib.model.FormatLabel;
import mg.yoan.lib.repository.ArrivalLineRepository;
import mg.yoan.lib.repository.BookEditionRepository;
import mg.yoan.lib.repository.BookRepository;
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
  @Mock BookEditionRepository bookEditionRepository;
  @Mock BookRepository bookRepository;

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

  @Test
  void getStockByBook_returnsMapGroupedByFormat() {
    UUID bookId = UUID.randomUUID();
    UUID editionHardcoverId = UUID.randomUUID();
    UUID editionPocketId = UUID.randomUUID();

    Format hardcover =
        Format.builder().id(UUID.randomUUID()).formatLabel(FormatLabel.HARDCOVER).build();
    Format pocket = Format.builder().id(UUID.randomUUID()).formatLabel(FormatLabel.POCKET).build();

    BookEdition editionHardcover =
        BookEdition.builder().id(editionHardcoverId).format(hardcover).build();
    BookEdition editionPocket = BookEdition.builder().id(editionPocketId).format(pocket).build();

    when(bookRepository.existsById(bookId)).thenReturn(true);
    when(bookEditionRepository.findAllByBookId(bookId))
        .thenReturn(List.of(editionHardcover, editionPocket));

    when(arrivalLineRepository.sumQuantityByBookEditionId(editionHardcoverId)).thenReturn(50);
    when(saleLineRepository.sumQuantityByBookEditionId(editionHardcoverId)).thenReturn(2);
    when(arrivalLineRepository.sumQuantityByBookEditionId(editionPocketId)).thenReturn(0);
    when(saleLineRepository.sumQuantityByBookEditionId(editionPocketId)).thenReturn(0);

    Map<String, Integer> result = stockService.getStockByBook(bookId);

    assertThat(result).containsEntry("HARDCOVER", 48).containsEntry("POCKET", 0);
  }

  @Test
  void getStockByBook_withUnknownBook_throwsNotFoundException() {
    UUID bookId = UUID.randomUUID();
    when(bookRepository.existsById(bookId)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> stockService.getStockByBook(bookId));
  }
}
