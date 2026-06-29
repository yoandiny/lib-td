package mg.yoan.lib.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import mg.yoan.lib.model.Author;
import mg.yoan.lib.model.Book;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.Format;
import mg.yoan.lib.model.dto.BookEditionSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class BookEditionSpecification {
  private BookEditionSpecification() {}

  public static Specification<BookEdition> fromCriteria(BookEditionSearchCriteria criteria) {
    return (root, query, cb) -> {
      var predicates = new ArrayList<>();

      if (criteria.getIsbn() != null && !criteria.getIsbn().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("isbn")), like(criteria.getIsbn())));
      }
      if (criteria.getMinPrice() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
      }
      if (criteria.getMaxPrice() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
      }
      if (criteria.getFormatLabel() != null) {
        Join<BookEdition, Format> formatJoin = root.join("format");
        predicates.add(cb.equal(formatJoin.get("formatLabel"), criteria.getFormatLabel()));
      }

      boolean needsBookJoin =
          criteria.getTitle() != null
              || criteria.getGenre() != null
              || criteria.getMinYear() != null
              || criteria.getMaxYear() != null
              || criteria.getAuthorName() != null;

      if (needsBookJoin) {
        Join<BookEdition, Book> bookJoin = root.join("book");
        if (criteria.getTitle() != null && !criteria.getTitle().isBlank()) {
          predicates.add(cb.like(cb.lower(bookJoin.get("title")), like(criteria.getTitle())));
        }
        if (criteria.getGenre() != null && !criteria.getGenre().isBlank()) {
          predicates.add(cb.like(cb.lower(bookJoin.get("genre")), like(criteria.getGenre())));
        }
        if (criteria.getMinYear() != null) {
          predicates.add(
              cb.greaterThanOrEqualTo(bookJoin.get("publicationYear"), criteria.getMinYear()));
        }
        if (criteria.getMaxYear() != null) {
          predicates.add(
              cb.lessThanOrEqualTo(bookJoin.get("publicationYear"), criteria.getMaxYear()));
        }
        if (criteria.getAuthorName() != null && !criteria.getAuthorName().isBlank()) {
          Join<Book, Author> authorJoin = bookJoin.join("author");
          String pattern = like(criteria.getAuthorName());
          predicates.add(
              cb.or(
                  cb.like(cb.lower(authorJoin.get("firstName")), pattern),
                  cb.like(cb.lower(authorJoin.get("lastName")), pattern)));
        }
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static String like(String value) {
    return "%" + value.toLowerCase() + "%";
  }
}
