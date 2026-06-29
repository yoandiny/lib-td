package mg.yoan.lib.model.dto;

import java.util.UUID;
import lombok.Data;
import mg.yoan.lib.model.PaymentMethod;

@Data
public class PaymentRequest {
  private UUID saleId;
  private PaymentMethod method;
}
