package mg.yoan.lib.model.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class BookEditionRequest {
  private String isbn;
  private UUID bookId;
  private UUID formatId;
  private Double price;
}
