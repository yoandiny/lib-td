package mg.yoan.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "arrival")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Arrival {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "arrived_at", nullable = false)
  private Instant arrivedAt;

  @ToString.Exclude
  @OneToMany(
      mappedBy = "arrival",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = jakarta.persistence.FetchType.LAZY)
  @JsonIgnore
  private List<ArrivalLine> lines;
}
