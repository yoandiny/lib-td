package mg.yoan.lib.repository;

import java.util.UUID;
import mg.yoan.lib.model.ArrivalLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalLineRepository extends JpaRepository<ArrivalLine, UUID> {
  @Query(
      "SELECT COALESCE(SUM(al.quantity), 0) FROM ArrivalLine al WHERE al.bookEdition.id ="
          + " :bookEditionId")
  Integer sumQuantityByBookEditionId(@Param("bookEditionId") UUID bookEditionId);
}
