package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.Customer;
import mg.yoan.lib.model.Sale;
import mg.yoan.lib.model.SaleLine;
import mg.yoan.lib.model.SaleStatus;
import mg.yoan.lib.model.dto.SaleLineRequest;
import mg.yoan.lib.model.dto.SaleRequest;
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
  void create_withValidCustomerAndLines_createsInProgressSale() {
    UUID customerId = UUID.randomUUID();
    UUID editionId = UUID.randomUUID();
    Customer customer = Customer.builder().id(customerId).build();
    BookEdition edition = BookEdition.builder().id(editionId).price(19.90).build();

    SaleLineRequest lineRequest = new SaleLineRequest();
    lineRequest.setBookEditionId(editionId);
    lineRequest.setQuantity(2);

    SaleRequest request = new SaleRequest();
    request.setCustomerId(customerId);
    request.setLines(List.of(lineRequest));

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(bookEditionRepository.findById(editionId)).thenReturn(Optional.of(edition));
    when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));
    when(saleLineRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    Sale result = saleService.create(request);

    assertThat(result.getStatus()).isEqualTo(SaleStatus.IN_PROGRESS);
    assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(39.80));
  }

  @Test
  void create_withUnknownCustomer_throwsNotFoundException() {
    UUID customerId = UUID.randomUUID();
    SaleRequest request = new SaleRequest();
    request.setCustomerId(customerId);
    when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> saleService.create(request));
  }

  @Test
  void create_withUnknownBookEdition_throwsNotFoundException() {
    UUID customerId = UUID.randomUUID();
    UUID editionId = UUID.randomUUID();
    Customer customer = Customer.builder().id(customerId).build();

    SaleLineRequest lineRequest = new SaleLineRequest();
    lineRequest.setBookEditionId(editionId);
    lineRequest.setQuantity(1);

    SaleRequest request = new SaleRequest();
    request.setCustomerId(customerId);
    request.setLines(List.of(lineRequest));

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));
    when(bookEditionRepository.findById(editionId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> saleService.create(request));
  }

  @Test
  void create_withBookEditionMissingPrice_throwsIllegalArgumentException() {
    UUID customerId = UUID.randomUUID();
    UUID editionId = UUID.randomUUID();
    Customer customer = Customer.builder().id(customerId).build();
    BookEdition edition = BookEdition.builder().id(editionId).price(null).build();

    SaleLineRequest lineRequest = new SaleLineRequest();
    lineRequest.setBookEditionId(editionId);
    lineRequest.setQuantity(1);

    SaleRequest request = new SaleRequest();
    request.setCustomerId(customerId);
    request.setLines(List.of(lineRequest));

    when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
    when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));
    when(bookEditionRepository.findById(editionId)).thenReturn(Optional.of(edition));

    assertThrows(IllegalArgumentException.class, () -> saleService.create(request));
  }

  @Test
  void getAll_returnsAllSales() {
    when(saleRepository.findAll())
            .thenReturn(List.of(Sale.builder().id(UUID.randomUUID()).build()));

    assertThat(saleService.getAll()).hasSize(1);
  }

  @Test
  void getById_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(saleRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> saleService.getById(id));
  }

  @Test
  void getByCustomer_withExistingCustomer_returnsSales() {
    UUID customerId = UUID.randomUUID();
    when(customerRepository.existsById(customerId)).thenReturn(true);
    when(saleRepository.findAllByCustomerId(customerId))
            .thenReturn(List.of(Sale.builder().id(UUID.randomUUID()).build()));

    assertThat(saleService.getByCustomer(customerId)).hasSize(1);
  }

  @Test
  void getByCustomer_withUnknownCustomer_throwsNotFoundException() {
    UUID customerId = UUID.randomUUID();
    when(customerRepository.existsById(customerId)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> saleService.getByCustomer(customerId));
  }

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

  @Test
  void cancel_withInProgressSale_setsStatusCancelled() {
    UUID saleId = UUID.randomUUID();
    Sale sale = Sale.builder().id(saleId).status(SaleStatus.IN_PROGRESS).build();

    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

    assertThat(saleService.cancel(saleId).getStatus()).isEqualTo(SaleStatus.CANCELLED);
  }

  @Test
  void cancel_withAlreadyCancelledSale_throwsIllegalStateException() {
    UUID saleId = UUID.randomUUID();
    Sale sale = Sale.builder().id(saleId).status(SaleStatus.CANCELLED).build();

    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));

    assertThrows(IllegalStateException.class, () -> saleService.cancel(saleId));
    verify(saleRepository, never()).save(any());
  }
}