package mg.yoan.lib.model.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class SaleRequest {
  private UUID customerId;
  private List<SaleLineRequest> lines;
}
