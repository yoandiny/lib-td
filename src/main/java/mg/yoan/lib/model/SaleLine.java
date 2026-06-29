package mg.yoan.lib.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "sale_line")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleLine {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_sale", nullable = false)
  private Sale sale;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_book_edition", nullable = false)
  private BookEdition bookEdition;

  // TODO: price history should be added later on instead
  @Column(name = "unit_price", nullable = false)
  private BigDecimal unitPrice;

  @Column(nullable = false)
  private Integer quantity;
}
