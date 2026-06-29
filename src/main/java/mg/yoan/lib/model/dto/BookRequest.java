package mg.yoan.lib.model.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class BookRequest {
  private String title;
  private UUID authorId;
  private String isbn;
  private Integer publishYear;
  private String genre;
}
