package mg.yoan.lib.repository.specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import mg.yoan.lib.model.BookEdition;
import mg.yoan.lib.model.FormatLabel;
import mg.yoan.lib.model.dto.BookEditionSearchCriteria;
import org.junit.jupiter.api.Test;

class BookEditionSpecificationTest {

  @Test
  void fromCriteria_withAllCriteria_buildsAllPredicates() {
    BookEditionSearchCriteria criteria = new BookEditionSearchCriteria();
    criteria.setIsbn("ISBN");
    criteria.setMinPrice(10.0);
    criteria.setMaxPrice(20.0);
    criteria.setFormatLabel(FormatLabel.HARDCOVER);
    criteria.setTitle("Title");
    criteria.setGenre("Roman");
    criteria.setMinYear(1900);
    criteria.setMaxYear(2024);
    criteria.setAuthorName("Victor");

    Root<BookEdition> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    Expression<String> expression = mock(Expression.class);
    Path path = mock(Path.class);
    Join formatJoin = mock(Join.class);
    Join bookJoin = mock(Join.class);
    Join authorJoin = mock(Join.class);

    when(root.get(any(String.class))).thenReturn(path);
    when(root.join("format")).thenReturn(formatJoin);
    when(root.join("book")).thenReturn(bookJoin);
    when(formatJoin.get(any(String.class))).thenReturn(path);
    when(bookJoin.get(any(String.class))).thenReturn(path);
    when(bookJoin.join("author")).thenReturn(authorJoin);
    when(authorJoin.get(any(String.class))).thenReturn(path);
    when(cb.lower(any())).thenReturn(expression);
    when(cb.like(any(), any(String.class))).thenReturn(predicate);
    when(cb.greaterThanOrEqualTo(any(), any(Double.class))).thenReturn(predicate);
    when(cb.lessThanOrEqualTo(any(), any(Double.class))).thenReturn(predicate);
    when(cb.greaterThanOrEqualTo(any(), any(Integer.class))).thenReturn(predicate);
    when(cb.lessThanOrEqualTo(any(), any(Integer.class))).thenReturn(predicate);
    when(cb.equal(any(), any())).thenReturn(predicate);
    when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
    when(cb.and(any(Predicate[].class))).thenReturn(predicate);

    BookEditionSpecification.fromCriteria(criteria).toPredicate(root, query, cb);

    verify(root).join("format");
    verify(root).join("book");
    verify(bookJoin).join("author");
    verify(cb).like(any(), eq("%isbn%"));
    verify(cb).like(any(), eq("%title%"));
    verify(cb).like(any(), eq("%roman%"));
  }

  @Test
  void fromCriteria_withBlankStrings_ignoresBlankPredicates() {
    BookEditionSearchCriteria criteria = new BookEditionSearchCriteria();
    criteria.setIsbn(" ");
    criteria.setTitle(" ");
    criteria.setGenre(" ");
    criteria.setAuthorName(" ");

    Root<BookEdition> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(cb.and(any(Predicate[].class))).thenReturn(predicate);

    BookEditionSpecification.fromCriteria(criteria).toPredicate(root, query, cb);

    verify(cb).and(any(Predicate[].class));
  }
}
