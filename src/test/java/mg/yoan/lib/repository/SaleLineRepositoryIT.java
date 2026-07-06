package mg.yoan.lib.repository;

import java.time.LocalDateTime;
import java.util.List;

import mg.yoan.lib.model.*;
import mg.yoan.lib.model.dto.GenreRevenue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;

@SpringBootTest
@Testcontainers
class SaleLineRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    AuthorRepository authorRepository;
    @Autowired BookRepository bookRepository;
    @Autowired FormatRepository formatRepository;
    @Autowired BookEditionRepository bookEditionRepository;
    @Autowired CustomerRepository customerRepository;
    @Autowired SaleRepository saleRepository;
    @Autowired SaleLineRepository saleLineRepository;

    @Test
    void sumRevenueGroupedByGenre_ignoresNonValidatedSales() {
        Author author = authorRepository.save(Author.builder().firstName("Victor").lastName("Hugo").build());
        Book book =
                bookRepository.save(
                        Book.builder().title("Les Misérables").author(author).genre("Roman").build());
        Format format = formatRepository.save(Format.builder().formatLabel(FormatLabel.HARDCOVER).build());
        BookEdition edition =
                bookEditionRepository.save(
                        BookEdition.builder().book(book).format(format).price(19.90).build());
        Customer customer =
                customerRepository.save(
                        Customer.builder().fullName("Jean Dupont").email("jean@test.com").build());

        Sale validatedSale =
                saleRepository.save(
                        Sale.builder()
                                .customer(customer)
                                .saleDate(LocalDateTime.now())
                                .status(SaleStatus.VALIDATED)
                                .totalAmount(BigDecimal.valueOf(39.80))
                                .build());
        saleLineRepository.save(
                SaleLine.builder()
                        .sale(validatedSale)
                        .bookEdition(edition)
                        .unitPrice(BigDecimal.valueOf(19.90))
                        .quantity(2)
                        .build());

        Sale inProgressSale =
                saleRepository.save(
                        Sale.builder()
                                .customer(customer)
                                .saleDate(LocalDateTime.now())
                                .status(SaleStatus.IN_PROGRESS)
                                .totalAmount(BigDecimal.valueOf(19.90))
                                .build());
        saleLineRepository.save(
                SaleLine.builder()
                        .sale(inProgressSale)
                        .bookEdition(edition)
                        .unitPrice(BigDecimal.valueOf(19.90))
                        .quantity(1)
                        .build());

        List<GenreRevenue> result = saleLineRepository.sumRevenueGroupedByGenre();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGenre()).isEqualTo("Roman");
        assertThat(result.get(0).getRevenue()).isEqualByComparingTo("39.80");
    }
}