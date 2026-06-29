package mg.yoan.lib.repository;

import java.util.List;
import java.util.UUID;
import mg.yoan.lib.model.BookEdition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookEditionRepository
    extends JpaRepository<BookEdition, UUID>, JpaSpecificationExecutor<BookEdition> {

  List<BookEdition> findAllByBookId(UUID bookId);
}
