package mg.yoan.lib.repository;

import java.util.List;
import java.util.UUID;
import mg.yoan.lib.model.Sale;
import mg.yoan.lib.model.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
  List<Sale> findAllByCustomerId(UUID customerId);

  List<Sale> findAllByStatus(SaleStatus status);
}
