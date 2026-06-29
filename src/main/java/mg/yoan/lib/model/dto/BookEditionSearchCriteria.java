package mg.yoan.lib.model.dto;

import lombok.Data;
import mg.yoan.lib.model.FormatLabel;

@Data
public class BookEditionSearchCriteria {
  private String isbn;
  private FormatLabel formatLabel;
  private Double minPrice;
  private Double maxPrice;

  private String title;
  private String genre;
  private Integer minYear;
  private Integer maxYear;

  private String authorName;
}
