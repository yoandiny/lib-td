package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.Sale;
import mg.yoan.lib.model.SaleLine;
import mg.yoan.lib.model.SaleStatus;
import mg.yoan.lib.repository.BookEditionRepository;
import mg.yoan.lib.repository.CustomerRepository;
import mg.yoan.lib.repository.SaleLineRepository;
import mg.yoan.lib.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

  @Mock SaleRepository saleRepository;
  @Mock SaleLineRepository saleLineRepository;
  @Mock CustomerRepository customerRepository;
  @Mock BookEditionRepository bookEditionRepository;
  @Mock StockService stockService;

  @InjectMocks SaleService saleService;

  @Test
  void validate_withSufficientStock_setsStatusValidated() {
    UUID saleId = UUID.randomUUID();
    UUID editionId = UUID.randomUUID();

    SaleLine line =
        SaleLine.builder().bookEdition(BookEdition.builder().id(editionId).build()).build();
    Sale sale =
        Sale.builder().id(saleId).status(SaleStatus.IN_PROGRESS).saleLines(List.of(line)).build();

    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(stockService.getStock(editionId)).thenReturn(5);
    when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

    Sale result = saleService.validate(saleId);

    assertThat(result.getStatus()).isEqualTo(SaleStatus.VALIDATED);
  }

  @Test
  void validate_withInsufficientStock_throwsIllegalStateException() {
    UUID saleId = UUID.randomUUID();
    UUID editionId = UUID.randomUUID();

    SaleLine line =
        SaleLine.builder().bookEdition(BookEdition.builder().id(editionId).build()).build();
    Sale sale =
        Sale.builder().id(saleId).status(SaleStatus.IN_PROGRESS).saleLines(List.of(line)).build();

    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(stockService.getStock(editionId)).thenReturn(-1);

    assertThrows(IllegalStateException.class, () -> saleService.validate(saleId));
    verify(saleRepository, never()).save(any());
  }
}
