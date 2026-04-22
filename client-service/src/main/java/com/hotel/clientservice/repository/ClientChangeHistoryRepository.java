package com.hotel.clientservice.repository;

import com.hotel.clientservice.entity.ClientChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientChangeHistoryRepository extends JpaRepository<ClientChangeHistory, Long> {

    List<ClientChangeHistory> findByClientIdOrderByChangedAtDesc(Long clientId);
}
