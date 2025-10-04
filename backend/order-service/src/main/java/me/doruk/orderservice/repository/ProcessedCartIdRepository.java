package me.doruk.orderservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.doruk.orderservice.entity.ProcessedCartId;

public interface ProcessedCartIdRepository extends JpaRepository<ProcessedCartId, UUID> {
}
