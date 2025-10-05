package me.doruk.orderservice.repository;

import me.doruk.orderservice.entity.Order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  Optional<Order> findById(String id);

  Optional<List<Order>> findAllByCustomerId(Long customerId);
}
