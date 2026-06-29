package mg.yoan.lib.repository;

import java.util.UUID;
import mg.yoan.lib.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {}
