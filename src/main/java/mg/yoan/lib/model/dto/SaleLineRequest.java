package mg.yoan.lib.model.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class SaleLineRequest {
  private UUID bookEditionId;
  private Integer quantity;
}
