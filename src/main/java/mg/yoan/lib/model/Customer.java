package mg.yoan.lib.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "customer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column private String phone;

  @Column(name = "register_at")
  private LocalDateTime registerAt;
}
