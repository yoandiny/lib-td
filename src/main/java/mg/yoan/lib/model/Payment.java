package mg.yoan.lib.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "payment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_sale", nullable = false, unique = true)
  private Sale sale;

  @Column(nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentMethod method;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  public void process() {
    if (!PaymentStatus.PENDING.equals(this.status)) {
      throw new IllegalStateException("Only PENDING payments can be processed");
    }
    this.status = PaymentStatus.PAID;
    this.paidAt = LocalDateTime.now();
  }
}
