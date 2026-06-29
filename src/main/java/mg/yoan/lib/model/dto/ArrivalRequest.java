package mg.yoan.lib.model.dto;

import java.time.Instant;
import java.util.List;
import lombok.Data;

@Data
public class ArrivalRequest {
  private Instant arrivedAt;
  private List<ArrivalLineRequest> lines;
}
