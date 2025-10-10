package me.doruk.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.doruk.orderservice.entity.Customer;
import me.doruk.orderservice.request.UserCreateRequest;
import me.doruk.orderservice.repository.CustomerRepository;
import me.doruk.orderservice.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final CustomerRepository customerRepository;

  // Admin methods

  public List<UserResponse> listAllUsers() {
    final List<Customer> users = customerRepository.findAll();

    return users.stream().map(user -> UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build()).toList();
  }

  public UserResponse createUser(final UserCreateRequest request) {
    System.out.println("Create user called: " + request);
    Customer customer = new Customer();
    customer.setName(request.getName());
    customer.setEmail(request.getEmail());

    Customer savedUser = customerRepository.save(customer);

    return UserResponse.builder()
        .id(savedUser.getId())
        .name(savedUser.getName())
        .email(savedUser.getEmail())
        .build();
  }
}
