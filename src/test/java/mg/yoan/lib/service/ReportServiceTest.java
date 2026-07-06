package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import mg.yoan.lib.model.dto.GenreRevenue;
import mg.yoan.lib.repository.SaleLineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock SaleLineRepository saleLineRepository;
    @InjectMocks ReportService reportService;

    @Test
    void getRevenueByGenre_returnsAggregatedMap() {
        GenreRevenue roman = mock(GenreRevenue.class);
        when(roman.getGenre()).thenReturn("Roman");
        when(roman.getRevenue()).thenReturn(new BigDecimal("39.80"));

        GenreRevenue fantasy = mock(GenreRevenue.class);
        when(fantasy.getGenre()).thenReturn("Fantasy");
        when(fantasy.getRevenue()).thenReturn(new BigDecimal("22.00"));

        when(saleLineRepository.sumRevenueGroupedByGenre()).thenReturn(List.of(roman, fantasy));

        Map<String, BigDecimal> result = reportService.getRevenueByGenre();

        assertThat(result)
                .containsEntry("Roman", new BigDecimal("39.80"))
                .containsEntry("Fantasy", new BigDecimal("22.00"));
    }

    @Test
    void getRevenueByGenre_withNoValidatedSales_returnsEmptyMap() {
        when(saleLineRepository.sumRevenueGroupedByGenre()).thenReturn(List.of());

        assertThat(reportService.getRevenueByGenre()).isEmpty();
    }
}