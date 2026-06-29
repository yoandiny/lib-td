package mg.yoan.lib.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "author")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @ToString.Exclude
  @OneToMany(mappedBy = "author")
  private List<Book> books;
}
