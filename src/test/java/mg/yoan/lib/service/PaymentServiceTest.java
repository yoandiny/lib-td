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
import mg.yoan.lib.model.Payment;
import mg.yoan.lib.model.PaymentMethod;
import mg.yoan.lib.model.PaymentStatus;
import mg.yoan.lib.model.Sale;
import mg.yoan.lib.model.SaleStatus;
import mg.yoan.lib.model.dto.PaymentRequest;
import mg.yoan.lib.repository.PaymentRepository;
import mg.yoan.lib.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock PaymentRepository paymentRepository;
  @Mock SaleRepository saleRepository;
  @InjectMocks PaymentService paymentService;

  @Test
  void create_withValidatedSale_createsPendingPayment() {
    UUID saleId = UUID.randomUUID();
    Sale sale =
        Sale.builder()
            .id(saleId)
            .status(SaleStatus.VALIDATED)
            .totalAmount(BigDecimal.valueOf(22.00))
            .build();

    PaymentRequest request = new PaymentRequest();
    request.setSaleId(saleId);
    request.setMethod(PaymentMethod.CARD);

    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(paymentRepository.findBySaleId(saleId)).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

    Payment result = paymentService.create(request);

    assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
    assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(22.00));
  }

  @Test
  void create_withUnknownSale_throwsNotFoundException() {
    UUID saleId = UUID.randomUUID();
    PaymentRequest request = new PaymentRequest();
    request.setSaleId(saleId);
    when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> paymentService.create(request));
  }

  @Test
  void create_withNonValidatedSale_throwsIllegalStateException() {
    UUID saleId = UUID.randomUUID();
    Sale sale = Sale.builder().id(saleId).status(SaleStatus.IN_PROGRESS).build();
    PaymentRequest request = new PaymentRequest();
    request.setSaleId(saleId);

    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));

    assertThrows(IllegalStateException.class, () -> paymentService.create(request));
    verify(paymentRepository, never()).save(any());
  }

  @Test
  void create_withExistingPaymentForSale_throwsIllegalStateException() {
    UUID saleId = UUID.randomUUID();
    Sale sale =
        Sale.builder().id(saleId).status(SaleStatus.VALIDATED).totalAmount(BigDecimal.TEN).build();
    PaymentRequest request = new PaymentRequest();
    request.setSaleId(saleId);

    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(paymentRepository.findBySaleId(saleId))
        .thenReturn(Optional.of(Payment.builder().id(UUID.randomUUID()).build()));

    assertThrows(IllegalStateException.class, () -> paymentService.create(request));
    verify(paymentRepository, never()).save(any());
  }

  @Test
  void process_withPendingPayment_setsStatusPaid() {
    UUID id = UUID.randomUUID();
    Payment payment = Payment.builder().id(id).status(PaymentStatus.PENDING).build();

    when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
    when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

    Payment result = paymentService.process(id);

    assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
    assertThat(result.getPaidAt()).isNotNull();
  }

  @Test
  void process_withNonPendingPayment_throwsIllegalStateException() {
    UUID id = UUID.randomUUID();
    Payment payment = Payment.builder().id(id).status(PaymentStatus.PAID).build();

    when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

    assertThrows(IllegalStateException.class, () -> paymentService.process(id));
    verify(paymentRepository, never()).save(any());
  }

  @Test
  void getById_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(paymentRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> paymentService.getById(id));
  }

  @Test
  void getBySaleId_withUnknownSale_throwsNotFoundException() {
    UUID saleId = UUID.randomUUID();
    when(paymentRepository.findBySaleId(saleId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> paymentService.getBySaleId(saleId));
  }

  @Test
  void getAll_returnsAllPayments() {
    when(paymentRepository.findAll())
        .thenReturn(List.of(Payment.builder().id(UUID.randomUUID()).build()));

    assertThat(paymentService.getAll()).hasSize(1);
  }

  @Test
  void failPayment_withPendingPayment_setsStatusFailed() {
    UUID id = UUID.randomUUID();
    Payment payment = Payment.builder().id(id).status(PaymentStatus.PENDING).build();

    when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));
    when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

    assertThat(paymentService.failPayment(id).getStatus()).isEqualTo(PaymentStatus.FAILED);
  }

  @Test
  void failPayment_withNonPendingPayment_throwsIllegalStateException() {
    UUID id = UUID.randomUUID();
    Payment payment = Payment.builder().id(id).status(PaymentStatus.PAID).build();

    when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

    assertThrows(IllegalStateException.class, () -> paymentService.failPayment(id));
    verify(paymentRepository, never()).save(any());
  }
}
