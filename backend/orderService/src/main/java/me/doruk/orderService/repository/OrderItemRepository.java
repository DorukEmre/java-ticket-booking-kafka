package me.doruk.orderService.repository;

import me.doruk.orderService.entity.OrderItem;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  Optional<List<OrderItem>> findAllByOrderId(Long orderId);
}
