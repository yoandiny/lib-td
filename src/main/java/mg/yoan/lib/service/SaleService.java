package mg.yoan.lib.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.*;
import mg.yoan.lib.model.dto.SaleLineRequest;
import mg.yoan.lib.model.dto.SaleRequest;
import mg.yoan.lib.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SaleService {
  private final SaleRepository saleRepository;
  private final SaleLineRepository saleLineRepository;
  private final CustomerRepository customerRepository;
  private final BookEditionRepository bookEditionRepository;
  private final StockService stockService;

  @Transactional
  public Sale create(SaleRequest request) {
    Customer customer =
        customerRepository
            .findById(request.getCustomerId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Customer with id " + request.getCustomerId() + " not found"));
    var sale =
        Sale.builder()
            .customer(customer)
            .saleDate(Instant.now())
            .status(SaleStatus.IN_PROGRESS)
            .totalAmount(BigDecimal.ZERO)
            .build();

    var savedSale = saleRepository.save(sale);
    var lines = request.getLines().stream().map(l -> buildSaleLine(savedSale, l)).toList();
    saleLineRepository.saveAll(lines);

    BigDecimal total =
        lines.stream()
            .map(l -> l.getUnitPrice().multiply(BigDecimal.valueOf(l.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    savedSale.setTotalAmount(total);
    savedSale.setSaleLines(lines);

    return saleRepository.save(savedSale);
  }

  public List<Sale> getAll() {
    return saleRepository.findAll();
  }

  public Sale getById(UUID id) {
    return saleRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Sale with id " + id + " not found"));
  }

  public List<Sale> getByCustomer(UUID customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new NotFoundException("Customer with id " + customerId + " not found");
    }
    return saleRepository.findAllByCustomerId(customerId);
  }

  @Transactional
  public Sale validate(UUID id) {
    Sale sale = getById(id);

    for (SaleLine line : sale.getSaleLines()) {
      int stock = stockService.getStock(line.getBookEdition().getId());
      if (stock < 0) {
        throw new IllegalStateException(
            "Cannot validate sale "
                + id
                + ": insufficient stock for BookEdition "
                + line.getBookEdition().getId()
                + " (stock would be "
                + stock
                + ")");
      }
    }

    sale.validate();
    return saleRepository.save(sale);
  }

  @Transactional
  public Sale cancel(UUID id) {
    var sale = getById(id);
    sale.cancel();
    return saleRepository.save(sale);
  }

  private SaleLine buildSaleLine(Sale sale, SaleLineRequest lineRequest) {
    BookEdition bookEdition =
        bookEditionRepository
            .findById(lineRequest.getBookEditionId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "BookEdition with id " + lineRequest.getBookEditionId() + " not found"));

    if (bookEdition.getPrice() == null) {
      throw new IllegalArgumentException(
          "BookEdition with id " + bookEdition.getId() + " has no price set");
    }

    return SaleLine.builder()
        .sale(sale)
        .bookEdition(bookEdition)
        .unitPrice(BigDecimal.valueOf(bookEdition.getPrice()))
        .quantity(lineRequest.getQuantity())
        .build();
  }
}
