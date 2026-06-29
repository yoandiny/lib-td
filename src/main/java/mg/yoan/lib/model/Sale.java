package mg.yoan.lib.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "sale")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_customer", nullable = false)
  private Customer customer;

  @Column(name = "sale_date", nullable = false)
  private LocalDateTime saleDate;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SaleStatus status;

  @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private List<SaleLine> saleLines;

  @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL)
  @ToString.Exclude
  private Payment payment;

  public boolean isPending() {
    return SaleStatus.IN_PROGRESS.equals(this.status);
  }

  public void validate() {
    if (!isPending()) {
      throw new IllegalStateException("Only IN_PROGRESS sales can be validated");
    }
    this.status = SaleStatus.VALIDATED;
  }

  public void cancel() {
    if (SaleStatus.CANCELLED.equals(this.status)) {
      throw new IllegalStateException("Sale is already cancelled");
    }
    this.status = SaleStatus.CANCELLED;
  }
}
