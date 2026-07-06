package mg.yoan.lib.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.repository.ArrivalLineRepository;
import mg.yoan.lib.repository.BookEditionRepository;
import mg.yoan.lib.repository.BookRepository;
import mg.yoan.lib.repository.SaleLineRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {
  private final ArrivalLineRepository arrivalLineRepository;
  private final SaleLineRepository saleLineRepository;
  private final BookEditionRepository bookEditionRepository;
  private final BookRepository bookRepository;

  public int getStock(UUID bookEditionId) {
    int arrived = arrivalLineRepository.sumQuantityByBookEditionId(bookEditionId);
    int sold = saleLineRepository.sumQuantityByBookEditionId(bookEditionId);
    return arrived - sold;
  }

  public Map<String, Integer> getStockByBook(UUID bookId) {
    if (!bookRepository.existsById(bookId)) {
      throw new NotFoundException("Book with id " + bookId + " not found");
    }

    List<BookEdition> editions = bookEditionRepository.findAllByBookId(bookId);
    Map<String, Integer> stockByFormat = new TreeMap<>();

    for (BookEdition edition : editions) {
      String label =
              edition.getFormat() != null ? edition.getFormat().getFormatLabel().name() : "UNKNOWN";
      int stock = getStock(edition.getId());
      stockByFormat.merge(label, stock, Integer::sum);
    }

    return stockByFormat;
  }
}