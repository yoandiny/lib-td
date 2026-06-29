package mg.yoan.lib.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "arrival_line")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalLine {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "arrival_id", nullable = false)
  private Arrival arrival;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_edition_id", nullable = false)
  private BookEdition bookEdition;

  @Column(nullable = false)
  private Integer quantity;
}
