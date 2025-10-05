package me.doruk.orderservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.doruk.orderservice.entity.OrderRequestLog;

public interface OrderRequestLogRepository extends JpaRepository<OrderRequestLog, UUID> {
  Optional<OrderRequestLog> findByOrderId(Long orderId);
}
