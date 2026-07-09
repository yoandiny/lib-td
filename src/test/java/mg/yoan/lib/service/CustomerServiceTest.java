package mg.yoan.lib.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Customer;
import mg.yoan.lib.model.dto.CustomerRequest;
import mg.yoan.lib.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock CustomerRepository customerRepository;
  @InjectMocks CustomerService customerService;

  @Test
  void create_savesAndReturnsCustomer() {
    CustomerRequest request =
        CustomerRequest.builder().fullName("Jean Dupont").email("jean@test.com").build();

    Customer saved =
        Customer.builder()
            .id(UUID.randomUUID())
            .fullName("Jean Dupont")
            .email("jean@test.com")
            .build();
    when(customerRepository.save(any(Customer.class))).thenReturn(saved);

    assertThat(customerService.create(request).getFullName()).isEqualTo("Jean Dupont");
  }

  @Test
  void getAll_returnsAllCustomers() {
    when(customerRepository.findAll())
        .thenReturn(List.of(Customer.builder().id(UUID.randomUUID()).build()));

    assertThat(customerService.getAll()).hasSize(1);
  }

  @Test
  void getById_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(customerRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> customerService.getById(id));
  }

  @Test
  void update_withExistingId_updatesCustomer() {
    UUID id = UUID.randomUUID();
    Customer existing = Customer.builder().id(id).fullName("Old").email("old@test.com").build();
    CustomerRequest request =
        CustomerRequest.builder().fullName("Jean Dupont").email("jean@test.com").build();

    when(customerRepository.findById(id)).thenReturn(Optional.of(existing));
    when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

    assertThat(customerService.update(id, request).getFullName()).isEqualTo("Jean Dupont");
  }

  @Test
  void delete_withExistingId_deletesCustomer() {
    UUID id = UUID.randomUUID();
    when(customerRepository.existsById(id)).thenReturn(true);

    customerService.delete(id);

    verify(customerRepository).deleteById(id);
  }

  @Test
  void delete_withUnknownId_throwsNotFoundException() {
    UUID id = UUID.randomUUID();
    when(customerRepository.existsById(id)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> customerService.delete(id));
  }
}
