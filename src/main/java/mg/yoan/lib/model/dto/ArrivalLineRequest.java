package mg.yoan.lib.model.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class ArrivalLineRequest {
  private UUID bookEditionId;
  private Integer quantity;
}
