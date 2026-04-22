package com.hotel.clientservice.repository;

import com.hotel.clientservice.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByFirstNameContainingIgnoreCase(String firstName);

    List<Client> findByLastNameContainingIgnoreCase(String lastName);

    Optional<Client> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Page<Client> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    Page<Client> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    Page<Client> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    long countByCreatedAtAfter(LocalDateTime threshold);
}
