package mg.yoan.lib.repository;

import java.util.List;
import java.util.UUID;
import mg.yoan.lib.model.SaleLine;
import mg.yoan.lib.model.dto.GenreRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleLineRepository extends JpaRepository<SaleLine, UUID> {
  @Query(
      """
    SELECT COALESCE(SUM(sl.quantity), 0) FROM SaleLine sl
    WHERE sl.bookEdition.id = :bookEditionId
    AND sl.sale.status <> mg.yoan.lib.model.SaleStatus.CANCELLED
""")
  Integer sumQuantityByBookEditionId(@Param("bookEditionId") UUID bookEditionId);

  @Query(
      """
          SELECT COALESCE(be.book.genre, 'UNKNOWN') AS genre,
                 COALESCE(SUM(sl.unitPrice * sl.quantity), 0) AS revenue
          FROM SaleLine sl
          JOIN sl.bookEdition be
          WHERE sl.sale.status = mg.yoan.lib.model.SaleStatus.VALIDATED
          GROUP BY COALESCE(be.book.genre, 'UNKNOWN')
      """)
  List<GenreRevenue> sumRevenueGroupedByGenre();
}
