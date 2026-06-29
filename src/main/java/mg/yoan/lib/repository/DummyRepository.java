package mg.yoan.lib.repository;

import java.util.List;
import mg.yoan.lib.PojaGenerated;
import mg.yoan.lib.repository.model.Dummy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@PojaGenerated
@Repository
public interface DummyRepository extends JpaRepository<Dummy, String> {

  @Override
  List<Dummy> findAll();
}
