package mg.yoan.lib.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import mg.yoan.lib.exception.NotFoundException;
import mg.yoan.lib.model.Customer;
import mg.yoan.lib.model.dto.CustomerRequest;
import mg.yoan.lib.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
  private final CustomerRepository customerRepository;

  public Customer create(CustomerRequest request) {
    Customer customer =
        Customer.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .registerAt(request.getRegisterAt())
            .build();
    return customerRepository.save(customer);
  }

  public List<Customer> getAll() {
    return customerRepository.findAll();
  }

  public Customer getById(UUID id) {
    return customerRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));
  }

  public Customer update(UUID id, CustomerRequest request) {
    Customer customer = getById(id);
    customer.setFullName(request.getFullName());
    customer.setEmail(request.getEmail());
    customer.setPhone(request.getPhone());
    customer.setRegisterAt(request.getRegisterAt());
    return customerRepository.save(customer);
  }

  public void delete(UUID id) {
    if (!customerRepository.existsById(id)) {
      throw new NotFoundException("Customer with id " + id + " not found");
    }
    customerRepository.deleteById(id);
  }
}
