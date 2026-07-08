package mg.yoan.lib.repository;

import java.util.UUID;
import mg.yoan.lib.model.Format;
import mg.yoan.lib.model.FormatLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormatRepository extends JpaRepository<Format, UUID> {
  boolean existsByFormatLabel(FormatLabel formatLabel);
}
